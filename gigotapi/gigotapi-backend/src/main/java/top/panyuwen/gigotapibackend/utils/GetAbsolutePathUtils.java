package top.panyuwen.gigotapibackend.utils;

import cn.hutool.core.util.StrUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GetAbsolutePathUtils {
    /**
     * 获取绝对路径方法
     * @param
     * @return 绝对路径
     */
    public static String getAbsolutePathUtil(String path){
        if(StrUtil.isBlank(path)){
            return "";
        }
        Path basePath = Paths.get(""); // 默认为当前工作目录
        Path absolutePath = basePath.resolve(path).toAbsolutePath();
        return absolutePath.toString();
    }
}
