package top.panyuwen.gigotapibackend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    /**
     * 上传图片
     * @param file
     * @return
     * @throws IOException
     */
    String uploadAvatar(MultipartFile file) throws IOException;

    /**
     * 删除图片
     * @return
     */
    boolean deleteFile(String url);

}
