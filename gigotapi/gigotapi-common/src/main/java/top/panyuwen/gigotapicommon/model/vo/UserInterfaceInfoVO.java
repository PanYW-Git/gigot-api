package top.panyuwen.gigotapicommon.model.vo;

import com.google.gson.Gson;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import top.panyuwen.gigotapicommon.model.entity.UserInterfaceInfo;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息视图
 *
 * @author PYW
 * @from www.panyuwen.top
 */
@Data
public class UserInterfaceInfoVO implements Serializable {

    private final static Gson GSON = new Gson();

    /**
     * id
     */
    private Long id;


    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应参数
     */
    private String responseParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 消费金币
     */
    private Integer payGoldCoin;

    /**
     * 点赞
     */
    private Integer liked;

    /**
     * 请求示例
     */
    private String requestExample;

    /**
     * 调用总次数
     */
    private Long totalInvokes;

    /**
     * 返回格式（如JSON等）
     */
    private String returnFormat;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    private Integer status;

    /**
     * 创建人id
     */
    private String userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 包装类转对象
     *
     * @param userInterfaceInfoVO
     * @return
     */
    public static UserInterfaceInfo voToObj(UserInterfaceInfoVO userInterfaceInfoVO) {
        if (userInterfaceInfoVO == null) {
            return null;
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoVO, userInterfaceInfo);
        return userInterfaceInfo;
    }

    /**
     * 对象转包装类
     *
     * @param userInterfaceInfo
     * @return
     */
    public static UserInterfaceInfoVO objToVo(UserInterfaceInfo userInterfaceInfo) {
        if (userInterfaceInfo == null) {
            return null;
        }
        UserInterfaceInfoVO userInterfaceInfoVO = new UserInterfaceInfoVO();
        BeanUtils.copyProperties(userInterfaceInfo, userInterfaceInfoVO);
        return userInterfaceInfoVO;
    }
}
