# Spring Cloud 案例



### 新建项目

File -> New Project -> Spring Initializer -> 选择 Web / Actuator 

删除 `src` 文件夹，本案例以模块的方式开发

`pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.zrc</groupId>
    <artifactId>spring-cloud-tutorial2</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <modules>
        <module>user-api</module>
    </modules>
    <name>spring-cloud-tutorial2</name>
    <description>Demo project for Spring Boot</description>

    <packaging>pom</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <maven.compiler.source>${java.version}</maven.compiler.source>
        <maven.compiler.target>${java.version}</maven.compiler.target>
        <spring-cloud.version>Greenwich.SR2</spring-cloud.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- spring boot 依赖-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>2.1.7.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!-- spring cloud 依赖-->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Maven Java 编译器插件-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```



### 接口模块 user-api

File -> New Module (`user-api`)-> 普通的Maven项目即可

`pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>spring-cloud-tutorial2</artifactId>
        <groupId>com.zrc</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>user-api</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-feign</artifactId>
            <version>1.4.7.RELEASE</version>
        </dependency>
    </dependencies>
</project>
```

`IUserService`

```java
package com.zrc.springcloud.api;

import com.zrc.springcloud.domain.User;
import com.zrc.springcloud.fallback.UserServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name="${user.service.name}",fallback = UserServiceFallback.class)
public interface IUserService {
    /**
     * 保存用户
     *
     * @param user
     * @return
     */
    @PostMapping("/user/save")
    boolean saveUser(User user);


    /**
     * 查询所有的用户列表
     *
     * @return non-null
     */
    @GetMapping("/user/find/all")
    List<User> findAll();
}
```

### Eureka Sever 模块

```properties
## Spring Cloud Eureka 服务器应用名称
spring.application.name = eureka-server

## Spring Cloud Eureka 服务器服务端口
server.port = 10000

## Spring Cloud Eureka 服务器作为注册中心
## 通常情况下，不需要再注册到其他注册中心去
## 同时，它也不需要获取客户端信息
### 取消向注册中心注册
eureka.client.register-with-eureka = false
### 取消向注册中心获取注册信息（服务、实例信息）
eureka.client.fetch-registry = false
## 解决 Peer / 集群 连接问题
eureka.instance.hostname = localhost
eureka.client.serviceUrl.defaultZone = http://${eureka.instance.hostname}:${server.port}/eureka
```

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```



访问地址 查看注册列表

http://localhost:10000/

### 服务提供模块  user-service-provider

```java
@Service("inMemoryUserService")
public class InMemoryUserService implements IUserService {
    private Map<Long, User> repository = new ConcurrentHashMap<>();

    @Override
    public boolean saveUser(User user) {
        return repository.put(user.getId(), user) == null;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList(repository.values());
    }
}
```

```java
@RestController
public class UserServiceProviderController implements IUserService{
    @Autowired
    @Qualifier("inMemoryUserService") // 实现 Bean ： InMemoryUserService
    private IUserService userService;


    @Override
    public boolean saveUser(User user) {
        return userService.saveUser(user);
    }

    @Override
    public List<User> findAll() {
        return userService.findAll();
    }
}
```

```properties
## 用户服务提供方应用信息
spring.application.name = user-service-provider

## 服务端口
server.port = 9090
```

```java
@SpringBootApplication
@EnableDiscoveryClient // 激活服务发现客户端
public class UserServiceProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceProviderApplication.class, args);
    }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>spring-cloud-tutorial2</artifactId>
        <groupId>com.zrc</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>user-service-provider</artifactId>
    <dependencies>
        <dependency>
            <groupId>com.zrc</groupId>
            <artifactId>user-api</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>

        <!-- 依赖 Spring Cloud Netflix Eureka -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
            <version>1.4.7.RELEASE</version>
        </dependency>
    </dependencies>

