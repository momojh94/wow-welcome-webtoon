package com.webtoon.core.comment.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentCreateRequest {

    @NotBlank
    @Size(max = 500)
    private String content;

    public CommentCreateRequest(String content) {
        this.content = content;
    }
}
