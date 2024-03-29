package top.panyuwen.gigotapicommon.model.vo.analysis;

import lombok.Data;
import top.panyuwen.gigotapicommon.model.entity.InterfaceLogWeekCount;
import top.panyuwen.gigotapicommon.model.entity.ProductOrderPayoutRank;
import top.panyuwen.gigotapicommon.model.entity.ProductOrderTotalDay;

import java.io.Serializable;
import java.util.List;

/**
 * @author PYW
 */
@Data
public class TopInterfaceInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接口调用数（一周）
     */
    private List<InterfaceLogWeekCount> interfaceLogWeekCounts;

    /**
     * 接口调用排行（）
     */
    private List<InterfaceInfoTotalCountVO> interfaceInfoTotalCount;

    /**
     * 最受欢迎
     */
    private String mostPopular;
}
