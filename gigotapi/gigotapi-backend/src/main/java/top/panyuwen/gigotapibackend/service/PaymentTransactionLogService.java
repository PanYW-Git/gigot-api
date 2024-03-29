package top.panyuwen.gigotapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction;
import top.panyuwen.gigotapicommon.model.entity.PaymentTransactionLog;

/**
* @author PYW
* @description 针对表【payment_transaction_log(付款信息)】的数据库操作Service
* @createDate 2024-02-29 17:06:18
*/
public interface PaymentTransactionLogService extends IService<PaymentTransactionLog> {

    boolean create(Transaction transaction);
}
