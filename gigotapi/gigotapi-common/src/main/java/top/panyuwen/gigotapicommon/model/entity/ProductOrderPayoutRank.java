package top.panyuwen.gigotapicommon.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName product_order_payout_rank
 */
@TableName(value ="product_order_payout_rank")
@Data
public class ProductOrderPayoutRank implements Serializable {
    /**
     * 用户昵称
     */
    @TableField(value = "userName")
    private String userName;

    /**
     * 
     */
    @TableField(value = "total")
    private Long total;

    /**
     * 
     */
    @TableField(value = "payOutRank")
    private Long payOutRank;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}