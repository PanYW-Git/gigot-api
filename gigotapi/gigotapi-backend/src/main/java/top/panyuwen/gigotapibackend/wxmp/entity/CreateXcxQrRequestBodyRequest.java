package top.panyuwen.gigotapibackend.wxmp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;


/**
 * @author PYW
 */
@Data
public class CreateXcxQrRequestBodyRequest implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private String scene;

    private String page;

    private boolean check_path;
}
