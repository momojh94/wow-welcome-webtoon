package com.webtoon.api.comment.controller;

import com.webtoon.api.common.ApiResponse;
import com.webtoon.core.comment.dto.EpisodeStarRatingRequest;
import com.webtoon.core.comment.dto.EpisodeStarRatingResponse;
import com.webtoon.core.comment.service.StarRatingService;
import com.webtoon.core.user.service.TokenChecker;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class StarRatingController {
    private final StarRatingService starRatingService;
    private final TokenChecker tokenChecker;

    public StarRatingController(StarRatingService starRatingService,
                                TokenChecker tokenChecker) {
        this.starRatingService = starRatingService;
        this.tokenChecker = tokenChecker;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/episodes/{epIdx}/rating")
    public ApiResponse<EpisodeStarRatingResponse> create(@RequestHeader("Authorization") String accessToken,
                                                         @PathVariable("epIdx") Long epIdx,
                                                         @RequestBody @Valid EpisodeStarRatingRequest request) {
        switch (tokenChecker.validateToken(accessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(accessToken);
                if (userIdx == -1) {
                    break;
                }
                return ApiResponse.succeed(starRatingService.create(epIdx, userIdx, request.getRating()));
            case 1: // 만료된 토큰
                return ApiResponse.fail("44", "access denied : invalid access token");
            default:
        }

        return ApiResponse.fail("42", "access denied : maybe captured or faked token");
    }
}