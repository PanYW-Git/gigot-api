package top.panyuwen.gigotapibackend.utils;


import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import top.panyuwen.gigotapibackend.utils.redis.CacheClient;
import top.panyuwen.gigotapicommon.model.vo.LoginUserVO;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static top.panyuwen.gigotapibackend.constant.RedisConstants.LOGIN_TOKEN_KEY;
import static top.panyuwen.gigotapibackend.constant.RedisConstants.LOGIN_TOKEN_TTL;

@Component
public class UserHolder {

    @Resource
    private CacheClient cacheClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void saveUser(LoginUserVO loginUserVO){
        cacheClient.set(LOGIN_TOKEN_KEY + loginUserVO.getId(), loginUserVO, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
    }

    public LoginUserVO getUser(String authorization){
        return JSON.parseObject(cacheClient.get(LOGIN_TOKEN_KEY +authorization), LoginUserVO.class);
    }

    public void removeUser(String authorization){
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY + authorization);
    }
}
