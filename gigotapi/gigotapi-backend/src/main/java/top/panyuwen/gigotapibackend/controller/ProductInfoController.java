package top.panyuwen.gigotapibackend.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.panyuwen.gigotapibackend.annotation.AuthCheck;
import top.panyuwen.gigotapibackend.common.BaseResponse;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.common.ResultUtils;
import top.panyuwen.gigotapibackend.common.request.DeleteListRequest;
import top.panyuwen.gigotapibackend.common.request.DeleteRequest;
import top.panyuwen.gigotapibackend.constant.UserConstant;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.exception.ThrowUtils;

import top.panyuwen.gigotapibackend.model.dto.productinfo.ProductInfoAddRequest;
import top.panyuwen.gigotapibackend.model.dto.productinfo.ProductInfoEditRequest;
import top.panyuwen.gigotapibackend.model.dto.productinfo.ProductInfoQueryRequest;
import top.panyuwen.gigotapibackend.model.dto.productinfo.ProductInfoUpdateRequest;
import top.panyuwen.gigotapibackend.service.ProductInfoService;
import top.panyuwen.gigotapibackend.service.UserService;
import top.panyuwen.gigotapibackend.utils.redis.CacheClient;
import top.panyuwen.gigotapiclientsdk.client.GigotApiClient;
import top.panyuwen.gigotapicommon.model.entity.ProductInfo;
import top.panyuwen.gigotapicommon.model.entity.User;
import top.panyuwen.gigotapicommon.model.vo.ProductInfoVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/productInfo")
@Slf4j
public class ProductInfoController {

