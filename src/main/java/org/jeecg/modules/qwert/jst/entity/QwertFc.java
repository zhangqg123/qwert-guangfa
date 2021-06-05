package org.jeecg.modules.qwert.jst.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: qwert_fc
 * @Author: jeecg-boot
 * @Date:   2021-06-05
 * @Version: V1.0
 */
@Data
@TableName("qwert_fc")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="qwert_fc对象", description="qwert_fc")
public class QwertFc implements Serializable {
    private static final long serialVersionUID = 1L;

	/**设备编号*/
	@Excel(name = "设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
    private java.lang.String devNo;
	/**数据点名称*/
	@Excel(name = "数据点名称", width = 15)
    @ApiModelProperty(value = "数据点名称")
    private java.lang.String targetName;
	/**时间*/
	@Excel(name = "时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "时间")
    private java.util.Date time;
	/**数据点编号*/
	@Excel(name = "数据点编号", width = 15)
    @ApiModelProperty(value = "数据点编号")
    private java.lang.String tagname;
	/**点值*/
	@Excel(name = "点值", width = 15)
    @ApiModelProperty(value = "点值")
    private java.lang.Double pv;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
}
