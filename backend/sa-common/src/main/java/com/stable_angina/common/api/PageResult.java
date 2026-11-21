package com.stable_angina.common.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统一分页响应体
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {

    @Schema(description = "当前页数据")
    private List<T> list;

    @Schema(description = "总条数")
    private long total;

    @Schema(description = "当前页码，从 1 开始")
    private long pageNum;

    @Schema(description = "每页条数")
    private long pageSize;

    public static <T> PageResult<T> of(List<T> list, long total, long pageNum, long pageSize) {
        PageResult<T> result = new PageResult<>();
        result.list = list;
        result.total = total;
        result.pageNum = pageNum;
        result.pageSize = pageSize;
        return result;
    }
}
