package top.panyuwen.gigotapibackend.model.dto.productorder;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import top.panyuwen.gigotapibackend.common.PageRequest;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 查询请求
 *
 * @author PYW
 * @from www.panyuwen.top
 */
//@EqualsAndHashCode(callSuper = true)
@Data
public class ProductOrderUpdateRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 二维码地址
     */
    private String codeUrl;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String orderName;

    /**
     * 金额(分)
     */
    private Long total;

    /**
     * 交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）
     USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)
     */
    private String status;

    /**
     * 支付方式（默认 WX- 微信 ZFB- 支付宝）
     */
    private String payType;

    /**
     * 商品信息
     */
    private String productInfo;

    /**
     * 支付宝formData
     */
    private String formData;

    /**
     * 增加金币个数
     */
    private Long addGoldCoin;

    /**
     * 过期时间
     */
    private Date expirationTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @OrderBy(asc=false)
    private LocalDateTime updateTime;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}