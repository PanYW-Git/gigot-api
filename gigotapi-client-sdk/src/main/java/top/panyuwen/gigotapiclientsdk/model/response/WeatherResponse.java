package top.panyuwen.gigotapiclientsdk.model.response;

import lombok.Data;

/**
 * @author PYW
 */
@Data
public class WeatherResponse {
    private String city;
    private WeatherDetail info;

    // Constructors, getters, and setters
}

@Data
class WeatherDetail {
    private String date;
    private String week;
    private String type;
    private String low;
    private String high;
    private String fengxiang;
    private String fengli;
    private WeatherSubDetail night;
    private WeatherAir air;
    private String tip;

    // Constructors, getters, and setters
}

@Data
class WeatherSubDetail {
    private String type;
    private String fengxiang;
    private String fengli;

    // Constructors, getters, and setters
}

@Data
class WeatherAir {
    private int aqi;
    private int aqi_level;
    private String aqi_name;
    private String co;
    private String no2;
    private String o3;
    private String pm10;
    private String pm2_5;
    private String so2;

    // Constructors, getters, and setters
}
