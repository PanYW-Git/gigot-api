package top.panyuwen.gigotapibackend.wxmp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class WxMpLoginResponse implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private String openid;

    private String session_key;

    private int errcode;

    private String errmsg;

    private String rid;

}
