package top.panyuwen.gigotapibackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.*;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.common.request.DeleteListRequest;
import top.panyuwen.gigotapibackend.config.WxMpConfig;
import top.panyuwen.gigotapibackend.constant.CommonConstant;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.exception.ThrowUtils;
import top.panyuwen.gigotapibackend.mapper.UserMapper;
import top.panyuwen.gigotapibackend.model.dto.user.*;
import top.panyuwen.gigotapibackend.model.enums.UserRoleEnum;
import top.panyuwen.gigotapibackend.service.FileService;
import top.panyuwen.gigotapibackend.service.UserService;
import top.panyuwen.gigotapibackend.utils.*;
import top.panyuwen.gigotapibackend.utils.redis.CacheClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import top.panyuwen.gigotapibackend.wxmp.entity.CreateXcxQrRequestBodyRequest;
import top.panyuwen.gigotapibackend.wxmp.entity.WxMpLoginResponse;
import top.panyuwen.gigotapicommon.model.entity.User;
import top.panyuwen.gigotapicommon.model.vo.LoginUserVO;
import top.panyuwen.gigotapicommon.model.vo.LoginXcxQrVO;
import top.panyuwen.gigotapicommon.model.vo.UserVO;

import static top.panyuwen.gigotapibackend.constant.RedisConstants.*;
import static top.panyuwen.gigotapibackend.constant.RedisConstants.LOGIN_TOKEN_TTL;
import static top.panyuwen.gigotapibackend.constant.UserConstant.*;

