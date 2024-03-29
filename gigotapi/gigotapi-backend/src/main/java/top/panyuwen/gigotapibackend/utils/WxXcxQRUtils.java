package top.panyuwen.gigotapibackend.utils;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.panyuwen.gigotapibackend.wxmp.entity.CreateXcxQrRequestBodyRequest;

import java.io.File;
import java.io.IOException;

/**
 * @author PYW
 */
@Slf4j
public class WxXcxQRUtils {

    private String appId;

    private String secret;

    public WxXcxQRUtils(String appId, String secret){
        this.appId = appId;
        this.secret = secret;
    }

    private OkHttpClient client = new OkHttpClient();

    /**
     * 获取小程序二维码
     * @param createXcxQrRequestBodyRequest 封装请求参数
     * @param qrCodeoutputPath 二维码输出路径
     * @return 二维码名称（相对路径）
     * @throws IOException
     */
    public String getXcxQRUtils(CreateXcxQrRequestBodyRequest createXcxQrRequestBodyRequest, String qrCodeoutputPath) throws IOException {
        // 这里假设你要调用的小程序接口是 /getQRCode，你需要替换成实际的接口地址
        String accessToken = getAccessToken(appId, secret);
        log.info("accessToken:{}",accessToken);
        String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken;
        // 这里假设你的小程序接口需要的参数是 scene，你需要根据实际情况调整
        String scene = createXcxQrRequestBodyRequest.getScene();
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
        // 结果是图片输出到系统
        String absolutePath = GetAbsolutePathUtils.getAbsolutePathUtil(qrCodeoutputPath);
        FileUtil.writeBytes(response.body().bytes(), absolutePath + File.separator + scene + ".jpg");
        return scene + ".jpg";
    }

    // 获取 Access Token，这里简单示例一种获取方式，你可能需要根据实际情况调整
    private String getAccessToken(String appId, String secret) throws IOException {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + secret;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()){
                throw new IOException("Unexpected code " + response);
            }

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
}
