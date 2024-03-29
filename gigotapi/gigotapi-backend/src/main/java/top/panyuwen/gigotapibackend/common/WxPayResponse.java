package top.panyuwen.gigotapibackend.common;

import java.io.Serializable;

/**
 * 微信支付回调返回类
 */
public class WxPayResponse implements Serializable {

    /**
     * 错误码，SUCCESS为清算机构接收成功，其他错误码为失败。
     */
    private int code;

    /**
     * 返回信息，如非空，为错误原因。
     */
    private String message;

    public WxPayResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public WxPayResponse(int code) {
        this(code, "");
    }

    public WxPayResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }
}
