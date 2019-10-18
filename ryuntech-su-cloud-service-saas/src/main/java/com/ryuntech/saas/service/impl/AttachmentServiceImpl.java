package com.ryuntech.saas.service.impl;

import com.ryuntech.common.service.impl.BaseServiceImpl;
import com.ryuntech.saas.api.mapper.AttachmentMapper;
import com.ryuntech.saas.api.model.Attachment;
import com.ryuntech.saas.api.service.IAttachmentService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 附件表 服务实现类
 * </p>
 *
 * @author antu
 * @since 2019-10-17
 */
@Service
public class AttachmentServiceImpl extends BaseServiceImpl<AttachmentMapper, Attachment> implements IAttachmentService {

}