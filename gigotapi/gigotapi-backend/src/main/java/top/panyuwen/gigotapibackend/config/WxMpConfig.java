package top.panyuwen.gigotapibackend.config;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author PYW
 */
@Slf4j
@Configuration
@PropertySource("classpath:application-dev.yml")
@ConfigurationProperties(prefix = "wx.mp")
@Data
public class WxMpConfig {

    private String appId;

    private String secret;
}
