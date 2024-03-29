package top.panyuwen.gigotapicommon.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginXcxQrVO implements Serializable {

    private String qrName;

    private String scene;

    private static final long serialVersionUID = 1L;
}
