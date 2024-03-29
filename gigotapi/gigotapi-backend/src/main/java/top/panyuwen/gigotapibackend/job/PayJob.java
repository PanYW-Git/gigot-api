package top.panyuwen.gigotapibackend.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.panyuwen.gigotapibackend.service.ProductOrderService;
import top.panyuwen.gigotapibackend.utils.RedissonLockUtil;
import top.panyuwen.gigotapicommon.model.entity.ProductOrder;
import top.panyuwen.gigotapicommon.model.enums.PayTypeEnum;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class PayJob {

    @Resource
    private ProductOrderService productOrderService;
    @Resource
    private RedissonLockUtil redissonLockUtil;

    /**
     * 微信订单确认
     * 每25s查询一次未支付过期的订单
     */
    @Scheduled(cron = "0/25 * * * * ?")
    public void wxOrderConfirm() {
        redissonLockUtil.redissonDistributedLocks("wxOrderConfirm", () -> {
            productOrderService.noPayOrderByDurationHandler(PayTypeEnum.WX);
        });
    }
}
