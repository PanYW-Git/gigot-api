package top.panyuwen.gigotapicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 
 * @TableName interface_log
 */
@TableName(value ="interface_log")
@Data
public class InterfaceLog implements Serializable {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 请求时间
     */
    private Date requestTime;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 请求地址
     */
    private String requestUrl;

    /**
     * 请求长度（上传流量）
     */
    private Long requestContentLength;

    /**
     * 响应码
     */
    private Integer responseStatusCode;

    /**
     * 响应长度（下载流量）
     */
    private Long responseContentLength;

    /**
     * 请求处理时间
     */
    private Long requestDuration;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户ip
     */
    private String clientIp;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @OrderBy(asc=false)
    private LocalDateTime updateTime;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        InterfaceLog other = (InterfaceLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getInterfaceId() == null ? other.getInterfaceId() == null : this.getInterfaceId().equals(other.getInterfaceId()))
            && (this.getRequestTime() == null ? other.getRequestTime() == null : this.getRequestTime().equals(other.getRequestTime()))
            && (this.getRequestMethod() == null ? other.getRequestMethod() == null : this.getRequestMethod().equals(other.getRequestMethod()))
            && (this.getRequestUrl() == null ? other.getRequestUrl() == null : this.getRequestUrl().equals(other.getRequestUrl()))
            && (this.getRequestContentLength() == null ? other.getRequestContentLength() == null : this.getRequestContentLength().equals(other.getRequestContentLength()))
            && (this.getResponseStatusCode() == null ? other.getResponseStatusCode() == null : this.getResponseStatusCode().equals(other.getResponseStatusCode()))
            && (this.getResponseContentLength() == null ? other.getResponseContentLength() == null : this.getResponseContentLength().equals(other.getResponseContentLength()))
            && (this.getRequestDuration() == null ? other.getRequestDuration() == null : this.getRequestDuration().equals(other.getRequestDuration()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getClientIp() == null ? other.getClientIp() == null : this.getClientIp().equals(other.getClientIp()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getInterfaceId() == null) ? 0 : getInterfaceId().hashCode());
        result = prime * result + ((getRequestTime() == null) ? 0 : getRequestTime().hashCode());
        result = prime * result + ((getRequestMethod() == null) ? 0 : getRequestMethod().hashCode());
        result = prime * result + ((getRequestUrl() == null) ? 0 : getRequestUrl().hashCode());
        result = prime * result + ((getRequestContentLength() == null) ? 0 : getRequestContentLength().hashCode());
        result = prime * result + ((getResponseStatusCode() == null) ? 0 : getResponseStatusCode().hashCode());
        result = prime * result + ((getResponseContentLength() == null) ? 0 : getResponseContentLength().hashCode());
        result = prime * result + ((getRequestDuration() == null) ? 0 : getRequestDuration().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getClientIp() == null) ? 0 : getClientIp().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", interfaceId=").append(interfaceId);
        sb.append(", requestTime=").append(requestTime);
        sb.append(", requestMethod=").append(requestMethod);
        sb.append(", requestUrl=").append(requestUrl);
        sb.append(", requestContentLength=").append(requestContentLength);
        sb.append(", responseStatusCode=").append(responseStatusCode);
        sb.append(", responseContentLength=").append(responseContentLength);
        sb.append(", requestDuration=").append(requestDuration);
        sb.append(", userId=").append(userId);
        sb.append(", clientIp=").append(clientIp);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}