package top.panyuwen.gigotapiclientsdk.exception;

public class GigotApiException extends Exception{

    private int code;

    public GigotApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public GigotApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public GigotApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public GigotApiException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
