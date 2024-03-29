package top.panyuwen.gigotapibackend.constant;

/**
 * 用户常量
 *
 * @author PYW
 * @from www.panyuwen.top
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    /**
     * 默认密码
     */
    String DEFAULT_PASSWORD = "123456";

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

    /**
     * 账号默认前缀
     */
    String DEFAULT_ACCOUNT_PREFIX = "gigotApi_";

    /**
     * 赠送金币
     */
    Long DEFAULT_BALANCE_GOLDCOIN = 30L;

    /**
     * 默认个人简介
     */
    String DEFAULT_USER_PROFILE = "这个人很神秘，什么都没有写";

    /**
     * 默认头像地址
     */
    String DEFAULT_USER_AVATAR = "/api/file/downloadAvatar?name=defalut-avatar.jpg";

    /**
     * 微信小程序二维码生成地址
     */
    String DEFAULT_XCX_QRCODE_PATH = "src/main/resources/static/img/WxXcxQrCode/";

    /**
     * 名称默认前缀
     */
    String DEFAULT_NAME_PREFIX = "gigotApi_";

    // endregion
}
