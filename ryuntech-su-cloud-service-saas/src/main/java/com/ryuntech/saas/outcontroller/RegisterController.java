package com.ryuntech.saas.outcontroller;

import com.ryuntech.common.utils.Result;
import com.ryuntech.saas.api.dto.WeChatIndexDTO;
import com.ryuntech.saas.api.model.Company;
import com.ryuntech.saas.api.model.Index;
import com.ryuntech.saas.api.model.SysUser;
import com.ryuntech.saas.api.service.ICompanyService;
import com.ryuntech.saas.api.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ryuntech.common.constant.enums.CommonEnums.OPERATE_ERROR;

/**
 * 对外小程序注册接口
 * @author EDZ
 */
@Slf4j
@RestController
@RequestMapping("/outregister")
@Api(value = "RegisterController", tags = {"对外小程序注册接口"})
public class RegisterController extends ModuleBaseController {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    ICompanyService companyService;


    /**
     * 注册第一步
     *
     * @return
     */
    @PostMapping("/frist")
    @ApiOperation(value = "注册第一步验证手机号")
    public Result<SysUser> frist(SysUser sysUser) {
        Object value =   redisTemplate.opsForValue().get(sysUser.getPhone() + "ryun_code");
        if (value!=null&&value.toString().equals(sysUser.getVcode())){
            //判断手机号是否已经存在
            SysUser sysUser1 = sysUserService.selectByUser(sysUser);
            if (sysUser1==null){
                //手机号不存在用户，可以注册
                return new Result();
            }else {
                return new Result(OPERATE_ERROR,"手机号已经存在");
            }
        }
        return new Result();
    }

    /**
     * 注册第二步
      * @param sysUser
     * @return
     */
    @PostMapping("/second")
    @ApiOperation(value = "注册第二步验证手机号")
    public Result<SysUser> second(SysUser sysUser) {
//        验证公司名是否存在
        String companyName = sysUser.getCompanyName();
        if (StringUtils.isBlank(companyName)){
            return new Result<>(OPERATE_ERROR,"公司名不能为空");
        }
        Company company = companyService.selectByCompany(new Company().setName(companyName));
        if (company!=null){
            return new Result<>(OPERATE_ERROR,"公司名已经存在");
        }
        //开始注册操作

        return new Result();
    }
}
