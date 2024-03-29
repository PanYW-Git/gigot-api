package top.panyuwen.gigotapiinterface.utils;

import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import top.panyuwen.gigotapiclientsdk.exception.ErrorCode;
import top.panyuwen.gigotapiclientsdk.exception.GigotApiException;

import java.lang.reflect.Field;

/**
 * @Author: QiMu
 * @Date: 2023年09月22日 13:45
 * @Version: 1.0
 * @Description:
 */
@Slf4j
public class RequestUtils {

    /**
     * 生成url
     *
     * @param baseUrl 基本url
     * @param params  params
     * @return {@link String}
     * @throws GigotApiException api异常
     */
    public static <T> String buildUrl(String baseUrl, T params) throws GigotApiException {
        StringBuilder url = new StringBuilder(baseUrl);
        Field[] fields = params.getClass().getDeclaredFields();
        boolean isFirstParam = true;
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            // 跳过serialVersionUID属性
            if ("serialVersionUID".equals(name)) {
                continue;
            }
            try {
                Object value = field.get(params);
                if (value != null) {
                    if (isFirstParam) {
                        url.append("?").append(name).append("=").append(value);
                        isFirstParam = false;
                    } else {
                        url.append("&").append(name).append("=").append(value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new GigotApiException(ErrorCode.OPERATION_ERROR, "构建url异常");
            }
        }
        return url.toString();
    }

    /**
     * get请求
     *
     * @param baseUrl 基本url
     * @param params  params
     * @return {@link String}
     * @throws GigotApiException api异常
     */
    public static <T> String get(String baseUrl, T params) throws GigotApiException {
        return get(buildUrl(baseUrl, params));
    }

    /**
     * get请求
     *
     * @param url url
     * @return {@link String}
     */
    public static String get(String url) {
        String result = HttpRequest.get(url).execute().body();
        log.info("【interface】：请求地址：{}，响应数据：{}", url, result);
        return result;
    }

    /**
     * 返回单个结果转换为map String
     */
    public static String convertToListMap(String response) {
        // 转换为map
        try {
            // 第一个字符为[，说明不是List<Map<String, Object>>类型
            if(response.charAt(0) != '['){
                // 开头加上[{，结尾加上}]
                return "[" + response + "]";
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}