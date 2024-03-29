package top.panyuwen.gigotapicommon.service;

/**
* @author PYW
* @description 针对表【user_interfaceinfo(用户接口调用表)】的数据库操作Service
* @createDate 2023-12-29 10:55:22
*/
public interface InnerUserInterfaceInfoService{

    /**
     * 调调用接口后处理（计数，扣除金币）
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invoke(Long interfaceInfoId,Long userId);


}
