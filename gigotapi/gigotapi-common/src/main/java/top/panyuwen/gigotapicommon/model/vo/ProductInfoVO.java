package top.panyuwen.gigotapicommon.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import top.panyuwen.gigotapicommon.model.entity.ProductInfo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 接口信息视图
 *
 * @author PYW
 * @from www.panyuwen.top
 */
@Data
public class ProductInfoVO implements Serializable {

    private final static Gson GSON = new Gson();

    /**
     * id
     */
    @TableId
    private Long id;

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

    /**
     * 包装类转对象
     *
     * @param productInfoVO
     * @return
     */
    public static ProductInfo voToObj(ProductInfoVO productInfoVO) {
        if (productInfoVO == null) {
            return null;
        }
        ProductInfo productInfo = new ProductInfo();
        BeanUtils.copyProperties(productInfoVO, productInfo);
        return productInfo;
    }

    /**
     * 对象转包装类
     *
     * @param productInfo
     * @return
     */
    public static ProductInfoVO objToVo(ProductInfo productInfo) {
        if (productInfo == null) {
            return null;
        }
        ProductInfoVO productInfoVO = new ProductInfoVO();
        BeanUtils.copyProperties(productInfo, productInfoVO);
        return productInfoVO;
    }
}
