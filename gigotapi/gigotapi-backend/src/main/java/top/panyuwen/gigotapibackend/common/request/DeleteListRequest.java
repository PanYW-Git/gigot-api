package top.panyuwen.gigotapibackend.common.request;


import lombok.Data;

import java.util.List;

/**
 * @author PYW
 */
@Data
public class DeleteListRequest {
    /**
     * id组
     */
    private List<Long> ids;

    private static final long serialVersionUID = 1L;
}
