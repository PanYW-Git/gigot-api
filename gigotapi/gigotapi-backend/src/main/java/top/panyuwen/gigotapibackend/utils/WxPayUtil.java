package top.panyuwen.gigotapibackend.utils;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.config.WxPayConfig;
import top.panyuwen.gigotapibackend.exception.BusinessException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: PYW
 * @Date: 2024/02/06 08:51:32
 * @Version: 1.0
 * @Description: wx 签名工具类
 */
@Slf4j
@Data
@Component
public class WxPayUtil {
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

    @Autowired
    public WxPayUtil(WxPayConfig wxPayConfig) {
        log.info("加载微信支付配置", this.toString());
        log.info("WxPayUtil:{}", this.toString());
        this.mchId = wxPayConfig.getMchId();
        this.mchSerialNo = wxPayConfig.getMchSerialNo();
        this.privateKeyPath = wxPayConfig.getPrivateKeyPath();
        log.info("密钥文件：",privateKeyPath);
        this.apiV3Key = wxPayConfig.getApiV3Key();
        this.appId = wxPayConfig.getAppId();
        this.domain = wxPayConfig.getDomain();
        this.notifyDomain = wxPayConfig.getNotifyDomain();
    }

    /**
     *
     * @param description 商品标题
     * @param outTradeNo 订单号（由接入系统生成，可作为微信支付的查询唯一标识）
     * @param Amount 支付金额
     * @param expirationTime 过期时间
     * @return 微信支付二维码地址
     */
    public String generateWeChatPayQRCodeUrl(String outTradeNo, String description , Integer Amount , LocalDateTime expirationTime) {
        // 输出所有成员变量的值
        log.info("WxPayUtil:{}", this.toString());
        log.info("WxPayUtilParams: outTradeNo:{} Amount:{} expirationTime:{}", outTradeNo, Amount, expirationTime);
        // 构建service
        Config config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(mchId)
                        .privateKeyFromPath(privateKeyPath)
                        .merchantSerialNumber(mchSerialNo)
                        .apiV3Key(apiV3Key)
                        .build();
        // 构建service
        NativePayService service = new NativePayService.Builder().config(config).build();
        // request.setXxx(val)设置所需参数，具体参数可见Request定义
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(Amount);
        request.setAmount(amount);
        request.setAppid(appId);
        request.setMchid(mchId);
        request.setNotifyUrl(notifyDomain);
        request.setDescription(description);
        request.setOutTradeNo(outTradeNo);
        // 设置订单的过期时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        // 格式化为 RFC3339 格式的字符串
        String format = expirationTime.format(formatter);
        request.setTimeExpire(format);
        // 调用下单方法，得到应答
        PrepayResponse response = null;
        try{
            response = service.prepay(request);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }

        log.info("获取到的二维码地址：{}", response.getCodeUrl());
        // 返回二维码
        return response.getCodeUrl();
    }

    /**
     * 获取回调请求头：签名相关
     *
     * @param request HttpServletRequest
     * @return SignatureHeader
     */
    public static RequestParam getRequestParam(String requestBody,HttpServletRequest request) {
        // 获取通知签名
        String wechatSignature = request.getHeader("Wechatpay-Signature");
        String wechatpayNonce = request.getHeader("Wechatpay-Nonce");
        String wechatPaySerial = request.getHeader("Wechatpay-Serial");
        String wechatTimestamp = request.getHeader("Wechatpay-Timestamp");

        // 获取请求体


        // 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(wechatPaySerial)
                .nonce(wechatpayNonce)
                .signature(wechatSignature)
                .timestamp(wechatTimestamp)
                //获取请求体
                .body(requestBody)
                .build();
        return requestParam;
    }
}