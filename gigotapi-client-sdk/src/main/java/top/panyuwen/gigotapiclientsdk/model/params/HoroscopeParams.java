package top.panyuwen.gigotapiclientsdk.model.params;

import lombok.Data;

import java.io.Serializable;

/**
 * 星座运势接收参数
 * @author PYW
 */
@Data
public class HoroscopeParams implements Serializable {
    /**
     * 星座
     * 十二星座对应英文小写，aries, taurus, gemini, cancer, leo, virgo, libra, scorpio, sagittarius, capricorn, aquarius, pisces
     */
    private String type;

    /**
     * 时间周期
     * 今日明日一周等运势,today, nextday, week, month, year, love
     */
    private String time;

    private static final long serialVersionUID = 1L;
}
