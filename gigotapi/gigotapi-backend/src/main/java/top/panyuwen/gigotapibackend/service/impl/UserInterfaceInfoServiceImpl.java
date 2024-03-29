package top.panyuwen.gigotapibackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.constant.CommonConstant;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.exception.ThrowUtils;
import top.panyuwen.gigotapibackend.mapper.UserInterfaceinfoMapper;
import top.panyuwen.gigotapibackend.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import top.panyuwen.gigotapicommon.model.entity.InterfaceInfo;
import top.panyuwen.gigotapicommon.model.entity.UserInterfaceInfo;
import top.panyuwen.gigotapibackend.service.InterfaceInfoService;
import top.panyuwen.gigotapibackend.service.UserInterfaceInfoService;
import top.panyuwen.gigotapibackend.service.UserService;
import top.panyuwen.gigotapibackend.utils.SqlUtils;
import top.panyuwen.gigotapicommon.model.entity.User;
import top.panyuwen.gigotapicommon.model.vo.UserInterfaceInfoVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author PYW
* @description 针对表【user_interfaceinfo(用户接口调用表)】的数据库操作Service实现
* @createDate 2023-12-29 10:55:22
*/
@Service
@Slf4j
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceinfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {

    private final static Gson GSON = new Gson();

    @Resource
    private UserService userService;

    @Autowired
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 非空判断
     *
     * @param userInterfaceInfo
     * @param add
     */
    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = userInterfaceInfo.getId();
        Long userId = userInterfaceInfo.getUserId();
        Long interfaceId = userInterfaceInfo.getInterfaceId();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(ObjUtil.isEmpty(id), ErrorCode.PARAMS_ERROR,"id为空");
            ThrowUtils.throwIf(ObjUtil.isEmpty(userId), ErrorCode.PARAMS_ERROR,"userId为空");
            ThrowUtils.throwIf(ObjUtil.isEmpty(interfaceId), ErrorCode.PARAMS_ERROR,"interfaceId为空");
        }
        // 有参数则校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不正确");
        }
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "userId不正确");
        }
        if (interfaceId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "interfaceId不正确");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param userInterfaceInfoQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (userInterfaceInfoQueryRequest == null) {
            return queryWrapper;
        }
        Long id = userInterfaceInfoQueryRequest.getId();
        Long userId = userInterfaceInfoQueryRequest.getUserId();
        Long interfaceId = userInterfaceInfoQueryRequest.getInterfaceId();
        Integer status = userInterfaceInfoQueryRequest.getStatus();
        String sortField = userInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(interfaceId), "interfaceId", interfaceId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

