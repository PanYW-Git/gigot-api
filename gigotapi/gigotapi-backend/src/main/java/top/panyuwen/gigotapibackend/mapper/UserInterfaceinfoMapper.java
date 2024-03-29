package top.panyuwen.gigotapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.panyuwen.gigotapicommon.model.entity.UserInterfaceInfo;

/**
* @author PYW
* @description 针对表【user_interfaceinfo(用户接口调用表)】的数据库操作Mapper
* @createDate 2023-12-29 10:55:22
* @Entity generator.domain.UserInterfaceinfo
*/
@Mapper
public interface UserInterfaceinfoMapper extends BaseMapper<UserInterfaceInfo> {

}




