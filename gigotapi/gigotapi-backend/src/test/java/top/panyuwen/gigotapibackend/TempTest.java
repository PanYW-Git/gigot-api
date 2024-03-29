package top.panyuwen.gigotapibackend;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.alibaba.fastjson.JSON;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.panyuwen.gigotapibackend.config.WxMpConfig;
import top.panyuwen.gigotapibackend.config.WxPayConfig;
import top.panyuwen.gigotapibackend.job.FileCheckJob;
import top.panyuwen.gigotapibackend.job.MessageJob;
import top.panyuwen.gigotapibackend.service.ProductOrderService;
import top.panyuwen.gigotapibackend.service.UserService;
import top.panyuwen.gigotapibackend.utils.MailUtils;
import top.panyuwen.gigotapibackend.wxmp.entity.CreateXcxQrRequestBodyRequest;
import top.panyuwen.gigotapiclientsdk.client.GigotApiClient;
import top.panyuwen.gigotapiclientsdk.exception.GigotApiException;
import top.panyuwen.gigotapiclientsdk.model.params.HoroscopeParams;
import top.panyuwen.gigotapiclientsdk.model.response.HoroscopeResponse;
import top.panyuwen.gigotapicommon.model.entity.ProductOrder;
import top.panyuwen.gigotapicommon.model.enums.PayTypeEnum;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@SpringBootTest
@Slf4j
public class TempTest {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    WxPayConfig wxPayConfig;

    @Autowired
    MailUtils mailUtil;

    @Autowired
    private UserService userService;

    @Autowired
    ProductOrderService productOrderService;

    @Value("${gigot-api.upload.avatarUrlFilePath}")
    private String avatarUrlFilePath;

    @Autowired
    FileCheckJob fileCheckJob;

    @Autowired
    private MessageJob messageJob;

    @Test
    void tempTest() {
        productOrderService.noPayOrderByDurationHandler(PayTypeEnum.WX);
    }





    @Test
    void createQRTest() throws FileNotFoundException {
        // 二维码配置
        QrConfig config = new QrConfig(1000, 1000);
        // 设置纠错级别
        config.setErrorCorrection(ErrorCorrectionLevel.H);
        // 生成二维码
        QrCodeUtil.generate("doc.panyuwen.top", config, FileUtil.file("C:\\Users\\PYW\\Desktop\\qrcode.jpg"));
    }

    @Autowired
    private WxMpConfig wxMpConfig;

    private OkHttpClient client = new OkHttpClient();

    @Test
    void generateXcxQRCode() throws IOException {
        // 这里假设你要调用的小程序接口是 /getQRCode，你需要替换成实际的接口地址
        String appId = wxMpConfig.getAppId();
        String secret = wxMpConfig.getSecret();
        String accessToken = getAccessToken(appId, secret);
        log.info("accessToken:{}",accessToken);
        String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken;
        // 这里假设你的小程序接口需要的参数是 scene，你需要根据实际情况调整
        CreateXcxQrRequestBodyRequest createXcxQrRequestBodyRequest = new CreateXcxQrRequestBodyRequest();
        createXcxQrRequestBodyRequest.setPage("pages/index/index");
        createXcxQrRequestBodyRequest.setScene(IdUtil.fastSimpleUUID());
        createXcxQrRequestBodyRequest.setCheck_path(false);
        String requestBody = JSON.toJSONString(createXcxQrRequestBodyRequest);

        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json"), requestBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        // 结果是图片输出到桌面
        FileUtil.writeBytes(response.body().bytes(), "C:\\Users\\PYW\\Desktop\\xcxqrcode.jpg");
    }


    // 获取 Access Token，这里简单示例一种获取方式，你可能需要根据实际情况调整
    private String getAccessToken(String appId, String secret) throws IOException {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + secret;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            // 这里简单解析 JSON，你可能需要使用 JSON 解析库来解析响应
            return parseAccessToken(responseBody);
        }
    }
    private String parseAccessToken(String response) {
        // 解析 JSON 获取 access token
        // 在实际中，建议使用 JSON 解析库（如 Jackson）来解析 JSON 字符串
        // 这里简化为直接从字符串中截取 access token 的部分
        return response.substring(response.indexOf("access_token") + 15, response.indexOf("\",\"expires_in"));
    }


    @Test
    void wxPayTest() {
    }

    @Test
    void tempCreateUUID() {
        // 生成uuid
        log.info(IdUtil.fastUUID());

        // 生成32位字符，数字大小写字母，不要-

        log.info(IdUtil.fastSimpleUUID());
    }


//    @Resource
//    private GigotApiClient gigotApiClient;

    @Test
    void clientTest() {
        // 1. 封装参数 如没有参数不传
        HoroscopeParams horoscopeParams = new HoroscopeParams();
        horoscopeParams.setType("virgo");
        horoscopeParams.setTime("week");
        // 2.调用自定义方法
        HoroscopeResponse horoscope = getHoroscope(horoscopeParams);
        // 其他业务逻辑
        log.info("horoscope:" + horoscope);
    }

    public HoroscopeResponse getHoroscope(HoroscopeParams horoscopeParams) {
        HoroscopeResponse horoscopeResponse = null;
        try {
            // 3. 创建Client示例 并传入密钥
            GigotApiClient gigotApiClient = new GigotApiClient("2c9bb2f3c2a8a8dc6fc08a0a7524fa82", "c67f6eeb3412e4b6a56a41e906c7e6e7");
            // 4. 调用sdk并返回结果
            return gigotApiClient.getHoroscope(horoscopeParams);
        } catch (GigotApiException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
