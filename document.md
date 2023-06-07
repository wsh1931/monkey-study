1. [前言](#preview)
 2. [项目介绍](#introduce)
 3. [统一返回结果](#ResultVo)
 4. [登录功能实现](#login)
 5. [发布博客界面实现](#publishBlog)
    5.1 [集成markdown编辑器](#markdown)
    5.2  [创建Maven聚合工程](#createreunite)

 	 5.2.1 [遇到的坑点](#mavenTrap)
 5.3 [集成阿里云OSS实现图片上传](#aliyunOss)  
2. [实现用户聊天功能](#userChat)
6.1 [数据库实现](#userChatDataBase)
6.2 [后端代码实现](#userChatBackend)
6.3 [前端代码实现](#userChatWeb)
 [遇到的坑点](#trap)	
<div id="preview">

## 前言
#### 这个博客只记录在开发过程中遇到的不会的知识点
简单介绍一个写这个博客的目的。
因为之前学开发都是学完所需的知识点再去做项目，但是这时候在做项目的过程中发现以前学过的全忘了，所以为了减少这种情况，我打算以后通过项目学习技术，说的直接点就是，项目中需要用到哪些技术，那我就去学哪些技术，并用到此项目中。
</div>

<div id = "introduce">

## 项目介绍

这是一个SpringBoot + Vue 的分布式前后端项目，具体技术边学边用，因此等此项目完结了，这个项目介绍也就完结了。
目前用到的技术：
数据库：MySql
前端: Vue, Element-ui, LocalStorge, 
后端：Spring, SpringMvc, Mybatis-Plus, SptingBoot, SpringSecurity
其他：阿里云OSS, MavonEditor, WebSocket
</div>

# 统一返回结果
<div id = "ResultVo">

```java
package com.monkey.monkeybackend.utils.result;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO {
    private int code;
    private String msg;
    private Object data;
}

```

```java
package com.monkey.monkeybackend.utils.result;

public class ResultStatus {

    public static final int OK=10000;

    public static final int NO=10001; // 添加购物车失败

}


```

</div>

<div id = "login">
登录功能我选则的是Jwt_Token实现，将生成的Token存到本地游览器LocalStorge中
下面介绍Token实现的流程
<img src="https://monkey-blog.oss-cn-beijing.aliyuncs.com/picture/Token.png"></img>

几个实现登录的工具类
先引入依赖
```swift
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
            <version>2.7.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>

        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        
```

## 1：检测Token是否过期工具类

```java
package com.monkey.monkeybackend.config.SpringSecurity;

import com.monkey.monkeybackend.Mapper.User.UserMapper;
import com.monkey.monkeybackend.Pojo.user.User;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 从哪里读取token
        String token = request.getHeader("Authorization");

        // token以Bearer开头
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);

        String userid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        User user = userMapper.selectById(Integer.parseInt(userid));

        if (user == null) {
            throw new RuntimeException("用户名未登录");
        }

        UserDetailsImpl loginUser = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, null);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
```

## 2： 生成Token类

```java
package com.monkey.monkeybackend.config.SpringSecurity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    public static final long JWT_TTL = 60 * 60 * 1000L * 24 * 14;  // 有效期14天
    public static final String JWT_KEY = "SDFGjhdsfalshdfHFdsjkdsfds121232131afasdfac";

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (ttlMillis == null) {
            ttlMillis = JwtUtil.JWT_TTL;
        }

        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)
                .setSubject(subject)
                .setIssuer("sg")
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(expDate);
    }

    public static SecretKey generalKey() {
        byte[] encodeKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        return new SecretKeySpec(encodeKey, 0, encodeKey.length, "HmacSHA256");
    }

    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}
```

## 3：路径拦截器

```java
package com.monkey.monkeybackend.config.SpringSecurity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/user/login", "/user/register", "/user/getUserInfoBytoken").permitAll()
                .antMatchers("/blog/article/getArticleContentByLabelId", "/blog/article/pagination",
                        "/blog/article/fireRecently").permitAll()
                .antMatchers("/blog/label/getLabelList").permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

}

```
## 4：得到用户信息工具类

```java
package com.monkey.monkeybackend.config.SpringSecurity;

import com.monkey.monkeybackend.Pojo.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/*
 * 通过从数据库中查到的用户名和密码判断该用户是否合格
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private User user;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

```
## 5：判断账号账号密码是否存在
```java
package com.monkey.monkeybackend.config.SpringSecurity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeybackend.Mapper.User.UserMapper;
import com.monkey.monkeybackend.Pojo.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);
        User user = userMapper.selectOne(userQueryWrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return new UserDetailsImpl(user);
    }
}
```


用户注册实现
```java
// 用户注册
    @Override
    public ResultVO userRegister(Map<String, String> userInfo) {
        String username = userInfo.get("username");
        String password = userInfo.get("password");
        String confirePassword = userInfo.get("confirePassword");

        username = username.trim(); // 删除首位空白字符
        if (username.length() == 0) {
            return new ResultVO(ResultStatus.NO, "用户名不能为空", null);
        }

        if (password == null || password.length() == 0) {
            return new ResultVO(ResultStatus.NO, "密码不能为空", null);
        }

        if (username.length() > 20) {
            return new ResultVO(ResultStatus.NO, "用户名长度不能大于20", null);
        }

        if (password.length() > 20) {
            return new ResultVO(ResultStatus.NO, "密码长度不能大于20", null);
        }

        if (!password.equals(confirePassword)) {
            return new ResultVO(ResultStatus.NO, "两次密码不一致", null);
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);
        Long selectCount = userMapper.selectCount(userQueryWrapper);
        if (selectCount > 0) {
            return new ResultVO(ResultStatus.NO, "该用户名已存在，请重新输入", null);
        }

        String encode = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/246711_md_08990849f1.png";
        User user = new User();
        user.setPassword(encode);
        user.setPhoto(photo);
        user.setUsername(username);
        user.setRegisterTime(new Date());
        int insert = userMapper.insert(user);
        if (insert > 0) {
            return new ResultVO(ResultStatus.OK, "注册成功", null);
        }

        return new ResultVO(ResultStatus.OK, "注册失败", null);
    }
```
## 用户登录实现
```java
// 用户登录
    @Override
    public ResultVO userLogin(Map<String, String> userInfo) {
        String username = userInfo.get("username");
        String password = userInfo.get("password");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = // 将用户名与密码封装成一个加密之后的字符串
                new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken); // 登录失败自动处理

        UserDetailsImpl userDetails = (UserDetailsImpl) authenticate.getPrincipal();
        User user = userDetails.getUser();

        String token = JwtUtil.createJWT(user.getId().toString());

        return new ResultVO(ResultStatus.OK, "登录成功", token);
    }
```
## 用户登录前端实现
LoginViews.vue

```swift
loginUser() {
            const vue = this;
            store.dispatch("login", {
                username: this.userInformation.username,
                password: this.userInformation.password,
                success() {
                    store.dispatch("getUserInfoBytoken", {
                        success() {   
                            vue.$modal.msgSuccess("登录成功");
                            router.push({
                                name: "home",
                            });
                        },
                        error() {
                            vue.$modal.msgError("登录失败");
                        }
                    })
                },

                error(response) {
                    vue.$modal.msgError(response.msg)
                }
                
            })
        }
```

store.user.js
```swift
login(context, data) {
            $.ajax({
                url: "http://localhost:4000/user/login",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password
                },
                success(response) {
                    if (response.code == "10000") {
                        localStorage.setItem("token", response.data);
                        context.commit("updateToken", response.data);
                        data.success(response);
                    } else {
                        data.error(response);
                    }
                },
                error(response) {
                    data.error(response);
                }
            })
        },

        // 通过token得到用户信息
        getUserInfoBytoken(context, data) {
            $.ajax({
                url: "http://localhost:4000/user/getUserInfoBytoken",
                type: "get",
                headers: {
                    Authorization: "Bearer " + context.state.token,
                },
                success(response) {
                    console.log(response)
                    if (response.code == "10000") {
                        context.commit("updateUserInfo", {
                            ...response.data,
                            is_login: true,
                        });
                        if (data != null) data.success(response)
                    } else {
                        if (data != null) data.error(response);
                    }
                },
                errror(response) {
                    data.error(response);
                }
            })
        },
```

</div>

<div id="publishBlog">

## 5. 发布博客界面实现
<div id="markdown">
### 5.1 集成markdown编辑器

```
1：安装mavonEditor包
npm install mavon-editor --s
```
```
2：导入并使用mavonEditor
在需要使用Markdown的Vue组件导入mavonEditor
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
```
```
3：使用组件
components: {
        mavonEditor 
    },
```
```
4: 使用组件
<mavon-editor v-model="ruleForm.content" ></mavon-editor>
```
5： 效果如下
![](https://img-blog.csdnimg.cn/img_convert/335fbe86ece1ce6dbacce764f797c545.png#pic_center)
</div>

### 5.2 创建maven聚合工程
<div id = "createreunite">
<div id="mavenTrap">

#### 坑点
```
1: 所有的maven聚合工程的父项目pom文件中的packing属性必需是pom, 
子模块中的packing是jar类型的，不然在target中找不到application.properties配置类
```

</div>
</div>

<div id = "aliyunOss">

### 1: 阿里云OSS的作用：
```
两个用户之间不能直接访问到对方电脑中的图片，文件，视频，以及前端默认不能访问本地图片，文件，
视频，所以我们可以把这些信息都存到一个公共的地方存储，便于用户共同访问。
```
## 2： 阿里云OSS的使用
### 后端实现 
#### 1: 创建service-oss maven类型模块
![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/4f00bd3838cdbdb8108fe85f6b91210b.png#pic_center)
####  2: 编写配置文件
```
#服务端口
server.port=5000
#服务名
spring.application.name=service-oss

#环境设置：dev、test、prod
spring.profiles.active=dev

#阿里云 OSS
#不同的服务器，地址不同
aliyun.oss.file.endpoint=oss-cn-beijing.aliyuncs.com
aliyun.oss.file.keyid=你的keyid // 注意这几个字段前面后面都不能存在空格
aliyun.oss.file.keysecret=你的keysecret
#bucket可以在控制台创建，也可以使用java代码创建
aliyun.oss.file.bucketname=monkey-blog
```
#### 3: 编写启动类
![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/016b9516023d6c43379d9e4d50356612.png#pic_center)
#### 4：增加一个工具类，读取配置文件中的内容
```java
package com.monkey.monkeyoss.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// 常量属性读取的配置类

@Component
public class ConstantPropertiesUtlis implements InitializingBean {
    // 读取配置文件内容
    @Value("${aliyun.oss.file.endpoint}")
    private String endPoint;
    @Value("${aliyun.oss.file.keyid}")
    private String keyId;
    @Value("${aliyun.oss.file.keysecret}")
    private String keySecret;
    @Value("${aliyun.oss.file.bucketname}")
    private String bucketName;

    // 定义公开静态常量，方便外面的类调用
    public static String END_POINT;
    public static String KEY_ID;
    public static String KER_SECRET;
    public static String BUCKET_NAME;
    /*
     在初始化的时候执行这个方法
     因为属性类型是private，所以当项目启动，Spring加载之后，执行接口的一个方法，读取字段值
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        END_POINT = endPoint;
        KEY_ID = keyId;
        KER_SECRET = keySecret;
        BUCKET_NAME = bucketName;
    }
}

```
### Controller
```java
package com.monkey.monkeyoss.controller;

import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyoss.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping("/monkeyoss")
public class OssController {
    @Autowired
    private OssService ossService;

    // 上传文章封面的方法
    @PostMapping("/upload")
    public ResultVO uploadOssFile(@RequestParam("file") MultipartFile picture,
                                  @RequestParam("module") String module) {
        // 获取图片
        // 返回上传到阿里云oss的路径
        String url = ossService.uploadFile(picture, module);
        return new ResultVO(ResultStatus.OK, null, url);
    }

    // 删除文件的方法
    @DeleteMapping("/remove")
    public ResultVO removeFile(@RequestParam("fileUrl") String fileUrl) {
        return ossService.removeFile(fileUrl);
    }
}

```
### ServiceImpl代码
```java
package com.monkey.monkeyoss.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyoss.service.OssService;
import com.monkey.monkeyoss.utils.ConstantPropertiesUtlis;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.DocFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
    // 上传文件到阿里云oss, 并返回阿里云图片存储
    @Override
    public String uploadFile(MultipartFile file, String module) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = ConstantPropertiesUtlis.END_POINT;
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ConstantPropertiesUtlis.KEY_ID;
        String accessKeySecret = ConstantPropertiesUtlis.KER_SECRET;
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ConstantPropertiesUtlis.BUCKET_NAME;
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "exampledir/exampleobject.txt";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        String filePath= "D:\\localpath\\examplefile.txt";


        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 获取上传文件的输入流
            InputStream inputStream = file.getInputStream();
            // 获取文件名称
            String filename = file.getOriginalFilename();
            String name = file.getName();
            // 保证文件名不重复
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            // 1: 通过 uuid生成随机值
            filename = uuid +  filename;
            // 2: 通过日期生成路径
            // 获取当前日期
            String dataPath = new DateTime().toString("yyyy/MM/dd");
            filename = module + dataPath + "/" + filename;
            // 创建PutObjectRequest对象。
            /*
            * 第一个参数： Bucket名称
            * 第二个参数：上传到OSS文件的路径或文件名称
            * 第三个参数：上传文件的输入流
            * */
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filename, inputStream);
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);

            // 返回上传到阿里云OSS文件的路径
//            https://monkey-blog.oss-cn-beijing.aliyuncs.com/article/relax.jpg
            String fileUrl = " https://" + bucketName + "." + endpoint + "/" + filename;
            return fileUrl;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        return null;
    }

    // 删除阿里云文件
    @Override
    public ResultVO removeFile(String fileUrl) {
        System.err.println(fileUrl);
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = ConstantPropertiesUtlis.END_POINT;
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ConstantPropertiesUtlis.KEY_ID;
        String accessKeySecret = ConstantPropertiesUtlis.KER_SECRET;
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ConstantPropertiesUtlis.BUCKET_NAME;
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String objectName = fileUrl;
        try {
            // 原图片地址https://monkey-blog.oss-cn-beijing.aliyuncs.com/articlePicture/2023/05/29/c873d24883b44e6ab47b22eb92eaef0d04.png
            // 需要图片地址articlePicture/2023/05/29/02bb26a36e004b19b7a57f5a348f312e03.jpg
            objectName = new URL(objectName).getPath().substring(1);
            objectName = objectName.substring(1);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 删除文件。
            ossClient.deleteObject(bucketName, objectName);
            return new ResultVO(ResultStatus.OK, null, null);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return new ResultVO(ResultStatus.NO, null, null);
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            return new ResultVO(ResultStatus.NO, null, null);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
```
## 前端代码
```html
<el-form-item label="文章封面">
  <el-upload
      style="display: flex;"
      class="upload-box"
      :headers="{ Authorization: 'Bearer ' + $store.state.user.token}"
      action="http://localhost:5000/monkeyoss/upload"
      :on-success="onUploadSuccess"
      :on-remove="onUploadRemove"
      list-type="picture-card"
      :multiple="false"
      :limit="1"
      :data="{module: 'articlePicture/'}"
      >
      <i class="el-icon-plus avatar-uploader-icon"></i>
      </el-upload>            
  </el-form-item>
```
## Method方法
```javascript
// 删除阿里云的文件
        onUploadRemove(file) {
            const vue = this;
            console.log(vue)
            console.log(store.state.user.token);
            $.ajax({
                url: "http://localhost:5000/monkeyoss/remove",
                type: "delete",
                headers: {
                    Authorization: 'Bearer ' + store.state.user.token
                },
                data: {
                    fileUrl: file.response.data
                },
                success(response) {
                    if (response.code == "10000") {
                        vue.$modal.msgSuccess("删除后图片成功");
                        vue.ruleForm.photo = "";
                    } else {
                        vue.$modal.msgError("删除图片失败");
                    }
                },
                error() {
                    vue.$modal.msgError("删除图片失败")
                }
            })
        },
        // 上传成功之后判断上传的图片是否成功
        onUploadSuccess(response) {
            if (response.code == "10000") {
                this.$modal.msgSuccess("上传成功");
                this.ruleForm.photo = response.data; // 得到阿里云中的地址
            } else {
                this.$modal.msgError("上传失败");
            }
        },  
        beforeAvatarUpload(file) {
            const isJPG = file.type === 'image/jpeg';
            const isPng = file.type === 'image/png'
            const isLt2M = file.size / 1024 / 1024 < 2;
            if (!isJPG && !isPng) {
                this.$message.error('上传头像图片只能是 JPG/PNG 格式!');
            }
            if (!isLt2M) {
                this.$message.error('上传头像图片大小不能超过 2MB!');
            }
            return isJPG && isLt2M;
        }, 
        submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            alert('submit!');
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
      }
    }
    }
```

</div>


<div id="trap">
## 遇到的 坑点

```
1: componenet注解会默认覆盖SpringBoot注解。
2: 扫描的mapperScan范围不能包括@Service注解，不然会导致mybatis-plus不能执行BaseMapper中的Sql语句
```

</div>

<div id="userChat">

# 最终效果

![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/41757a2c78d8b8153fee2d8335cb4b27.png#pic_center)


<div id="userChatDataBase">
# 数据库

![用户信息类](https://img-blog.csdnimg.cn/img_convert/a9e408381d7024ec0c8c669d258235c4.png#pic_center)
![聊天信息类](https://img-blog.csdnimg.cn/img_convert/b81fa60bd720d4fc5faefa58a92e11cf.png#pic_center)
</div>
<div id="userChatBackend">

## 后端代码

###  1：VO类
```java
package com.monkey.monkeyblog.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// 聊天用户信息列表
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserChatVo {
    private Long id;
    private Long senderId; // 回复人id
    private String senderPhoto; // 聊天人头像
    private String senderName; // 聊天人姓名
    private String senderBrief; // 聊天人简介
    private String receiverName; // 回复人姓名
    private Long receiverId; // 回复者id
    private String receiverPhoto; // 回复人头像
    private String receiverBrief; // 回复人简介
    private String lastContent; // 最后聊天内容
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date LastCreateTime; // 最后聊天时间
    private Long isLike; // 当前用户是否关注回复人(0表示未关注，1表示已关注)
    private String direction; // 聊天人的方向
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")

    private Date createTime; // 每条记录聊天时间
    private String content; // 每条聊天内容
}
```
## 后端逻辑类
```java
package com.monkey.monkeyblog.service.Impl.chat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.mapper.ChatHistoryMapper;
import com.monkey.monkeyblog.mapper.user.UserFansMapper;
import com.monkey.monkeyblog.pojo.ChatHistory;
import com.monkey.monkeyblog.pojo.Vo.UserChatVo;
import com.monkey.monkeyblog.pojo.user.UserFans;
import com.monkey.monkeyblog.service.chat.WebSocketChatService;
import com.monkey.spring_security.mapper.user.UserMapper;
import com.monkey.spring_security.pojo.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WebSocketChatServiceImpl implements WebSocketChatService {

    @Autowired
    private ChatHistoryMapper chatHistoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserFansMapper userFansMapper;

    //通过当前登录用户登录id得到该用户聊天信息列表（左边）
    @Override
    public ResultVO getReplyUserListByUserId(Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        QueryWrapper<ChatHistory> chatHistoryQueryWrapper = new QueryWrapper<>();
        chatHistoryQueryWrapper.eq("sender_id", userId).or().eq("receiver_id", userId);
        chatHistoryQueryWrapper.orderByDesc("create_time");
        List<ChatHistory> chatHistoryList = chatHistoryMapper.selectList(chatHistoryQueryWrapper);
        // 1: 将sender_id, receiver_id交换排序去重后再按时间降序取出最晚消息发出的时间
        // 1.1 将 sender_id, receiver_id按sender_id在前，receiver_id在后
        Map<Long, Boolean> isSwap = new HashMap<>(); // 判断(sender_id, receiver_id)是否交换过
        Long len = Long.parseLong(String.valueOf(chatHistoryList.size()));
        for (long i = 0; i < len; i ++ ) {
            ChatHistory chatHistory = chatHistoryList.get((int)i);
            Long senderId = chatHistory.getSenderId();
            Long receiverId = chatHistory.getReceiverId();
            if (senderId > receiverId) {
                chatHistory.setSenderId(receiverId);
                chatHistory.setReceiverId(senderId);
                chatHistoryList.set((int) i, chatHistory);
                isSwap.put(i, true);
            }
        }

        // 找出时间最晚的一条记录，将多余的数据去除.
        List<ChatHistory> chatHistories = new ArrayList<>();
        Map<Map.Entry<Long, Long>, Boolean> resList = new HashMap<>();
        for (int i = 0; i < len; i ++ ) {
            ChatHistory chatHistory = chatHistoryList.get(i);
            Long receiverId = chatHistory.getReceiverId();
            Long senderId = chatHistory.getSenderId();
            Map.Entry<Long, Long> key1 = new AbstractMap.SimpleEntry<>(senderId, receiverId);
            if (resList.get(key1) == null || !resList.get(key1)) {
                // 判断之前是否交换过sender_id, receiver_id
                if (isSwap.get(i) != null && isSwap.get(i)) {
                    chatHistories.set(i, chatHistory);
                }
                chatHistories.add(chatHistory);
                resList.put(key1, true);
            }
        }

        List<UserChatVo> userChatVoList = new ArrayList<>();

        // 若当前用户没有向该作者发送过消息，则在数据库填入一个空字段以便在列表左边显示
        long statrReceiverId = Long.parseLong(  data.get("receiverId"));
        QueryWrapper<ChatHistory> chatHistoryQueryWrapper1 = new QueryWrapper<>();
        chatHistoryQueryWrapper1.eq("sender_id", userId).eq("receiver_id", statrReceiverId)
                .or().eq("receiver_id", userId).eq("sender_id", statrReceiverId);
        Long count = chatHistoryMapper.selectCount(chatHistoryQueryWrapper1);
        if (count <= 0) {
            ChatHistory chatHistory = new ChatHistory();
            chatHistory.setSenderId(userId);
            chatHistory.setReceiverId(statrReceiverId);
            chatHistory.setCreateTime(new Date());
            chatHistory.setContent("快开始聊天吧。");
            chatHistoryMapper.insert(chatHistory);
            ChatHistory chatHistory1 = chatHistoryMapper.selectById(chatHistory.getId());
            UserChatVo userChatVo = new UserChatVo();
            Long senderId = chatHistory1.getSenderId();
            Long receiverId = chatHistory1.getReceiverId();

            userChatVo.setId(chatHistory1.getId());
            userChatVo.setLastCreateTime(chatHistory1.getCreateTime());
            userChatVo.setLastContent(chatHistory1.getContent());
            // 通过发送者id得到发送者信息

            User sendUser = userMapper.selectById(senderId);
            userChatVo.setSenderId(senderId);
            userChatVo.setSenderName(sendUser.getUsername());
            userChatVo.setSenderPhoto(sendUser.getPhoto());
            userChatVo.setSenderBrief(sendUser.getBrief());
            // 通过接收者id得到接收者信息

            User receiverUser = userMapper.selectById(receiverId);
            userChatVo.setReceiverName(receiverUser.getUsername());
            userChatVo.setReceiverPhoto(receiverUser.getPhoto());
            userChatVo.setReceiverBrief(receiverUser.getBrief());
            userChatVo.setReceiverId(receiverUser.getId());

            // 判断发送者是否关注了接收者
            QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
            userFansQueryWrapper.eq("fans_id", senderId);
            userFansQueryWrapper.eq("user_id", receiverId);
            Long selectCount = userFansMapper.selectCount(userFansQueryWrapper);
            userChatVo.setIsLike(selectCount);
            userChatVoList.add(userChatVo);
        }

        for (int i = 0; i < chatHistories.size(); i ++ ) {
            UserChatVo userChatVo = new UserChatVo();
            ChatHistory chatHistory = chatHistories.get(i);
            Long senderId = chatHistory.getSenderId();
            Long receiverId = chatHistory.getReceiverId();

            userChatVo.setId(chatHistory.getId());
            userChatVo.setLastCreateTime(chatHistory.getCreateTime());
            userChatVo.setLastContent(chatHistory.getContent());
            // 通过发送者id得到发送者信息

            User sendUser = userMapper.selectById(senderId);
            userChatVo.setSenderId(senderId);
            userChatVo.setSenderName(sendUser.getUsername());
            userChatVo.setSenderPhoto(sendUser.getPhoto());
            userChatVo.setSenderBrief(sendUser.getBrief());
            // 通过接收者id得到接收者信息

            User receiverUser = userMapper.selectById(receiverId);
            userChatVo.setReceiverName(receiverUser.getUsername());
            userChatVo.setReceiverPhoto(receiverUser.getPhoto());
            userChatVo.setReceiverBrief(receiverUser.getBrief());
            userChatVo.setReceiverId(receiverUser.getId());

            // 判断发送者是否关注了接收者
            QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
            userFansQueryWrapper.eq("fans_id", senderId);
            userFansQueryWrapper.eq("user_id", receiverId);
            Long selectCount = userFansMapper.selectCount(userFansQueryWrapper);
            userChatVo.setIsLike(selectCount);
            userChatVoList.add(userChatVo);
        }
        return new ResultVO(ResultStatus.OK, null, userChatVoList);
    }

    // 得到聊天对话框信息（右边）
    @Override
    public ResultVO showChatInformation(Map<String, String> data) {
        long senderId = Long.parseLong(data.get("senderId"));
        long receiverId = Long.parseLong(data.get("receiverId"));
        QueryWrapper<ChatHistory> chatHistoryQueryWrapper = new QueryWrapper<>();
        chatHistoryQueryWrapper.orderByAsc("create_time");
        // 得到双方聊天记录
        chatHistoryQueryWrapper.eq("sender_id", senderId).eq("receiver_id", receiverId)
                .or().eq("receiver_id", senderId).eq("sender_id", receiverId);
        List<ChatHistory> chatHistoryList = chatHistoryMapper.selectList(chatHistoryQueryWrapper);
        List<UserChatVo> userChatVoList = new ArrayList<>();
        for (ChatHistory chatHistory : chatHistoryList) {
            UserChatVo userChatVo = new UserChatVo();
            userChatVo.setId(chatHistory.getId());
            Long receiverId1 = chatHistory.getReceiverId();
            Long senderId1 = chatHistory.getSenderId();
            // 通过senderId, receiverId得到对应信息
            User senderUser = userMapper.selectById(senderId1);
            userChatVo.setSenderBrief(senderUser.getBrief());
            userChatVo.setSenderPhoto(senderUser.getPhoto());
            userChatVo.setSenderId(senderUser.getId());
            userChatVo.setSenderName(senderUser.getUsername());

            User receiverUser = userMapper.selectById(receiverId1);
            userChatVo.setReceiverId(receiverUser.getId());
            userChatVo.setReceiverName(receiverUser.getUsername());
            userChatVo.setReceiverBrief(receiverUser.getBrief());
            userChatVo.setReceiverPhoto(receiverUser.getPhoto());
            userChatVo.setContent(chatHistory.getContent());
            userChatVo.setCreateTime(chatHistory.getCreateTime());
            userChatVoList.add(userChatVo);
        }
        // 因为websocket一次发送的消息有允许发送的消息有大小限制所以每次确定最多发送十条消息

        if (userChatVoList.size() < 10) {
            return new ResultVO(ResultStatus.OK, null, userChatVoList);
        } else {
            List<UserChatVo> res = new ArrayList<>();
            for (int i = userChatVoList.size() - 10; i < userChatVoList.size(); i ++ ) {
                res.add(userChatVoList.get(i));
            }
            return new ResultVO(ResultStatus.OK, null, res);
        }
    }
}

```
## 后端webSocket类
```java
package com.monkey.monkeyblog.service.Impl.chat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyblog.mapper.ChatHistoryMapper;
import com.monkey.monkeyblog.pojo.ChatHistory;
import com.monkey.monkeyblog.pojo.Vo.UserChatVo;
import com.monkey.spring_security.JwtUtil;
import com.monkey.spring_security.mapper.user.UserMapper;
import com.monkey.spring_security.pojo.user.User;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/chat/{token}")  // 注意不要以'/'结尾
public class WebSocketChatServer {

    // 将用户id 映射到每个websocket实例，以便通过用户id找到其对应的websocket
    // static 所有实例访问同一个哈希表
    public static ConcurrentHashMap<Long, WebSocketChatServer> userList = new ConcurrentHashMap<>();

    // 用户从后端向前端发送消息
    private Session session = null;

    private User user;

    private static UserMapper userMapper;

    private static ChatHistoryMapper chatHistoryMapper;

    @Autowired
    public void setChatHistoryMapper(ChatHistoryMapper chatHistoryMapper) {
        WebSocketChatServer.chatHistoryMapper = chatHistoryMapper;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketChatServer.userMapper = userMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        // 建立连接
        System.err.println("chat Connect !! ");
        this.session = session;
        Long senderId = this.getUserIdBytoken(token);
        this.user = userMapper.selectById(senderId);
        if (this.user != null) {
            userList.put(senderId, this);
        } else {
            try {
                this.session.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.err.println(this.user);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        System.err.println("chat messsage!!");
        JSONObject data = JSONObject.parseObject(message);
        String event = (String)data.get("event");
        if ("start_chat".equals(event)) {
            JSONArray messageArray = JSONArray.parseArray(data.getString("message"));
            List<UserChatVo> userChatVoList = this.getListByJSON(messageArray, UserChatVo.class);
            // 判断聊天者方向
            List<UserChatVo> chatVoList = this.judgeDirection(userChatVoList);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", event);
            jsonObject.put("messageList", chatVoList);
            this.sendMessage(jsonObject.toJSONString());
        } else if ("send_message".equals(event)) {
            String dataString = data.getString("message");
            Long receiverId = data.getLong("receiverId");
            // 向发送者和接收者传递消息
            this.sendMessageDeleiverMessage(dataString, receiverId);
        }
    }

    // 通过接收者id发送消息给接收者
    private void sendMessageDeleiverMessage(String dataString, Long receiverId) {
        // 将该消息加入数据库
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setContent(dataString);
        chatHistory.setCreateTime(new Date());
        chatHistory.setReceiverId(receiverId);
        chatHistory.setSenderId(user.getId());
        chatHistoryMapper.insert(chatHistory);
        ChatHistory history = chatHistoryMapper.selectById(chatHistory.getId());

        // 将消息返回前端
        JSONObject jsonObjectReceiver = new JSONObject();
        jsonObjectReceiver.put("event", "receive_message");
        User receiver = userMapper.selectById(receiverId);
        UserChatVo userChatVoReceiver = new UserChatVo();
        userChatVoReceiver.setContent(dataString);
        userChatVoReceiver.setCreateTime(history.getCreateTime());
        userChatVoReceiver.setDirection("左");
        userChatVoReceiver.setReceiverName(receiver.getUsername());
        userChatVoReceiver.setReceiverPhoto(receiver.getPhoto());
        jsonObjectReceiver.put("information", userChatVoReceiver);
        WebSocketChatServer webSocketChatServer = userList.get(receiverId);
        System.err.println(webSocketChatServer);
        if (webSocketChatServer != null) {
            webSocketChatServer.sendMessage(jsonObjectReceiver.toJSONString());
        }


        JSONObject jsonObjectSender = new JSONObject();
        jsonObjectSender.put("event", "send_message");
        UserChatVo userChatVoSender = new UserChatVo();
        userChatVoSender.setSenderName(this.user.getUsername());
        userChatVoSender.setSenderPhoto(this.user.getPhoto());
        userChatVoSender.setCreateTime(history.getCreateTime());
        userChatVoSender.setDirection("右");
        userChatVoSender.setContent(dataString);
        jsonObjectSender.put("information", userChatVoSender);
        userList.get(this.user.getId()).sendMessage(jsonObjectSender.toJSONString());
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.err.println("chat Close !! ");
        // 关闭连接时删除该用户
        if (this.user != null) {
            userList.remove(this.user.getId());
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    // 后端像前端发送单个信息
    public void sendMessage(String message) {
        System.err.println("返回消息");
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 通过token得到当前用户id
    public Long getUserIdBytoken(String token) {
        Long userId = -1L;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return userId;
    }

    // 将JSON集合转化成JAVA中的实体类
    public <T> List<T> getListByJSON(JSONArray jsonArray, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            T object = JSONObject.toJavaObject(jsonObject, clazz);
            list.add(object);
        }
        return list;
    }

    private List<UserChatVo> judgeDirection(List<UserChatVo> userChatVoList) {
        for (UserChatVo userChat : userChatVoList) {
            Long senderId = userChat.getSenderId();
            Long receiverId = userChat.getReceiverId();
            if (senderId.equals(this.user.getId())) {
                userChat.setDirection("右");
            } else if (receiverId.equals(this.user.getId())) {
                userChat.setDirection("左");
            }
        }

        return userChatVoList;
    }
}
```
</div>
<div id="userChatWeb">

## 前端代码
```html
<template>
    <div class="WebSocketChat-container" style="text-align: center;overflow:auto;">
        <el-container style="width: 70%;
        margin-left: 200px; 
        padding: 0px;
        height: 653px; 
        margin-top: 15px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04)">
        <el-aside width="300px" style="background-color: #E8E8E8;">  
            <el-row>
                <el-col :span="6">
                    <img :src="$store.state.user.photo"
                        width="50px"
                        height="50px"
                        style="border-radius: 50%;
                        margin-top: 10px;
                        margin-left: 10px;" alt="">
                </el-col>
                <el-col :span="18" style="text-align: left; margin-top: 20px;" class="more-hide" >
                    {{ $store.state.user.username }}
                </el-col>
            </el-row>
            <el-row>
                <el-input @input="getUserListByUsername(username)" v-model="username"  placeholder="请输入用户名称" class="input-style">
                    <template slot="prepend">
                        <i class="el-icon-search"></i>
                    </template>
                </el-input>
            </el-row>
            <el-row >
                <el-row 
                :class="['hover', {selected:isSelected == charUserInformation.id}]" v-for="charUserInformation in chatUserInformationList" :key="charUserInformation.id" >
                    <div
                    v-if="charUserInformation.receiverId != $store.state.user.id" 
                    @click="showChatInformation(charUserInformation.receiverId, charUserInformation.senderId, charUserInformation.id)" >
                        <div @click="showRow(charUserInformation.id)"> 
                        <el-col :span="6">
                            <img :src="charUserInformation.receiverPhoto"
                             width="50px"
                              height="50px"
                               style="border-radius: 50%;margin-left: 15px; margin-top: 5px;" alt="">
                        </el-col>
                        <el-col :span="18" style="text-align: left;">
                            <el-row >
                                <el-col class="more-hide" :span="12">
                                    {{ charUserInformation.receiverName }}
                                </el-col>
                                <el-col :span="4" v-if="charUserInformation.isLike == '0'">
                                    <div style="background-color: #F7F7FC; font-size: 10px; color: rgba(0, 0, 0, 0.5);">
                                        <span>未关注</span>
                                    </div>
                                </el-col>
                                <el-col :span="4" v-else>
                                    <div style="background-color: #F7F7FC; font-size: 10px; color: rgba(0, 0, 0, 0.5);">
                                        <span>
                                            已关注
                                        </span>
                                    </div>
                                </el-col>
                                <el-col :span="8" style="font-size: 10px; color: rgba(0, 0, 0, 0.5);">
                                    {{ charUserInformation.lastCreateTime | formatDate }}
                                </el-col>
                            </el-row>
                            <el-row class="more-hide" style="font-size: 14px; color: rgba(0, 0, 0, 0.5); margin-top: 5px;">
                                {{ charUserInformation.lastContent }}
                            </el-row>
                        </el-col>
                    </div>
                </div>
                <div
                    v-else
                    @click="showChatInformation(charUserInformation.senderId, charUserInformation.receiverId, charUserInformation.id)" >
                        <div @click="showRow(charUserInformation.id)"> 
                        <el-col :span="6">
                            <img :src="charUserInformation.senderPhoto"
                             width="50px"
                              height="50px"
                               style="border-radius: 50%;margin-left: 15px; margin-top: 5px;" alt="">
                        </el-col>
                        <el-col :span="18" style="text-align: left;">
                            <el-row >
                                <el-col class="more-hide" :span="12">
                                    {{ charUserInformation.senderName }}
                                </el-col>
                                <el-col :span="4" v-if="charUserInformation.isLike == '0'">
                                    <div style="background-color: #F7F7FC; font-size: 10px; color: rgba(0, 0, 0, 0.5);">
                                        <span>未关注</span>
                                    </div>
                                </el-col>
                                <el-col :span="4" v-else>
                                    <div style="background-color: #F7F7FC; font-size: 10px; color: rgba(0, 0, 0, 0.5);">
                                        <span>
                                            已关注
                                        </span>
                                    </div>
                                </el-col>
                                <el-col :span="8" style="font-size: 10px; color: rgba(0, 0, 0, 0.5);">
                                    {{ charUserInformation.lastCreateTime | formatDate }}
                                </el-col>
                            </el-row>
                            <el-row class="more-hide" style="font-size: 14px; color: rgba(0, 0, 0, 0.5); margin-top: 5px;">
                                {{ charUserInformation.lastContent }}
                            </el-row>
                        </el-col>
                    </div>
                </div>
                </el-row>
            </el-row>
        </el-aside>
        <el-main style="background-color: white; padding: 0px;" v-if="isChoice">
            <el-row>
              <span class="more-hide" style="width: 400px;">{{ $store.state.user.username }}</span>
            </el-row>
            <br>
            <el-row class="chatBox" style="height: 490px; overflow: auto;padding: 10px;">
                <el-row  v-for="message in messageList" :key="message.id">
                    <el-row  style="margin-top: 10px;">
                    <div v-if="message.direction == '右'" style="text-align: right; width: 70%; margin-left: 195px;"> 
                        <el-col :span="22" >
                            <el-row>
                                <span style="color: rgba(0, 0, 0, 0.5); font-size: 10px; margin-right: 10px;">
                                    {{ message.createTime }}
                                </span>
                                <span>
                                    <!-- 啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊 -->
                                    {{ message.senderName }}
                                </span>
                                
                            </el-row>
                            <el-row style="background-color: #A6C6F7; display: flex; flex-wrap: wrap; padding: 5px;font-size: 16px;">
                                {{ message.content }}
                            </el-row>
                        </el-col>
                        <el-col :span="2">
                            <!-- todo 点击跳到个人主页 -->
                            <img width="40px" 
                            height="40px" 
                            style="border-radius: 50%; margin-top: 5px;cursor: pointer;" 
                            :src="message.senderPhoto"/>
                        </el-col>
                    </div>
                    <div v-else-if="message.direction == '左'" style="width: 70%">
                        <el-col :span="2">
                            <!-- todo 点击跳到个人主页 -->
                            <img width="40px"
                              height="40px"
                              style="border-radius: 50%; 
                              cursor: pointer; margin-top: 5px;" 
                              :src="message.senderPhoto"/>
                        </el-col>
                        <el-col :span="22" style="text-align: left;">
                            <el-row >
                                <span>
                                    <!-- 啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊 -->
                                    {{ message.senderName }}
                                </span>
                                <span style="color: rgba(0, 0, 0, 0.5); font-size: 10px; margin-left: 10px;">
                                    {{ message.createTime }}
                                </span>
                            </el-row>
                            <el-row style="background-color: #A0F3A0; display: flex; flex-wrap: wrap; padding: 5px; font-size: 16px;">
                                {{ message.content }}
                            </el-row>
                        </el-col>
                    </div>
                </el-row>
                </el-row>
            </el-row>
                <el-input
                    v-model="message"
                    type="textarea"
                    :autosize="{ minRows: 5 , maxRows: 5}"
                    placeholder="按Enter发送，Ctrl + Enter换行, 只会保留最近前10条消息。"
                    :show-word-limit="true"
                    text-autosize
                    resize="none"
                    minlength="1"
                    maxlength="1000"
                    @keydown.native="handleKeyDown($event)"
                >
                </el-input>
        </el-main>


        <el-main v-else>
            <el-row>
                请选择需要聊天的用户
            </el-row>
            <el-divider></el-divider>
            <el-row style="height: 350px">
            </el-row>
            <el-divider></el-divider>
        </el-main>
        </el-container>
    </div>
</template>

<script>
import $ from "jquery"
import store from "@/store";

export default {
    name: "WebSocketChat",
    data() {
        return {
            // 是否点击了左边框
            isChoice: false,
            //聊天用户信息
            chatUserInformationList: [],
            // 右边框展示信息
            showInformation: {
                receiverName: "",
                receiverBrief: "",
            },
            socketUrl: `ws://localhost:4000/websocket/chat/${store.state.user.token}`,
            // 聊天消息
            messageList: {},
            socket: null,
            // 是否选中该行
            isSelected: null,
            // 聊天框发送消息
            message: "",
            // 接收人id
            receiverId: "",
            //消息种类，sendMessage表示发送消息. receiveMessage 表示接收消息
            messageKind: "", 
            // 初始化时的接收人id
            startReceiverId: "",
            // 通过用户名模糊查找用户信息
            username: "",
        }
    },

    // 每次点击人之后自动跳到页面最底部
    updated(){
        let scrollContainer = document.querySelector('.chatBox')
        scrollContainer.scrollTop = scrollContainer.scrollHeight
    },

    filters: {
        formatDate: value => {
        if (!value) return '';

        // 转换成 Date 对象
        const date = new Date(value);

        // 格式化输出
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);

        return `${year}-${month}-${day}`;
        }
    },

    created() {
        this.startReceiverId = this.$route.params.receiverId;
        this.initWebSocket();
        setTimeout(() => {
            this.getReplyUserListByUserId(store.state.user.id, this.startReceiverId);
        }, 600)
    },

    unmounted() {
        this.socket.close();
    },
    methods: {
        // 通过用户名模糊查找用户信息中的用户名
        async getUserListByUsername(username) {
            const vue = this;
            await vue.getReplyUserListByUserId(store.state.user.id, vue.startReceiverId)
            $.ajax({
                url: "http://localhost:4000/webSocketChat/getUserListByUsername",
                type: "get",
                dataType:"json",
                contentType:"application/json",
                data: {
                    username,
                    userChatVo: JSON.stringify(vue.chatUserInformationList)
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.chatUserInformationList = response.data;
                    } else {
                        vue.$modal.msgError("发生未知错误");
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源");
                }
            })
        },
        handleKeyDown(e) {
        if (e.keyCode === 13 && !e.ctrlKey) {
            // Enter，发送消息
            this.sendMessages(this.message, this.receiverId);
            e.preventDefault();
        } else if (e.keyCode === 13 && e.ctrlKey) {
            // Ctrl+Enter，换行
            this.message += '\n';
        }
        },
        // 发送聊天消息
        sendMessages(message, receiverId) {
        // 发送消息的逻辑
            this.socket.send(JSON.stringify({
                event: "send_message",
                message,
                receiverId
            }));

            this.message = "";
        },
        initWebSocket() {
            // 创建WebSocket
            this.socket = new WebSocket(this.socketUrl);
            this.socket.onopen = () => {
                console.log("chat connect !!");
            };
            this.socket.onmessage = (message) => {
                console.log("chat onmessage!!");
                let data = JSON.parse(message.data);
                console.log(data)
                if (data.event == "start_chat") {
                    this.messageList = data.messageList;
                } else if (data.event == "send_message") {
                    if (data.information != null) this.messageList.push(data.information)
                    this.getReplyUserListByUserId(store.state.user.id);
                } else if (data.event == "receive_message") {
                    if (data.information != null) this.messageList.push(data.information)
                    this.getReplyUserListByUserId(store.state.user.id);
                }
            },
            this.socket.onclose = () => {
                console.log("chat onclose !! ");
            }
        },
        // 展示所选行
        showRow(index) {
            this.isSelected = index;
        },
        // 得到聊天对话框信息
        showChatInformation(receiverId, senderId, index) {
            this.isChoice = true;
            const vue = this;
            this.isSelected = index
            this.receiverId = receiverId;
            $.ajax({
                url: "http://localhost:4000/webSocketChat/showChatInformation",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                data: {
                    senderId,
                    receiverId,
                },
                success(response) {
                    if (response.code == '10000') {
                        vue.socket.send(JSON.stringify({
                            event: "start_chat",
                            message: response.data
                        }));
                    } else {
                        vue.$modal.msgError("发生未知错误，加载信息失败");
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源");
                }
            })
        },
        // 通过当前登录用户登录id得到该用户聊天列表
        async getReplyUserListByUserId(userId, receiverId) {
            const vue = this;
           await $.ajax({
                url: "http://localhost:4000/webSocketChat/getReplyUserListByUserId",
                type: "get",
                data: {
                    userId,
                    receiverId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == "10000") {
                        vue.chatUserInformationList = response.data;
                    } else {
                        vue.$modal.msgError("发生未知错误")
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源");
                }
            })
        }
    }
}
</script>

<style scoped>
span {
  display: inline-block; /* 将span元素设置为块级元素，方便设置宽度 */
  max-width: 200px; /* 最大宽度为200px，当宽度超出时会出现省略号 */
  white-space: nowrap; /* 触发文本溢出和隐藏 */
  overflow: hidden; /* 触发文本溢出和隐藏 */
  text-overflow: ellipsis; /* 显示省略号 */
}
.selected {
    background-color: #F5F7FA;
}
.hover:hover {
    background-color: #F5F7FA;
    cursor: pointer;
}
.more-hide {
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
}
  .text {
    font-size: 14px;
  }

  .item {
    margin-bottom: 18px;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }
  .clearfix:after {
    clear: both
  }

  .box-card {
    width: 480px;
  }
</style>
</style>
```
</div>
</div>