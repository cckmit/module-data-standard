package com.dnt.data.standard.server.model.entity.response;

import lombok.Data;

import java.util.List;

/**
 * @description: excel导入数据结果集合--返回对象 <br>
 * @date: 2021/9/6 下午5:58 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class ExcelUploadResponse {

    /** 插入数据的数量**/
    private Integer insertNum;
    /** 更新数据的数量**/
    private Integer updateNum;
    /** 已有的不导入的数据数量**/
    private Integer keepNum;
    /** 所有知识数量**/
    private Integer allNum;
    /**错误信息**/
    private List<ExcelImportErrorResponse> errList;
}
