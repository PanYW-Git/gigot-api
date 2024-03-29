package top.panyuwen.gigotapibackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.panyuwen.gigotapibackend.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import top.panyuwen.gigotapicommon.model.entity.InterfaceInfo;
import top.panyuwen.gigotapicommon.model.vo.InterfaceInfoVO;

import javax.servlet.http.HttpServletRequest;


/**
* @author PYW
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-12-05 17:11:19
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 获取查询包装类
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 获取接口信息VO
     *
     * @param interfaceInfo
     * @param request
     * @return
     */
    InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo, HttpServletRequest request);

    /**
     * 获取接口信息
     * @param id
     * @return
     */
    InterfaceInfo getInterfaceInfoById(Long id);

    /**
     * 验证接口信息
     *
     * @param interfaceInfo
     * @param b
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean b);

    /**
     * 获取接口信息VO分页
     *
     * @param interfaceInfoPage
     */
    Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage);

    /**
     * 接口调用次数+1
     * @param id
     * @return
     */
    boolean invoke(Long id,Long UserId);

    /**
     * 分页查询接口信息
     * @param interfaceInfoQueryRequest
     * @return
     */
    Page<InterfaceInfo> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 清除所有Redis缓存
     * @param id
     */
    void deleteRedisCache(Long id);

    /**
     * 获取接口总调用次数
     * @return
     */
    Long getInterfaceInfoTotalInvokesCount();


}
