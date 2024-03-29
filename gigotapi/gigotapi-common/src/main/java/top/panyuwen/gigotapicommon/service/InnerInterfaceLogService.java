package top.panyuwen.gigotapicommon.service;

import top.panyuwen.gigotapicommon.model.entity.InterfaceLog;

public interface InnerInterfaceLogService {

    /**
     * 存储日志
     */
    boolean save(InterfaceLog interfaceLog);

}
