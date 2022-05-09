package com.dnt.data.standard.server.model.version.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @description: 版本发布--入参数据实体 <br>
 * @date: 2022/4/19 上午10:24 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("版本发布入参请求对象")
public class VersionReleaseRequest extends PageEntity {

    /**
     * 版本名称
     */
    @ApiModelProperty("版本名称")
    private String versionName;
    /**
     * 版本编号
     */
    @ApiModelProperty("版本编号")
    private String versionCode;
    /**
     * 参考标准
     */
    @ApiModelProperty("参考标准")
    private String referenceStandard;
    /**
     * 描述说明
     */
    @ApiModelProperty("描述说明")
    private String description;

    /**==========================选择发布的数据================================***/

    /**
     * 发布的数据
     */
    @ApiModelProperty("发布的内容  Map&lt;String, List&lt;VersionReleaseSelectDataRequest&gt;&gt; ")
    private Map<String, List<VersionReleaseSelectDataRequest>> releaseData;
}
