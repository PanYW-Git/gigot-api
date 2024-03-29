package top.panyuwen.gigotapiclientsdk.exception;

import lombok.Data;

/**
 * 异常响应
 * @author PYW
 */
@Data
public class ErrorResponse {

    private int code;

    private String message;
}