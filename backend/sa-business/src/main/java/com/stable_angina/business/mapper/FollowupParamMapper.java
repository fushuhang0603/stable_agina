package com.stable_angina.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stable_angina.business.domain.dto.FollowupParamQueryDTO;
import com.stable_angina.business.domain.entity.FollowupParam;
import com.stable_angina.business.domain.vo.FollowupParamVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 随访参数
 */
@Mapper
public interface FollowupParamMapper extends BaseMapper<FollowupParam> {

    /**
     * 随访参数分页：参数键 / 参数名称分别模糊匹配
     */
    Page<FollowupParamVO> selectParamPage(Page<FollowupParamVO> page, @Param("query") FollowupParamQueryDTO query);

    /**
     * 按参数键统计数量，excludeId 用于修改时排除自身
     */
    long countByParamKey(@Param("paramKey") String paramKey, @Param("excludeId") Long excludeId);

    /**
     * 参数分组列表
     */
    List<String> selectGroups();
}
