package com.monkey.monkeyUtils.springsecurity;

import com.alibaba.fastjson.annotation.JSONField;
import com.monkey.monkeyUtils.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 通过从数据库中查到的用户名和密码判断该用户是否合格
 * */
@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private User user;

    // 用户权限集合
    private List<String> permissions;

    // 转化后的集合，防止每次执行此方法的时候都进行转化
    // 此注解的作用是防止redis序列化，
    // 最后要将UserDetailsImpl存入redis中
    // 因为它不属于java pojo中的类，序列化可能存在问题，防止运行时异常
    @JSONField(serialize = false)
    List<SimpleGrantedAuthority> authorities;
    /**
     * 获取权限信息
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/19 16:27
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 把permissions中的String类型的权限信息封装成SimpleGrantedAuthority返回
        if (authorities == null) {
            authorities = this.permissions
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        return authorities;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public UserDetailsImpl(User user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }
}
