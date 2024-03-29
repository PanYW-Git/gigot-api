package top.panyuwen.gigotapibackend.utils.redis;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static top.panyuwen.gigotapibackend.constant.RedisConstants.CACHE_NULL_TTL;
import static top.panyuwen.gigotapibackend.constant.RedisConstants.LOCK_KEY;

/**
 * 基于StringRedisTemplate封装缓存工具类
 *
 * set:将任意java对象序列化为json并存储在string类型的key中，并且可以设置TTL过期时间
 * setWithLogicalExpire方法:将任意java对象序列化为json并存储在string类型的key中，并且可以设置逻辑过期时间，用于处理缓存击穿问题
 * getWithPassThrough:根据指定的key查询缓存，并反序列化为指定类型，利用缓存空值的方式解决缓存穿透问题
 * getWithLogicalExpire:根据指定的key查询缓存，并反序列化为指定类型，需要利用逻辑过期解决缓存击穿问题
 * @author PYW
 */
@Slf4j
@Component
public class CacheClient {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    /**
     *  将任意java对象序列化为json并存储在string类型的key中，并且可以设置TTL过期时间
     * @param key
     * @param value
     * @param timeout
     * @param unit
     */
    public void set(String key, Object value, Long timeout, TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key,JSON.toJSONString(value),timeout,unit);
    }

    /**
     * 将任意java对象序列化为json并存储在string类型的key中，并且可以设置逻辑过期时间，用于处理缓存击穿问题
     * @param key
     * @param value
     * @param timeout
     * @param unit
     */
    public void setWithLogicalExpire(String key, Object value, Long timeout, TimeUnit unit){
        // 设置逻辑过期
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(timeout)));
        // 写入redis
        stringRedisTemplate.opsForValue().set(key,JSON.toJSONString(redisData),timeout,unit);
    }

    /**
     *  根据指定的key获取缓存
     * @param key
     * @return JSON
     */
    public String get(Object key){
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 根据指定的key查询缓存，并反序列化为指定类型，利用缓存空值的方式解决缓存穿透问题
     * @param keyPrefix
     * @param id
     * @param type
     * @param timeout
     * @param unit
     * @param dbFallback
     * @param <R>
     * @param <ID>
     * @return
     */
    public <R,ID> R getWithPassThrough(String keyPrefix, ID id, Class<R> type,Long timeout, TimeUnit unit, Function<ID,R> dbFallback){
        String key = keyPrefix + id;
        //1.从redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        //2.判断缓存是否命中
        /*
            StrUtil.isNotBlank
                形参为null false
                形参为"" false
                形参为"\t\n" false
                形参为"abc" true
         */
        if (StrUtil.isNotBlank(json)) {
            //3.命中，返回信息
            return JSON.parseObject(json, type);
        }
        //判断是否是空值
        if(json != null){
            // 返回错误信息
            return null;
        }
        //4.未命中，根据id查询数据库
        R r = dbFallback.apply(id);

        if (r == null) {
            //5.不存在放回404
            //解决缓存穿透问题，向redis插入空值
            stringRedisTemplate.opsForValue().set(keyPrefix + id,"",CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        //6.将商铺写入redis，返回商铺信息
        this.set(key,r,timeout,unit);
        return r;
    }

    //开启线程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    //锁过期时间 对外提供getset方法,默认10s
    private Long lockTimeout = 10L;
    private TimeUnit lockUnit = TimeUnit.SECONDS;
    public void setLockTimeout(Long lockTimeout,TimeUnit lockUnit) {
        this.lockTimeout = lockTimeout;
        this.lockUnit = lockUnit;
    }

    /**
     * 根据指定的key查询缓存，并反序列化为指定类型，需要利用逻辑过期解决缓存击穿问题
     * @param keyPrefix key前缀
     * @param id 标识（注：如果方法中参数id这个地方就传id）
     * @param type 返回值类型
     * @param timeout 过期时间
     * @param unit 过期时间单位
     * @param dbfailback 如果未获取到缓存中的数据执行的方法
     * @param <R>
     * @param <ID>
     * @return 查询结果
     */
    public <R,ID> R getWithLogicalExpire(String keyPrefix,ID id,Class<R> type,Long timeout,TimeUnit unit,Function<ID,R> dbfailback) {
        String key = keyPrefix + id;
        //1.从redis查询商铺缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        //2.判断缓存是否命中
        if (StrUtil.isBlank(json)) {
            //3.未命中，直接返回
            log.debug("未命中，开始判断redis中是否有\"\"数据");
            //检查是否存在redis但是redis数据为"" 说明用户已经请求过，且查询过数据库没有这个值，解决缓存穿透问题
            if(json == ""){
                log.debug("\"\"数据，直接返回null不走数据库");
                return null;
            }
            log.debug("redis中不存在这个数据，查询数据库");
            //查询数据库是否有这个数据
            R rMiss = dbfailback.apply(id);

            if(rMiss == null){
                log.debug("数据库查询结果为null，存入\"\"数据到redis");
                //解决缓存穿透问题，向redis插入空值
                stringRedisTemplate.opsForValue().set(keyPrefix + id,"",CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            log.debug("数据库查询结果不为null，存入数据到redis，并返回结果");
            //存入redis
            setWithLogicalExpire(key,rMiss,timeout,unit);
            //返回数据
            return rMiss;
        }
        //4.命中，需要把json反序列化为对象
        RedisData tempRedisData = JSON.parseObject(json, RedisData.class);
        LocalDateTime tempExpireTime = tempRedisData.getExpireTime();
        R r = JSON.parseObject(tempRedisData.getData().toString(),type);
        //5.判断是否过期
        if(LocalDateTime.now().isBefore(tempExpireTime)){
            //5.1未过期，直接返回店铺信息
            return r;
        }
        //5.2过期，需要缓存重建
        //6.缓存重建
        //6.1获取互斥锁
        String lockKey = LOCK_KEY + keyPrefix + id;
        boolean isLock = tryLock(lockKey);
        //6.2判断是否获取锁成功
        if(isLock){
            log.debug("获取锁成功！");
            //6.3成功
            //Redis doubleCheck 重新检查缓存，可能在获取锁之前其他线程已经将数据放入缓存
            //"Double Check" 是指在查询缓存之前，首先进行一次检查，看看数据是否存在于缓存中
            // 如果存在，则直接返回缓存数据。
            // 如果不存在,再进一步进行查询数据库的操作，并在查询到数据后，将数据存入缓存中，以供下一次查询使用。
            RedisData redisDataDoubleCheck = JSON.parseObject(stringRedisTemplate.opsForValue().get(key), RedisData.class);
            LocalDateTime expireTimeDoubleCheck = redisDataDoubleCheck.getExpireTime();
            if (LocalDateTime.now().isBefore(expireTimeDoubleCheck)) {
                //3.未过期，直接返回
                R rDoubleCheck = JSON.parseObject(tempRedisData.getData().toString(),type);
                log.debug("DoubleCheck未过期返回temp：{}",rDoubleCheck);
                return rDoubleCheck;
            }
            //过期，开启独立线程，实现缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                //重建缓存
                //实际开发中应该设置30分钟，这个地方只设置20s方便测试
                try {
                    //存入数据库
                    R r1 = dbfailback.apply(id);
                    //写入redis
                    setWithLogicalExpire(key,r1,timeout,unit);
                    log.debug("重建缓存成功！");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    // 释放锁
                    unlock(lockKey);
                }
            });
        }

        //6.4获取锁失败，返回旧的店铺信息
        log.debug("获取锁失败！");
        return r;
    }

    public boolean delete(String key){
        return stringRedisTemplate.delete(key);
    }

    public Long delete(Collection<String> keys){
        return stringRedisTemplate.delete(keys);
    }

    /**
     * 获取锁
     * @param key
     * @return false：锁被占用获取失败 true：锁没被占用
     */
    private boolean tryLock(String key){
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", lockTimeout, lockUnit);
        //需要转换为基本数据类型
        //拆箱可能会有空指针异常，所以使用糊涂包的工具类拆箱
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 解锁
     * @param key
     */
    private void unlock(String key){
        stringRedisTemplate.delete(key);
    }
}
