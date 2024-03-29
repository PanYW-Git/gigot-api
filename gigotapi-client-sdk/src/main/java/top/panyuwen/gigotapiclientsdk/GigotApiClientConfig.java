package top.panyuwen.gigotapiclientsdk;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import top.panyuwen.gigotapiclientsdk.client.GigotApiClient;

/**
 * 配置类，用于配置Gigot API客户端的相关参数
 * @author PYW
 */
@Configuration
@ConfigurationProperties("gigot-api.client")
@Data
@ComponentScan
public class GigotApiClientConfig {

    /**
     * secretId
     */
    private String secretId;

    /**
     * secretId
     */
    private String secretKey;

    /**
     * 返回GigotApiClient的Bean
     *
     * @return GigotApiClient对象
     */
    @Bean
    public GigotApiClient gigotApiClient(){
        return new GigotApiClient(secretId, secretKey);
    }
}
