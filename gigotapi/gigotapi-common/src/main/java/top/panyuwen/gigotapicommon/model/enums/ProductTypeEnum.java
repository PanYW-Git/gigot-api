package top.panyuwen.gigotapicommon.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author PYW
 */

public enum ProductTypeEnum {

    /**
     * 支付成功
     */
    EXPERIENCE("体验", "EXPERIENCE"),

    /**
     * 充值活动
     */
    RECHARGEACTIVITY("充值活动", "RECHARGEACTIVITY"),

    /**
     * 充值
     */
    RECHARGE("充值", "RECHARGE");

    private final String text;

    private final String value;

    ProductTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static ProductTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ProductTypeEnum anEnum : ProductTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
