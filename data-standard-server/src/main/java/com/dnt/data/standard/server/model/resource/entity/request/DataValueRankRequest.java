package com.dnt.data.standard.server.model.resource.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import lombok.Data;

/**
 * @description: 资产盘点数据价值排行--入参数对象 <br>
 * @date: 2021/10/18 下午2:16 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class DataValueRankRequest extends PageEntity {
    private Long dataSourceType;
    private Long interval;
    private String rankBy;
    private Long rankType;

}
