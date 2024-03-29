package top.panyuwen.gigotapibackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author PYW
 */
@Data
public class UserNameUpdateRequest implements Serializable {

    private final Long SerialVersionUID = 1L;

    private Long id;

    private String userName;
}