    @Resource
    private ProductInfoService productInfoService;

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
     * @param productInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addProductInfo(@RequestBody ProductInfoAddRequest productInfoAddRequest, HttpServletRequest request) {
        if (productInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductInfo productInfo = new ProductInfo();
        BeanUtils.copyProperties(productInfoAddRequest, productInfo);
        productInfoService.validProductInfo(productInfo, true);
        User loginUser = userService.getLoginUser(request);
        productInfo.setUserId(loginUser.getId());
        boolean result = productInfoService.save(productInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newProductInfoId = productInfo.getId();
        // 存入缓存
        cacheClient.set(CACHE_PRODUCTINFO_KEY + newProductInfoId, productInfo, CACHE_PRODUCTINFO_TTL, TimeUnit.MINUTES);
        stringRedisTemplate.opsForZSet().add(CACHE_PRODUCTINFO_ALL_KEY, JSON.toJSONString(productInfo), CACHE_PRODUCTINFO_ALL_TTL);
        return ResultUtils.success(newProductInfoId);
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
    public BaseResponse<Boolean> deleteByIdProductInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        ProductInfo oldProductInfo = productInfoService.getById(id);
        ThrowUtils.throwIf(oldProductInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldProductInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = productInfoService.removeById(id);
        productInfoService.deleteRedisCache(id);
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
    public BaseResponse<Boolean> deleteByIdsProductInfo(@RequestBody DeleteListRequest deleteListRequest, HttpServletRequest request) {
        if (deleteListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        List<Long> ids = deleteListRequest.getIds();
        // 判断是否存在
        List<ProductInfo> productInfos = productInfoService.listByIds(ids);
        if (productInfos.size() != ids.size()) {
            List<Long> idsInDB = productInfos.stream().map(ProductInfo::getId).collect(Collectors.toList());
            List<Long> differentIds = CollUtil.disjunction(ids, idsInDB).stream().collect(Collectors.toList());
            log.error("differentIds:{}", differentIds);
            ThrowUtils.throwIf(differentIds.size() > 0, ErrorCode.NOT_FOUND_ERROR, "未找到删除数据：differentIds：" + differentIds);
        }
        // 校验删除人是本人或者管理员
        List<Long> createUserIds = productInfos.stream().map(ProductInfo::getUserId).distinct().collect(Collectors.toList());
        boolean isNotSelfAndAdmin = (createUserIds.size() > 1 || !createUserIds.get(0).equals(user.getId())) && !userService.isAdmin(request);
        if (isNotSelfAndAdmin) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "只允许管理员或者本人删除");
        }
        boolean b = productInfoService.removeByIds(ids);
        List<String> idKeyList = productInfos.stream().map(item -> CACHE_USER_KEY + item.getId()).collect(Collectors.toList());
        Long deleteCount = stringRedisTemplate.delete(idKeyList);
        stringRedisTemplate.delete(CACHE_PRODUCTINFO_ALL_KEY);
        return ResultUtils.success(true);
    }

    /**
     * 更新（仅管理员）
     *
     * @param productInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateProductInfo(@RequestBody ProductInfoUpdateRequest productInfoUpdateRequest) {
        if (productInfoUpdateRequest == null || productInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductInfo productInfo = new ProductInfo();
        BeanUtils.copyProperties(productInfoUpdateRequest, productInfo);
        // 参数校验
        productInfoService.validProductInfo(productInfo, false);
        long id = productInfoUpdateRequest.getId();
        // 判断是否存在
        ProductInfo oldProductInfo = productInfoService.getById(id);
        ThrowUtils.throwIf(oldProductInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = productInfoService.updateById(productInfo);
        productInfoService.deleteRedisCache(id);
        return ResultUtils.success(result);
    }


    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<ProductInfoVO> getProductInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductInfo productInfo = productInfoService.getProductInfoById(id);
        if (productInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(productInfoService.getProductInfoVO(productInfo, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param productInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ProductInfoVO>> listProductInfoVOByPage(@RequestBody ProductInfoQueryRequest productInfoQueryRequest,
                                                                         HttpServletRequest request) {
        Page<ProductInfo> productInfoPage = productInfoService.listProductInfoByPage(productInfoQueryRequest);
        return ResultUtils.success(productInfoService.getProductInfoVOPage(productInfoPage));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param productInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<ProductInfoVO>> listMyProductInfoVOByPage(@RequestBody ProductInfoQueryRequest productInfoQueryRequest,
                                                                           HttpServletRequest request) {
        if (productInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        productInfoQueryRequest.setUserId(loginUser.getId());
        long current = productInfoQueryRequest.getCurrent();
        long size = productInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<ProductInfo> productInfoPage = productInfoService.page(new Page<>(current, size),
                productInfoService.getQueryWrapper(productInfoQueryRequest));
        return ResultUtils.success(productInfoService.getProductInfoVOPage(productInfoPage));
    }

    // endregion

//    /**
//     * 分页搜索（从 ES 查询，封装类）
//     *
//     * @param productInfoQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/search/page/vo")
//    public BaseResponse<Page<ProductInfoVO>> searchProductInfoVOByPage(@RequestBody ProductInfoQueryRequest productInfoQueryRequest,
//            HttpServletRequest request) {
//        long size = productInfoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<ProductInfo> productInfoPage = productInfoService.searchFromEs(productInfoQueryRequest);
//        return ResultUtils.success(productInfoService.getProductInfoVOPage(productInfoPage, request));
//    }

    /**
     * 编辑（用户）
     *
     * @param productInfoEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editProductInfo(@RequestBody ProductInfoEditRequest productInfoEditRequest, HttpServletRequest request) {
        if (productInfoEditRequest == null || productInfoEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductInfo productInfo = new ProductInfo();
        BeanUtils.copyProperties(productInfoEditRequest, productInfo);
        // 参数校验
        productInfoService.validProductInfo(productInfo, false);
        User loginUser = userService.getLoginUser(request);
        long id = productInfoEditRequest.getId();
        // 判断是否存在
        ProductInfo oldProductInfo = productInfoService.getById(id);
        ThrowUtils.throwIf(oldProductInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldProductInfo.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = productInfoService.updateById(productInfo);
        productInfoService.deleteRedisCache(id);
        return ResultUtils.success(result);
    }

}
