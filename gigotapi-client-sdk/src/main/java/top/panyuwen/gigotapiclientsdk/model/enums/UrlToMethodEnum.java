package top.panyuwen.gigotapiclientsdk.model.enums;

import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口信息状态枚举
 *
 * @author PYW
 * @from www.panyuwen.top
 */
public enum UrlToMethodEnum {

    name("/api/name", "getUsernameByPost"),
    poisonousChickenSoup("/api/poisonousChickenSoup", "getPoisonousChickenSoup"),
    weiboHotSearch("/api/weiboHotSearch", "getWeiboHotSearch"),
    horoscope("/api/horoscope", "getHoroscope"),
    publicIp("/api/publicIp", "getPublicIp"),
    randomWallpaper("/api/randomWallpaper", "getRandomWallpaper"),
    webFaviconIcon("/api/webFaviconIcon", "getWebFaviconIcon"),
    loveTalk("/api/loveTalk", "getLoveTalk"),
    weather("/api/weather", "getWeather");

    private final String path;

    private final String method;

    UrlToMethodEnum(String path, String method) {
        this.path = path;
        this.method = method;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.method).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static UrlToMethodEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (UrlToMethodEnum anEnum : UrlToMethodEnum.values()) {
            if (anEnum.method.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
