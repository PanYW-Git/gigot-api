package top.panyuwen.gigotapibackend.model.dto.interfaceinfo;

import lombok.Data;

/**
 * @Author: PYW
 * @Date: 2023/09/15 03:52:36
 * @Version: 1.0
 * @Description: 请求参数字段
 */
@Data
public class ResponseParamsField {
    private String id;
    private String fieldName;
    private String type;
    private String desc;
}