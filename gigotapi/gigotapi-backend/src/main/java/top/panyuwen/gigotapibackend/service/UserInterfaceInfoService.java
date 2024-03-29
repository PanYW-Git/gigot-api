package top.panyuwen.gigotapibackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.panyuwen.gigotapibackend.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import top.panyuwen.gigotapicommon.model.entity.UserInterfaceInfo;
import top.panyuwen.gigotapicommon.model.vo.UserInterfaceInfoVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author PYW
* @description 针对表【user_interfaceinfo(用户接口调用表)】的数据库操作Service
* @createDate 2023-12-29 10:55:22
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    /**
     * 校验用户接口调用信息
     * @param userInterfaceInfo
     * @param b
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean b);

    /**
     * 获取用户接口调用信息VO
     * @param userInterfaceInfo
     * @param request
     * @return
     */
    UserInterfaceInfoVO getUserInterfaceInfoVO(UserInterfaceInfo userInterfaceInfo, HttpServletRequest request);

    /**
     * 获取查询包装类
     * @param userInterfaceInfoQueryRequest
     * @return
     */
    QueryWrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest);

    /**
     * 获取用户接口调用信息VO分页
     * @param userInterfaceInfoPage
     * @param request
     * @return
     */
    Page<UserInterfaceInfoVO> getUserInterfaceInfoVOPage(Page<UserInterfaceInfo> userInterfaceInfoPage, HttpServletRequest request);

    /**
     * 调用接口后处理（计数，扣除金币）
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invoke(Long interfaceInfoId,Long userId);
}
