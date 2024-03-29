package top.panyuwen.gigotapibackend.model.dto.productorder;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author PYW
 */
@Data
public class ProductOrderUpdateStatusRequest implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private Long id;

    private String status;
}
