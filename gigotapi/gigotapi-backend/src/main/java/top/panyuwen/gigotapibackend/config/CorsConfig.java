package top.panyuwen.gigotapibackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有来源访问
        config.addAllowedOriginPattern("*");

        // 允许所有请求方法
        config.addAllowedMethod("*");

        // 允许所有请求头，或者根据需要指定具体的请求头
        config.addAllowedHeader("*");

        // 允许携带凭证信息（比如 Cookie）
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}