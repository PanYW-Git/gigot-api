package top.panyuwen.gigotapibackend.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.panyuwen.gigotapibackend.annotation.AuthCheck;
import top.panyuwen.gigotapibackend.common.*;
import top.panyuwen.gigotapibackend.common.request.DeleteListRequest;
import top.panyuwen.gigotapibackend.common.request.DeleteRequest;
import top.panyuwen.gigotapibackend.common.request.IdRequest;
import top.panyuwen.gigotapibackend.constant.UserConstant;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.exception.ThrowUtils;
import top.panyuwen.gigotapibackend.model.dto.interfaceinfo.*;
import top.panyuwen.gigotapibackend.model.enums.InterfaceInfoStatusEnum;
import top.panyuwen.gigotapibackend.service.InterfaceInfoService;
import top.panyuwen.gigotapibackend.service.UserService;
import top.panyuwen.gigotapibackend.utils.redis.CacheClient;
import top.panyuwen.gigotapiclientsdk.client.GigotApiClient;
import top.panyuwen.gigotapiclientsdk.exception.GigotApiException;
import top.panyuwen.gigotapiclientsdk.model.BaseRequest;
import top.panyuwen.gigotapicommon.model.entity.InterfaceInfo;
import top.panyuwen.gigotapicommon.model.entity.User;
import top.panyuwen.gigotapicommon.model.vo.InterfaceInfoVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static top.panyuwen.gigotapibackend.constant.RedisConstants.*;

