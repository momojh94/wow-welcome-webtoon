package com.www.platform.controller;

import com.www.core.common.ApiResponse;
import com.www.core.common.service.TokenChecker;
import com.www.platform.dto.EpisodeStarRatingRequestDto;
import com.www.platform.dto.EpisodeStarRatingResponseDto;
import com.www.platform.service.StarRatingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    public ApiResponse<EpisodeStarRatingResponseDto> createStarRating(@RequestHeader("Authorization") String accessToken,
                                                                      @PathVariable("epIdx") Long epIdx,
                                                                      @RequestBody EpisodeStarRatingRequestDto request) {
        switch (tokenChecker.validateToken(accessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(accessToken);
                if (userIdx == -1) {
                    break;
                }
                return ApiResponse.of(starRatingService.createStarRating(epIdx, userIdx, request.getRating()));
            case 1: // 만료된 토큰
                return ApiResponse.fail("44", "access denied : invalid access token");
            default:
        }

        return ApiResponse.fail("42", "access denied : maybe captured or faked token");
    }
}