package top.panyuwen.gigotapibackend.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户更新个人信息请求
 *
 * @author PYW
 * @from www.panyuwen.top
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    private static final long serialVersionUID = 1L;
}