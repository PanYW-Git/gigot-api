package top.panyuwen.gigotapiinterface.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ConvertToGigotApiResponseUtils {

    public static String baseConvert(String response) {
        JSONObject originalJsonObject = JSONObject.parseObject(response);
        // 创建一个新的 JSONObject 对象，放入处理后的数据
        JSONObject gigotApiJsonObject = new JSONObject();
        gigotApiJsonObject.put("code", 0);
        gigotApiJsonObject.put("data", originalJsonObject.get("data"));
        gigotApiJsonObject.put("message", "ok");
        return gigotApiJsonObject.toJSONString();
    }

    /**
     * 自定义data转换
     */
    public static String customDataConvert(String dataValue) {
        // 创建一个新的 JSONObject 对象，放入处理后的数据
        JSONObject gigotApiJsonObject = new JSONObject();
        gigotApiJsonObject.put("code", 0);
        gigotApiJsonObject.put("data", JSON.parse(dataValue));
        gigotApiJsonObject.put("message", "ok");
        return gigotApiJsonObject.toJSONString();
    }
}
