package top.panyuwen.gigotapibackend.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.exception.ThrowUtils;
import top.panyuwen.gigotapibackend.mapper.InterfaceLogMapper;
import top.panyuwen.gigotapibackend.service.InterfaceLogService;
import top.panyuwen.gigotapicommon.model.entity.InterfaceLog;

/**
* @author PYW
* @description 针对表【interface_log】的数据库操作Service实现
* @createDate 2024-02-22 14:27:42
*/
@Service
public class InterfaceLogServiceImpl extends ServiceImpl<InterfaceLogMapper, InterfaceLog>
    implements InterfaceLogService {

    @Autowired
    private InterfaceLogMapper interfaceLogMapper;

    /**
     * 非空判断
     *
     * @param InterfaceLog
     * @param add
     */
    @Override
    public void validInterfaceLog(InterfaceLog InterfaceLog, boolean add) {
        if (InterfaceLog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long interfaceId = InterfaceLog.getInterfaceId();
        String requestMethod = InterfaceLog.getRequestMethod();
        String requestUrl = InterfaceLog.getRequestUrl();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(
                    requestMethod, requestUrl),
                    ErrorCode.PARAMS_ERROR,"请求方式，请求地址必填！");
            ThrowUtils.throwIf(ObjUtil.isEmpty(
                    interfaceId),
                    ErrorCode.PARAMS_ERROR, "接口信息id必填！");
        }
    }

    @Override
    public Integer getInterfaceInfoAverageCost() {
        return interfaceLogMapper.getInterfaceInfoAverageCost();
    }
}




