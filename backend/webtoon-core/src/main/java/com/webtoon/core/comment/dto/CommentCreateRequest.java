package com.webtoon.core.comment.dto;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateRequest {

    @NotBlank(message = "content를 입력해주세요.")
    @Size(max = 500, message = "댓글은 최대 500자까지 작성할 수 있습니다.")
    private String content;

    @Builder
    public CommentCreateRequest(String content) {
        this.content = content;
    }
}
