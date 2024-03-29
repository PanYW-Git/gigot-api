package top.panyuwen.gigotapibackend.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
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
import top.panyuwen.gigotapibackend.model.dto.productorder.PayCreateOrderRequest;
import top.panyuwen.gigotapibackend.model.dto.productorder.ProductOrderQueryRequest;
import top.panyuwen.gigotapibackend.model.dto.productorder.ProductOrderUpdateStatusRequest;
import top.panyuwen.gigotapibackend.service.PayService;
import top.panyuwen.gigotapicommon.model.enums.ProductOrderStatusEnum;
import top.panyuwen.gigotapibackend.service.ProductOrderService;
import top.panyuwen.gigotapibackend.service.UserService;
import top.panyuwen.gigotapibackend.utils.redis.CacheClient;
import top.panyuwen.gigotapiclientsdk.client.GigotApiClient;
import top.panyuwen.gigotapicommon.model.entity.ProductOrder;
import top.panyuwen.gigotapicommon.model.entity.User;
import top.panyuwen.gigotapicommon.model.vo.ProductOrderVO;

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
@RequestMapping("/productOrder")
@Slf4j
public class ProductOrderController {

    @Resource
    private ProductOrderService productOrderService;

    @Resource
    private UserService userService;

    @Autowired
    private GigotApiClient gigotApiClient;

    @Autowired
    private CacheClient cacheClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private List<PayService> payServices;

    // region 增删改查

    /**
     * 新增订单
     */
    @PostMapping("/pay/create")
    public BaseResponse<ProductOrderVO> payCreateProductOrder(@RequestBody PayCreateOrderRequest payCreateOrderRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(productOrderService.payCreateProductOrder(payCreateOrderRequest,loginUser.getId()));
    }

    /**
     * 查询订单状态
     * @param productOrderQueryRequest
     * @return
     */
    @PostMapping("/get/status")
    public BaseResponse<Boolean> getOrderStatus(@RequestBody ProductOrderQueryRequest productOrderQueryRequest){
        String orderNo = productOrderQueryRequest.getOrderNo();
        if(ObjUtil.isEmpty(orderNo)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String cacheStatus = stringRedisTemplate.opsForValue().get(CACHE_PRODUCTORDER_STATUS_KEY + orderNo);
        if(StrUtil.isBlank(cacheStatus)){
            return ResultUtils.success(false);
        }
        ProductOrder productOrder = productOrderService.getProductOrderByOutTradeNo(orderNo);
        if (ProductOrderStatusEnum.SUCCESS.getValue().equals(productOrder.getStatus())) {
            return ResultUtils.success(true);
        }
        stringRedisTemplate.opsForValue().set(CACHE_PRODUCTORDER_STATUS_KEY + orderNo, Boolean.FALSE.toString(), 5, TimeUnit.MINUTES);
        return ResultUtils.success(false);
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
    public BaseResponse<Boolean> deleteByIdProductOrder(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        ProductOrder oldProductOrder = productOrderService.getById(id);
        ThrowUtils.throwIf(oldProductOrder == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldProductOrder.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = productOrderService.removeById(id);
        productOrderService.deleteRedisCache(id);
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
    public BaseResponse<Boolean> deleteByIdsProductOrder(@RequestBody DeleteListRequest deleteListRequest, HttpServletRequest request) {
        if (deleteListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        List<Long> ids = deleteListRequest.getIds();
        // 判断是否存在
        List<ProductOrder> productOrders = productOrderService.listByIds(ids);
        if (productOrders.size() != ids.size()) {
            List<Long> idsInDB = productOrders.stream().map(ProductOrder::getId).collect(Collectors.toList());
            List<Long> differentIds = CollUtil.disjunction(ids, idsInDB).stream().collect(Collectors.toList());
            log.error("differentIds:{}", differentIds);
            ThrowUtils.throwIf(differentIds.size() > 0, ErrorCode.NOT_FOUND_ERROR, "未找到删除数据：differentIds：" + differentIds);
        }
        // 校验删除人是本人或者管理员
        List<Long> createUserIds = productOrders.stream().map(ProductOrder::getUserId).distinct().collect(Collectors.toList());
        boolean isNotSelfAndAdmin = (createUserIds.size() > 1 || !createUserIds.get(0).equals(user.getId())) && !userService.isAdmin(request);
        if (isNotSelfAndAdmin) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "只允许管理员或者本人删除");
        }
        boolean b = productOrderService.removeByIds(ids);
        List<String> idKeyList = productOrders.stream().map(item -> CACHE_USER_KEY + item.getId()).collect(Collectors.toList());
        Long deleteCount = stringRedisTemplate.delete(idKeyList);
        for (Long id : ids) {
            stringRedisTemplate.delete(CACHE_PRODUCTORDER_KEY + id);
        }
        stringRedisTemplate.delete(CACHE_PRODUCTORDER_MY_ALL_KEY);
        return ResultUtils.success(true);
    }


    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<ProductOrderVO> getProductOrderVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductOrder productOrder = productOrderService.getProductOrderById(id);
        if (productOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(productOrderService.getProductOrderVO(productOrder));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param productOrderQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ProductOrderVO>> listProductOrderVOByPage(@RequestBody ProductOrderQueryRequest productOrderQueryRequest,
                                                                         HttpServletRequest request) {
        Page<ProductOrder> productOrderPage = productOrderService.listProductOrderByPage(productOrderQueryRequest);
        return ResultUtils.success(productOrderService.getProductOrderVOPage(productOrderPage));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param productOrderQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<ProductOrderVO>> listMyProductOrderVOByPage(@RequestBody ProductOrderQueryRequest productOrderQueryRequest,
                                                                           HttpServletRequest request) {
        if (productOrderQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        productOrderQueryRequest.setUserId(loginUser.getId());
        long current = productOrderQueryRequest.getCurrent();
        long size = productOrderQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        log.info("准备分页查询");
        Page<ProductOrder> productOrderPage = productOrderService.page(new Page<>(current, size),
                productOrderService.getQueryWrapper(productOrderQueryRequest));
        log.info("分页查询结果：productOrderPage：{}",productOrderPage.getRecords());
        return ResultUtils.success(productOrderService.getProductOrderVOPage(productOrderPage));
    }

    /**
     * 更新订单状态
     */
    @PostMapping("/update/status")
    public BaseResponse<Boolean> updateStatusById(@RequestBody ProductOrderUpdateStatusRequest productOrderUpdateStatusRequest) {
        return ResultUtils.success(productOrderService.updateStatusById(productOrderUpdateStatusRequest));
    }

    // endregion

//    /**
//     * 分页搜索（从 ES 查询，封装类）
//     *
//     * @param productOrderQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/search/page/vo")
//    public BaseResponse<Page<ProductOrderVO>> searchProductOrderVOByPage(@RequestBody ProductOrderQueryRequest productOrderQueryRequest,
//            HttpServletRequest request) {
//        long size = productOrderQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<ProductOrder> productOrderPage = productOrderService.searchFromEs(productOrderQueryRequest);
//        return ResultUtils.success(productOrderService.getProductOrderVOPage(productOrderPage, request));
//    }


}
