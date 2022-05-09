package com.dnt.data.standard.server.model.entity.response;

import lombok.Data;

/**
 * @description: 文件导入--错误对象 <br>
 * @date: 2021/9/6 下午4:20 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class ExcelImportErrorResponse {

    private String sheetName;

    private Integer rowNum;

    private String primaryQuestion;

    private String errReason;

    public ExcelImportErrorResponse() {
    }

    public ExcelImportErrorResponse(Integer rowNum, String primaryQuestion, String errReason) {
        this.rowNum = rowNum;
        this.primaryQuestion = primaryQuestion;
        this.errReason = errReason;
    }

    public ExcelImportErrorResponse(String sheetName, Integer rowNum, String primaryQuestion, String errReason) {
        this.sheetName = sheetName;
        this.rowNum = rowNum;
        this.primaryQuestion = primaryQuestion;
        this.errReason = errReason;
    }
}
