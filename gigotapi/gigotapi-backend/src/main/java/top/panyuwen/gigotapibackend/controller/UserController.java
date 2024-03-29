package top.panyuwen.gigotapibackend.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.panyuwen.gigotapibackend.annotation.AuthCheck;
import top.panyuwen.gigotapibackend.common.BaseResponse;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.common.ResultUtils;
import top.panyuwen.gigotapibackend.common.request.DeleteListRequest;
import top.panyuwen.gigotapibackend.common.request.DeleteRequest;
import top.panyuwen.gigotapibackend.config.WxOpenConfig;
import top.panyuwen.gigotapibackend.constant.UserConstant;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.exception.ThrowUtils;
import top.panyuwen.gigotapibackend.model.dto.user.*;
import top.panyuwen.gigotapibackend.service.FileService;
import top.panyuwen.gigotapibackend.service.UserService;
import top.panyuwen.gigotapibackend.utils.redis.CacheClient;
import top.panyuwen.gigotapicommon.model.entity.User;
import top.panyuwen.gigotapicommon.model.vo.LoginUserVO;
import top.panyuwen.gigotapicommon.model.vo.LoginXcxQrVO;
import top.panyuwen.gigotapicommon.model.vo.UserVO;
import top.panyuwen.gigotapicommon.model.vo.UserVoucherVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static top.panyuwen.gigotapibackend.constant.RedisConstants.*;

