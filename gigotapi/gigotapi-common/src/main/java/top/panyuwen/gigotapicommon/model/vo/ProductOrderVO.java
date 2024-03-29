package top.panyuwen.gigotapicommon.model.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import top.panyuwen.gigotapicommon.model.entity.ProductInfo;
import top.panyuwen.gigotapicommon.model.entity.ProductOrder;
import top.panyuwen.gigotapicommon.model.entity.ProductOrder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 视图
 *
 * @author PYW
 * @from www.panyuwen.top
 */
@Data
public class ProductOrderVO implements Serializable {
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
    private ProductInfo productInfo;

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
    private LocalDateTime expirationTime;

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
     * 包装类转对象
     *
     * @param productOrderVO
     * @return
     */
    public static ProductOrder voToObj(ProductOrderVO productOrderVO) {
        if (productOrderVO == null) {
            return null;
        }
        ProductOrder productOrder = new ProductOrder();
        BeanUtils.copyProperties(productOrderVO, productOrder);
        return productOrder;
    }

    /**
     * 对象转包装类
     *
     * @param productOrder
     * @return
     */
    public static ProductOrderVO objToVo(ProductOrder productOrder) {
        if (productOrder == null) {
            return null;
        }
        ProductOrderVO productOrderVO = new ProductOrderVO();
        BeanUtils.copyProperties(productOrder, productOrderVO);
        return productOrderVO;
    }
}