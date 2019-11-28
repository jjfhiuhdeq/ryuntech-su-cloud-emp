package com.ryuntech.saas.outcontroller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.gson.Gson;
import com.ryuntech.common.utils.DateUtil;
import com.ryuntech.common.utils.HttpUtils;
import com.ryuntech.common.utils.QueryPage;
import com.ryuntech.common.utils.Result;
import com.ryuntech.saas.api.dto.CustomerMonitorDTO;
import com.ryuntech.saas.api.dto.CustomerUserInfoDTO;
import com.ryuntech.saas.api.form.CustomerMonitorForm;
import com.ryuntech.saas.api.helper.ApiConstant;
import com.ryuntech.saas.api.helper.constant.RiskWarnConstants;
import com.ryuntech.saas.api.model.*;
import com.ryuntech.saas.api.service.ICustomerMonitorService;
import com.ryuntech.saas.api.service.ICustomerUserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.ryuntech.common.constant.enums.CommonEnums.OPERATE_ERROR;
import static com.ryuntech.saas.api.helper.ApiConstant.APPKEY;

/**
 * @author EDZ
 */
@Slf4j
@RestController
@RequestMapping("/minimonitor")
@Api(value = "MiniMonitorController", tags = {"小程序登录接口"})
public class MiniMonitorController extends ModuleBaseController{
    @Autowired
    private ICustomerMonitorService iCustomerMonitorService;

    @Autowired
    private ICustomerUserInfoService iCustomerUserInfoService;



    @PostMapping("/outlist")
    @ApiOperation(value = "分页、条件查询用户列表信息")
    @ApiImplicitParam(name = "followupRecord", value = "查询条件", required = true, dataType = "FollowupRecord", paramType = "body")
    public Result<IPage<CustomerMonitor>> list(@RequestBody CustomerMonitor customerMonitor, QueryPage queryPage) {
        log.info("customerMonitor.getMonitorId()"+customerMonitor.getMonitorId());
        return  iCustomerMonitorService.pageList(customerMonitor, queryPage);
    }


    /**
     * 获取Auth Code
     * @return
     */
    protected static final String[] randomAuthentHeader() {
        String timeSpan = String.valueOf(System.currentTimeMillis() / 1000);
        return new String[] { DigestUtils.md5Hex(APPKEY.concat(timeSpan).concat(ApiConstant.SECKEY)).toUpperCase(), timeSpan };
    }
    /**
     * 添加合同跟进信息
     *
     * @param customerMonitorForm
     * @return
     */
    @PostMapping("/outadd")
    @ApiOperation(value = "添加客户监控信息")
    @ApiImplicitParam(name = "customerMonitor", value = "客户监控信息", required = true, dataType = "CustomerMonitor", paramType = "body")
    public Result add(@RequestBody CustomerMonitorForm customerMonitorForm) throws Exception {
        if (null!=customerMonitorForm){
            List<String> customerIds = customerMonitorForm.getCustomerIds();
            for (String customerId:customerIds){
//                判断该公司是否存在企查查中

                CustomerUserInfoDTO customerUserInfoDTO = iCustomerUserInfoService.selectCustomerUserInfoDTO(new CustomerUserInfo().setCustomerId(customerId));

                String geteciImage = ApiConstant.GETECIIMAGE+"?key="+APPKEY;

                String[] autherHeader = randomAuthentHeader();
                HashMap<String, String> reqHeader = new HashMap<>();
                reqHeader.put("Token",autherHeader[0]);
                reqHeader.put("Timespan",autherHeader[1]);
                Gson gson = new Gson();
                String customerName = customerUserInfoDTO.getCustomerName();
                String urlName=geteciImage+"&keyWord="+customerName;
                String content = HttpUtils.Get(urlName,reqHeader);
                ApiGetEciImage apiGetEciImage = gson.fromJson(content, ApiGetEciImage.class);

                if (apiGetEciImage != null && StringUtils.isNotBlank(apiGetEciImage.getResult())) {
                    String status = apiGetEciImage.getApiHeader().getStatus();
                    if (status.equals("201")){
                        return new Result(OPERATE_ERROR,"没有查询到对应的公司");
                    }
                }


                String  monitorId = String.valueOf(generateId());
                CustomerMonitor customerM = new CustomerMonitor();
                customerM.setMonitorId(monitorId);
                customerM.setCustomerId(customerId);
                customerM.setCreated(new Date());
                customerM.setUpdated(new Date());
                customerM.setCustomerName(customerUserInfoDTO.getCustomerName());
                iCustomerMonitorService.saveOrUpdate(customerM);
            }
        }
        return new Result();
    }
}