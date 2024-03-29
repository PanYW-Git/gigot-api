package top.panyuwen.gigotapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.panyuwen.gigotapicommon.model.entity.ProductOrder;

/**
* @author PYW
* @description 针对表【product_order(商品订单)】的数据库操作Mapper
* @createDate 2024-01-10 14:39:44
* @Entity generator.domain.ProductOrder
*/
@Mapper
public interface ProductOrderMapper extends BaseMapper<ProductOrder> {

    /**
     * 获取成功支付总额
     * @return
     */
    Long getSucessTotalAmount();
}




