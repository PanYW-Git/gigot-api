package top.panyuwen.gigotapibackend.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import top.panyuwen.gigotapibackend.service.PayService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author PYW
 */
@Service
@Qualifier("ALIPAY")
public class AliPayServiceImpl implements PayService {
    @Override
    public String pay(String outTradeNo, String description, Integer Amount) {
        return null;
    }

    @Override
    public String doPaymentNotify(String notifyData, HttpServletRequest request) {
        return null;
    }
}
