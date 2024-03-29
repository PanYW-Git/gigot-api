package top.panyuwen.gigotapibackend.interceptor;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.panyuwen.gigotapibackend.utils.UserHolder;
import top.panyuwen.gigotapicommon.model.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import static top.panyuwen.gigotapibackend.constant.RedisConstants.*;
import static top.panyuwen.gigotapibackend.constant.SystemConstant.REQUEST_ID_HEADER;


/**
 * @author PYW
 */
@Slf4j
public class RefreshToTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshToTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 统计访问流量
        // 获取公网地址

        stringRedisTemplate.opsForHyperLogLog().add(SYSTEM_PV_KEY, UUID.fastUUID() + "-" + request.getRemoteAddr());
        // 获取请求路径
        String requestURI = request.getRequestURI();
        //1.获取请求头中的token
        String token = request.getHeader("Authorization");
        log.debug("请求路径：{}  token：{}" ,requestURI, token);
        //2.判断用户是否存在
        if(StrUtil.isBlank(token)){
            //4.不存在，拦截
            log.debug("请求头authorization为空");
            return true;
        }
        //3.从redis中获取
        String loginKey = LOGIN_TOKEN_KEY + token;
        String loginUserVOString = stringRedisTemplate.opsForValue().get(loginKey);
        //4.判断用户是否存在
        if(StrUtil.isBlank(loginUserVOString)){
            log.debug("用户不存在,loginUserVOString:{}",loginUserVOString);
            return true;
        }
        //将查询到的Hash数据转为UserDto对象
        //BeanUtil.fillBeanWithMap 填充bean通过map集合
        //参数一：从哪个map中填充？
        //参数二：填充哪个bean？
        //参数三：是否忽略异常？false不忽略，抛出，true，忽略异常
        //返回值：填充的bean
        LoginUserVO loginUserVO = JSON.parseObject(loginUserVOString, LoginUserVO.class);
        //5.存在，保存到ThreadLocal
        UserHolder.saveUser(loginUserVO);
        //6.重新设置有效期（只要用户访问就重置用户登录命令redis的有效期）
        stringRedisTemplate.expire(loginKey,LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
