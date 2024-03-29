package top.panyuwen.gigotapibackend.service.impl.inner;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import top.panyuwen.gigotapibackend.service.UserInterfaceInfoService;
import top.panyuwen.gigotapicommon.service.InnerUserInterfaceInfoService;

/**
 * @author PYW
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Autowired
    private UserInterfaceInfoService userInterfaceInfoService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean invoke(Long interfaceInfoId, Long userId) {
        return userInterfaceInfoService.invoke(interfaceInfoId, userId);
    }
}
