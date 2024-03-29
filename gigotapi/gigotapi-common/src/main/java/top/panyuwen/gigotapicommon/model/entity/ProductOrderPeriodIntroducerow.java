package top.panyuwen.gigotapicommon.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName product_order_period_introducerow
 */
@TableName(value ="product_order_period_introducerow")
@Data
public class ProductOrderPeriodIntroducerow implements Serializable {
    /**
     * 
     */
    @TableField(value = "day_total")
    private Long dayTotal;

    /**
     * 
     */
    @TableField(value = "day_over_day")
    private String dayOverDay;

    /**
     * 
     */
    @TableField(value = "week_over_week")
    private String weekOverWeek;

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
        ProductOrderPeriodIntroducerow other = (ProductOrderPeriodIntroducerow) that;
        return (this.getDayTotal() == null ? other.getDayTotal() == null : this.getDayTotal().equals(other.getDayTotal()))
            && (this.getDayOverDay() == null ? other.getDayOverDay() == null : this.getDayOverDay().equals(other.getDayOverDay()))
            && (this.getWeekOverWeek() == null ? other.getWeekOverWeek() == null : this.getWeekOverWeek().equals(other.getWeekOverWeek()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDayTotal() == null) ? 0 : getDayTotal().hashCode());
        result = prime * result + ((getDayOverDay() == null) ? 0 : getDayOverDay().hashCode());
        result = prime * result + ((getWeekOverWeek() == null) ? 0 : getWeekOverWeek().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", dayTotal=").append(dayTotal);
        sb.append(", dayOverDay=").append(dayOverDay);
        sb.append(", weekOverWeek=").append(weekOverWeek);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}