package com.stable_angina.business.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stable_angina.common.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 患者主索引
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patient")
@Schema(description = "患者主索引")
public class Patient extends BaseEntity {

    @Schema(description = "患者主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "患者编号")
    private String patientNo;

    @Schema(description = "关联 sys_user.id（患者端账号，可空）")
    private Long userId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别：0 未知 1 男 2 女")
    private Integer gender;

    @Schema(description = "出生日期")
    private LocalDate birthDate;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "确诊日期")
    private LocalDate diagnoseDate;

    @Schema(description = "过敏史")
    private String allergy;

    @Schema(description = "合并症")
    private String comorbidity;

    @Schema(description = "危险分层：HIGH 高危 / MID 中危 / LOW 低危")
    private String riskLevel;

    @Schema(description = "随访状态：FOLLOWING 随访中 / OVERDUE 已逾期")
    private String status;

    @Schema(description = "管床医生（sys_user.id）")
    private Long doctorId;

    @Schema(description = "最近随访日期")
    private LocalDate lastVisitDate;

    @Schema(description = "下次随访日期")
    private LocalDate nextVisitDate;

    @Schema(description = "备注")
    private String remark;
}
