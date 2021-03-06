package com.ryuntech.saas.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ryuntech.common.model.BaseModel;
import com.ryuntech.saas.api.model.Attachment;
import com.ryuntech.saas.api.model.FollowupRecordComment;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


/**
 * @author EDZ
 */
@Data
@Accessors(chain = true)
public class ContractRecordDTO{
    private static final long serialVersionUID = 1L;

    private String followupId;

    /**
     * 预计回款金额
     */
    private String estimateAmount;
    /**
     * 合同编号
     */
    private String contractId;
    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 跟进内容
     */
    private String content;

    /**
     * 跟进人编号
     */
    private String staffId;

    /**
     * 跟进人姓名
     */
    private String staffName;

    /**
     * 附件编码
     */
    private String attachmentCode;

    /**
     * 图片文件集合
     */
    private List<Attachment> attachments;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date followupTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date contractTime;

    private String customerId;
    private String customerName;
    private String contractAmount;
    private String balanceAmount;
    private String collectionAmount;
    private String status;
    private String department;
    private String contractCode;
    private String contacts;
    private String contactsPhone;

    /**
     * 跟进评论
     */
    private List<FollowupRecordComment> followupRecordComments;
}
