package com.mock.report;

import com.mock.vo.DataBaseVo;
import com.mock.vo.FieldType;
import com.mock.vo.VoField;
import lombok.Getter;
import lombok.Setter;
import io.swagger.annotations.ApiModelProperty;

@Getter
@Setter
public class PuFaWechat extends DataBaseVo {

    @ApiModelProperty(value = "商户编号", required = true, notes = "商户编号")
    @VoField(desc = "商户编号", dynamic=false)
    private String cpId;

    @ApiModelProperty(value = "支付渠道", required = true, notes = "支付渠道")
    @VoField(desc = "支付渠道", dynamic=false)
    private Long chanId;

    @ApiModelProperty(value = "二级商户名称", required = true, notes = "二级商户名称")
    @VoField(desc = "二级商户名称", dynamic=false)
    private String subMechantName;

    @ApiModelProperty(value = "二级商户简称", required = true, notes = "二级商户简称")
    @VoField(desc = "二级商户简称", dynamic=false)
    private String subMerchantShortname;

    @ApiModelProperty(value = "经营类目", required = true, notes = "经营类目")
    @VoField(desc = "经营类目", fieldType= FieldType.WECHAT_BUSINESS)
    private String business;

    @ApiModelProperty(value = "联系人", required = true, notes = "联系人")
    @VoField(desc = "联系人")
    private String contact;

    @ApiModelProperty(value = "手机号码", required = true, notes = "手机号码")
    @VoField(desc = "手机号码", maxLen=11, reg="^1[0-9]{10}$")
    private String contactPhone;

    @ApiModelProperty(value = "联系邮箱", required = true, notes = "联系邮箱")
    @VoField(desc = "联系邮箱", reg="^\\w+@\\w+\\.\\w+$")
    private String contactEmail;

    @ApiModelProperty(value = "联系电话", required = true, notes = "联系电话")
    @VoField(desc = "联系电话")
    private String servicePhone;

    @ApiModelProperty(value = "商户备注信息", required = true, notes = "商户备注信息")
    @VoField(desc = "商户备注信息")
    private String merchantRemark;
}
