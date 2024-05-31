package com.siyu.server.entity.mapper;

import com.siyu.server.entity.dto.AttachmentDTO;
import org.flowable.engine.task.Attachment;

public class AttachmentDTOMapper {

    public static AttachmentDTO copy(Attachment source, AttachmentDTO target) {
        target.setType(source.getType());
        target.setContent(source.getUrl());
        return target;
    }
}