</project>
```

启动模块测试

用Postman 提交数据 http://localhost:9090/user/save

```JSON
{
"id":10001,
"name":"ZRC"
}
```

查询列表：http://localhost:9090/user/find/all

```json
[
	{
		id: 10001,
		name: "ZRC"
	}
]
```

### 客户端模块 user-service-client （Ribbion+Feign）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>spring-cloud-tutorial2</artifactId>
        <groupId>com.zrc</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>user-service-client</artifactId>
    <dependencies>
        <dependency>
            <groupId>com.zrc</groupId>
            <artifactId>user-api</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>

        <!-- 依赖 Ribbon -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-ribbon</artifactId>
            <version>1.4.7.RELEASE</version>
        </dependency>

        <!-- 依赖 Spring Cloud Netflix Eureka -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
            <version>1.4.7.RELEASE</version>
        </dependency>
    </dependencies>


</project>
```

```properties
## 服务端口
server.port = 8080

## 用户 Ribbon 客户端应用
spring.application.name = user-service-client

## 提供方服务名称
provider.service.name = user-service-provider
## 提供方服务主机
provider.service.host = localhost
## 提供方服务端口
provider.service.port = 9090
## 配置 @FeignClient(name = "${user.service.name}") 中的占位符
## user.service.name 实际需要制定 UserService 接口的提供方
## 也就是 user-service-provider，可以使用 ${provider.service.name} 替代
user.service.name = ${provider.service.name}

## Spring Cloud Eureka 客户端 注册到 Eureka 服务器
eureka.client.serviceUrl.defaultZone = http://localhost:10000/eureka
```

```java
@SpringBootApplication
@RibbonClient("user-service-provider")
@EnableFeignClients(clients = IUserService.class)
@EnableDiscoveryClient // 激活服务发现客户端
public class UserServiceClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceClientApplication.class, args);
    }
}

```

```java
@RestController
public class UserServiceClientController implements IUserService {
    @Autowired
    private IUserService userService;

    // 通过方法继承，URL 映射 ："/user/save"
    @Override
    public boolean saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // 通过方法继承，URL 映射 ："/user/find/all"
    @Override
    public List<User> findAll() {
        return userService.findAll();
    }
}

```

这样 user 就通过 Ribbon 以及 Eureka注册 完成了对 user-service-provider 的调用

查看 http://localhost:10000/ 可以发现2个均已注册：

user-service-client 【8080】+ user-service-provider【9090】

查看 两个地址返回的结果相同，当多个 user-service-provider 启动（端口不同 application name相同）时可通过Ribbon做到负载均衡，ribbon是客户端负载均衡

http://localhost:9090/user/find/all

http://localhost:8080/user/find/all

用postman 提交数据到 9090 或者 8080 均有效

http://localhost:8080/user/save

http://localhost:9090/user/save

本案例用的是内存数据库，因此可以再开一个 服务提供方

端口：9091

IDEA 配置启动参数  勾选 Allow parallel run ，然后启动一个后，修改配置文件端口再启动一个

查看 Eureka 应用列表如下：http://localhost:10000/ 

| Application               | AMIs       | Availability Zones | Status                                                       |
| :------------------------ | :--------- | :----------------- | :----------------------------------------------------------- |
| **USER-SERVICE-CLIENT**   | **n/a**(1) | (1)                | **UP** (1) - [localhost:user-service-client:8080](http://localhost:8080/actuator/info) |
| **USER-SERVICE-PROVIDER** | **n/a**(2) | (2)                | **UP** (2) - [localhost:user-service-provider:9091](http://localhost:9091/actuator/info) , [localhost:user-service-provider:9090](http://localhost:9090/actuator/info) |

然后通过postman 向 8080保存一个用户

查看服务

http://localhost:9090/user/find/all  -- 有用户

http://localhost:9091/user/find/all -- 无用户

http://localhost:8080/user/find/all  -- 有时候有，有时候没有，说明负载均衡有效，有时候连9090有时候9091

多次提交发现 有的用户提交到了9090，有的则提交到了 9091 也再次证明了负载均衡

![1566278518141](1566278518141.png)