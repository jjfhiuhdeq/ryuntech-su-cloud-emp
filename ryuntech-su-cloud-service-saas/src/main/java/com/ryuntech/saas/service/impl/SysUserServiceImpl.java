package com.ryuntech.saas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.ryuntech.common.constant.generator.IncrementIdGenerator;
import com.ryuntech.common.constant.generator.UniqueIdGenerator;
import com.ryuntech.common.service.impl.BaseServiceImpl;
import com.ryuntech.common.utils.HttpUtils;
import com.ryuntech.common.utils.QueryPage;
import com.ryuntech.common.utils.Result;
import com.ryuntech.saas.api.dto.SysUserDTO;
import com.ryuntech.saas.api.form.SysUserForm;
import com.ryuntech.saas.api.helper.ApiConstant;
import com.ryuntech.saas.api.helper.SecurityUtils;
import com.ryuntech.saas.api.helper.constant.RoleConstants;
import com.ryuntech.saas.api.mapper.*;
import com.ryuntech.saas.api.model.*;
import com.ryuntech.saas.api.service.SysUserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.ryuntech.common.constant.enums.CommonEnums.OPERATE_ERROR;
import static com.ryuntech.saas.api.helper.ApiConstant.APPKEY;

/**
 * @author antu
 * @date 2019-05-22
 */
