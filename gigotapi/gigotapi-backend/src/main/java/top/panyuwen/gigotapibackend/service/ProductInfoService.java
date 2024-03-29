package top.panyuwen.gigotapibackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.panyuwen.gigotapibackend.model.dto.productinfo.ProductInfoQueryRequest;
import top.panyuwen.gigotapicommon.model.entity.ProductInfo;
import top.panyuwen.gigotapicommon.model.enums.ProductTypeEnum;
import top.panyuwen.gigotapicommon.model.vo.ProductInfoVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
* @author PYW
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-12-05 17:11:19
*/
public interface ProductInfoService extends IService<ProductInfo> {

    /**
     * 获取查询包装类
     *
     * @param productInfoQueryRequest
     * @return
     */
    QueryWrapper<ProductInfo> getQueryWrapper(ProductInfoQueryRequest productInfoQueryRequest);

    /**
     * 获取接口信息VO
     *
     * @param productInfo
     * @param request
     * @return
     */
    ProductInfoVO getProductInfoVO(ProductInfo productInfo, HttpServletRequest request);

    /**
     * 获取接口信息
     * @param id
     * @return
     */
    ProductInfo getProductInfoById(Long id);

    /**
     * 验证接口信息
     *
     * @param productInfo
     * @param b
     */
    void validProductInfo(ProductInfo productInfo, boolean b);

    /**
     * 获取接口信息VO分页
     *
     * @param productInfoPage
     */
    Page<ProductInfoVO> getProductInfoVOPage(Page<ProductInfo> productInfoPage);

    /**
     * 分页查询接口信息
     * @param productInfoQueryRequest
     * @return
     */
    Page<ProductInfo> listProductInfoByPage(ProductInfoQueryRequest productInfoQueryRequest);

    /**
     * 清除所有Redis缓存
     * @param id
     */
    void deleteRedisCache(Long id);

    /**
     * 根据商品类型获取List
     * @param productTypeEnum
     * @return
     */
    List<ProductInfo> getProductInfoTypeList(ProductTypeEnum productTypeEnum);
}
