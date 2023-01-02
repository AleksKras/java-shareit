package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentDto;

public interface CommentMapper {
    Comment toComment(CommentDto commentDto);

    CommentDto toDto(Comment comment);

}