@Service
public class SysUserServiceImpl  extends BaseServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private EmployeeRoleMapper employeeRoleMapper;


    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public SysUser findByName(String username) {
        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
        return sysUser;
    }

    @Override
    public List<SysUser> list(SysUser user) {
        QueryWrapper queryWrapper=null;
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
             queryWrapper= new QueryWrapper<SysUser>().eq("username", user.getUsername());
        }
        //插入权限数据
        return sysUserMapper.selectList(queryWrapper);
    }

    @Override
    public void create(SysUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.save(user);
    }

    @Override
    public void update(SysUser user) {
        user.setPassword(null);
        sysUserMapper.updateById(user);
    }

    @Override
    public void changePass(SysUser user) {
        SysUser entity = new SysUser();
        SysUser sysUser = this.findByName(SecurityUtils.getUsername());
        if (sysUser != null) {
            entity.setId(sysUser.getId());
            entity.setPassword(passwordEncoder.encode(user.getPassword()));
            sysUserMapper.updateById(entity);
        }
    }

    @Override
    public SysUser selectUserRoleById(SysUser user) {
        return sysUserMapper.selectUserRoleById(user);
    }

    @Override
    public IPage<SysUser> selectUsersRoleById(SysUser user, QueryPage queryPage) {

        Page<SysUser> page = new Page<>(queryPage.getPageCode(), queryPage.getPageSize());
        if (user.getId()!=null) {
            queryWrapper.eq("id", user.getId());
        }
        if (user.getUsername()!=null) {
            queryWrapper.eq("username", user.getUsername());
        }
        return sysUserMapper.selectUsersRoleById(page,user);
    }

    @Override
    public List<SysUser> selectUserMap(SysUser user) {
        return sysUserMapper.selectUserMap(user);
    }

    @Override
    public SysUser selectByUserDTO(SysUserDTO userDTO) {
        if (userDTO.getUsername()!=null) {
            queryWrapper.eq("username", userDTO.getUsername());
        }
        if (userDTO.getPhone()!=null) {
            queryWrapper.eq("phone", userDTO.getPhone());
        }
        if (StringUtils.isNotBlank(userDTO.getId())){
            queryWrapper.eq("id", userDTO.getId());
        }
        if (userDTO.getOpenId()!=null) {
            queryWrapper.eq("open_id", userDTO.getOpenId());
        }
        return sysUserMapper.selectOne(queryWrapper);
    }

    @Override
    public SysUser selectByUser(SysUser user) {
        if (StringUtils.isNotBlank(user.getId())){
            queryWrapper.eq("id", user.getId());
        }
        if (user.getUsername()!=null) {
            queryWrapper.eq("username", user.getUsername());
        }
        if (user.getPhone()!=null) {
            queryWrapper.eq("phone", user.getPhone());
        }
        return baseMapper.selectOne(queryWrapper);
    }



    /**
     * 获取Auth Code
     * @return
     */
    protected static final String[] randomAuthentHeader() {
        String timeSpan = String.valueOf(System.currentTimeMillis() / 1000);
        return new String[] { DigestUtils.md5Hex(APPKEY.concat(timeSpan).concat(ApiConstant.SECKEY)).toUpperCase(), timeSpan };
    }

    @Override
    public SysUser register(SysUserForm sysUserForm) throws Exception {
        //生成用户
        //生成主键
        UniqueIdGenerator uniqueIdGenerator = UniqueIdGenerator.getInstance(IncrementIdGenerator.getServiceId());

        Long id = uniqueIdGenerator.nextId();
        SysUser sysUser = new SysUser();
        sysUser.setId(String.valueOf(id));
//        登陆名默认为手机号
        sysUser.setUsername(sysUserForm.getPhone());
        sysUser.setPhone(sysUserForm.getPhone());
        sysUser.setAvatar(sysUserForm.getAvatar());
        sysUser.setPassword(sysUserForm.getPassword());
        sysUser.setCreateTime(new Date());
        sysUser.setUpdatedAt(new Date());
        sysUserMapper.insert(sysUser);

        //生成员工
        Employee employee = new Employee();
        String employeeId = String.valueOf(uniqueIdGenerator.nextId());
        employee.setEmployeeId(employeeId);
        employee.setUserId(sysUser.getId());
        employee.setCompanyName(sysUserForm.getCompanyName());
        employee.setName(sysUserForm.getName());
        employee.setMobile(sysUserForm.getPhone());
//        账号状态正常
        employee.setStatus(1);
        employee.setCreatedAt(new Date());
        employee.setUpdatedAt(new Date());
        employeeMapper.insert(employee);


        //创建公司
        Company company = new Company();
        String companyId = String.valueOf(uniqueIdGenerator.nextId());
        company.setCompanyId(companyId);
        company.setName(sysUserForm.getCompanyName());
//        设置负责员工为当前
        company.setEmployeeId(employee.getEmployeeId());
        company.setCreatedAt(new Date());
        company.setUpdatedAt(new Date());
//        判断企查查是否存在公司
//        is_qichacha
        String geteciImage = ApiConstant.GETECIIMAGE+"?key="+APPKEY;

        String[] autherHeader = randomAuthentHeader();
        HashMap<String, String> reqHeader = new HashMap<>();
        reqHeader.put("Token",autherHeader[0]);
        reqHeader.put("Timespan",autherHeader[1]);
        Gson gson = new Gson();
        String customerName = sysUserForm.getCompanyName();
        String urlName=geteciImage+"&keyWord="+customerName;
        String content = HttpUtils.Get(urlName,reqHeader);
        ApiGetEciImage apiGetEciImage = gson.fromJson(content, ApiGetEciImage.class);
        if (null==apiGetEciImage){
            company.setIsQichacha("0");
        }
        if (apiGetEciImage != null && StringUtils.isNotBlank(apiGetEciImage.getStatus())) {
            String status = apiGetEciImage.getStatus();
            if (!status.equals("200")){
                company.setIsQichacha("0");
            }else {
                company.setIsQichacha("1");
            }
        }
        companyMapper.insert(company);


        //默认分配管理员角色
        SysRole sysRole = new SysRole();
        Long roleId = uniqueIdGenerator.nextId();
        sysRole.setRid(String.valueOf(roleId));
        sysRole.setRval(RoleConstants.ADMINROOT);
        sysRole.setRname(RoleConstants.ADMIN);
        sysRole.setRdesc(RoleConstants.ADMINDESC);
        sysRole.setCreated(new Date());
        sysRole.setUpdated(new Date());
        sysRole.setIsAdmin(RoleConstants.ISADMIN);
        sysRole.setCompanyId(employee.getEmployeeId());
        sysRoleMapper.insert(sysRole);


        EmployeeRole employeeRole = new EmployeeRole();
        employeeRole.setEmployeeId(employee.getEmployeeId());
        Long employeeRoleId = uniqueIdGenerator.nextId();
        employeeRole.setId(String.valueOf(employeeRoleId));
        employeeRole.setRoleId(sysRole.getRid());
        employeeRole.setCreatedAt(new Date());
        employeeRole.setUpdatedAt(new Date());
        employeeRoleMapper.insert(employeeRole);


        return sysUser;
    }
}
