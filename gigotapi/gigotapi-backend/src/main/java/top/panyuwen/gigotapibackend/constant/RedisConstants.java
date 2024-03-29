package top.panyuwen.gigotapibackend.constant;


import cn.hutool.core.util.RandomUtil;

/**
 * Redis常量
 *
 * @author PYW
 */
public class RedisConstants {


    /**
     * 登录验证码
     */
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 10L;

    /**
     * 用户登录时间
     */
    public static final String LOGIN_TOKEN_KEY = "login:token:";
    public static final Long LOGIN_TOKEN_TTL = 120L;

    /**
     * 用户登录时间
     */
    public static final String LOGIN_NOTFOUND_KEY = "login:notfound:";
    public static final Long LOGIN_NOTFOUND_TTL = 2L;

    /**
     * 小程序登录Scene
     */
    public static final String LOGIN_XCX_SCENE_KEY = "login:xcx:scene:";
    public static final Long LOGIN_XCX_SCENE_TTL = 10L;

    /**
     * 验证码
     */
    public static final String VERIFICATIONCODE_CACHE_KEY = "verificationcode:cache:";
    public static final Long VERIFICATIONCODE_CACHE_TTL = 10L;

    /**
     * 签到
     */
    public static final String SYSTEM_SIGN_KEY = "system:sign:";
    public static final Long SYSTEM_SIGN_TTL = -1L;


    /**
     * 未找到数据缓存（解决缓存穿透问题）
     */
    public static final Long CACHE_NULL_TTL = 2L;

    /**
     * 互斥锁
     */
    public static final String LOCK_KEY = "lock:";
    public static final Long LOCK_TTL = RandomUtil.randomLong(20,40);

    /**
     * 用户缓存
     */
    public static final String CACHE_USER_KEY = "cache:user:";
    public static final Long CACHE_USER_TTL = RandomUtil.randomLong(20,40);

    /**
     * 接口缓存
     */
    public static final String CACHE_INTERFACEINFO_KEY = "cache:interfaceinfo:";
    public static final Long CACHE_INTERFACEINFO_TTL = RandomUtil.randomLong(20,40);

    /**
     * 所有接口缓存
     */
    public static final String CACHE_INTERFACEINFO_ALL_KEY = "cache:interfaceinfo:all";
    public static final Long CACHE_INTERFACEINFO_ALL_TTL = -1L;

    /**
     * 产品订单缓存
     */
    public static final String CACHE_PRODUCTORDER_KEY = "cache:productorder:";
    public static final Long CACHE_PRODUCTORDER_TTL = RandomUtil.randomLong(20,40);

    /**
     * 产品订单状态
     */
    public static final String CACHE_PRODUCTORDER_STATUS_KEY = "cache:productorder:status:";
    public static final Long CACHE_PRODUCTORDER_STATUS_TTL = RandomUtil.randomLong(20,40);


    /**
     * 所有产品订单缓存
     */
    public static final String CACHE_PRODUCTORDER_MY_ALL_KEY = "cache:productorder:my:all:";
    public static final Long CACHE_PRODUCTORDER_MY_ALL_TTL = RandomUtil.randomLong(20,40);

    /**
     * 产品信息缓存
     */
    public static final String CACHE_PRODUCTINFO_KEY = "cache:productinfo:";
    public static final Long CACHE_PRODUCTINFO_TTL = -1L;

    /**
     * 所有产品信息缓存
     */
    public static final String CACHE_PRODUCTINFO_ALL_KEY = "cache:productinfo:all";
    public static final Long CACHE_PRODUCTINFO_ALL_TTL = RandomUtil.randomLong(20,40);

    /**
     * sdk下载时间缓存
     */
    public static final String CACHE_FILE_SDK_KEY = "cache:file:sdk:";
    public static final Long CACHE_FILE_SDK_TTL = 5L;

    /**
     * 网站访问PV缓存
     */
    public static final String SYSTEM_PV_KEY = "system:pv";

}
