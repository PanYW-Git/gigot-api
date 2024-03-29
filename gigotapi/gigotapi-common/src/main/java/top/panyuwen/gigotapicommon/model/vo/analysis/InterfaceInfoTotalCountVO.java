package top.panyuwen.gigotapicommon.model.vo.analysis;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author PYW
 */
@Data
public class InterfaceInfoTotalCountVO implements Serializable {

    private Long id;

    private String name;

    private Long totalInvokes;
}
