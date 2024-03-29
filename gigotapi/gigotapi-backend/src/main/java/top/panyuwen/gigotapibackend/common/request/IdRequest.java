package top.panyuwen.gigotapibackend.common.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author PYW
 */
@Data
public class IdRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
