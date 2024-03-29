package top.panyuwen.gigotapibackend.model.dto.userinterfaceinfo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import top.panyuwen.gigotapibackend.common.PageRequest;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author PYW
 * @from www.panyuwen.top
 */
//@EqualsAndHashCode(callSuper = true)
@Data
public class UserInterfaceInfoQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 查询文本
     */
    private String searchText;

    /**
     * 调用人id
     */
    private Long userId;

    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 总调用次数
     */
    private Long totalInvokes;

    /**
     * 调用状态（0- 正常 1- 禁用·）
     */
    private Integer status;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}