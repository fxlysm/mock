package com.mock.report;

import com.mock.vo.DataBaseVo;
import com.mock.vo.FieldDataService;
import com.mock.vo.FieldType;
import com.mock.vo.VoField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PuFaAlipay extends DataBaseVo {

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
    @VoField(desc = "经营类目", fieldType= FieldType.SELECT, options = "puFaAlipayBusiness")
    private String business;

    @ApiModelProperty(value = "商户所在省份编码", notes = "商户所在省份编码")
    @VoField(desc = "商户所在省份编码", fieldType=FieldType.PROVINCE)
    private String provinceCode;

    @ApiModelProperty(value = "商户所在城市编码", notes = "商户所在城市编码")
    @VoField(desc = "商户所在城市编码", fieldType=FieldType.CITY)
    private String cityCode;

    @ApiModelProperty(value = "商户所在区县编码", notes = "商户所在区县编码")
    @VoField(desc = "商户所在区县编码", fieldType=FieldType.DISTRICT)
    private String districtCode;

    @ApiModelProperty(value = "商户所详细经营地址", notes = "商户所详细经营地址")
    @VoField(desc = "商户所详细经营地址", colspan=2)
    private String address;

    @ApiModelProperty(value = "联系人", required = true, notes = "联系人")
    @VoField(desc = "联系人")
    private String contact;

    @ApiModelProperty(value = "联系人类型", required = true, notes = "联系人类型")
    @VoField(desc = "联系人类型", fieldType=FieldType.SELECT, options= FieldDataService.contactTypes)
    private String contactType;

    @ApiModelProperty(value = "联系人身份证号码", required = true, notes = "联系人身份证号码")
    @VoField(desc = "联系人身份证号码", maxLen=18,  reg="^\\w+$")
    private String idCardNo;

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
    @VoField(desc = "商户备注信息", colspan=2)
    private String merchantRemark;
}
