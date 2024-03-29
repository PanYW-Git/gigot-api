package top.panyuwen.gigotapicommon.model.vo.analysis;

import lombok.Data;

@Data
public class IntroduceRowVO {

    private static final long serialVersionUID = 1L;

    /**
     * 接口调用总数
     */
    private Long interfaceInfoCount;

    /**
     * 接口调用平均时间（最近1000条）
     */
    private Integer cost;

    /**
     * 系统访问量（PV）
     */
    private Long pv;

    /**
     * 在线用户数
     */
    private Long onLineUserCount;

    /**
     * 支付笔数
     */
    private Long successPayCount;

    /**
     * 待支付订单
     */
    private Long noPayCount;

    /**
     * 成功支付总数
     */
    private Long sucessTotalAmount;

    /**
     * 当日支付总数
     */
    private Long dayTotal;

    /**
     * 同日比
     */
    private String dayOverDay;

    /**
     * 同周比
     */
    private String weekOverWeek;
}
