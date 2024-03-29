package top.panyuwen.gigotapibackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.constant.CommonConstant;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.exception.ThrowUtils;
import top.panyuwen.gigotapibackend.mapper.ProductOrderMapper;
import top.panyuwen.gigotapibackend.model.dto.productorder.PayCreateOrderRequest;
import top.panyuwen.gigotapibackend.model.dto.productorder.ProductOrderQueryRequest;
import top.panyuwen.gigotapibackend.model.dto.productorder.ProductOrderUpdateStatusRequest;
import top.panyuwen.gigotapibackend.service.ProductInfoService;
import top.panyuwen.gigotapibackend.service.ProductOrderService;
import top.panyuwen.gigotapibackend.service.UserService;
import top.panyuwen.gigotapibackend.service.PayService;
import top.panyuwen.gigotapibackend.utils.MailUtils;
import top.panyuwen.gigotapibackend.utils.SqlUtils;
import top.panyuwen.gigotapibackend.utils.redis.CacheClient;
import top.panyuwen.gigotapicommon.model.entity.ProductInfo;
import top.panyuwen.gigotapicommon.model.entity.ProductOrder;
import top.panyuwen.gigotapicommon.model.entity.User;
import top.panyuwen.gigotapicommon.model.enums.PayTypeEnum;
import top.panyuwen.gigotapicommon.model.enums.ProductOrderStatusEnum;
import top.panyuwen.gigotapicommon.model.enums.ProductTypeEnum;
import top.panyuwen.gigotapicommon.model.vo.ProductOrderVO;
import top.panyuwen.gigotapicommon.model.vo.analysis.IntroduceRowVO;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static top.panyuwen.gigotapibackend.constant.PayConstant.EXPIRATION_TIME;
import static top.panyuwen.gigotapibackend.constant.RedisConstants.*;

