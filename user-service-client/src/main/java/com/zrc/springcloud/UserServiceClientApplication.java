package com.zrc.springcloud;

import com.zrc.springcloud.api.IUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created with IntelliJ IDEA.
 * User: ZRC
 * Date Time: 2019/8/20 10:34
 * Description: No Description
 */
@SpringBootApplication
@RibbonClient("user-service-provider")
@EnableFeignClients(clients = IUserService.class)
@EnableDiscoveryClient // 激活服务发现客户端
public class UserServiceClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceClientApplication.class, args);
    }
}
