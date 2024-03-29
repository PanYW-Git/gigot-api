package top.panyuwen.gigotapibackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.constant.CommonConstant;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.exception.ThrowUtils;
import top.panyuwen.gigotapibackend.mapper.ProductInfoMapper;
import top.panyuwen.gigotapibackend.model.dto.productinfo.ProductInfoQueryRequest;
import top.panyuwen.gigotapibackend.service.ProductInfoService;
import top.panyuwen.gigotapibackend.service.UserService;
import top.panyuwen.gigotapibackend.utils.SqlUtils;
import top.panyuwen.gigotapibackend.utils.redis.CacheClient;
import top.panyuwen.gigotapicommon.model.entity.ProductInfo;
import top.panyuwen.gigotapicommon.model.enums.ProductTypeEnum;
import top.panyuwen.gigotapicommon.model.vo.ProductInfoVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static top.panyuwen.gigotapibackend.constant.RedisConstants.*;

/**
 * @author PYW
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-12-05 17:11:19
 */
@Service
@Slf4j
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo>
        implements ProductInfoService {

    private final static Gson GSON = new Gson();

    @Resource
    private UserService userService;

    @Autowired
    private CacheClient cacheClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 非空判断
     *
     * @param productInfo
     * @param add
     */
    @Override
    public void validProductInfo(ProductInfo productInfo, boolean add) {
        if (productInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = productInfo.getName();
        String description = productInfo.getDescription();

        Long addGoldCoin = productInfo.getAddGoldCoin();


        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(
                    name),
                    ErrorCode.PARAMS_ERROR,"名称必填！");
        }
        // 有参数则校验
        if (ObjUtil.isNotEmpty(addGoldCoin) && name.length() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加金币数不正确");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param productInfoQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<ProductInfo> getQueryWrapper(ProductInfoQueryRequest productInfoQueryRequest) {
        QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
        if (productInfoQueryRequest == null) {
            return queryWrapper;
        }
        Long id = productInfoQueryRequest.getId();
        String searchText = productInfoQueryRequest.getSearchText();
        String name = productInfoQueryRequest.getName();
        String description = productInfoQueryRequest.getDescription();
        Long total= productInfoQueryRequest.getTotal();
        Long addGoldCoin = productInfoQueryRequest.getAddGoldCoin();
        Integer status = productInfoQueryRequest.getStatus();
        String sortField = productInfoQueryRequest.getSortField();
        String sortOrder = productInfoQueryRequest.getSortOrder();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("name", searchText).or().like("description", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(total), "total", total);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(addGoldCoin), "addGoldCoin", addGoldCoin);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

//    @Override
//    public Page<ProductInfo> searchFromEs(ProductInfoQueryRequest productInfoQueryRequest) {
//        Long id = productInfoQueryRequest.getId();
//        String name = productInfoQueryRequest.getName();
//        String searchText = productInfoQueryRequest.getSearchText();
//        String description = productInfoQueryRequest.getDescription();
//        String url = productInfoQueryRequest.getUrl();
//        String requestHeader = productInfoQueryRequest.getRequestHeader();
//        String responseHeader = productInfoQueryRequest.getResponseHeader();
//        Integer status = productInfoQueryRequest.getStatus();
//        String method = productInfoQueryRequest.getMethod();
//        Long userId = productInfoQueryRequest.getUserId();
//        // es 起始页为 0
//        long current = productInfoQueryRequest.getCurrent();
//        long pageSize = productInfoQueryRequest.getPageSize();
//        String sortField = productInfoQueryRequest.getSortField();
//        String sortOrder = productInfoQueryRequest.getSortOrder();
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        // 过滤
//        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
//        if (id != null) {
//            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
//        }
//        if (notId != null) {
//            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
//        }
//        if (userId != null) {
//            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
//        }
//        // 必须包含所有标签
//        if (CollectionUtils.isNotEmpty(tagList)) {
//            for (String tag : tagList) {
//                boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
//            }
//        }
//        // 包含任何一个标签即可
//        if (CollectionUtils.isNotEmpty(orTagList)) {
//            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
//            for (String tag : orTagList) {
//                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tags", tag));
//            }
//            orTagBoolQueryBuilder.minimumShouldMatch(1);
//            boolQueryBuilder.filter(orTagBoolQueryBuilder);
//        }
//        // 按关键词检索
//        if (StringUtils.isNotBlank(searchText)) {
//            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
//            boolQueryBuilder.should(QueryBuilders.matchQuery("description", searchText));
//            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
//            boolQueryBuilder.minimumShouldMatch(1);
//        }
//        // 按标题检索
//        if (StringUtils.isNotBlank(title)) {
//            boolQueryBuilder.should(QueryBuilders.matchQuery("title", title));
//            boolQueryBuilder.minimumShouldMatch(1);
//        }
//        // 按内容检索
//        if (StringUtils.isNotBlank(content)) {
//            boolQueryBuilder.should(QueryBuilders.matchQuery("content", content));
//            boolQueryBuilder.minimumShouldMatch(1);
//        }
//        // 排序
//        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
//        if (StringUtils.isNotBlank(sortField)) {
//            sortBuilder = SortBuilders.fieldSort(sortField);
//            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
//        }
//        // 分页
//        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
//        // 构造查询
//        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
//                .withPageable(pageRequest).withSorts(sortBuilder).build();
//        SearchHits<ProductInfoEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, ProductInfoEsDTO.class);
//        Page<ProductInfo> page = new Page<>();
//        page.setTotal(searchHits.getTotalHits());
//        List<ProductInfo> resourceList = new ArrayList<>();
//        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
//        if (searchHits.hasSearchHits()) {
//            List<SearchHit<ProductInfoEsDTO>> searchHitList = searchHits.getSearchHits();
//            List<Long> productInfoIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
//                    .collect(Collectors.toList());
//            List<ProductInfo> productInfoList = baseMapper.selectBatchIds(productInfoIdList);
//            if (productInfoList != null) {
//                Map<Long, List<ProductInfo>> idProductInfoMap = productInfoList.stream().collect(Collectors.groupingBy(ProductInfo::getId));
//                productInfoIdList.forEach(productInfoId -> {
//                    if (idProductInfoMap.containsKey(productInfoId)) {
//                        resourceList.add(idProductInfoMap.get(productInfoId).get(0));
//                    } else {
//                        // 从 es 清空 db 已物理删除的数据
//                        String delete = elasticsearchRestTemplate.delete(String.valueOf(productInfoId), ProductInfoEsDTO.class);
//                        log.info("delete productInfo {}", delete);
//                    }
//                });
//            }
//        }
//        page.setRecords(resourceList);
//        return page;
//    }

    @Override
    public ProductInfoVO getProductInfoVO(ProductInfo productInfo, HttpServletRequest request) {
        // 转为vo对象
        ProductInfoVO productInfoVO = ProductInfoVO.objToVo(productInfo);
        return productInfoVO;
    }

    @Override
    public ProductInfo getProductInfoById(Long id) {
        return cacheClient.getWithLogicalExpire(CACHE_PRODUCTINFO_KEY,id,ProductInfo.class,CACHE_PRODUCTINFO_TTL, TimeUnit.MINUTES,this::getById);
    }

    @Override
    public Page<ProductInfoVO> getProductInfoVOPage(Page<ProductInfo> productInfoPage) {
        List<ProductInfo> productInfoList = productInfoPage.getRecords();
        Page<ProductInfoVO> productInfoVOPage = new Page<>(productInfoPage.getCurrent(), productInfoPage.getSize(), productInfoPage.getTotal());
        if (CollectionUtils.isEmpty(productInfoList)) {
            return productInfoVOPage;
        }
        List<ProductInfoVO> productInfoVOList = productInfoList.stream().map(productInfo -> {
            ProductInfoVO productInfoVO = new ProductInfoVO();
            BeanUtil.copyProperties(productInfo,productInfoVO);
            return productInfoVO;
        }).collect(Collectors.toList());
        // 2. 已登录，获取用户点赞、收藏状态
        // 填充信息
        productInfoVOPage.setRecords(productInfoVOList);
        return productInfoVOPage;
    }


    @Override
    public Page<ProductInfo> listProductInfoByPage(ProductInfoQueryRequest productInfoQueryRequest) {
        long current = productInfoQueryRequest.getCurrent();
        long size = productInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 从缓存中获取接口分页信息
        // 计算 offset 和 count
        long offset = (current - 1) * size;
        long count = size;
        // 获取redis中的数据
        Set<String> allProductInfosInRedis = stringRedisTemplate.opsForZSet().reverseRange(CACHE_PRODUCTINFO_ALL_KEY, 0, -1);
        Page<ProductInfo> page = new Page<>(current, size);
        if(ObjUtil.isNotEmpty(allProductInfosInRedis)){
            // 对元素进行分页处理
            List<ProductInfo> pagedInterfaces = allProductInfosInRedis.stream()
                    .skip(offset)
                    .limit(count)
                    .map(this::convertJSONToProductInfo)
                    .collect(Collectors.toList());
            page.setRecords(pagedInterfaces);
            page.setTotal(allProductInfosInRedis.size());
            return page;
        }
        // 未命中
        // 查询数据库

        List<ProductInfo> allProductInfoList = list();
        page.setRecords(allProductInfoList);
        page.setTotal(allProductInfoList.size());
        //存储数据到redis中
        for (ProductInfo productInfo : page.getRecords()) {
            // 将 ProductInfo 转为 JSON 字符串
            String json = convertProductInfoToJSON(productInfo);

            // 添加到 Redis 的 ZSet 中，使用默认的 score（即按照插入顺序）
            stringRedisTemplate.opsForZSet().add(CACHE_PRODUCTINFO_ALL_KEY, json,productInfo.getAddGoldCoin());

        }
        return page;
    }

    private String convertProductInfoToJSON(ProductInfo productInfo) {
        return JSON.toJSONString(productInfo);
    }
    private ProductInfo convertJSONToProductInfo(String productInfoJSON) {
        return JSON.parseObject(productInfoJSON, ProductInfo.class);
    }

    @Override
    public void deleteRedisCache(Long id){
        stringRedisTemplate.delete(CACHE_PRODUCTINFO_ALL_KEY);
        stringRedisTemplate.delete(CACHE_PRODUCTINFO_KEY + id);
    }

    @Override
    public List<ProductInfo> getProductInfoTypeList(ProductTypeEnum productTypeEnum) {
        String productType = productTypeEnum.getValue();
        return list(new QueryWrapper<ProductInfo>().eq("productType", productType));
    }


}




