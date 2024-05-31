package com.siyu.server.entity.mapper;

import com.siyu.server.entity.dto.flowable.CommentDTO;
import org.flowable.engine.task.Comment;

public class CommentDTOMapper {

    public static CommentDTO copy(Comment source, CommentDTO target) {
        target.setContent(source.getFullMessage());
        target.setType(source.getType());
        return target;
    }
}
