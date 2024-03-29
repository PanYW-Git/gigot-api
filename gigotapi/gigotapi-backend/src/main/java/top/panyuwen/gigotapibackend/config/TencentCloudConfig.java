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
@ConfigurationProperties(prefix = "tencent-cloud.sms")
@Data
public class TencentCloudConfig {

    private String secretId;

    private String secretKey;

    private String sdkAppId;

    private String templateId;
}
