package top.panyuwen.gigotapibackend.service.impl.inner;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.service.InterfaceInfoService;
import top.panyuwen.gigotapicommon.model.entity.InterfaceInfo;
import top.panyuwen.gigotapicommon.service.InnerInterfaceInfoService;

/**
 * @author PYW
 */
@DubboService
@Slf4j
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Autowired
    InterfaceInfoService interfaceInfoService;

    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        log.info("Dubbo调用查询getInvokeUser  url:{}  method:{}", url, method);
        if (StrUtil.isBlank(url) || StrUtil.isBlank(method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<InterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterfaceInfo::getUrl, url).eq(InterfaceInfo::getMethod, method);
        return interfaceInfoService.getOne(queryWrapper);
    }
}
