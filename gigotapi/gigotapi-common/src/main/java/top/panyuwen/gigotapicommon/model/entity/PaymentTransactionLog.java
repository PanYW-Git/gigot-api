package top.panyuwen.gigotapicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 付款信息
 * @TableName payment_transaction_log
 */
@TableName(value ="payment_transaction_log")
@Data
public class PaymentTransactionLog implements Serializable {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 微信支付订单号
     */
    private String transactionId;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）
                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)
     */
    private String tradeState;

    /**
     * 交易状态描述
     */
    private String tradeStateDesc;

    /**
     * 支付完成时间
     */
    private String successTime;

    /**
     * 用户标识
     */
    private String openid;

    /**
     * 用户支付金额
     */
    private Long payerTotal;

    /**
     * 货币类型
     */
    private String currency;

    /**
     * 用户支付币种
     */
    private String payerCurrency;

    /**
     * 接口返回内容
     */
    private String content;

    /**
     * 总金额(分)
     */
    private Long total;

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

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        PaymentTransactionLog other = (PaymentTransactionLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderNo() == null ? other.getOrderNo() == null : this.getOrderNo().equals(other.getOrderNo()))
            && (this.getTransactionId() == null ? other.getTransactionId() == null : this.getTransactionId().equals(other.getTransactionId()))
            && (this.getTradeType() == null ? other.getTradeType() == null : this.getTradeType().equals(other.getTradeType()))
            && (this.getTradeState() == null ? other.getTradeState() == null : this.getTradeState().equals(other.getTradeState()))
            && (this.getTradeStateDesc() == null ? other.getTradeStateDesc() == null : this.getTradeStateDesc().equals(other.getTradeStateDesc()))
            && (this.getSuccessTime() == null ? other.getSuccessTime() == null : this.getSuccessTime().equals(other.getSuccessTime()))
            && (this.getOpenid() == null ? other.getOpenid() == null : this.getOpenid().equals(other.getOpenid()))
            && (this.getPayerTotal() == null ? other.getPayerTotal() == null : this.getPayerTotal().equals(other.getPayerTotal()))
            && (this.getCurrency() == null ? other.getCurrency() == null : this.getCurrency().equals(other.getCurrency()))
            && (this.getPayerCurrency() == null ? other.getPayerCurrency() == null : this.getPayerCurrency().equals(other.getPayerCurrency()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getTotal() == null ? other.getTotal() == null : this.getTotal().equals(other.getTotal()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderNo() == null) ? 0 : getOrderNo().hashCode());
        result = prime * result + ((getTransactionId() == null) ? 0 : getTransactionId().hashCode());
        result = prime * result + ((getTradeType() == null) ? 0 : getTradeType().hashCode());
        result = prime * result + ((getTradeState() == null) ? 0 : getTradeState().hashCode());
        result = prime * result + ((getTradeStateDesc() == null) ? 0 : getTradeStateDesc().hashCode());
        result = prime * result + ((getSuccessTime() == null) ? 0 : getSuccessTime().hashCode());
        result = prime * result + ((getOpenid() == null) ? 0 : getOpenid().hashCode());
        result = prime * result + ((getPayerTotal() == null) ? 0 : getPayerTotal().hashCode());
        result = prime * result + ((getCurrency() == null) ? 0 : getCurrency().hashCode());
        result = prime * result + ((getPayerCurrency() == null) ? 0 : getPayerCurrency().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getTotal() == null) ? 0 : getTotal().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", transactionId=").append(transactionId);
        sb.append(", tradeType=").append(tradeType);
        sb.append(", tradeState=").append(tradeState);
        sb.append(", tradeStateDesc=").append(tradeStateDesc);
        sb.append(", successTime=").append(successTime);
        sb.append(", openid=").append(openid);
        sb.append(", payerTotal=").append(payerTotal);
        sb.append(", currency=").append(currency);
        sb.append(", payerCurrency=").append(payerCurrency);
        sb.append(", content=").append(content);
        sb.append(", total=").append(total);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}