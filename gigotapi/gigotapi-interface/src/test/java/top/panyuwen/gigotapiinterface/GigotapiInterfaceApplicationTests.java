package top.panyuwen.gigotapiinterface;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import top.panyuwen.gigotapiclientsdk.model.response.*;
import top.panyuwen.gigotapiinterface.utils.ConvertToGigotApiResponseUtils;
import top.panyuwen.gigotapiinterface.utils.RequestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class GigotapiInterfaceApplicationTests {

    @Test
    void contextLoads() throws InterruptedException, MalformedURLException {
    }

    @Test
    void getWeiboHotSearch(){
        String responseJson = RequestUtils.get("https://weibo.com/ajax/side/hotSearch");
        // 解析 JSON
        JSONObject jsonObject = JSONObject.parseObject(responseJson);

        // 获取微博的realtime数组
        JSONArray realtimeArray = jsonObject.getJSONObject("data").getJSONArray("realtime");
        // 遍历realtime数组并只保留note、label_name和num字段

        List<WeiboHot> weiboHotList = new ArrayList<>();
        for (int i = 0; i < realtimeArray.size(); i++) {
            JSONObject realtimeObject = realtimeArray.getJSONObject(i);
            JSONObject filteredObject = new JSONObject();
            String note = realtimeObject.getString("note");
            filteredObject.put("index", i+1);
            filteredObject.put("title", note);
            filteredObject.put("hotType", realtimeObject.getString("label_name"));
            filteredObject.put("hotNum", realtimeObject.getInteger("num"));
            filteredObject.put("url", "https://s.weibo.com/weibo?q=%23"+ URLUtil.encode(note) +"%23");
            WeiboHot weiboHot = filteredObject.toJavaObject(WeiboHot.class);
            weiboHotList.add(weiboHot);
        }
        WeiboHotSearchResponse weiboHotSearchResponse = new WeiboHotSearchResponse();
        weiboHotSearchResponse.setWeibohotSearch(weiboHotList);

        // 输出修改后的 JSON
        System.out.println(weiboHotSearchResponse);
    }

    @Test
    void getPublicIp() throws IOException {
        InputStream databaseStream = new ClassPathResource("GeoLite2-city.mmdb").getInputStream();
        // 初始化数据库阅读器
        DatabaseReader dbReader = new DatabaseReader.Builder(databaseStream).build();
        String ipAddress  = "106.83.4.202";

        // 解析 IP 地址并获取城市信息
        CityResponse cityResponse = null;
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            cityResponse = dbReader.city(inetAddress);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeoIp2Exception e) {
            // 从 CityResponse 中提取城市信息
            String cityName = "";
            String countryName = "";
            // 构造响应数据
            Map<String, String> responseData = new HashMap<>();
            responseData.put("ipAddress", ipAddress);
            responseData.put("city", cityName);
            responseData.put("country", countryName);
            // 将 Map 转换为 JSON 字符串
            System.out.println(RequestUtils.convertToListMap(JSON.toJSONString(responseData)));
            return;
        }

        // 从 CityResponse 中提取城市信息
        String cityName = cityResponse.getCity().getName();
        String countryName = cityResponse.getCountry().getName();

        // 构造响应数据
        Map<String, String> responseData = new HashMap<>();
        responseData.put("ipAddress", ipAddress);
        responseData.put("city", cityName);
        responseData.put("country", countryName);

        // 将 Map 转换为 JSON 字符串
        System.out.println(RequestUtils.convertToListMap(JSON.toJSONString(responseData)));

    }

    @Test
    void getHoroscope(){
        String type = "taurus";
        String time = "week";
        String responseJson = RequestUtils.get("https://api.vvhan.com/api/horoscope?type=" + type + "&time=" + time);
        // 2. 处理返回结果
        JSONObject data = JSONObject.parseObject(responseJson).getJSONObject("data");
        HoroscopeResponse horoscopeResponse = data.toJavaObject(HoroscopeResponse.class);
        System.out.println(horoscopeResponse);
    }

    @Test
    void getRandomWallpaper(){
        String wallpaperJson = RequestUtils.get("https://btstu.cn/sjbz/api.php?lx=dongman&format=json");
        RandomWallpaperResponse randomWallpaperResponse = JSONObject.parseObject(wallpaperJson).toJavaObject(RandomWallpaperResponse.class);
        System.out.println(randomWallpaperResponse);


    }

    @Test
    void getWeather(){
        String weatherJson = RequestUtils.get("https://api.vvhan.com/api/weather");
        WeatherResponse weatherResponse = JSON.parseObject(weatherJson, WeatherResponse.class);
        System.out.println(weatherResponse);
    }

}
