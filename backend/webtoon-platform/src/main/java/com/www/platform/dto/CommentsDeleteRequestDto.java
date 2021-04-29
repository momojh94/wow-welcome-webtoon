package com.www.platform.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentsDeleteRequestDto {
    private Long idx;
    private int account;

    @Builder
    public CommentsDeleteRequestDto(Long idx, int account) {
        this.idx = idx;
        this.account = account;
    }
}
