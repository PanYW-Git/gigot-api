package top.panyuwen.gigotapibackend.model.dto.interfaceinfo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 搜索字段
     */
    private String SearchText;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应参数
     */
    private String responseParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 消费金币
     */
    private Long payGoldCoin;

    /**
     * 点赞
     */
    private Integer liked;

    /**
     * 请求示例
     */
    private String requestExample;

    /**
     * 调用总次数
     */
    private Long totalInvokes;

    /**
     * 返回格式（如JSON等）
     */
    private String returnFormat;

    /**
     * 创建人id
     */
    private Long userId;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}