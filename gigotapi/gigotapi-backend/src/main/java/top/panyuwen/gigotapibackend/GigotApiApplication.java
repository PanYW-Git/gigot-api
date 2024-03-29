package top.panyuwen.gigotapibackend;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

/**
 * 主类（项目启动入口）
 *
 * @author PYW
 * @from www.panyuwen.top
 */
@SpringBootApplication()
@MapperScan("top.panyuwen.gigotapibackend.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableDubbo
public class GigotApiApplication {

    public static void main(String[] args) {
        //dubbo缓存报错解决
        ApplicationHome home = new ApplicationHome(GigotApiApplication.class);
        File jarFile = home.getSource();
        String dirPath = jarFile.getParentFile().toString();
        String filePath = dirPath + File.separator + ".dubbo";
        System.out.println(filePath);

        System.setProperty("dubbo.meta.cache.filePath", filePath);
        System.setProperty("dubbo.mapping.cache.filePath",filePath);

        SpringApplication.run(GigotApiApplication.class, args);
    }

}
