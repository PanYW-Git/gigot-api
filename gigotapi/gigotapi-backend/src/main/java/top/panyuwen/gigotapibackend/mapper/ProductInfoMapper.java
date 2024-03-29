package top.panyuwen.gigotapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.panyuwen.gigotapicommon.model.entity.ProductInfo;

/**
* @author PYW
* @description 针对表【product_info(产品信息)】的数据库操作Mapper
* @createDate 2024-01-25 14:05:11
* @Entity generator.domain.ProductInfo
*/
@Mapper
public interface ProductInfoMapper extends BaseMapper<ProductInfo> {

}