/**
 * 接口管理
 *
 * @author PYW
 * @from www.panyuwen.top
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Autowired
    private GigotApiClient gigotApiClient;

    @Autowired
    private CacheClient cacheClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newInterfaceInfoId = interfaceInfo.getId();
        // 存入缓存
        cacheClient.set(CACHE_INTERFACEINFO_KEY + newInterfaceInfoId, interfaceInfo, CACHE_INTERFACEINFO_TTL, TimeUnit.MINUTES);
        stringRedisTemplate.opsForZSet().add(CACHE_INTERFACEINFO_ALL_KEY, JSON.toJSONString(interfaceInfo), CACHE_INTERFACEINFO_ALL_TTL);
        stringRedisTemplate.expire(CACHE_INTERFACEINFO_ALL_KEY, CACHE_INTERFACEINFO_ALL_TTL, TimeUnit.MINUTES);
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/deleteById")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteByIdInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        interfaceInfoService.deleteRedisCache(id);
        return ResultUtils.success(b);
    }

    /**
     * 批量删除
     *
     * @param deleteListRequest
     * @param request
     * @return
     */
    @PostMapping("/deleteByIds")
    public BaseResponse<Boolean> deleteByIdsInterfaceInfo(@RequestBody DeleteListRequest deleteListRequest, HttpServletRequest request) {
        if (deleteListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        List<Long> ids = deleteListRequest.getIds();
        // 判断是否存在
        List<InterfaceInfo> interfaceInfos = interfaceInfoService.listByIds(ids);
        if (interfaceInfos.size() != ids.size()) {
            List<Long> idsInDB = interfaceInfos.stream().map(InterfaceInfo::getId).collect(Collectors.toList());
            List<Long> differentIds = CollUtil.disjunction(ids, idsInDB).stream().collect(Collectors.toList());
            log.error("differentIds:{}", differentIds);
            ThrowUtils.throwIf(differentIds.size() > 0, ErrorCode.NOT_FOUND_ERROR, "未找到删除数据：differentIds：" + differentIds);
        }
        // 校验删除人是本人或者管理员
        List<Long> createUserIds = interfaceInfos.stream().map(InterfaceInfo::getUserId).distinct().collect(Collectors.toList());
        boolean isNotSelfAndAdmin = (createUserIds.size() > 1 || !createUserIds.get(0).equals(user.getId())) && !userService.isAdmin(request);
        if (isNotSelfAndAdmin) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "只允许管理员或者本人删除");
        }
        boolean b = interfaceInfoService.removeByIds(ids);
        List<String> idKeyList = interfaceInfos.stream().map(item -> CACHE_USER_KEY + item.getId()).collect(Collectors.toList());
        Long deleteCount = stringRedisTemplate.delete(idKeyList);
        stringRedisTemplate.delete(CACHE_INTERFACEINFO_ALL_KEY);
        return ResultUtils.success(true);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        interfaceInfoService.deleteRedisCache(id);
        return ResultUtils.success(result);
    }

    /**
     * 发布（仅管理员）
     *
     * @param idRequest id
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest) throws GigotApiException {
        // 参数校验
        Long id = idRequest.getId();
        if (idRequest == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验接口是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 校验接口是否可以调用
        top.panyuwen.gigotapiclientsdk.model.entity.User user = new top.panyuwen.gigotapiclientsdk.model.entity.User();
        user.setName("羊腿");
        // todo 这个地方传的是固定值，后续需要改为从接口信息中获取
        URL url = null;
        try {
            url = new URL(oldInterfaceInfo.getUrl());
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无效的地址转换");
        }
        String path = url.getPath();
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setPath(path);
        baseRequest.setMethod(oldInterfaceInfo.getMethod());
        baseRequest.setRequestParams(null);
        Object clientResult = gigotApiClient.parseAddressAndCallInterface(baseRequest);
        if (clientResult == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口验证失败");
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        interfaceInfoService.deleteRedisCache(id);
        return ResultUtils.success(result);
    }

    /**
     * 下线（仅管理员）
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        // 参数校验
        Long id = idRequest.getId();
        if (idRequest == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验接口是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        interfaceInfoService.deleteRedisCache(id);
        return ResultUtils.success(result);
    }

    /**
     * 调用
     *
     * @param interfaceInfoInvokeRequest
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
        // 参数校验
        Long id = interfaceInfoInvokeRequest.getId();
        if (interfaceInfoInvokeRequest == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验接口是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getInterfaceInfoById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR, "接口不存在");

        // 校验接口是否上线
        if(!oldInterfaceInfo.getStatus().equals(InterfaceInfoStatusEnum.ONLINE.getValue())){
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "接口未上线");
        }

        // 获取api密钥
        User userVoucher = userService.getVoucher(request);
        String secretId = userVoucher.getSecretId();
        String secretKey = userVoucher.getSecretKey();
        log.info("获取当前登录用户API密钥 secretId:{}  secretKey:{}", secretId, secretKey);
        // 创建一个client发送请求
        GigotApiClient client = new GigotApiClient(secretId, secretKey);
        // 调用sdk自动判断调用接口
        // 调用sdk
        URL url = null;
        try {
            url = new URL(oldInterfaceInfo.getUrl());
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无效的地址转换");
        }
        String path = url.getPath();
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setPath(path);
        baseRequest.setMethod(oldInterfaceInfo.getMethod());
        baseRequest.setRequestParams(interfaceInfoInvokeRequest.getRequestParams());
        baseRequest.setUserRequest(request);
        Object result = null;
        try {
            // 调用sdk解析地址方法
            result = client.parseAddressAndCallInterface(baseRequest);
        } catch (GigotApiException e) {
            throw new BusinessException(e.getCode(), e.getMessage());
        }
        if (ObjUtil.isEmpty(request)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请求SDK失败");
        }
        log.info("调用api接口返回结果：" + result);
        // 重构用户缓存
        userService.updateUserCache(userVoucher.getId());
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<InterfaceInfoVO> getInterfaceInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getInterfaceInfoById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVO(interfaceInfo, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
                                                                         HttpServletRequest request) {
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.listInterfaceInfoByPage(interfaceInfoQueryRequest);
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<InterfaceInfoVO>> listMyInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
                                                                           HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        interfaceInfoQueryRequest.setUserId(loginUser.getId());
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage));
    }

    // endregion

//    /**
//     * 分页搜索（从 ES 查询，封装类）
//     *
//     * @param interfaceInfoQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/search/page/vo")
//    public BaseResponse<Page<InterfaceInfoVO>> searchInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
//            HttpServletRequest request) {
//        long size = interfaceInfoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.searchFromEs(interfaceInfoQueryRequest);
//        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage, request));
//    }

    /**
     * 编辑（用户）
     *
     * @param interfaceInfoEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editInterfaceInfo(@RequestBody InterfaceInfoEditRequest interfaceInfoEditRequest, HttpServletRequest request) {
        if (interfaceInfoEditRequest == null || interfaceInfoEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoEditRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User loginUser = userService.getLoginUser(request);
        long id = interfaceInfoEditRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldInterfaceInfo.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        interfaceInfoService.deleteRedisCache(id);
        return ResultUtils.success(result);
    }

}
