package top.panyuwen.gigotapiclientsdk.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户
 * @author PYW
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
}
