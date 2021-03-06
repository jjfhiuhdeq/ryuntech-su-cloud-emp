package com.ryuntech.saas.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ryuntech.common.utils.QueryPage;
import com.ryuntech.common.utils.Result;
import com.ryuntech.saas.api.dto.ContractRecordDTO;
import com.ryuntech.saas.api.dto.FollowupRecordDTO;
import com.ryuntech.saas.api.form.ContractRecordForm;
import com.ryuntech.saas.api.form.FollowupRecordForm;
import com.ryuntech.saas.api.model.FollowupRecord;
import com.ryuntech.saas.api.model.Partner;

import java.util.List;

/**
 * <p>
 * 跟进表 服务类
 * </p>
 *
 * @author antu
 * @since 2019-10-24
 */
public interface IFollowupRecordService extends IBaseService<FollowupRecord> {

    /**
     * 分页查询跟进信息
     * @param followupRecord
     * @param queryPage
     * @return
     */
    Result<IPage<FollowupRecord>> pageList(FollowupRecord followupRecord, QueryPage queryPage);

    /**
     * 分页查询跟进信息
     * @param followupRecordForm
     * @param queryPage
     * @return
     */
    Result<IPage<FollowupRecordDTO>> selectPageList(FollowupRecordForm followupRecordForm, QueryPage queryPage);

    /**
     * 合同跟进列表
     * @param contractRecordForm
     * @return
     */
    List<ContractRecordDTO> contractRecordList(ContractRecordForm contractRecordForm);

    /**
     * 添加跟进
     * @param followupRecordForm
     * @return
     */
    Boolean addFollowupRecord(FollowupRecordForm followupRecordForm);

}
