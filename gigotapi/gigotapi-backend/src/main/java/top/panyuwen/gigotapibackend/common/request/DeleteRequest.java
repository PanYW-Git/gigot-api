package top.panyuwen.gigotapibackend.common.request;

import java.io.Serializable;
import lombok.Data;

/**
 * 删除请求
 *
 * @author PYW
 * @from www.panyuwen.top
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}