/**
* @author PYW
* @description 针对表【product_order(商品订单)】的数据库操作Service实现
* @createDate 2024-01-10 14:39:44
*/
@Service
@Slf4j
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder>
    implements ProductOrderService {

    private final static Gson GSON = new Gson();

    public static final String PRODUCT_ORDER_PREFIX = "PO-";

    @Resource
    private UserService userService;

    @Resource
    private ProductOrderMapper productOrderMapper;

    @Resource
    private CacheClient cacheClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private ProductInfoService productInfoService;

    @Resource
    private List<PayService> payServices;

    @Autowired
    private MailUtils mailUtils;

    /**
     * 按付费类型获取产品订单服务
     *
     * @param payType 付款类型
     * @return {@link ProductOrderService}
     */
    @Override
    public PayService getProductOrderServiceByPayType(String payType) {
        return payServices.stream()
                .filter(s -> {
                    Qualifier qualifierAnnotation = s.getClass().getAnnotation(Qualifier.class);
                    return qualifierAnnotation != null && qualifierAnnotation.value().equals(payType);
                })
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "暂无该支付方式"));
    }

    @Override
    public String doOrderNotify(String notifyData, HttpServletRequest request) {
        String payType;
        if (notifyData.startsWith("gmt_create=") && notifyData.contains("gmt_create") && notifyData.contains("sign_type") && notifyData.contains("notify_type")) {
            payType = PayTypeEnum.ALIPAY.getValue();
        } else {
            payType = PayTypeEnum.WX.getValue();
        }
        // 获取支付类型对应的serviceImpl
        PayService productOrderServiceByPayType = this.getProductOrderServiceByPayType(payType);

        return productOrderServiceByPayType.doPaymentNotify(notifyData, request);
    }

    @Override
    public boolean updateStatusByOutTradeNo(String outTradeNo, String status) {
        LambdaUpdateWrapper<ProductOrder> updateWrapper = new LambdaUpdateWrapper<ProductOrder>();
        updateWrapper.eq(ProductOrder::getOrderNo, outTradeNo);
        updateWrapper.set(ProductOrder::getStatus, status);
        return this.update(updateWrapper);
    }

    @Override
    public void sendPaidEmail(ProductOrder productOrder) {
        // 非空判断
        ThrowUtils.throwIf(ObjectUtils.isEmpty(productOrder), ErrorCode.PARAMS_ERROR, "订单信息为空！");
        String orderNo = productOrder.getOrderNo();
        String orderName = productOrder.getOrderName();
        Long total = productOrder.getTotal();
        if(StrUtil.isBlank(orderNo) || StrUtil.isBlank(orderName) || ObjUtil.isEmpty(total)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单信息为空！");
        }
        try {
            // 获取收件人邮箱
            User user = userService.getById(productOrder.getUserId());
            // 校验是否绑定邮箱
            String email = user.getEmail();
            if(StrUtil.isBlank(email)){
                // 未绑定邮箱直接返回
                return;
            }
            // 加载html
            String emailContent = mailUtils.loadEmailTemplate("static/email/paidEmail.html");
            // 替换模板变量
            emailContent = mailUtils.populateTemplate(emailContent, orderNo, orderName, String.valueOf(new BigDecimal(productOrder.getTotal()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)));

            // 发送短信
            mailUtils.sendMail(email,emailContent, "【羊腿Api开放平台】支付通知");
        } catch (MessagingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮件发送失败！");
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加载邮件模板失败！");
        }
    }

    @Override
    public boolean updateStatusById(ProductOrderUpdateStatusRequest productOrderUpdateStatusRequest) {
        Long id = productOrderUpdateStatusRequest.getId();
        String status = productOrderUpdateStatusRequest.getStatus();
        // 非空判断
        ThrowUtils.throwIf(ObjectUtils.isEmpty(id), ErrorCode.PARAMS_ERROR, "订单信息为空！");
        ThrowUtils.throwIf(StringUtils.isBlank(status), ErrorCode.PARAMS_ERROR,"订单状态不能为空！");

        // 查询订单是否存在
        ProductOrder productOrderInDB = this.getById(id);
        if (productOrderInDB == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单信息不存在！");
        }

        // 更新订单
        ProductOrder productOrder = BeanUtil.copyProperties(productOrderUpdateStatusRequest, ProductOrder.class);

        // 重构缓存
        this.deleteRedisCache(id);
        return this.updateById(productOrder);
    }

    @Override
    public boolean noPayOrderByDurationHandler(PayTypeEnum payTypeEnum) {
        String payType = payTypeEnum.getValue();
        Instant instant = Instant.now().minus(Duration.ofMinutes(EXPIRATION_TIME));
        LambdaQueryWrapper<ProductOrder> productOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        productOrderLambdaQueryWrapper.eq(ProductOrder::getStatus, ProductOrderStatusEnum.NOTPAY.getValue())
                .eq(StrUtil.isNotBlank(payType), ProductOrder::getPayType, payType);
        // 现在时间是否超过过期时间
        productOrderLambdaQueryWrapper.and(i -> i.le(ProductOrder::getExpirationTime, instant));
        List<ProductOrder> expirationProductOrders = this.list(productOrderLambdaQueryWrapper);
        // 更新状态
        boolean updateFlag = false;
        for (ProductOrder expirationProductOrder : expirationProductOrders) {
            LambdaUpdateWrapper<ProductOrder> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ProductOrder::getStatus, ProductOrderStatusEnum.CLOSED.getValue())
                    .eq(ProductOrder::getId, expirationProductOrder.getId());
            updateFlag = this.update(updateWrapper);
        }
        return updateFlag;
    }

    @Override
    public Long getCountByStatus(ProductOrderStatusEnum productOrderStatusEnum) {
        return this.count(
                new LambdaQueryWrapper<ProductOrder>()
                        .eq(ProductOrder::getStatus, productOrderStatusEnum.getValue()));
    }

    @Override
    public Long getSucessTotalAmount() {
        return productOrderMapper.getSucessTotalAmount();
    }

    @Override
    public void getPeriodIntroduceRow(IntroduceRowVO introduceRowVO) {

    }


    /**
     * 非空判断
     *
     * @param productOrder
     * @param add
     */
    @Override
    public void validProductOrder(ProductOrder productOrder, boolean add) {
        if (productOrder == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = productOrder.getUserId();
        Long productId = productOrder.getProductId();
        Long total = productOrder.getTotal();
        String orderName = productOrder.getOrderName();
        String payType = productOrder.getPayType();
        Long addGoldCoin = productOrder.getAddGoldCoin();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(
                    orderName, payType),
                    ErrorCode.PARAMS_ERROR,"订单名称，支付类型不能为空！");
            if(ObjUtil.isEmpty(userId) || ObjUtil.isEmpty(productId) || ObjUtil.isEmpty(total)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID，商品ID不能为空，请检查");
            }
        }
        // 有参数则校验
        if (ObjUtil.isEmpty(total) && total <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "金额必须为正数");
        }
        if (ObjUtil.isEmpty(addGoldCoin) && total <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加金币必须为正数");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param productOrderQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<ProductOrder> getQueryWrapper(ProductOrderQueryRequest productOrderQueryRequest) {
        QueryWrapper<ProductOrder> queryWrapper = new QueryWrapper<>();
        if (productOrderQueryRequest == null) {
            return queryWrapper;
        }
        Long id = productOrderQueryRequest.getId();
        String searchText = productOrderQueryRequest.getSearchText();
        String orderNo = productOrderQueryRequest.getOrderNo();
        Long userId = productOrderQueryRequest.getUserId();
        Long productId = productOrderQueryRequest.getProductId();
        String orderName = productOrderQueryRequest.getOrderName();
        String status = productOrderQueryRequest.getStatus();
        String payType = productOrderQueryRequest.getPayType();
        String productInfo = productOrderQueryRequest.getProductInfo();
        String sortField = productOrderQueryRequest.getSortField();
        String sortOrder = productOrderQueryRequest.getSortOrder();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("orderName", searchText).or().like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(orderNo), "orderNo", orderNo);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(productId), "productId", productId);
        queryWrapper.like(StringUtils.isNotBlank(orderName), "orderName", orderName);
        queryWrapper.like(StringUtils.isNotBlank(payType), "payType", payType);
        queryWrapper.like(StringUtils.isNotBlank(productInfo), "productInfo", productInfo);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