/**
 * 用户服务实现
 *
 * @author PYW
 * @from www.panyuwen.top
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "gigot_";

    @Value("${gigot-api.upload.apiUrl}")
    private String apiUrl;

    @Autowired
    private CacheClient cacheClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private FileService fileService;

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private WxMpConfig wxMpConfig;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 6 || checkPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            log.info("注册加密：{}", encryptPassword);
            // 3. 插入数据
            String secretId = generateSecretId(userAccount);
            String secretKey = generateSecretKey(userAccount);
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserAvatar(apiUrl + DEFAULT_USER_AVATAR);
            user.setUserName("gigotApi_" + userAccount);
            user.setSecretId(secretId);
            user.setSecretKey(secretKey);
            user.setUserProfile(DEFAULT_USER_PROFILE);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    /**
     * 生成secretId
     */
    private String generateSecretId(String userAccount) {
        String secretId = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomString(8));
        return secretId;
    }

    /**
     * 生成secretKey
     */
    private String generateSecretKey(String userAccount) {
        String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomString(8));
        return secretKey;
    }


    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不小于6位");
        }
        // 检查redis中是否有
        String loginNotFoundKey = LOGIN_NOTFOUND_KEY + userAccount;
        String cacheNotFound = cacheClient.get(loginNotFoundKey);
        if (cacheNotFound != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户已登录，请勿重新登录！");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        log.info("登录解密：{}", encryptPassword);
        // 查询账号是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User accountResult = this.baseMapper.selectOne(queryWrapper);
        if (accountResult == null) {
            cacheClient.set(loginNotFoundKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到当前用户请注册后再试！");
        }
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        // 3. 记录用户的登录态
        // 写入redis
        LoginUserVO loginUserVO = BeanUtil.copyProperties(user, LoginUserVO.class);
        cacheClient.set(LOGIN_TOKEN_KEY + user.getId(), loginUserVO, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        String unionId = wxOAuth2UserInfo.getUnionId();
        String mpOpenId = wxOAuth2UserInfo.getOpenid();
        // 单机锁
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionId", unionId);
            User user = this.getOne(queryWrapper);
            // 被封号，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (user == null) {
                user = new User();
                user.setUnionId(unionId);
                user.setMpOpenId(mpOpenId);
                user.setUserAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                user.setUserName(wxOAuth2UserInfo.getNickname());
                boolean result = this.save(user);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败");
                }
            }
            // 记录用户的登录态
            // 写入redis
            LoginUserVO loginUserVO = BeanUtil.copyProperties(user, LoginUserVO.class);
            cacheClient.set(LOGIN_TOKEN_KEY + user.getId(), loginUserVO, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
            // 写入本地线程池
            UserHolder.saveUser(loginUserVO);
            return loginUserVO;
        }
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        LoginUserVO loginUserVO = UserHolder.getUser();
        log.info("loginUserVO：", loginUserVO);
        User currentUser = BeanUtil.copyProperties(loginUserVO, User.class);
        if (ObjectUtil.isEmpty(currentUser) || ObjectUtil.isEmpty(currentUser.getId())) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        String currentUserString = cacheClient.get(LOGIN_TOKEN_KEY + userId);
        currentUser = JSON.parseObject(currentUserString, User.class);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        LoginUserVO loginUserVO = UserHolder.getUser();
        User currentUser = BeanUtil.copyProperties(loginUserVO, User.class);
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        String currentUserString = cacheClient.get(LOGIN_TOKEN_KEY + userId);
        currentUser = JSON.parseObject(currentUserString, User.class);
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        LoginUserVO loginUserVO = UserHolder.getUser();
        User currentUser = BeanUtil.copyProperties(loginUserVO, User.class);
        return isAdmin(currentUser);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (UserHolder.getUser() == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        LoginUserVO user = UserHolder.getUser();
        UserHolder.removeUser();
        stringRedisTemplate.delete(LOGIN_TOKEN_KEY + user.getId());
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public User updateVoucher(User user) {
        if (ObjUtil.isEmpty(user)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新凭证用户为空");
        }

        String secretId = generateSecretId(user.getUserAccount());
        String secretKey = generateSecretKey(user.getUserAccount());
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setSecretId(secretId);
        updateUser.setSecretKey(secretKey);
        boolean updateFlag = updateById(updateUser);
        if (!updateFlag) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新凭证失败");
        }
        return updateUser;
    }

    @Override
    public User getVoucher(HttpServletRequest request) {
        User loginUser = this.getLoginUser(request);
        // 是否登录
        if (ObjUtil.isEmpty(loginUser)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 获取凭证
        User user = this.getById(loginUser.getId());
        return user;
    }

    @Override
    public boolean deleteUserByIds(DeleteListRequest deleteListRequest, HttpServletRequest request) {
        if (deleteListRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = this.getLoginUser(request);
        List<Long> ids = deleteListRequest.getIds();
        // 判断是否存在
        List<User> users = this.listByIds(ids);
        if (users.size() != ids.size()) {
            List<Long> idsInDB = users.stream().map(User::getId).collect(Collectors.toList());
            List<Long> differentIds = CollUtil.disjunction(ids, idsInDB).stream().collect(Collectors.toList());
            log.error("differentIds:{}", differentIds);
            ThrowUtils.throwIf(differentIds.size() > 0, ErrorCode.NOT_FOUND_ERROR, "未找到删除数据：differentIds：" + differentIds);
        }
        // 校验是否为管理员
        if (!this.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "只允许管理员删除");
        }
        boolean removeByIdsFlag = this.removeByIds(ids);
        if (!removeByIdsFlag) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        List<String> idKeyList = users.stream().map(item -> CACHE_USER_KEY + item.getId()).collect(Collectors.toList());
        Long deleteCount = stringRedisTemplate.delete(idKeyList);
        return true;
    }

    @Override
    public Long addUser(UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 默认密码123456
        user.setUserPassword("123456");
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        // 写入redis
        Long userId = user.getId();
        cacheClient.set(CACHE_USER_KEY + userId, user, CACHE_USER_TTL, TimeUnit.MINUTES);
        return userId;
    }

    @Override
    public boolean updateUser(UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查询用户信息
        User userInDB = this.getById(userUpdateRequest.getId());
        if (ObjUtil.isEmpty(userInDB)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未查询到修改信息");
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = this.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 删除用户头像
        String defaultAvatarUrl = apiUrl + DEFAULT_USER_AVATAR;
        // 用户头像删除校验条件
        String avatarUrlInDB = userInDB.getUserAvatar();
        boolean deleteAvatarFlag =
                StrUtil.isNotBlank(avatarUrlInDB) &&
                        StrUtil.isNotBlank(userUpdateRequest.getUserAvatar()) &&
                        !avatarUrlInDB.equals(user.getUserAvatar()) &&
                        !defaultAvatarUrl.equals(user.getUserAvatar());
        log.info("准备删除的头像文件名：{} , 判断条件deleteAvatarFlag：{}", avatarUrlInDB, deleteAvatarFlag);
        if (deleteAvatarFlag) {
            //删除
            log.debug("准备删除的头像文件名：{}", avatarUrlInDB);
            boolean isDelete = fileService.deleteFile(avatarUrlInDB);
            if (!isDelete) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新失败，用户头像删除失败");
            }
        }
        // 缓存处理
        // 1.删除User缓存
        stringRedisTemplate.delete(CACHE_USER_KEY + userUpdateRequest.getId());
        // 2. 更新登录缓存
        User updateAfterUser = this.getById(user.getId());
        LoginUserVO loginUserVO = BeanUtil.copyProperties(updateAfterUser, LoginUserVO.class);
        cacheClient.set(LOGIN_TOKEN_KEY + updateAfterUser.getId(), loginUserVO, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public Page<User> listUserByPage(UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(userQueryRequest));
        return userPage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean adjustmentGoldCoin(Long userId, Long addGoldCoin) {
        // 非空判断
        if (ObjUtil.isEmpty(userId) || ObjUtil.isEmpty(addGoldCoin)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户id，或者调整金额不能为空！");
        }
        // 判断addGoldCoin是正数还是负数
        if (addGoldCoin < 0L) {
            // 负数
            User user = this.getById(userId);
            if (user.getBalanceGoldCoin() + addGoldCoin < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "余额不足");
            }
        }
        boolean update = update().setSql("balanceGoldCoin = balanceGoldCoin +" + addGoldCoin).eq("id", userId).update();

        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调整金币失败");
        }
        return true;
    }

    @Override
    public boolean updateUserCache(Long userId) {
        // 重新设置用户缓存
        User user = this.getById(userId);
        UserVO userVO = this.getUserVO(user);
        String userVOJson = JSON.toJSONString(userVO);
        stringRedisTemplate.opsForValue().set(LOGIN_TOKEN_KEY + userId, userVOJson, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public boolean bindEmail(UserBindEmailRequest userBindEmailRequest, Long userId) {
        String email = userBindEmailRequest.getEmail();
        String verificationCode = userBindEmailRequest.getVerificationCode();
        if (StrUtil.isBlank(email) || StrUtil.isBlank(verificationCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱和验证码不能为空！");
        }
        String verificationCodeInCache = stringRedisTemplate.opsForValue().get(VERIFICATIONCODE_CACHE_KEY + email);
        if (!StrUtil.equals(verificationCode, verificationCodeInCache)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误！");
        }
        // 验证码正确，更新用户邮箱
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId);
        updateWrapper.set(User::getEmail, email);
        this.update(updateWrapper);
        // 重建缓存
        this.updateUserCache(userId);
        // 删除验证码
        stringRedisTemplate.delete(VERIFICATIONCODE_CACHE_KEY + email);
        return true;
    }

    @Override
    public Boolean unBindEmail(Long id) {
        if (ObjUtil.isEmpty(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户id必传");
        }
        boolean update = this.update(new LambdaUpdateWrapper<User>().eq(User::getId, id).set(User::getEmail, null));
        String email = this.getById(id).getEmail();
        // 重建缓存
        this.updateUserCache(id);
        // 删除验证码
        stringRedisTemplate.delete(VERIFICATIONCODE_CACHE_KEY + email);
        return true;
    }

    @Override
    public Boolean sendEmailVerificationCode(String email) {
        // 邮箱非空验证
        if (StrUtil.isBlank(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱不能为空");
        }
        // 验证邮箱格式
        boolean checkEmail = MailUtils.checkEmail(email);
        if (!checkEmail) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式不符合要求！");
        }
        // 发送验证码
        String verificationCode = RandomUtil.randomNumbers(6);

        try {
            // 加载模板
            String content = mailUtils.loadEmailTemplate("static/email/codeEmail.html");
            // 替换参数
            content = mailUtils.populateTemplate(content, verificationCode);
            // 发送邮件
            mailUtils.sendMail(email, content, "【羊腿Api开放平台】邮箱验证码");
        } catch (IOException | MessagingException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮件发送失败");
        }
        // 存储验证码到缓存中
        stringRedisTemplate.opsForValue().set(VERIFICATIONCODE_CACHE_KEY + email, verificationCode, VERIFICATIONCODE_CACHE_TTL, TimeUnit.MINUTES);
        return true;

    }

    /**
     * 用户签到
     *
     * @return
     */
    @Override
    public String sign(HttpServletRequest request) {
        User loginUser = this.getLoginUser(request);
        String key = getSignKey(loginUser);
        // 5.写入redis setbit key offset 1
        int offset = LocalDateTime.now().getDayOfMonth() - 1;
        Boolean signFlag = stringRedisTemplate.opsForValue().getBit(key, offset);

        if (signFlag) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "今日已签到");
        }
        Boolean flag = stringRedisTemplate.opsForValue().setBit(key, offset, true);


        // 增加金币
        this.adjustmentGoldCoin(loginUser.getId(), 10L);
        // 重建缓存
        this.updateUserCache(loginUser.getId());
        return "签到成功";
    }

    @Override
    public LoginUserVO userLoginByEmail(UserLoginByEmailRequest userLoginByEmailRequest) {
        String email = userLoginByEmailRequest.getEmail();
        String verificationCode = userLoginByEmailRequest.getVerificationCode();
        if (StrUtil.isBlank(email) || StrUtil.isBlank(verificationCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号或验证码不能为空");
        }

        // 检查短信验证码是否过期
        String loginCodeInCache = stringRedisTemplate.opsForValue().get(VERIFICATIONCODE_CACHE_KEY + email);
        if (StrUtil.isBlank(loginCodeInCache)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码未发送或已过期，请重新获取");
        }

        // 比对验证码
        if (!loginCodeInCache.equals(verificationCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误，请重新输入");
        }

        // 检查手机是否已经注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        User user = this.getOne(queryWrapper);
        if (ObjUtil.isEmpty(user)) {
            // 用户未注册，执行注册逻辑
            String randowmAccount = DEFAULT_ACCOUNT_PREFIX + IdUtil.fastSimpleUUID();
            String secretId = generateSecretId(randowmAccount);
            String secretKey = generateSecretKey(randowmAccount);

            user = new User();
            String passowrdMD5 = DigestUtils.md5DigestAsHex((SALT + DEFAULT_PASSWORD).getBytes());
            user.setUserAccount(randowmAccount);
            user.setUserPassword(passowrdMD5);
            user.setUserAvatar(apiUrl + DEFAULT_USER_AVATAR);
            user.setEmail(email);
            user.setUserRole("user");
            user.setUserName(RandomNickName.getRandomName());
            user.setSecretId(secretId);
            user.setSecretKey(secretKey);
            user.setBalanceGoldCoin(DEFAULT_BALANCE_GOLDCOIN);
            user.setUserProfile(DEFAULT_USER_PROFILE);
            boolean save = this.save(user);
            if (!save) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，请稍后再试");
            }
        }
        // 用户已注册，执行登录逻辑
        LoginUserVO loginUserVO = BeanUtil.copyProperties(user, LoginUserVO.class);
        cacheClient.set(LOGIN_TOKEN_KEY + user.getId(), loginUserVO, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userLoginByXcx(UserLoginByXcxRequest userLoginByXcxRequest) {
        String code = userLoginByXcxRequest.getCode();
        String scene = userLoginByXcxRequest.getScene();
        // 非空判断
        if (StrUtil.isBlank(code) || StrUtil.isBlank(scene)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "微信小程序传输的code和scene不能为空");
        }

        // 获取配置信息
        String appId = wxMpConfig.getAppId();
        String secret = wxMpConfig.getSecret();
        // 向微信服务端发起请求
        OkHttpClient client = new OkHttpClient();

        // 定义请求对象
        Request request = new Request.Builder()
                .url("https://api.weixin.qq.com/sns/jscode2session?" +
                        "appid=" + appId +
                        "&secret=" + secret +
                        "&js_code=" + code +
                        "&grant_type=authorization_code") // 替换为要请求的网址
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "请求微信OpenId失败，请稍后再试");
        }
        String body = null;
        try {
            body = response.body().string();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "请求微信服务器响应体转换字符串失败");
        }

        log.info("微信服务器登录返回响应体：{}", body);
        WxMpLoginResponse wxMpLoginResponse = JSON.parseObject(body, WxMpLoginResponse.class);
        int wxMpLoginResponseErrcode = wxMpLoginResponse.getErrcode();
        if (ObjUtil.isNotEmpty(wxMpLoginResponseErrcode)) {
            // 微信返回错误
            if (wxMpLoginResponseErrcode == 40029) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "微信服务器返回code无效");
            }
            if (wxMpLoginResponseErrcode == 45011) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录过于频繁请稍后再试！");
            }
            if (wxMpLoginResponseErrcode == 40226) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "微信高风险等级用户，小程序登录拦截！");
            }
            if (wxMpLoginResponseErrcode == -1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "微信服务器繁忙请稍后再试");
            }
        }
        // 返回正常执行登录逻辑
        // 判断该用户是否注册
        String openid = wxMpLoginResponse.getOpenid();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getMpOpenId, openid);
        User user = this.getOne(queryWrapper);
        if (ObjUtil.isEmpty(user)) {
            user = new User();
            String randowmAccount = DEFAULT_ACCOUNT_PREFIX + IdUtil.fastSimpleUUID();
            String passowrdMD5 = DigestUtils.md5DigestAsHex((SALT + DEFAULT_PASSWORD).getBytes());
            String secretId = generateSecretId(randowmAccount);
            String secretKey = generateSecretKey(randowmAccount);
            user.setUserAccount(randowmAccount);
            user.setUserPassword(passowrdMD5);
            user.setUserAvatar(apiUrl + DEFAULT_USER_AVATAR);
            user.setUserRole("user");
            user.setMpOpenId(openid);
            user.setUserName(RandomNickName.getRandomName());
            user.setSecretId(secretId);
            user.setSecretKey(secretKey);
            user.setBalanceGoldCoin(DEFAULT_BALANCE_GOLDCOIN);
            user.setUserProfile(DEFAULT_USER_PROFILE);
            boolean save = this.save(user);
            if (!save) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，保存用户信息异常，请稍后再试");
            }
        }
        // 用户已注册，执行登录逻辑
        LoginUserVO loginUserVO = BeanUtil.copyProperties(user, LoginUserVO.class);
        // 存入缓存
        stringRedisTemplate.opsForValue().set(LOGIN_XCX_SCENE_KEY + scene, user.getId().toString(), LOGIN_XCX_SCENE_TTL, TimeUnit.MINUTES);
        cacheClient.set(LOGIN_TOKEN_KEY + user.getId(), loginUserVO, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        return loginUserVO;
    }

    @Override
    public LoginUserVO getLoginUserXcxCheck(String scene) {
        // 判断小程序是否已经授权
        String userId = stringRedisTemplate.opsForValue().get(LOGIN_XCX_SCENE_KEY + scene);
        if(StrUtil.isBlank(userId)){
            log.info("缓存中未查询到微信小程序scene");
            return null;
        }
        String loginUserVOJson = stringRedisTemplate.opsForValue().get(LOGIN_TOKEN_KEY + userId);
        if (StrUtil.isBlank(loginUserVOJson)) {
            log.info("缓存中未查询到微信小程序登录信息");
            return null;
        }
        LoginUserVO currentUser = JSON.parseObject(loginUserVOJson, LoginUserVO.class);
        return currentUser;
    }

    @Override
    public LoginXcxQrVO userLoginByXcxCreateQR() {
        String appId = wxMpConfig.getAppId();
        String secret = wxMpConfig.getSecret();
        WxXcxQRUtils wxXcxQRUtils = new WxXcxQRUtils(appId, secret);
        CreateXcxQrRequestBodyRequest createXcxQrRequestBodyRequest = new CreateXcxQrRequestBodyRequest();
        String scene = IdUtil.fastSimpleUUID();
        createXcxQrRequestBodyRequest.setScene(scene);
        createXcxQrRequestBodyRequest.setPage("pages/index/index");
        createXcxQrRequestBodyRequest.setCheck_path(false);
        String qrName = null;
        try {
            qrName = wxXcxQRUtils.getXcxQRUtils(createXcxQrRequestBodyRequest, DEFAULT_XCX_QRCODE_PATH);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "微信小程序二维码生成失败，请刷新页面后再试");
        }
        LoginXcxQrVO loginXcxQrVO = new LoginXcxQrVO();
        loginXcxQrVO.setScene(scene);
        loginXcxQrVO.setQrName(qrName);

        return loginXcxQrVO;
    }

    @Override
    public Long getUserCount() {
        return this.count();
    }

    @Override
    public boolean updateUserName(User user) {
        Long id = user.getId();
        String userName = user.getUserName();
        if(ObjectUtil.isEmpty(id) || StrUtil.isBlank(userName)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean update = this.updateById(user);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户信息失败，请稍后再试");
        }
        // 重建登录缓存
        User userInDB = this.getById(id);
        LoginUserVO loginUserVO = BeanUtil.copyProperties(userInDB, LoginUserVO.class);
        // 存入缓存
        cacheClient.set(LOGIN_TOKEN_KEY + userInDB.getId(), loginUserVO, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        return true;

    }

    @Override
    public boolean updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest, Long id) {
        String oldPassword = userUpdatePasswordRequest.getOldPassword();
        String newPassword = userUpdatePasswordRequest.getNewPassword();
        String checkNewPassword = userUpdatePasswordRequest.getCheckNewPassword();
        if(StrUtil.isBlank(oldPassword)||StrUtil.isBlank(newPassword) || StrUtil.isBlank(checkNewPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(oldPassword.equals(newPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码不能与旧密码相同");
        }
        if(!newPassword.equals(checkNewPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的新密码不一致");
        }
        User user = this.getById(id);
        if(!DigestUtils.md5DigestAsHex((SALT + oldPassword).getBytes()).equals(user.getUserPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "旧密码错误！！！");
        }
        User userUpdate = new User();
        userUpdate.setId(id);
        userUpdate.setUserPassword(DigestUtils.md5DigestAsHex((SALT + newPassword).getBytes()));
        return this.updateById(userUpdate);
    }

    private String getSignKey(User loginUser) {
        // 1.获取当前用户
        // 2.获取当前日期
        int nowYear = LocalDateTime.now().getYear();
        int nowMonth = LocalDateTime.now().getMonth().getValue();

        // 5.拼接key
        StringJoiner js = new StringJoiner(":", SYSTEM_SIGN_KEY, "");
        js.add(loginUser.getId().toString());
        js.add(String.valueOf(nowYear));
        js.add(String.valueOf(nowMonth));
        return js.toString();
    }
}
