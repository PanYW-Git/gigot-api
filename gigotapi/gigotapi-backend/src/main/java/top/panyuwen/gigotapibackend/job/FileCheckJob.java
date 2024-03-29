package top.panyuwen.gigotapibackend.job;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.panyuwen.gigotapibackend.service.UserService;
import top.panyuwen.gigotapibackend.utils.GetAbsolutePathUtils;
import top.panyuwen.gigotapibackend.utils.RedissonLockUtil;
import top.panyuwen.gigotapicommon.model.entity.User;

import javax.annotation.Resource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static top.panyuwen.gigotapibackend.constant.UserConstant.DEFAULT_XCX_QRCODE_PATH;

/**
 * 清理文件定时任务
 * @author PYW
 */
@Slf4j
@Component
public class FileCheckJob {

    @Resource
    RedissonLockUtil redissonLockUtil;

    @Autowired
    private UserService userService;

    @Value("${gigot-api.upload.avatarUrlFilePath}")
    private String avatarUrlFilePath;

    /**
     * 每天晚上四点清理文件
     * 秒、分、时、日、月和星期
     */
    @Scheduled(cron = "00 00 4 * * *")
    public void cleanFile(){
        redissonLockUtil.redissonDistributedLocks("lock:cleanFile", () -> {
            log.info("开始删除微信小程序扫码登录图片====>");
            deleteWxXcxQrCode();
            log.info("开始删除未使用头像====>");
            deleteAvatarUrl();
            log.info("文件清理完成！");
        });
    }


    /**
     * 删除头像
     * @return
     */
    public void deleteAvatarUrl() {

        String absolutePath = GetAbsolutePathUtils.getAbsolutePathUtil(avatarUrlFilePath);

        File folder = new File(absolutePath);

        //获取文件夹下的所有文件
        File[] files = folder.listFiles();
        List<String> fileNameLsit = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        System.out.println("fileNameLsit:"+ fileNameLsit);

        // 对比数据库中的头像 每批比对的数据
        int batchSize = 0;
        boolean flag = true;

        while (flag){
            try {
                List<User> userList = userService.list(new QueryWrapper<User>().last("LIMIT " + batchSize + ",1000"));
                if(batchSize == 0 && userList.size() == 0){
                    break;
                }
                if(userList.isEmpty()){
                    flag = false;
                }
                List<String> avatarFileNameList = userList.stream().map(user -> {
                    String userAvatar = user.getUserAvatar();
                    try {
                        URL url = new URL(userAvatar);
                        String query = url.getQuery();
                        int index = query.indexOf("=");
                        if (index == -1) {
                            return "";
                        }
                        return query.substring(index + 1);
                    } catch (MalformedURLException e) {
                        log.info("无法解析头像文件名:{}", userAvatar);
                        return "";
                    }
                    // 解析头像
                }).collect(Collectors.toList());
                // 对比文件和数据库中的数据

                for (String avatarFileName : avatarFileNameList) {
                    for (int i = 0; i < fileNameLsit.size(); i++) {
                        log.info("fileNameLsit:{}  avatarFileName:{}",fileNameLsit.get(i),avatarFileName);
                        if(fileNameLsit.get(i).equals(avatarFileName)){
                            fileNameLsit.remove(i);
                        }
                    }
                }
                batchSize += 1000;
            } catch (Exception e){
                batchSize += 1000;
                continue;
            }
        }

        if (fileNameLsit.size() > 0){
            for (String fileName : fileNameLsit) {
                FileUtil.del(absolutePath + File.separator + fileName);
            }

        }
    }

    /**
     * 删除微信小程序扫码登录图片
     * @return
     */
    private void deleteWxXcxQrCode() {

        // 定义要删除文件的文件夹路径
        String absolutePath = GetAbsolutePathUtils.getAbsolutePathUtil(DEFAULT_XCX_QRCODE_PATH);

        // 获取当前时间
        Date currentTime = new Date();

        // 计算十五分钟之前的时间
        long fifteenMinutesAgo = currentTime.getTime() - 15 * 60 * 1000;

        // 创建 File 对象表示文件夹
        File folder = new File(absolutePath);

        // 获取文件夹下的所有文件
        File[] files = folder.listFiles();

        // 遍历文件夹下的所有文件
        if (files != null) {
            for (File file : files) {
                // 如果文件最后修改时间早于十五分钟之前的时间，则删除文件
                if (file.lastModified() < fifteenMinutesAgo) {
                    FileUtil.del(file);
                    System.out.println("Deleted file: " + file.getName());
                }
            }
        }
    }
}