//    @Override
//    public Page<ProductOrder> searchFromEs(ProductOrderQueryRequest productOrderQueryRequest) {
//        Long id = productOrderQueryRequest.getId();
//        String name = productOrderQueryRequest.getName();
//        String searchText = productOrderQueryRequest.getSearchText();
//        String description = productOrderQueryRequest.getDescription();
//        String url = productOrderQueryRequest.getUrl();
//        String requestHeader = productOrderQueryRequest.getRequestHeader();
//        String responseHeader = productOrderQueryRequest.getResponseHeader();
//        Integer status = productOrderQueryRequest.getStatus();
//        String method = productOrderQueryRequest.getMethod();
//        Long userId = productOrderQueryRequest.getUserId();
//        // es 起始页为 0
//        long current = productOrderQueryRequest.getCurrent();
//        long pageSize = productOrderQueryRequest.getPageSize();
//        String sortField = productOrderQueryRequest.getSortField();
//        String sortOrder = productOrderQueryRequest.getSortOrder();
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
//        SearchHits<ProductOrderEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, ProductOrderEsDTO.class);
//        Page<ProductOrder> page = new Page<>();
//        page.setTotal(searchHits.getTotalHits());
//        List<ProductOrder> resourceList = new ArrayList<>();
//        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
//        if (searchHits.hasSearchHits()) {
//            List<SearchHit<ProductOrderEsDTO>> searchHitList = searchHits.getSearchHits();
//            List<Long> productOrderIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
//                    .collect(Collectors.toList());
//            List<ProductOrder> productOrderList = baseMapper.selectBatchIds(productOrderIdList);
//            if (productOrderList != null) {
//                Map<Long, List<ProductOrder>> idProductOrderMap = productOrderList.stream().collect(Collectors.groupingBy(ProductOrder::getId));
//                productOrderIdList.forEach(productOrderId -> {
//                    if (idProductOrderMap.containsKey(productOrderId)) {
//                        resourceList.add(idProductOrderMap.get(productOrderId).get(0));
//                    } else {
//                        // 从 es 清空 db 已物理删除的数据
//                        String delete = elasticsearchRestTemplate.delete(String.valueOf(productOrderId), ProductOrderEsDTO.class);
//                        log.info("delete productOrder {}", delete);
//                    }
//                });
//            }
//        }
//        page.setRecords(resourceList);
//        return page;
//    }

    @Override
    public ProductOrderVO getProductOrderVO(ProductOrder productOrder) {
        // 转为vo对象
        ProductOrderVO productOrderVO = new ProductOrderVO();
        productOrderVO.setId(productOrder.getId());
        productOrderVO.setOrderNo(productOrder.getOrderNo());
        productOrderVO.setCodeUrl(productOrder.getCodeUrl());
        productOrderVO.setUserId(productOrder.getUserId());
        productOrderVO.setProductId(productOrder.getProductId());
        productOrderVO.setOrderName(productOrder.getOrderName());
        productOrderVO.setTotal(productOrder.getTotal());
        productOrderVO.setStatus(productOrder.getStatus());
        productOrderVO.setPayType(productOrder.getPayType());
        productOrderVO.setProductInfo(JSON.parseObject(productOrder.getProductInfo(), ProductInfo.class));
        productOrderVO.setFormData(productOrder.getFormData());
        productOrderVO.setAddGoldCoin(productOrder.getAddGoldCoin());
        productOrderVO.setExpirationTime(productOrder.getExpirationTime());
        productOrderVO.setCreateTime(productOrder.getCreateTime());
        productOrderVO.setUpdateTime(productOrder.getUpdateTime());


        return productOrderVO;
    }

    @Override
    public ProductOrder getProductOrderById(Long id) {
        return cacheClient.getWithLogicalExpire(CACHE_PRODUCTORDER_KEY,id,ProductOrder.class,CACHE_PRODUCTORDER_TTL, TimeUnit.MINUTES,this::getById);
    }

    @Override
    public Page<ProductOrderVO> getProductOrderVOPage(Page<ProductOrder> productOrderPage) {
        List<ProductOrder> productOrderList = productOrderPage.getRecords();
        Page<ProductOrderVO> productOrderVOPage = new Page<>(productOrderPage.getCurrent(), productOrderPage.getSize(), productOrderPage.getTotal());
        if (CollectionUtils.isEmpty(productOrderList)) {
            return productOrderVOPage;
        }
        List<ProductOrderVO> productOrderVOList = productOrderList.stream().map(productOrder -> {
            ProductOrderVO productOrderVO = this.getProductOrderVO(productOrder);
            return productOrderVO;
        }).collect(Collectors.toList());
        // 2. 已登录，获取用户点赞、收藏状态
        // 填充信息
        productOrderVOPage.setRecords(productOrderVOList);
        return productOrderVOPage;
    }


    @Override
    public Page<ProductOrder> listProductOrderByPage(ProductOrderQueryRequest productOrderQueryRequest) {
        long current = productOrderQueryRequest.getCurrent();
        long size = productOrderQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 从缓存中获取接口分页信息
        // 计算 offset 和 count
        long offset = (current - 1) * size;
        long count = size;
        // 获取redis中的数据
        Set<String> allInterfacesInRedis = stringRedisTemplate.opsForZSet().reverseRange(CACHE_PRODUCTORDER_MY_ALL_KEY, 0, -1);
        Page<ProductOrder> page = new Page<>(current, size);
        if(ObjUtil.isNotEmpty(allInterfacesInRedis)){
            //缓存命中
            log.info("命中");
            // 对元素进行分页处理
            List<ProductOrder> pagedInterfaces = allInterfacesInRedis.stream()
                    .skip(offset)
                    .limit(count)
                    .map(this::convertJSONToProductOrder)
                    .collect(Collectors.toList());
            page.setRecords(pagedInterfaces);
            page.setTotal(allInterfacesInRedis.size());
            return page;
        }
        log.info("未命中");
        // 未命中
        // 查询数据库

        List<ProductOrder> allProductOrderList = list();
        page.setRecords(allProductOrderList);
        page.setTotal(allProductOrderList.size());
        //存储数据到redis中
        for (ProductOrder productOrder : page.getRecords()) {
            // 将 ProductOrder 转为 JSON 字符串
            String json = convertProductOrderToJSON(productOrder);

            // 添加到 Redis 的 ZSet 中，使用默认的 score（即按照插入顺序）
            stringRedisTemplate.opsForZSet().add(CACHE_PRODUCTORDER_MY_ALL_KEY, json, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        }
        return page;
    }

    private String convertProductOrderToJSON(ProductOrder productOrder) {
        return JSON.toJSONString(productOrder);
    }
    private ProductOrder convertJSONToProductOrder(String productOrderJSON) {
        return JSON.parseObject(productOrderJSON, ProductOrder.class);
    }

    @Override
    public void deleteRedisCache(Long id){
        stringRedisTemplate.delete(CACHE_PRODUCTORDER_MY_ALL_KEY);
        stringRedisTemplate.delete(CACHE_PRODUCTORDER_KEY + id);
    }

    @Override
    public ProductOrderVO payCreateProductOrder(PayCreateOrderRequest payCreateOrderRequest, Long userId) {
        String payType = payCreateOrderRequest.getPayType();
        Long productId = payCreateOrderRequest.getProductId();
        if(StringUtils.isBlank(payType)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "payType不能为空");
        }
        if(ObjUtil.isEmpty(productId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "productId不能为空");
        }

        // 查询是否创建过此订单
        LambdaQueryWrapper<ProductOrder> generatedProductOrderQueryWrapper = new LambdaQueryWrapper<>();
        generatedProductOrderQueryWrapper.eq(ProductOrder::getUserId, userId)
                .eq(ProductOrder::getProductId, productId)
                .eq(ProductOrder::getStatus, ProductOrderStatusEnum.NOTPAY.getValue());
        // 已生成订单
        ProductOrder generatedProductOrder = this.getOne(generatedProductOrderQueryWrapper);
        if(ObjUtil.isNotEmpty(generatedProductOrder)){
            return this.getProductOrderVO(generatedProductOrder);
        }

        ProductInfo productInfo = productInfoService.getById(productId);
        String name = productInfo.getName();
        Long total = productInfo.getTotal();
        Long addGoldCoin = productInfo.getAddGoldCoin();
        // 1.健壮性校验
        if(StringUtils.isBlank(name)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "数据库productInfo的name为空，请联系管理员");
        }
        if(ObjUtil.isEmpty(total)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "数据库productInfo的total为空，请联系管理员");
        }
        if(ObjUtil.isEmpty(addGoldCoin)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "数据库productInfo的addGoldCoin为空，请联系管理员");
        }

        // 只允许购买一次体验
        if(ProductTypeEnum.EXPERIENCE.getValue().equals(productInfo.getProductType())){
            List<ProductInfo> productInfoTypeList = productInfoService.getProductInfoTypeList(ProductTypeEnum.EXPERIENCE);
            if(CollectionUtils.isEmpty(productInfoTypeList)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "没有该类型的商品");
            }
            for (ProductInfo info : productInfoTypeList) {
                LambdaQueryWrapper<ProductOrder> qw = new LambdaQueryWrapper<ProductOrder>()
                        .eq(ProductOrder::getProductId, info.getId())
                        .eq(ProductOrder::getUserId, userId);

                List<ProductOrder> list = this.list(qw);
                if(CollectionUtils.isNotEmpty(list)){
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "该类型商品只能购买一次!");
                }
            }
        }

        // 判断订单是否存在
        LambdaQueryWrapper<ProductOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductOrder::getUserId,userId).eq(ProductOrder::getProductId,productId).eq(ProductOrder::getStatus, "1");
        ProductOrder checkProductOrderInDB = getOne(queryWrapper);
        if(ObjectUtil.isNotEmpty(checkProductOrderInDB)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单已存在，请勿重复下单");
        }
        String orderNo = PRODUCT_ORDER_PREFIX + RandomUtil.randomNumbers(20);
        // 调用判断支付类型，得到相应支付类型的payService
        PayService payService = getProductOrderServiceByPayType(payType);
        String QRCode = payService.pay(orderNo, name, total.intValue());

        if(StrUtil.isBlank(QRCode)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "支付二维码生成失败，请联系管理员");
        }
        // 2.创建订单
        ProductOrder productOrder = new ProductOrder();
        // 生成32位纯数字uuid，作为订单号
        long id = IdUtil.getSnowflakeNextId();

        productOrder.setId(IdUtil.getSnowflakeNextId());
        productOrder.setOrderNo(orderNo);
        productOrder.setCodeUrl(QRCode);
        productOrder.setUserId(userId);
        productOrder.setProductId(productId);
        productOrder.setProductInfo(JSON.toJSONString(productInfo));
        productOrder.setOrderName(name);
        productOrder.setTotal(total);
        productOrder.setStatus(ProductOrderStatusEnum.NOTPAY.getValue());
        productOrder.setPayType(payType);
        productOrder.setFormData(null);
        productOrder.setAddGoldCoin(addGoldCoin);
        // 15分钟后订单过期
        productOrder.setExpirationTime(LocalDateTime.now().plusMinutes(EXPIRATION_TIME));
        boolean save = this.save(productOrder);
        // 存入缓存订单状态
        stringRedisTemplate.opsForValue().set(CACHE_PRODUCTORDER_STATUS_KEY + orderNo, Boolean.FALSE.toString(),EXPIRATION_TIME + 5 , TimeUnit.MINUTES);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建订单失败");
        }
        this.getProductOrderVO(productOrder);


        // 获取vo
        ProductOrderVO productOrderVO = this.getProductOrderVO(productOrder);
        // 3.返回
        return productOrderVO;
    }

    /**
     * 通过外部编号获得ProductOrder
     * @param outTradeNo
     * @return
     */
    @Override
    public ProductOrder getProductOrderByOutTradeNo(String outTradeNo) {
        LambdaQueryWrapper<ProductOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProductOrder::getOrderNo, outTradeNo);
        ProductOrder productOrder = this.getOne(lambdaQueryWrapper);
        if (productOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return productOrder;
    }


}