/**
 * 用户接口
 *
 * @author PYW
 * @from www.panyuwen.top
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Value("${gigot-api.upload.apiUrl}")
    private String apiUrl;

    @Resource
    private UserService userService;

    @Resource
    private WxOpenConfig wxOpenConfig;

    @Autowired
    private FileService fileService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CacheClient cacheClient;



    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 登录发送短信验证码
     * @param userLoginByEmailRequest
     * @param request
     * @return
     */
    @PostMapping("/login/email/sendVerificationCode")
    public BaseResponse<Boolean> loginSendVerificationCode(@RequestBody UserLoginByEmailRequest userLoginByEmailRequest, HttpServletRequest request) {
        String email = userLoginByEmailRequest.getEmail();
        if (StrUtil.isBlank(userLoginByEmailRequest.getEmail())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号不能为空");
        }
        // 校验缓存是否有数据，有数据说明已经发送
        String loginCodeInCache = stringRedisTemplate.opsForValue().get(VERIFICATIONCODE_CACHE_KEY + email);
        if (StrUtil.isNotBlank(loginCodeInCache)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"短信已发送，请十分钟后再试");
        }
        // 发送验证码
        return ResultUtils.success(userService.sendEmailVerificationCode(email));
    }

    /**
     * 登录（手机号，免注册）
     */
    @PostMapping("/login/email")
    public BaseResponse<LoginUserVO> userLoginByEmail(@RequestBody UserLoginByEmailRequest userLoginByEmailRequest, HttpServletRequest request) {
        return ResultUtils.success(userService.userLoginByEmail(userLoginByEmailRequest));
    }

    /**
     * 登录（微信小程序，免注册）
     */
    @PostMapping("/login/xcx")
    public BaseResponse<LoginUserVO> userLoginByXcx(@RequestBody UserLoginByXcxRequest userLoginByXcxRequest) {
        String code = userLoginByXcxRequest.getCode();
        log.info("接收到小程序的登录userLoginByXcxRequest：{}", userLoginByXcxRequest);
        LoginUserVO loginUserVO = userService.userLoginByXcx(userLoginByXcxRequest);
        return ResultUtils.success(null);
    }

    @PostMapping("/login/xcx/createQR")
    public BaseResponse<LoginXcxQrVO> userLoginByXcxCreateQR(){
        return ResultUtils.success(userService.userLoginByXcxCreateQR());
    }

    /**
     * 获取当前登录用户（小程序扫码登录校验）
     *
     * @return
     */
    @PostMapping("/get/login/xcx/check")
    public BaseResponse<LoginUserVO> getLoginUserXcxCheck(@RequestBody UserLoginXcxCheckRequest userLoginXcxCheckRequest) {
        String scene = userLoginXcxCheckRequest.getScene();
        return ResultUtils.success(userService.getLoginUserXcxCheck(scene));
    }

    /**
     * 用户登录（微信开放平台）
     */
    @GetMapping("/login/wx_open")
    public BaseResponse<LoginUserVO> userLoginByWxOpen(HttpServletRequest request, HttpServletResponse response,
                                                       @RequestParam("code") String code) {
        WxOAuth2AccessToken accessToken;
        try {
            WxMpService wxService = wxOpenConfig.getWxMpService();
            accessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, code);
            String unionId = userInfo.getUnionId();
            String mpOpenId = userInfo.getOpenid();
            if (StringUtils.isAnyBlank(unionId, mpOpenId)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
            }
            return ResultUtils.success(userService.userLoginByMpOpen(userInfo, request));
        } catch (Exception e) {
            log.error("userLoginByWxOpen error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
        }
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }



    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        return ResultUtils.success(userService.addUser(userAddRequest));
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/deleteById")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserById(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long deleteId = deleteRequest.getId();
        boolean b = userService.removeById(deleteId);
        stringRedisTemplate.delete(String.valueOf(deleteId));
        return ResultUtils.success(b);
    }

    /**
     * 删除用户（多个id）
     *
     * @param deleteListRequest
     * @param request
     * @return
     */
    @PostMapping("/deleteByIds")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserByIds(@RequestBody DeleteListRequest deleteListRequest, HttpServletRequest request) {
        boolean removeByIdsFlag = userService.deleteUserByIds(deleteListRequest, request);
        if (!removeByIdsFlag) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库删除异常");
        }
        return ResultUtils.success(removeByIdsFlag);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {

        boolean updateFlag = userService.updateUser(userUpdateRequest);
        if(!updateFlag){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/update/username")
    public BaseResponse<Boolean> updateUserName(@RequestBody UserNameUpdateRequest userNameUpdateRequest) {
        User user = new User();
        user.setId(userNameUpdateRequest.getId());
        user.setUserName(userNameUpdateRequest.getUserName());
        boolean updateFlag = userService.updateUserName(user);
        if(!updateFlag){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = cacheClient.getWithPassThrough(CACHE_USER_KEY + id, id, User.class, CACHE_USER_TTL, TimeUnit.SECONDS, userService::getById);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
        BaseResponse<User> response = getUserById(id, request);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
        Page<User> userPage = userService.listUserByPage(userQueryRequest);
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                       HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
                                              HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/update/voucher")
    public BaseResponse<UserVoucherVO> updateVoucher(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 是否登录
        if(ObjUtil.isEmpty(loginUser)){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 更新凭证
        User user = userService.updateVoucher(loginUser);
        // 更新成功校验
        if(ObjUtil.isEmpty(user.getSecretId()) || ObjUtil.isEmpty(user.getSecretKey())){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新凭证失败");
        }
        UserVoucherVO userVoucherVO = BeanUtil.copyProperties(user, UserVoucherVO.class);
        return ResultUtils.success(userVoucherVO);
    }

    @GetMapping("/get/voucher")
    public BaseResponse<UserVoucherVO> getVoucher(HttpServletRequest request) {
        User user = userService.getVoucher(request);
        UserVoucherVO userVoucherVO = BeanUtil.copyProperties(user, UserVoucherVO.class);
        return ResultUtils.success(userVoucherVO);
    }

    @PostMapping("/email/sendVerificationCode")
    public BaseResponse<Boolean> sendVerificationCode(@RequestBody UserSendEmailRequest userSendEmailRequest, HttpServletRequest request) {
        String email = userSendEmailRequest.getEmail();
        // 发送验证码
        return ResultUtils.success(userService.sendEmailVerificationCode(email));
    }

    @PostMapping("/update/password")
    public BaseResponse<Boolean> updatePassword(@RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(userUpdatePasswordRequest), ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        // 是否登录
        if(ObjUtil.isEmpty(loginUser)){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return ResultUtils.success(userService.updatePassword(userUpdatePasswordRequest,loginUser.getId()));
    }

    @PostMapping("/email/bind")
    public BaseResponse<Boolean> bindEmail(@RequestBody UserBindEmailRequest userBindEmailRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(userBindEmailRequest), ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        // 是否登录
        if(ObjUtil.isEmpty(loginUser)){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return ResultUtils.success(userService.bindEmail(userBindEmailRequest,loginUser.getId()));
    }

    @PostMapping("/email/unbind")
    public BaseResponse<Boolean> unbindEmail(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 是否登录
        if(ObjUtil.isEmpty(loginUser)){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return ResultUtils.success(userService.unBindEmail(loginUser.getId()));
    }

    @GetMapping("/sign")
    public BaseResponse<String> sign(HttpServletRequest request){
        return ResultUtils.success(userService.sign(request));
    }
}
