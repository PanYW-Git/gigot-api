package top.panyuwen.gigotapibackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserInterfaceInfoServiceTest {

    @Autowired
    UserInterfaceInfoService userInterfaceInfoService;
    @Test
    void invokeCount() {
        userInterfaceInfoService.invoke(1740293333210652673L,1734836623293214721L);
    }
}