//    @Override
//    public Page<UserInterfaceInfo> searchFromEs(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
//        Long id = userInterfaceInfoQueryRequest.getId();
//        String name = userInterfaceInfoQueryRequest.getName();
//        String searchText = userInterfaceInfoQueryRequest.getSearchText();
//        String description = userInterfaceInfoQueryRequest.getDescription();
//        String url = userInterfaceInfoQueryRequest.getUrl();
//        String requestHeader = userInterfaceInfoQueryRequest.getRequestHeader();
//        String responseHeader = userInterfaceInfoQueryRequest.getResponseHeader();
//        Integer status = userInterfaceInfoQueryRequest.getStatus();
//        String method = userInterfaceInfoQueryRequest.getMethod();
//        Long userId = userInterfaceInfoQueryRequest.getUserId();
//        // es 起始页为 0
//        long current = userInterfaceInfoQueryRequest.getCurrent();
//        long pageSize = userInterfaceInfoQueryRequest.getPageSize();
//        String sortField = userInterfaceInfoQueryRequest.getSortField();
//        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();
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
//        SearchHits<UserInterfaceInfoEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, UserInterfaceInfoEsDTO.class);
//        Page<UserInterfaceInfo> page = new Page<>();
//        page.setTotal(searchHits.getTotalHits());
//        List<UserInterfaceInfo> resourceList = new ArrayList<>();
//        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
//        if (searchHits.hasSearchHits()) {
//            List<SearchHit<UserInterfaceInfoEsDTO>> searchHitList = searchHits.getSearchHits();
//            List<Long> userInterfaceInfoIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
//                    .collect(Collectors.toList());
//            List<UserInterfaceInfo> userInterfaceInfoList = baseMapper.selectBatchIds(userInterfaceInfoIdList);
//            if (userInterfaceInfoList != null) {
//                Map<Long, List<UserInterfaceInfo>> idUserInterfaceInfoMap = userInterfaceInfoList.stream().collect(Collectors.groupingBy(UserInterfaceInfo::getId));
//                userInterfaceInfoIdList.forEach(userInterfaceInfoId -> {
//                    if (idUserInterfaceInfoMap.containsKey(userInterfaceInfoId)) {
//                        resourceList.add(idUserInterfaceInfoMap.get(userInterfaceInfoId).get(0));
//                    } else {
//                        // 从 es 清空 db 已物理删除的数据
//                        String delete = elasticsearchRestTemplate.delete(String.valueOf(userInterfaceInfoId), UserInterfaceInfoEsDTO.class);
//                        log.info("delete userInterfaceInfo {}", delete);
//                    }
//                });
//            }
//        }
//        page.setRecords(resourceList);
//        return page;
//    }

    @Override
    public UserInterfaceInfoVO getUserInterfaceInfoVO(UserInterfaceInfo userInterfaceInfo, HttpServletRequest request) {
        // 转为vo对象
        UserInterfaceInfoVO userInterfaceInfoVO = UserInterfaceInfoVO.objToVo(userInterfaceInfo);
        return userInterfaceInfoVO;
    }

    @Override
    public Page<UserInterfaceInfoVO> getUserInterfaceInfoVOPage(Page<UserInterfaceInfo> userInterfaceInfoPage, HttpServletRequest request) {
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoPage.getRecords();
        Page<UserInterfaceInfoVO> userInterfaceInfoVOPage = new Page<>(userInterfaceInfoPage.getCurrent(), userInterfaceInfoPage.getSize(), userInterfaceInfoPage.getTotal());
        if (CollectionUtils.isEmpty(userInterfaceInfoList)) {
            return userInterfaceInfoVOPage;
        }
        List<UserInterfaceInfoVO> userInterfaceInfoVOList = userInterfaceInfoList.stream().map(userInterfaceInfo -> {
            UserInterfaceInfoVO userInterfaceInfoVO = new UserInterfaceInfoVO();
            BeanUtil.copyProperties(userInterfaceInfo,userInterfaceInfoVO);
            return userInterfaceInfoVO;
        }).collect(Collectors.toList());
        // 2. 已登录，获取用户点赞、收藏状态
        // 填充信息
        userInterfaceInfoVOPage.setRecords(userInterfaceInfoVOList);
        return userInterfaceInfoVOPage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean invoke(Long interfaceInfoId, Long userId) {
        if(ObjUtil.isEmpty(interfaceInfoId) || ObjUtil.isEmpty(userId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        LambdaQueryWrapper<UserInterfaceInfo> qw = new LambdaQueryWrapper();
        qw.eq(UserInterfaceInfo::getInterfaceId,interfaceInfoId);
        qw.eq(UserInterfaceInfo::getUserId,userId);
        UserInterfaceInfo userInterfaceInfoInDB = getOne(qw);
        if(ObjUtil.isEmpty(userInterfaceInfoInDB)){
            // 不存在需要添加一条
            // 校验userId和interfaceInfoId是否存在
            InterfaceInfo checkInterfaceInfo = interfaceInfoService.getById(interfaceInfoId);
            User UserInDB = userService.getById(userId);
            if(ObjUtil.isEmpty(checkInterfaceInfo) || ObjUtil.isEmpty(UserInDB)){
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"接口信息或用户不存在");
            }
            userInterfaceInfoInDB = new UserInterfaceInfo();
            userInterfaceInfoInDB.setInterfaceId(interfaceInfoId);
            userInterfaceInfoInDB.setUserId(userId);
            userInterfaceInfoInDB.setTotalInvokes(1L);
            userInterfaceInfoInDB.setStatus(0);
            boolean saveFlag = save(userInterfaceInfoInDB);
            boolean interfaceInfoSaveFlag = interfaceInfoService.invoke(interfaceInfoId, userId);
            if (!(saveFlag && interfaceInfoSaveFlag)){
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"更新统计次数失败");
            }
            return true;
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        userInterfaceInfo.setId(userInterfaceInfoInDB.getId());
        userInterfaceInfo.setTotalInvokes(userInterfaceInfoInDB.getTotalInvokes()+1);
        boolean userInterfaceInfoInvokeCountFlag = updateById(userInterfaceInfo);
        boolean interfaceInfoInvokeCountFlag = interfaceInfoService.invoke(interfaceInfoId, userId);
        if(!(userInterfaceInfoInvokeCountFlag && interfaceInfoInvokeCountFlag)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"更新统计次数失败");
        }
        // 扣款
        return true;
    }
}