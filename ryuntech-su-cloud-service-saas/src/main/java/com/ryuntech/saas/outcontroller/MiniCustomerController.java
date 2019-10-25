package com.ryuntech.saas.outcontroller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ryuntech.common.utils.QueryPage;
import com.ryuntech.common.utils.Result;
import com.ryuntech.saas.api.model.CustomerUserInfo;
import com.ryuntech.saas.api.model.FollowupRecord;
import com.ryuntech.saas.api.model.ReceivableContract;
import com.ryuntech.saas.api.service.ICustomerUserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ryuntech.common.constant.enums.CommonEnums.OPERATE_ERROR;

/**
 * 小程序客户列表
 * @author EDZ
 */

@Slf4j
@RestController
@RequestMapping("/minicustomer")
@Api(value = "MiniCustomerController", tags = {"客户列表数据"})
public class MiniCustomerController extends ModuleBaseController{

    @Autowired
    private ICustomerUserInfoService customerUserInfoService;

    @PostMapping("/outlist")
    @ApiOperation(value = "分页、条件查询客户列表信息")
    @ApiImplicitParam(name = "customerUserInfo", value = "查询条件", required = true, dataType = "CustomerUserInfo", paramType = "body")
    public Result<IPage<CustomerUserInfo>> list(@RequestBody CustomerUserInfo customerUserInfo, QueryPage queryPage) {
        log.info("customerUserInfo.getCustomerId()"+customerUserInfo.getCustomerId());
//        if (StringUtils.isBlank(customerUserInfo.getCustomerId())){
//            return new Result<>(OPERATE_ERROR);
//        }
        return  customerUserInfoService.selectPageList(customerUserInfo, queryPage);
    }

    /**
     * 添加合同跟进信息
     *
     * @param customerUserInfo
     * @return
     */
    @PostMapping("/outadd")
    @ApiOperation(value = "添加客户信息")
    @ApiImplicitParam(name = "customerUserInfo", value = "合同跟进信息", required = true, dataType = "CustomerUserInfo", paramType = "body")
    public Result add(@RequestBody CustomerUserInfo customerUserInfo) {
//            生成客户编号
        log.info("customerUserInfo.getCustomerId()"+customerUserInfo.getCustomerName());
        if (StringUtils.isBlank(customerUserInfo.getCustomerName())){
            return new Result<>(OPERATE_ERROR,"客户姓名为空");
        }
//        生成客户编号
        long id = generateId();
        String customerId = String.valueOf(id);
        customerUserInfo.setCustomerId(customerId);
        boolean b = customerUserInfoService.saveOrUpdate(customerUserInfo);
        if (b){
            return new Result<>();
        }else {
            return new Result<>(OPERATE_ERROR);
        }
    }
}