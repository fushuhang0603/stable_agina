package com.stable_angina.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录返回：token + 用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录返回结果")
public class LoginVO implements Serializable {

    @Schema(description = "访问令牌，请求头格式：Bearer {token}")
    private String token;

    @Schema(description = "令牌有效期（秒）")
    private Long expiresIn;

    @Schema(description = "当前登录用户信息")
    private UserInfoVO userInfoVO;
}
