package top.panyuwen.gigotapiinterface.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.panyuwen.gigotapiclientsdk.model.entity.User;
import top.panyuwen.gigotapiclientsdk.model.params.HoroscopeParams;
import top.panyuwen.gigotapiclientsdk.model.params.PublicIpParams;
import top.panyuwen.gigotapiclientsdk.model.params.WebFaviconIconParams;
import top.panyuwen.gigotapiclientsdk.model.response.*;
import org.springframework.core.io.ClassPathResource;


import top.panyuwen.gigotapiinterface.utils.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询名称 api
 * @author PYW
 */
@RestController
@RequestMapping("/")
@Slf4j
public class InterfaceController {

    private final DatabaseReader dbReader;

    public InterfaceController() throws IOException {
        log.info("正在加载GeoIP2 数据库文件......");
        // 加载 GeoIP2 数据库文件
        InputStream databaseStream = new ClassPathResource("GeoLite2-city.mmdb").getInputStream();
        log.info("加载文件成功");
        // 初始化数据库阅读器
        this.dbReader = new DatabaseReader.Builder(databaseStream).build();
    }

    @GetMapping()
    public String getNameByGet(String name){
        return "Get 你的名字是" + name;
    }

    @PostMapping()
    public String getNameByPost(@RequestParam String name){
        return "Post 你的名字是" + name;
    }

    /**
     * 通过POST请求获取用户名
     * @param user 用户信息
     * @param request HTTP请求对象
     * @return 用户名
     */
    @PostMapping("/name")
    public UserResponse getUsernameByPost(@RequestBody User user, HttpServletRequest request){
        log.info("访问成功 user:{}" , user);
        UserResponse userResponse = BeanUtil.copyProperties(user, UserResponse.class);
        return userResponse;
    }

    /**
     * 获取毒鸡汤
     * @return 返回毒鸡汤
     */
    @GetMapping("/poisonousChickenSoup")
    public PoisonousChickenSoupResponse getPoisonousChickenSoup(){
        String poisonousChickenSoupJson = RequestUtils.get("https://api.btstu.cn/yan/api.php?charset=utf-8&encode=json");
        PoisonousChickenSoupResponse poisonousChickenSoupResponse = JSON.parseObject(poisonousChickenSoupJson, PoisonousChickenSoupResponse.class);
        return poisonousChickenSoupResponse;
    }

    /**
     * 获取微博热搜
     * @return 返回微博热搜
     */
    @GetMapping("/weiboHotSearch")
    public WeiboHotSearchResponse getWeiboHotSearch(){
        // 1. 访问微博热搜接口
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

        // 3.返回
        return weiboHotSearchResponse;
    }

    /**
     * 获取星座运势
     * @return 返回星座运势
     */
    @PostMapping("/horoscope")
    public HoroscopeResponse getHoroscope(@RequestBody HoroscopeParams horoscope) {
        String type = horoscope.getType();
        String time = horoscope.getTime();
        String responseJson = RequestUtils.get("https://api.vvhan.com/api/horoscope?type=" + type + "&time=" + time);
        // 2. 处理返回结果
        JSONObject data = JSONObject.parseObject(responseJson).getJSONObject("data");
        HoroscopeResponse horoscopeResponse = data.toJavaObject(HoroscopeResponse.class);
        return horoscopeResponse;
    }

    /**
     * 获取用户公网ip地址和城市信息
     */
    @PostMapping ("/publicIp")
    public PublicIpResponse getPublicIp(@RequestBody PublicIpParams publicIp) {
        String ipAddress = publicIp.getIpAddress();

        // 解析 IP 地址并获取城市信息
        CityResponse cityResponse = null;
        String cityName = "";
        String countryName = "";
        PublicIpResponse publicIpResponse = new PublicIpResponse();

        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            cityResponse = dbReader.city(inetAddress);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeoIp2Exception e) {
            log.info("未查询到ip地址");
            // 从 CityResponse 中提取城市信息
            // 构造响应数据
            publicIpResponse.setIpAddress(ipAddress);
            publicIpResponse.setCity(cityName);
            publicIpResponse.setCountry(countryName);
            // 将 Map 转换为 JSON 字符串
            return publicIpResponse;
        }

        // 从 CityResponse 中提取城市信息
        cityName = cityResponse.getCity().getName();
        countryName = cityResponse.getCountry().getName();

        // 构造响应数据
        publicIpResponse.setIpAddress(ipAddress);
        publicIpResponse.setCity(cityName);
        publicIpResponse.setCountry(countryName);
        // 返回结果
        return publicIpResponse;
    }

    /**
     * 获取随机壁纸
     * @return
     */
    @GetMapping("/randomWallpaper")
    public RandomWallpaperResponse getRandomWallpaper(){
        String wallpaperJson = RequestUtils.get("https://btstu.cn/sjbz/api.php?lx=dongman&format=json");
        return JSONObject.parseObject(wallpaperJson).toJavaObject(RandomWallpaperResponse.class);
    }

    /**
     * 获取网站图标
     * @return
     */
    @GetMapping("/webFaviconIcon")
    public String getWebFaviconIcon(WebFaviconIconParams webFaviconIcon){
        String url = webFaviconIcon.getUrl();
        // todo 响应为图片处理
        return RequestUtils.get("https://btstu.cn/getfav/api.php?url="+url);
    }

    /**
     * 土味情话
     * @return
     */
    @GetMapping("/loveTalk")
    public LoveTalkResponse getLoveTalk() {
        String loveTalk = RequestUtils.get("https://api.vvhan.com/api/love");
        LoveTalkResponse loveTalkResponse = new LoveTalkResponse();
        loveTalkResponse.setText(loveTalk);
        return loveTalkResponse;
    }

    /**
     * 天气信息
     * @return
     */
    @GetMapping("/weather")
    public WeatherResponse getWeather() {
        String weatherJson = RequestUtils.get("https://api.vvhan.com/api/weather");
        return JSON.parseObject(weatherJson, WeatherResponse.class);
    }
}
