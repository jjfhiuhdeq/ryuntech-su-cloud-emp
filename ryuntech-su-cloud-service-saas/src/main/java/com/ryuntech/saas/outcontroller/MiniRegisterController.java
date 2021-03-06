package com.ryuntech.saas.outcontroller;

import com.ryuntech.common.utils.Result;
import com.ryuntech.saas.api.dto.SysUserDTO;
import com.ryuntech.saas.api.form.SysUserForm;
import com.ryuntech.saas.api.model.*;
import com.ryuntech.saas.api.service.ICompanyService;
import com.ryuntech.saas.api.service.IEmployeeService;
import com.ryuntech.saas.api.service.IUserWechatService;
import com.ryuntech.saas.api.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ryuntech.common.constant.enums.CommonEnums.OPERATE_ERROR;

/**
 * 对外小程序注册接口
 * @author EDZ
 */
@Slf4j
@RestController
@RequestMapping("/miniregister")
@Api(value = "MiniRegisterController", tags = {"对外小程序注册接口"})
public class MiniRegisterController extends ModuleBaseController {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    ICompanyService companyService;

    @Autowired
    IEmployeeService iEmployeeService;

    @Autowired
    IUserWechatService iUserWechatService;


    /**
     * 注册第一步
     *
     * @return
     */
    @PostMapping("/outfrist")
    @ApiOperation(value = "注册第一步验证手机号")
    public Result<SysUser> frist(@RequestBody SysUserDTO sysUserDTO) {
        Object value =   redisTemplate.opsForValue().get(sysUserDTO.getMobile() + "ryun_code");
        if (value!=null&&value.toString().equals(sysUserDTO.getVcode())){
            //判断手机号是否已经存在
            SysUser sysUser1 = sysUserService.selectByUserDTO(sysUserDTO);
            if (sysUser1==null){
                //手机号不存在用户，可以注册
                return new Result("注册成功");
            }else {
                return new Result(OPERATE_ERROR,"手机号已经存在");
            }
        }else {
            return new Result(OPERATE_ERROR,"参数异常");
        }
    }

    /**
     * 注册第二步
      * @param sysUserForm
     * @return
     */
    @PostMapping("/outsecond")
    @ApiOperation(value = "注册第二步验证手机号")
    public Result<SysUser> second(@RequestBody SysUserForm sysUserForm) throws Exception {
//        验证公司名是否存在
        String companyName = sysUserForm.getCompanyName();
        if (StringUtils.isBlank(companyName)){
            return new Result<>(OPERATE_ERROR,"公司名不能为空");
        }
        Company company = companyService.selectByCompany(new Company().setName(companyName));
        if (company!=null){
            return new Result<>(OPERATE_ERROR,"公司名已经存在");
        }
        /**
         * 开始注册操作
         */
        boolean register = sysUserService.saveRegister(sysUserForm.getCompanyName(),sysUserForm.getName(),sysUserForm.getMobile(),sysUserForm.getPassword());

        if (register){
            SysUser sysUser = sysUserService.selectByUser(new SysUser().setMobile(sysUserForm.getMobile()));
            List<Employee> employeeList = iEmployeeService.selectByEmployeeList(new Employee().setSysUserId(sysUser.getSysUserId()));
            /**
             * 查询小程序
              */
            UserWechat userWechat = iUserWechatService.selectByUserWeChat(new UserWechat().setUserId(sysUser.getSysUserId()));
            SysUserDTO sysUserDTO = new SysUserDTO();
            if (null!=employeeList&&employeeList.size()!=0){
                sysUserDTO.setEmployeeList(employeeList);
            }
            if (null!=userWechat){
                sysUserDTO.setNickname(userWechat.getNickname());
                sysUserDTO.setUnionId(userWechat.getUnionId());
            }
            sysUserDTO.setSysUserId(sysUser.getSysUserId());
            sysUserDTO.setAvatar(sysUser.getAvatar());
            sysUserDTO.setMobile(sysUser.getMobile());
            sysUserDTO.setStatus(sysUser.getStatus());
            if (sysUser!=null){
                return new Result(sysUserDTO);
            }else {
                return new Result(OPERATE_ERROR,"手机号不存在");
            }
        }else {
            return new Result(OPERATE_ERROR,"数据异常");
        }


    }
}
