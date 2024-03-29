package top.panyuwen.gigotapibackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
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
import org.springframework.transaction.annotation.Transactional;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.constant.CommonConstant;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.exception.ThrowUtils;
import top.panyuwen.gigotapibackend.mapper.InterfaceInfoMapper;
import top.panyuwen.gigotapibackend.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import top.panyuwen.gigotapibackend.utils.redis.CacheClient;
import top.panyuwen.gigotapicommon.model.entity.InterfaceInfo;
import top.panyuwen.gigotapibackend.service.InterfaceInfoService;
import top.panyuwen.gigotapibackend.service.UserService;
import top.panyuwen.gigotapibackend.utils.SqlUtils;
import top.panyuwen.gigotapicommon.model.vo.InterfaceInfoVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
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
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    private final static Gson GSON = new Gson();

    @Resource
    private UserService userService;

    @Autowired
    private CacheClient cacheClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private InterfaceInfoMapper interfaceInfoMapper;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    /**
     * 非空判断
     *
     * @param interfaceInfo
     * @param add
     */
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String description = interfaceInfo.getDescription();
        String url = interfaceInfo.getUrl();
        String method = interfaceInfo.getMethod();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(
                    name, url, method),
                    ErrorCode.PARAMS_ERROR,"名称，地址，请求方式必填！");
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (interfaceInfoQueryRequest == null) {
            return queryWrapper;
        }
        Long id = interfaceInfoQueryRequest.getId();
        String searchText = interfaceInfoQueryRequest.getSearchText();
        String name = interfaceInfoQueryRequest.getName();
        String description = interfaceInfoQueryRequest.getDescription();
        String url = interfaceInfoQueryRequest.getUrl();
        String requestHeader = interfaceInfoQueryRequest.getRequestHeader();
        String responseHeader = interfaceInfoQueryRequest.getResponseHeader();
        Integer status = interfaceInfoQueryRequest.getStatus();
        String method = interfaceInfoQueryRequest.getMethod();
        Long userId = interfaceInfoQueryRequest.getUserId();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.like(StringUtils.isNotBlank(url), "url", url);
        queryWrapper.like(StringUtils.isNotBlank(requestHeader), "requestHeader", requestHeader);
        queryWrapper.like(StringUtils.isNotBlank(responseHeader), "responseHeader", responseHeader);
        queryWrapper.like(StringUtils.isNotBlank(method), "method", method);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

