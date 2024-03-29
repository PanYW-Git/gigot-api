package top.panyuwen.gigotapicommon.model.vo.analysis;

import lombok.Data;
import top.panyuwen.gigotapicommon.model.entity.ProductOrderPayoutRank;
import top.panyuwen.gigotapicommon.model.entity.ProductOrderTotalDay;

import java.io.Serializable;
import java.util.List;

/**
 * @author PYW
 */
@Data
public class SalesCardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 每天收入额
     */
    private List<ProductOrderTotalDay> totalDay;

    /**
     * 收入用户排名
     */
    private List<ProductOrderPayoutRank> payoutRank;
}
