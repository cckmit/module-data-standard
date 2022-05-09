package com.dnt.data.standard.server.model.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 获取文件导入进度条--入参数对象 <br>
 * @date: 2021/9/7 上午10:14 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("获取上传数据进度条-入参对象")
public class ExcelProgressRequest {
    /**
     * 上传信息的批次号
     */
    private String processCode;
}
