package top.panyuwen.gigotapibackend.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 接口调用请求
 * @author PYW
 * @Description: 接口调用请求
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 请求参数
     */
    private Map<String, Object> RequestParams;

    private static final long serialVersionUID = 1L;
}
