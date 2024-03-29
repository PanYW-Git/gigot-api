package top.panyuwen.gigotapiinterface.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.panyuwen.gigotapiclientsdk.client.GigotApiClient;
import top.panyuwen.gigotapiclientsdk.model.entity.User;

@SpringBootTest
class APIClientTest {

    @Autowired
    private GigotApiClient gigotApiClient;

    @Test
    void APIClientTest(){
        User user = new User();
        user.setName("潘誉文");

    }
}