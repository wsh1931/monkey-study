package com.monkey.monkeyblog.pojo.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterVo {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String verifyCode;
}
