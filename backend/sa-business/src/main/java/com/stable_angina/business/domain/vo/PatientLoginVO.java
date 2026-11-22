package com.stable_angina.business.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 患者端登录结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "患者端登录结果")
public class PatientLoginVO implements Serializable {

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "有效期秒数")
    private Long expiresIn;

    @Schema(description = "患者本人档案")
    private PatientProfileVO profile;
}
