package top.panyuwen.gigotapibackend.model.dto.user;

import lombok.Data;

/**
 * @author PYW
 */
@Data
public class UserLoginByXcxRequest {

    private static final long serialVersionUID = 1L;

    private String code;

    private String scene;
}
