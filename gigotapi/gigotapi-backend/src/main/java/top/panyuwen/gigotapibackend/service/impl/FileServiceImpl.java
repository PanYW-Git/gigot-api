package top.panyuwen.gigotapibackend.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.panyuwen.gigotapibackend.common.ErrorCode;
import top.panyuwen.gigotapibackend.exception.BusinessException;
import top.panyuwen.gigotapibackend.service.FileService;
import top.panyuwen.gigotapibackend.utils.GetAbsolutePathUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件上传服务器
 *
 * @author PYW
 * @description: 文件上传服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {


    @Value("${gigot-api.upload.avatarUrlFilePath}")
    private String avatarFilePath;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String uploadAvatar(MultipartFile file) throws IOException {
        //获取原始文件名
        String originalFileName = file.getOriginalFilename();
        //获取文件后缀名
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        System.out.println(suffix);

        if (!StrUtil.endWithAny(suffix, ".jpg", ".png", ".jpeg")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请上传图片，仅支持以下图片格式：gif|png|jpg|jpeg|webp|svg|psd|bmp|tif");
        }

        //使用uuid重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;
        log.info("上传文件的原始图片文件名为：{}  根据UUid生成的文件名为：{}", originalFileName, fileName);


        //获取绝对路径
        String absolutePath = GetAbsolutePathUtils.getAbsolutePathUtil(avatarFilePath);
        //存储文件到服务器
        log.info("上传文件存储完整路径为：{}", absolutePath.toString() + File.separator + fileName);
        file.transferTo(new File(absolutePath.toString() + File.separator + fileName));
        return fileName;
    }

    @Override
    public boolean deleteFile(String url) {
        // 判断地址文件夹
        String filePath = urlLocationHandler(url);
        return FileUtil.del(filePath);
    }

    /**
     * 解析地址在哪个文件夹下
     *
     * @param urlForCustorm
     */
    public String urlLocationHandler(String urlForCustorm) {
        String result = null;
        //非空判断
        if (StrUtil.isBlank(urlForCustorm)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "传输的地址为空");
        }

        // 地址转为Url对象
        URL url = null;
        try {
            url = new URL(urlForCustorm);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String path = url.getPath();
        String query = url.getQuery();


        // 根据请求路径判断删除的是哪个文件下的数据
        // 头像
        if (path.equals("/api/file/downloadAvatar")) {
            result = avatarFilePath + "/";
        }

        // 路径校验完成后判断result是否为空
        if (StrUtil.isBlank(result)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "路径错误，无法找到文件位置");
        }


        // 获取文件名称
        // 使用正则表达式提取文件名
        Pattern pattern = Pattern.compile("name=([^&]+)");
        Matcher matcher = pattern.matcher(query);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名称参数错误，无法找到文件位置");
        }
        String fileName = matcher.group(1);
        result = result + fileName;
        if (StrUtil.isBlank(result)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "路径错误，无法找到文件位置");
        }

        // 获取文件绝对路径
        result = GetAbsolutePathUtils.getAbsolutePathUtil(result);
        log.debug("解析地址成功！文件绝对路径 result:{}", result);
        return result;
    }
}
