package top.panyuwen.gigotapibackend.config;

import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
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
@ConfigurationProperties(prefix = "wx.pay")
@Data
public class WxPayConfig {

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户Api序列号
     */
    private String mchSerialNo;

    /**
     * 商户私钥文件
     */
    private String privateKeyPath;

    /**
     * APIv3秘钥
     */
    private String apiV3Key;

    /**
     * 微信小程序/公众号/服务号id
     */
    private String appId;

    /**
     * 微信服务器地址
     */
    private String domain;

    /**
     * 接收支付结果地址
     */
    private String notifyDomain;


    public NotificationConfig getNotificationConfig() {
        // 使用自动更新平台证书的RSA配置
        // 一个商户号只能初始化一个配置，否则会因为重复的下载任务报错
        return new RSAAutoCertificateConfig.Builder()
                .merchantId(mchId)
                .privateKeyFromPath(privateKeyPath)
                .merchantSerialNumber(mchSerialNo)
                .apiV3Key(apiV3Key)
                .build();
    }

    @Override
    public String toString() {
        return "WxPayConfig{" +
                "mchId='" + mchId + '\'' +
                ", mchSerialNo='" + mchSerialNo + '\'' +
                ", privateKeyPath='" + privateKeyPath + '\'' +
                ", apiV3Key='" + apiV3Key + '\'' +
                ", appId='" + appId + '\'' +
                ", domain='" + domain + '\'' +
                ", notifyDomain='" + notifyDomain + '\'' +
                '}';
    }
}
