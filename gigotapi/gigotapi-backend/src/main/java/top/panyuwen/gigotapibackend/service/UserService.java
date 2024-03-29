package top.panyuwen.gigotapibackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.panyuwen.gigotapibackend.common.request.DeleteListRequest;
import top.panyuwen.gigotapibackend.model.dto.user.*;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import top.panyuwen.gigotapicommon.model.entity.User;
import top.panyuwen.gigotapicommon.model.vo.LoginUserVO;
import top.panyuwen.gigotapicommon.model.vo.LoginXcxQrVO;
import top.panyuwen.gigotapicommon.model.vo.UserVO;

/**
 * 用户服务
 *
 * @author PYW
 * @from www.panyuwen.top
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户登录（微信开放平台）
     *
     * @param wxOAuth2UserInfo 从微信获取的用户信息
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 更新用户的 api秘钥
     * @param user
     * @return
     */
    User updateVoucher(User user);

    /**
     * 获取用户的 api秘钥
     * @param request
     * @return
     */
    User getVoucher(HttpServletRequest request);

    /**
     * 批量删除
     * @param deleteListRequest
     * @param request
     */
    boolean deleteUserByIds(DeleteListRequest deleteListRequest, HttpServletRequest request);

    /**
     * 添加
     * @param userAddRequest
     * @return
     */
    Long addUser(UserAddRequest userAddRequest);

    /**
     * 更新
     * @param userUpdateRequest
     * @return
     */
    boolean updateUser(UserUpdateRequest userUpdateRequest);

    /**
     * 分页查询
     * @param userQueryRequest
     * @return
     */
    Page<User> listUserByPage(UserQueryRequest userQueryRequest);

    /**
     * 调整金币（增加传输正数，减少传输负数）
     * @param userId
     * @param addGoldCoin
     * @return
     */
    boolean adjustmentGoldCoin(Long userId, Long addGoldCoin);

    /**
     * 更新用户缓存
     * @return
     */
    boolean updateUserCache(Long userId);

    /**
     * 绑定邮箱
     * @param userBindEmailRequest
     * @return
     */
    boolean bindEmail(UserBindEmailRequest userBindEmailRequest, Long userId);

    /**
     * 解绑邮箱
     * @param id
     * @return
     */
    Boolean unBindEmail(Long id);

    /**
     * 发送验证码
     * @param email
     */
    Boolean sendEmailVerificationCode(String email);

    /**
     * 签到
     * @return
     */
    String sign(HttpServletRequest request);


    /**
     * 登录（手机免注册登录）
     * @param userLoginByPhoneRequest
     * @return
     */
    LoginUserVO userLoginByEmail(UserLoginByEmailRequest userLoginByPhoneRequest);

    /**
     * 登录（小程序免注册登录）
     * @param userLoginByXcxRequest
     * @return
     */
    LoginUserVO userLoginByXcx(UserLoginByXcxRequest userLoginByXcxRequest);

    /**
     * 小程序扫码登录校验登录状态
     * @param scene
     * @return
     */
    LoginUserVO getLoginUserXcxCheck(String scene);

    /**
     * 小程序扫码登录创建二维码
     * @return
     */
    LoginXcxQrVO userLoginByXcxCreateQR();

    /**
     * 获取当前用户总数（去掉已删除）
     * @return
     */
    Long getUserCount();

    /**
     * 修改用户名
     * @param user
     * @return
     */
    boolean updateUserName(User user);

    /**
     * 修改密码
     * @param userUpdatePasswordRequest
     * @param id
     * @return
     */
    boolean updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest, Long id);
}
