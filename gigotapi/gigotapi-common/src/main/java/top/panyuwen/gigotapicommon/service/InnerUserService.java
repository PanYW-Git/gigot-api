package top.panyuwen.gigotapicommon.service;

import top.panyuwen.gigotapicommon.model.entity.User;

/**
 * 用户服务
 *
 * @author PYW
 * @from www.panyuwen.top
 */
public interface InnerUserService {

    /**
     * 获取数据库中是否已分配给用户密钥（secretId）
     */
    User getInvokeUser(String secretId);
}
