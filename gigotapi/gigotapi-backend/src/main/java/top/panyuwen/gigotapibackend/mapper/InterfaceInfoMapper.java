package top.panyuwen.gigotapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.panyuwen.gigotapicommon.model.entity.InterfaceInfo;

/**
* @author PYW
* @description 针对表【interface_info(接口信息)】的数据库操作Mapper
* @createDate 2023-12-05 17:11:19
* @Entity generator.domain.InterfaceInfo
*/
@Mapper
public interface InterfaceInfoMapper extends BaseMapper<InterfaceInfo> {
    /**
    * @Description: 获取接口信息总调用次数
    * @param:
    * @return:
    * @auther: PYW
    * @createDate: 2023-3-05
    */
    Integer getInterfaceInfoTotalInvokesCount();


}




