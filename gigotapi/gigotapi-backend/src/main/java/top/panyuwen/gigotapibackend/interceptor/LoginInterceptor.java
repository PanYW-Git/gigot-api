package top.panyuwen.gigotapibackend.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.utils.UserHolder;
import top.panyuwen.gigotapicommon.model.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 登录拦截器
 * @author PYW
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private UserHolder userHolder;

    public LoginInterceptor(UserHolder userHolder){
        this.userHolder = userHolder;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        log.info("获取到的token=============》authorization：{}",authorization);
        LoginUserVO user = userHolder.getUser(authorization);
        if(user == null){
            //未获取到登录信息
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        log.debug("LoginInterceptor资源放行.....");
        return HandlerInterceptor.super.preHandle(request, response, handler);
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
