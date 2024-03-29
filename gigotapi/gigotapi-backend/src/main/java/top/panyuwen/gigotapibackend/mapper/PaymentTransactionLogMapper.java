package top.panyuwen.gigotapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.panyuwen.gigotapicommon.model.entity.PaymentTransactionLog;

/**
* @author PYW
* @description 针对表【payment_transaction_log(付款信息)】的数据库操作Mapper
* @createDate 2024-02-29 17:06:18
* @Entity generator.domain.PaymentTransactionLog
*/
@Mapper
public interface PaymentTransactionLogMapper extends BaseMapper<PaymentTransactionLog> {

}




