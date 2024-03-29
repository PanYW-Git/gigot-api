package top.panyuwen.gigotapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.panyuwen.gigotapicommon.model.entity.User;

/**
 * 用户数据库操作
 *
 * @author PYW
 * @from www.panyuwen.top
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




