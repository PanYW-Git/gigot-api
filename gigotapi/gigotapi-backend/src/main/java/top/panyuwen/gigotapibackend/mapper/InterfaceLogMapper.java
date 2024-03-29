package top.panyuwen.gigotapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.panyuwen.gigotapicommon.model.entity.InterfaceLog;

/**
* @author PYW
* @description 针对表【interface_log】的数据库操作Mapper
* @createDate 2024-02-22 14:30:24
* @Entity generator.domain.InterfaceLog
*/
@Mapper
public interface InterfaceLogMapper extends BaseMapper<InterfaceLog> {

    /**
     * @Description: 获取调用平均时长（最近1000条）
     * @param:
     * @return:
     * @auther: PYW
     * @createDate: 2023-3-15
     */
    Integer getInterfaceInfoAverageCost();

}




