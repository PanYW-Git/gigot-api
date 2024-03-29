package top.panyuwen.gigotapibackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.panyuwen.gigotapibackend.model.dto.productorder.PayCreateOrderRequest;
import top.panyuwen.gigotapibackend.model.dto.productorder.ProductOrderQueryRequest;
import top.panyuwen.gigotapibackend.model.dto.productorder.ProductOrderUpdateStatusRequest;
import top.panyuwen.gigotapicommon.model.entity.ProductOrder;
import top.panyuwen.gigotapicommon.model.enums.PayTypeEnum;
import top.panyuwen.gigotapicommon.model.enums.ProductOrderStatusEnum;
import top.panyuwen.gigotapicommon.model.vo.ProductOrderVO;
import top.panyuwen.gigotapicommon.model.vo.analysis.IntroduceRowVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author PYW
* @description 针对表【product_order(商品订单)】的数据库操作Service
* @createDate 2024-01-10 14:39:44
*/
public interface ProductOrderService extends IService<ProductOrder> {

    /**
     * 获取查询包装类
     *
     * @param productOrderQueryRequest
     * @return
     */
    QueryWrapper<ProductOrder> getQueryWrapper(ProductOrderQueryRequest productOrderQueryRequest);

    /**
     * 获取接口信息VO
     *
     * @param productOrder
     * @return
     */
    ProductOrderVO getProductOrderVO(ProductOrder productOrder);

    /**
     * 获取接口信息
     * @param id
     * @return
     */
    ProductOrder getProductOrderById(Long id);

    /**
     * 验证接口信息
     *
     * @param productOrder
     * @param b
     */
    void validProductOrder(ProductOrder productOrder, boolean b);

    /**
     * 获取接口信息VO分页
     *
     * @param productOrderPage
     */
    Page<ProductOrderVO> getProductOrderVOPage(Page<ProductOrder> productOrderPage);


    /**
     * 分页查询接口信息
     * @param productOrderQueryRequest
     * @return
     */
    Page<ProductOrder> listProductOrderByPage(ProductOrderQueryRequest productOrderQueryRequest);

    /**
     * 清除所有Redis缓存
     * @param id
     */
    void deleteRedisCache(Long id);

    /**
     * 支付创建订单
     * @param payCreateOrderRequest
     * @param userId
     * @return
     */
    ProductOrderVO payCreateProductOrder(PayCreateOrderRequest payCreateOrderRequest, Long userId);

    /**
     * 获取外部订单号
     * @param outTradeNo
     * @return
     */
    ProductOrder getProductOrderByOutTradeNo(String outTradeNo);

    /**
     * 判断支付方式类型
     * @param payType
     * @return
     */
    PayService getProductOrderServiceByPayType(String payType);

    /**
     * 支付回调
     * @param notifyData
     * @param request
     * @return
     */
    String doOrderNotify(String notifyData, HttpServletRequest request);

    /**
     * 通过外部订单号更新订单状态
     * @param outTradeNo
     * @param status
     * @return
     */
    boolean updateStatusByOutTradeNo(String outTradeNo, String status);

    /**
     * 发送支付成功邮件
     * @param productOrder
     */
    void sendPaidEmail(ProductOrder productOrder);

    /**
     * 更新状态（根据id）
     * @param productOrderUpdateStatusRequest
     * @return
     */
    boolean updateStatusById(ProductOrderUpdateStatusRequest productOrderUpdateStatusRequest);

    /**
     * 超时未支付订单处理器
     * @param value
     * @return
     */
    boolean noPayOrderByDurationHandler(PayTypeEnum payTypeEnum);

    /**
     * 获取成功订单数量
     * @return
     */
    Long getCountByStatus(ProductOrderStatusEnum productOrderStatusEnum);

    /**
     * 获取总支付金额
     * @return
     */
    Long getSucessTotalAmount();

    /**
     * 获取同日比，同周比，日销售额
     */
    void getPeriodIntroduceRow(IntroduceRowVO introduceRowVO);
}
