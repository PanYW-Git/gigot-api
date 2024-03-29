package top.panyuwen.gigotapibackend.model.dto.userinterfaceinfo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 * @author PYW
 * @from www.panyuwen.top
 */
@Data
public class UserInterfaceInfoUpdateRequest implements Serializable {
    /**
     * id
     */
    @TableId
    private Long id;

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