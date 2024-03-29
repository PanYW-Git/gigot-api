package top.panyuwen.gigotapitest;

import top.panyuwen.gigotapiclientsdk.client.GigotApiClient;
import top.panyuwen.gigotapiclientsdk.exception.GigotApiException;
import top.panyuwen.gigotapiclientsdk.model.response.LoveTalkResponse;
import top.panyuwen.gigotapiclientsdk.model.response.UserResponse;

import javax.annotation.Resource;



public class Main {

    @Resource
    private GigotApiClient gigotApiClient;

    public static void main(String[] args) throws GigotApiException {
        GigotApiClient gigotApiClient = new GigotApiClient("4a81da1bed82d415a7ff8320e7e35db2","123b8fad45c913b1a526ee7bae9ee776");
        LoveTalkResponse loveTalk = gigotApiClient.getLoveTalk();
        System.out.println(loveTalk);
    }

    public void getNameTest(){

    }


}
