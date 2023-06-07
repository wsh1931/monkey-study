package com.monkey.monkeyblog.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.service.user.UserService;

import com.monkey.spring_security.JwtUtil;
import com.monkey.spring_security.mapper.user.UserMapper;
import com.monkey.spring_security.pojo.user.User;
import com.monkey.spring_security.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;


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

    // 通过token得到用户信息
    @Override
    public ResultVO getUserInfoByToken() {
        // 从token中获取用户名与密码
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) usernamePasswordAuthenticationToken.getPrincipal();
        User user = userDetails.getUser();
        return new ResultVO(ResultStatus.OK, null, user);
    }
}

