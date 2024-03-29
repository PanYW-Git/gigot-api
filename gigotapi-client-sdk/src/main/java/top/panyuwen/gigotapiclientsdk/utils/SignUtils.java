package top.panyuwen.gigotapiclientsdk.utils;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 签名工具类
 *
 * @author PYW
 */
public class SignUtils {

    /**
     * 获取签名值
     *
     * @param body 请求体
     * @param secretKey 秘钥
     * @return 签名值
     */
    public static String getSign(String body, String secretKey) {
        return DigestUtil.sha256Hex(body + "." + secretKey);
    }

}
