package top.panyuwen.gigotapiclientsdk.model.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author PYW
 */
@Data
public class HoroscopeResponse {
    private Todo todo;
    private Fortunetext fortunetext;
    private Fortune fortune;
    @JSONField(name = "shortcomment")
    private String shortComment;
    @JSONField(name = "luckycolor")
    private String luckyColor;
    private Index index;
    @JSONField(name = "luckynumber")
    private String luckyNumber;
    private String time;
    private String title;
    private String type;
    @JSONField(name = "luckyconstellation")
    private String luckyConstellation;
}

@Data
class Todo {
    private String yi;
    private String ji;
}

@Data
class Fortunetext {
    private String all;
    private String love;
    private String work;
    private String money;
    private String health;
}

@Data
class Fortune {
    private int all;
    private int love;
    private int work;
    private int money;
    private int health;
}

@Data
class Index {
    private String all;
    private String love;
    private String work;
    private String money;
    private String health;
}