//    @Override
//    public Page<InterfaceInfo> searchFromEs(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
//        Long id = interfaceInfoQueryRequest.getId();
//        String name = interfaceInfoQueryRequest.getName();
//        String searchText = interfaceInfoQueryRequest.getSearchText();
//        String description = interfaceInfoQueryRequest.getDescription();
//        String url = interfaceInfoQueryRequest.getUrl();
//        String requestHeader = interfaceInfoQueryRequest.getRequestHeader();
//        String responseHeader = interfaceInfoQueryRequest.getResponseHeader();
//        Integer status = interfaceInfoQueryRequest.getStatus();
//        String method = interfaceInfoQueryRequest.getMethod();
//        Long userId = interfaceInfoQueryRequest.getUserId();
//        // es 起始页为 0
//        long current = interfaceInfoQueryRequest.getCurrent();
//        long pageSize = interfaceInfoQueryRequest.getPageSize();
//        String sortField = interfaceInfoQueryRequest.getSortField();
//        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
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
//        SearchHits<InterfaceInfoEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, InterfaceInfoEsDTO.class);
//        Page<InterfaceInfo> page = new Page<>();
//        page.setTotal(searchHits.getTotalHits());
//        List<InterfaceInfo> resourceList = new ArrayList<>();
//        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
//        if (searchHits.hasSearchHits()) {
//            List<SearchHit<InterfaceInfoEsDTO>> searchHitList = searchHits.getSearchHits();
//            List<Long> interfaceInfoIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
//                    .collect(Collectors.toList());
//            List<InterfaceInfo> interfaceInfoList = baseMapper.selectBatchIds(interfaceInfoIdList);
//            if (interfaceInfoList != null) {
//                Map<Long, List<InterfaceInfo>> idInterfaceInfoMap = interfaceInfoList.stream().collect(Collectors.groupingBy(InterfaceInfo::getId));
//                interfaceInfoIdList.forEach(interfaceInfoId -> {
//                    if (idInterfaceInfoMap.containsKey(interfaceInfoId)) {
//                        resourceList.add(idInterfaceInfoMap.get(interfaceInfoId).get(0));
//                    } else {
//                        // 从 es 清空 db 已物理删除的数据
//                        String delete = elasticsearchRestTemplate.delete(String.valueOf(interfaceInfoId), InterfaceInfoEsDTO.class);
//                        log.info("delete interfaceInfo {}", delete);
//                    }
//                });
//            }
//        }
//        page.setRecords(resourceList);
//        return page;
//    }

    @Override
    public InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo, HttpServletRequest request) {
        // 转为vo对象
        InterfaceInfoVO interfaceInfoVO = InterfaceInfoVO.objToVo(interfaceInfo);
        return interfaceInfoVO;
    }

    @Override
    public InterfaceInfo getInterfaceInfoById(Long id) {
        return cacheClient.getWithLogicalExpire(CACHE_INTERFACEINFO_KEY,id,InterfaceInfo.class,CACHE_INTERFACEINFO_TTL, TimeUnit.MINUTES,this::getById);
    }

    @Override
    public Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage) {
        List<InterfaceInfo> interfaceInfoList = interfaceInfoPage.getRecords();
        Page<InterfaceInfoVO> interfaceInfoVOPage = new Page<>(interfaceInfoPage.getCurrent(), interfaceInfoPage.getSize(), interfaceInfoPage.getTotal());
        if (CollectionUtils.isEmpty(interfaceInfoList)) {
            return interfaceInfoVOPage;
        }
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoList.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtil.copyProperties(interfaceInfo,interfaceInfoVO);
            return interfaceInfoVO;
        }).collect(Collectors.toList());
        // 2. 已登录，获取用户点赞、收藏状态
        // 填充信息
        interfaceInfoVOPage.setRecords(interfaceInfoVOList);
        return interfaceInfoVOPage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean invoke(Long id, Long userId) {
        if(ObjUtil.isEmpty(id)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        InterfaceInfo interfaceInfoInDB = getById(id);
        if(ObjUtil.isEmpty(interfaceInfoInDB)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"未找到该数据");
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(interfaceInfoInDB.getId());
        interfaceInfo.setTotalInvokes(interfaceInfoInDB.getTotalInvokes()+1);
        // 扣减，注意这个地方需要取负数
        boolean flag = userService.adjustmentGoldCoin(userId, -interfaceInfoInDB.getPayGoldCoin());
        return update().setSql("totalInvokes = totalInvokes + 1").eq("id",interfaceInfo.getId()).update();
    }

    @Override
    public Page<InterfaceInfo> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String method = interfaceInfoQueryRequest.getMethod();
        String name = interfaceInfoQueryRequest.getName();
        String description = interfaceInfoQueryRequest.getDescription();

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 从缓存中获取接口分页信息
        // 计算 offset 和 count
        long offset = (current - 1) * size;
        long count = size;
        // 获取redis中的数据
        Set<String> allInterfacesInRedis = stringRedisTemplate.opsForZSet().reverseRange(CACHE_INTERFACEINFO_ALL_KEY, 0, -1);
        Page<InterfaceInfo> page = new Page<>(current, size);
        if(ObjUtil.isNotEmpty(allInterfacesInRedis)){
            //缓存命中
            // 对元素进行分页处理
            List<InterfaceInfo> pagedInterfaces = allInterfacesInRedis.stream()
                    .skip(offset)
                    .limit(count)
                    .filter(interfaceInRedis ->{
                                InterfaceInfo interfaceInfo = this.convertJSONToInterfaceInfo(interfaceInRedis);
                                // 查询条件
                                if(StrUtil.isAllBlank(method,name,description)){
                                    return true;
                                }
                                if(StrUtil.isNotBlank(method)){
                                    if(interfaceInfo.getMethod().contains(method)){
                                        return true;
                                    }
                                }
                                if(StrUtil.isNotBlank(name)){
                                    if(interfaceInfo.getName().contains(name)){
                                        return true;
                                    }
                                }
                                if(StrUtil.isNotBlank(description)){
                                    if(interfaceInfo.getDescription().contains(description)){
                                        return true;
                                    }
                                }
                                return false;
                            }
                    )
                    .map(this::convertJSONToInterfaceInfo)
                    .collect(Collectors.toList());
            page.setRecords(pagedInterfaces);
            page.setTotal(allInterfacesInRedis.size());
            return page;
        }
        // 未命中
        // 查询数据库

        List<InterfaceInfo> allInterfaceInfoList = list();
        page.setRecords(allInterfaceInfoList);
        page.setTotal(allInterfaceInfoList.size());
        //存储数据到redis中
        for (InterfaceInfo interfaceInfo : page.getRecords()) {
            // 将 InterfaceInfo 转为 JSON 字符串
            String json = convertInterfaceInfoToJSON(interfaceInfo);

            // 添加到 Redis 的 ZSet 中，使用默认的 score（即按照插入顺序）
            stringRedisTemplate.opsForZSet().add(CACHE_INTERFACEINFO_ALL_KEY, json,interfaceInfo.getTotalInvokes());
        }
        return page;
    }

    private String convertInterfaceInfoToJSON(InterfaceInfo interfaceInfo) {
        return JSON.toJSONString(interfaceInfo);
    }
    private InterfaceInfo convertJSONToInterfaceInfo(String interfaceInfoJSON) {
        return JSON.parseObject(interfaceInfoJSON, InterfaceInfo.class);
    }

    @Override
    public void deleteRedisCache(Long id){
        stringRedisTemplate.delete(CACHE_INTERFACEINFO_ALL_KEY);
        stringRedisTemplate.delete(CACHE_INTERFACEINFO_KEY + id);
    }

    @Override
    public Long getInterfaceInfoTotalInvokesCount() {
        return interfaceInfoMapper.getInterfaceInfoTotalInvokesCount().longValue();
    }


}




