package top.panyuwen.gigotapibackend.model.dto.productinfo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import top.panyuwen.gigotapibackend.common.PageRequest;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询请求
 *
 * @author PYW
 * @from www.panyuwen.top
 */
//@EqualsAndHashCode(callSuper = true)
@Data
public class ProductInfoQueryRequest extends PageRequest implements Serializable {
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
     * 产品名称
     */
    private String name;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 金额(分)
     */
    private Long total;

    /**
     * 增加积分个数
     */
    private Long addGoldCoin;

    /**
     * 产品类型（VIP-会员 RECHARGE-充值,RECHARGEACTIVITY-充值活动）
     */
    private String productType;

    /**
     * 商品状态（0- 默认下线 1- 上线）
     */
    private Integer status;

    /**
     * 过期时间
     */
    private Date expirationTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}