package top.panyuwen.gigotapibackend.service.impl.inner;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.service.InterfaceLogService;
import top.panyuwen.gigotapicommon.model.entity.InterfaceLog;
import top.panyuwen.gigotapicommon.service.InnerInterfaceLogService;

/**
 * @author PYW
 */
@DubboService
@Slf4j
public class InnerInterfaceLogServiceImpl implements InnerInterfaceLogService {

    @Autowired
    InterfaceLogService interfaceLogService;

    @Override
    public boolean save(InterfaceLog interfaceLog) {
        if(interfaceLog == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "interfaceLog为空");
        }
        interfaceLogService.validInterfaceLog(interfaceLog, true);
        log.info("存储接口调用日志：",interfaceLog);
        return interfaceLogService.save(interfaceLog);
    }
}
