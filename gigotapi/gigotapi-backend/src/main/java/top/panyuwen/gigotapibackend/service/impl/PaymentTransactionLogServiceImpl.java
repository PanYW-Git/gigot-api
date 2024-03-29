package top.panyuwen.gigotapibackend.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.pay.java.service.partnerpayments.model.TransactionAmount;
import com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction;
import org.springframework.stereotype.Service;
import top.panyuwen.gigotapibackend.mapper.PaymentTransactionLogMapper;
import top.panyuwen.gigotapibackend.service.PaymentTransactionLogService;
import top.panyuwen.gigotapicommon.model.entity.PaymentTransactionLog;

/**
* @author PYW
* @description 针对表【payment_transaction_log(付款信息)】的数据库操作Service实现
* @createDate 2024-02-29 17:06:18
*/
@Service
public class PaymentTransactionLogServiceImpl extends ServiceImpl<PaymentTransactionLogMapper, PaymentTransactionLog>
    implements PaymentTransactionLogService {

    @Override
    public boolean create(Transaction transaction) {
        TransactionAmount amount = transaction.getAmount();
        PaymentTransactionLog paymentTransactionLog = new PaymentTransactionLog();
        paymentTransactionLog.setOrderNo(transaction.getOutTradeNo());
        paymentTransactionLog.setTransactionId(transaction.getTransactionId());
        paymentTransactionLog.setTradeType(transaction.getTradeType().toString());
        paymentTransactionLog.setTradeState(transaction.getTradeState().toString());
        paymentTransactionLog.setTradeStateDesc(transaction.getTradeStateDesc());
        paymentTransactionLog.setSuccessTime(transaction.getSuccessTime());
        paymentTransactionLog.setOpenid(transaction.getPayer().getSpOpenid());
        paymentTransactionLog.setPayerTotal(amount.getPayerTotal().longValue());
        paymentTransactionLog.setCurrency(amount.getCurrency());
        paymentTransactionLog.setPayerCurrency(amount.getPayerCurrency());
        paymentTransactionLog.setContent(JSON.toJSONString(transaction));
        paymentTransactionLog.setTotal(amount.getTotal().longValue());
        return this.save(paymentTransactionLog);
    }
}




