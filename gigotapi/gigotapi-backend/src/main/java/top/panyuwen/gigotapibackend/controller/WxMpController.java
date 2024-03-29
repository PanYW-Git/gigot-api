package top.panyuwen.gigotapibackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.panyuwen.gigotapibackend.common.BaseResponse;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.common.ResultUtils;
import top.panyuwen.gigotapibackend.config.WxMpConfig;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.wxmp.WxMpConstant;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts.MenuButtonType;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;

/**
 * 微信公众号相关接口
 *
 * @author PYW
 * @from www.panyuwen.top
 **/
@RestController
@RequestMapping("/wxMp")
@Slf4j
public class WxMpController {

    @Resource
    private WxMpService wxMpService;

    @Resource
    private WxMpMessageRouter router;

    @Autowired
    private WxMpConfig wxMpConfig;

    @PostMapping("/receiveMessage")
    public void receiveMessage(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        // 校验消息签名，判断是否为公众平台发的消息
        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            response.getWriter().println("非法请求");
        }
        // 加密类型
        String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ? "raw"
                : request.getParameter("encrypt_type");
        // 明文消息
        if ("raw".equals(encryptType)) {
            return;
        }
        // aes 加密消息
        if ("aes".equals(encryptType)) {
            // 解密消息
            String msgSignature = request.getParameter("msg_signature");
            WxMpXmlMessage inMessage = WxMpXmlMessage
                    .fromEncryptedXml(request.getInputStream(), wxMpService.getWxMpConfigStorage(), timestamp,
                            nonce,
                            msgSignature);
            log.info("message content = {}", inMessage.getContent());
            // 路由消息并处理
            WxMpXmlOutMessage outMessage = router.route(inMessage);
            if (outMessage == null) {
                response.getWriter().write("");
            } else {
                response.getWriter().write(outMessage.toEncryptedXml(wxMpService.getWxMpConfigStorage()));
            }
            return;
        }
        response.getWriter().println("不可识别的加密类型");
    }

    @GetMapping("/check")
    public String check(String timestamp, String nonce, String signature, String echostr) {
        log.info("check");
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        } else {
            return "";
        }
    }

    /**
     * 微信扫码登录
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/wxLogin")
    public BaseResponse<String> wxLogin(@RequestBody HttpServletResponse response){
        // 微信登录回调地址
        String redirectUrl = null;
        try {
            redirectUrl = URLEncoder.encode("https://6c0f-125-80-245-26.ngrok-free.app/notify", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
        // 构造二维码链接地址
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid="+wxMpConfig.getAppId()+"" +
                "&redirect_uri=" +redirectUrl+
                "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
        String url1 = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx53d6736cc0ef2401&redirect_uri=https://6c0f-125-80-245-26.ngrok-free.app/notify&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
        return ResultUtils.success(url);
    }

    /**
     * 设置公众号菜单
     *
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/setMenu")
    public String setMenu() throws WxErrorException {
        log.info("setMenu");
        WxMenu wxMenu = new WxMenu();
        // 菜单一
        WxMenuButton wxMenuButton1 = new WxMenuButton();
        wxMenuButton1.setType(MenuButtonType.VIEW);
        wxMenuButton1.setName("主菜单一");
        // 子菜单
        WxMenuButton wxMenuButton1SubButton1 = new WxMenuButton();
        wxMenuButton1SubButton1.setType(MenuButtonType.VIEW);
        wxMenuButton1SubButton1.setName("跳转页面");
        wxMenuButton1SubButton1.setUrl(
                "https://yupi.icu");
        wxMenuButton1.setSubButtons(Collections.singletonList(wxMenuButton1SubButton1));

        // 菜单二
        WxMenuButton wxMenuButton2 = new WxMenuButton();
        wxMenuButton2.setType(MenuButtonType.CLICK);
        wxMenuButton2.setName("点击事件");
        wxMenuButton2.setKey(WxMpConstant.CLICK_MENU_KEY);

        // 菜单三
        WxMenuButton wxMenuButton3 = new WxMenuButton();
        wxMenuButton3.setType(MenuButtonType.VIEW);
        wxMenuButton3.setName("主菜单三");
        WxMenuButton wxMenuButton3SubButton1 = new WxMenuButton();
        wxMenuButton3SubButton1.setType(MenuButtonType.VIEW);
        wxMenuButton3SubButton1.setName("编程学习");
        wxMenuButton3SubButton1.setUrl("https://yupi.icu");
        wxMenuButton3.setSubButtons(Collections.singletonList(wxMenuButton3SubButton1));

        // 设置主菜单
        wxMenu.setButtons(Arrays.asList(wxMenuButton1, wxMenuButton2, wxMenuButton3));
        wxMpService.getMenuService().menuCreate(wxMenu);
        return "ok";
    }
}
