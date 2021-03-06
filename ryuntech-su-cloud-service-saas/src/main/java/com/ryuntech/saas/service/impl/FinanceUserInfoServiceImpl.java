package com.ryuntech.saas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryuntech.common.service.impl.BaseServiceImpl;
import com.ryuntech.common.utils.QueryPage;
import com.ryuntech.common.utils.Result;
import com.ryuntech.saas.api.dto.FinanceOrder;
import com.ryuntech.saas.api.helper.constant.OrderConstants;
import com.ryuntech.saas.api.helper.constant.PayResultConstant;
import com.ryuntech.saas.api.mapper.FinanceUserInfoMapper;
import com.ryuntech.saas.api.mapper.OrderMapper;
import com.ryuntech.saas.api.mapper.PartnerMapper;
import com.ryuntech.saas.api.model.FinanceUserInfo;
import com.ryuntech.saas.api.model.Order;
import com.ryuntech.saas.api.model.Partner;
import com.ryuntech.saas.api.service.IFinanceUserInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.ryuntech.common.constant.enums.CommonEnums.ORDER_ERROR;
import static com.ryuntech.common.constant.generator.UniqueIdGenerator.generateId;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author antu
 * @since 2019-08-14
 */
@Service
public class FinanceUserInfoServiceImpl extends BaseServiceImpl<FinanceUserInfoMapper, FinanceUserInfo> implements IFinanceUserInfoService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    FinanceUserInfoMapper financeUserInfoMapper;
    @Autowired
    PartnerMapper partnerMapper;

    @Override
    public Result<IPage<FinanceUserInfo>> pageList(FinanceUserInfo financeUserInfo, QueryPage queryPage) {
        Page<FinanceUserInfo> page = new Page<>(queryPage.getPageCode(), queryPage.getPageSize());
        if (financeUserInfo.getUserId()!=null) {
            queryWrapper.eq("userId", financeUserInfo.getUserId());
        }
        return super.pageList(queryWrapper,page);
    }

    @Override
    public Result addFinacneOrder(FinanceOrder financeOrder) {
        //插入融资客户信息
        String financeId = generateId()+"";

        //如果融资客户存在，则不插入
        FinanceUserInfo financeUserInfo = this.financeUserInfoMapper.selectOne(new QueryWrapper<FinanceUserInfo>().eq("mobile", financeOrder.getMobile()));
        if (financeUserInfo==null){
            financeUserInfo = new FinanceUserInfo();
            //用户不存在，不能插入，插入订单数据
            financeUserInfo.setUserId(financeId);
            financeUserInfo.setRealName(financeOrder.getRealname());
            financeUserInfo.setMobile(financeOrder.getMobile());
            financeUserInfo.setCreateTime(new Date());
            financeUserInfo.setSex(financeOrder.getSex());
            financeUserInfo.setMemberBirthday(financeOrder.getMemberBirthday());
            financeUserInfo.setCity(financeOrder.getCity());
            //纳税金额s
            financeUserInfo.setPayTaxes(financeOrder.getPayTaxes());
            //年开发票
            financeUserInfo.setAnnualInvoice(financeOrder.getAnnualInvoice());
            //房产信息 00 按揭房 01全款
            financeUserInfo.setHouseType(financeOrder.getHouseType());
            //00 本地 01外地
            financeUserInfo.setHouseAddressType(financeOrder.getHouseAddressType());

            //房产信息 00 按揭房 01全款
            financeUserInfo.setCarType(financeOrder.getCarType());
            //00 本地 01外地
            financeUserInfo.setCarAddressType(financeOrder.getCarAddressType());

//        营业执照注册时间
            financeUserInfo.setBussinessRegister(financeOrder.getBussinessRegister());
            //身份性质,法人/股东/其他,职业
            financeUserInfo.setAnnualInvoice(financeOrder.getAnnualInvoice());
//            公司
            financeUserInfo.setCompanyName(financeOrder.getCompanyName());
            financeUserInfoMapper.insert(financeUserInfo);
        }
        //查询是否有未完结的订单数据
        Order order1 = orderMapper.selectOne(new QueryWrapper<Order>().eq("member_id", financeUserInfo.getUserId()).eq("order_status", OrderConstants.PENDING));
        if (order1!=null){
            return new Result(ORDER_ERROR);
        }

        //开始插入订单信息  插入订单信息
        //贷款金额
        Order order = new Order();
        String orderPayAmount = financeOrder.getOrderPayAmount();
        String orderId = generateId()+"";
        order.setOrderid(orderId);
        order.setCity(financeOrder.getCity());
        order.setOrderPayAmount(orderPayAmount);
        order.setOrderTime(new Date());
        order.setCompanyName(financeOrder.getCompanyName());
        order.setModifyTime(new Date());
//        推荐码
        order.setReferralCode(financeOrder.getReferralCode());
        //融资客户编号
        order.setMemberId(financeUserInfo.getUserId());
        //合伙人编号
        //根据推荐码查询合伙人编号
//        Partner partner = partnerMapper.selectOne(new QueryWrapper<Partner>().eq("open_id", openId));

        Partner recommend = partnerMapper.selectOne(new QueryWrapper<Partner>().eq("recommend", financeOrder.getReferralCode()));
        if (recommend!=null) order.setPartnerId(recommend.getPartnerId());
        //订单状态
        order.setOrderStatus(OrderConstants.PENDING);
        //佣金阶段状态
        order.setPaymentStatus(PayResultConstant.UNLIQUIDATED);
        //申请渠道
        order.setOrderChenel(financeOrder.getOrderChenel());
        orderMapper.insert(order);
        return new Result("申请成功，请等待审核");
    }

    @Override
    public Result updateFinacneOrder(FinanceUserInfo financeUserInfo) {
        financeUserInfoMapper.updateById(financeUserInfo);
        //同步更新订单数据的客户名
        String userId = financeUserInfo.getUserId();
        //查询订单
        Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("member_id", userId));
        if (StringUtils.isNotBlank(financeUserInfo.getRealName())){
            order.setMemberName(financeUserInfo.getRealName());
            orderMapper.updateById(order);
        }
        return new Result();
    }

    @Override
    public FinanceUserInfo selectOne(FinanceOrder financeOrder) {
        QueryWrapper<FinanceUserInfo> financeUserInfoQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isBlank(financeOrder.getMobile())){
            financeUserInfoQueryWrapper.eq("mobile", financeOrder.getMobile());
        }
        return this.financeUserInfoMapper.selectOne(financeUserInfoQueryWrapper);
    }
}
