package top.panyuwen.gigotapibackend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.config.WxPayConfig;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.service.PayService;
import top.panyuwen.gigotapibackend.service.PaymentTransactionLogService;
import top.panyuwen.gigotapibackend.service.ProductOrderService;
import top.panyuwen.gigotapibackend.service.UserService;
import top.panyuwen.gigotapibackend.utils.WxPayUtil;
import top.panyuwen.gigotapicommon.model.entity.ProductOrder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static top.panyuwen.gigotapibackend.constant.PayConstant.EXPIRATION_TIME;
import static top.panyuwen.gigotapibackend.constant.RedisConstants.CACHE_PRODUCTORDER_STATUS_KEY;

/**
 * @author PYW
 */
@Service
@Slf4j
@Qualifier("WX")
public class WxPayServiceImpl implements PayService {


    private WxPayConfig wxConfig;

    @Autowired
    private WxPayUtil wxPayUtil;

    @Autowired
    private PaymentTransactionLogService paymentTransactionLogService;

    @Autowired
    @Lazy
    private ProductOrderService productOrderService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    public WxPayServiceImpl(WxPayConfig wxPayConfig){
        this.wxConfig = wxPayConfig;
    }



    /**
     * 微信支付
     * @param outTradeNo
     * @param description
     * @param Amount
     * @return
     */
    @Override
    public String pay(String outTradeNo, String description, Integer Amount) {
        return wxPayUtil.generateWeChatPayQRCodeUrl(outTradeNo, description, Amount, LocalDateTime.now().plusMinutes(EXPIRATION_TIME));
    }

    @Transactional
    @Override
    public String doPaymentNotify(String notifyData, HttpServletRequest request) {
        log.info("【微信支付回调通知处理】:{}", notifyData);
        // 初始化 NotificationParser
        // 以支付通知回调为例，验签、解密并转换成 Transaction
        if(StrUtil.isBlank(notifyData)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"微信支付回调notifyData返回为空");
        }
        RequestParam requestParam = WxPayUtil.getRequestParam(notifyData, request);
        NotificationParser parser = new NotificationParser(wxConfig.getNotificationConfig());
        Transaction transaction = parser.parse(requestParam, Transaction.class);
        log.info("【微信支付回调通知解密结果】:{}", transaction);
        String outTradeNo = transaction.getOutTradeNo();
        Transaction.TradeStateEnum tradeState = transaction.getTradeState();

        if(Transaction.TradeStateEnum.SUCCESS.equals(tradeState)){
            // 获取订单信息
            ProductOrder productOrder = productOrderService.getProductOrderByOutTradeNo(outTradeNo);
            // 处理重复通知
            if (Transaction.TradeStateEnum.SUCCESS.equals(productOrder.getStatus())) {
                // 删除缓存支付状态
                stringRedisTemplate.delete(CACHE_PRODUCTORDER_STATUS_KEY + outTradeNo);
                // 返回微信成功通知，让他不要再发了
                return Transaction.TradeStateEnum.SUCCESS.toString();
            }
            // 存储返回结果记录日志到数据库
            boolean createpaymentTransactionLogFlag = paymentTransactionLogService.create(transaction);
            if(!createpaymentTransactionLogFlag){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"微信支付回调通知日志存储失败");
            }
            // 修改缓存订单状态
            boolean updateStatusFlag = productOrderService.updateStatusByOutTradeNo(outTradeNo, Transaction.TradeStateEnum.SUCCESS.toString());
            // 修改用户金币
            boolean addGoldCoinFlag = userService.adjustmentGoldCoin(productOrder.getUserId(), productOrder.getAddGoldCoin());

            // 判断是否成功
            if(updateStatusFlag && addGoldCoinFlag){
                // 支付成功发送邮件
                productOrderService.sendPaidEmail(productOrder);
                // 更新缓存中的状态
                stringRedisTemplate.opsForValue().set(CACHE_PRODUCTORDER_STATUS_KEY + outTradeNo,"true");
                // 更新用户缓存
                userService.updateUserCache(productOrder.getUserId());
                // 返回成功
                return Transaction.TradeStateEnum.SUCCESS.toString();
            }
        }
        if (Transaction.TradeStateEnum.PAYERROR.equals(tradeState)) {
            log.error("【微信支付失败】" + transaction);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "微信支付失败");
        }
        if (Transaction.TradeStateEnum.USERPAYING.equals(tradeState)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "微信支付中....");
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR, "支付失败");
    }
}
