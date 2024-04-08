package top.panyuwen.gigotapibackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.panyuwen.gigotapibackend.interceptor.LoginInterceptor;
import top.panyuwen.gigotapibackend.interceptor.RefreshToTokenInterceptor;
import top.panyuwen.gigotapibackend.utils.UserHolder;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserHolder userHolder;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录拦截器
        //token刷新拦截器
        registry.addInterceptor(new RefreshToTokenInterceptor(stringRedisTemplate,userHolder)).addPathPatterns(
                //拦截所有资源
                "/**"
        ).order(0);
        registry.addInterceptor(new LoginInterceptor(userHolder)).excludePathPatterns(
                "/pay/order/notify",
                "/user/register",
                "/user/login",
                "/user/login/email/sendVerificationCode",
                "/user/login/email",
                "/user/login/xcx",
                "/user/login/xcx/createQR",
                "/file/downloadQRCode**",
                "/file/sdk",
                "/user/get/login/xcx/check",
                "/user/get/login",
                "/user/login/wx_open",
                "/upload/**",
                // swagger资源放行
                "/interfaceInfo/**",
                "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**",
                "/doc.html**"
        ).order(1);
    }

    /**
     * 全局跨域配置
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}
