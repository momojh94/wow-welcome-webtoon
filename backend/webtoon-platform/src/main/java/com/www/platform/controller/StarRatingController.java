package com.www.platform.controller;

import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.platform.dto.*;
import com.www.platform.service.StarRatingService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class StarRatingController {

    private StarRatingService starRatingService;
    private TokenChecker tokenChecker;

    @PostMapping("/episodes/{epIdx}/rating")
    public Response<EpisodeStarRatingResponseDto> insertStarRating(@RequestHeader("Authorization") String AccessToken,
                                                                   @PathVariable("epIdx") Long epIdx, @RequestBody StarRatingRequestDto dto) {
        Response<EpisodeStarRatingResponseDto> result = new Response<EpisodeStarRatingResponseDto>();

        switch (tokenChecker.validateToken(AccessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(AccessToken);
                if (-1 == userIdx) {
                    result.setCode(42);
                    result.setMsg("access denied : maybe captured or faked token");
                    break;
                }
                result = starRatingService.insertStarRating(epIdx, userIdx, dto.getRating());
                break;
            case 1: // 만료된 토큰
                result.setCode(44);
                result.setMsg("access denied : invalid access token");
                break;
            case 2: // 에러,올바르지 않은 토큰
                result.setCode(42);
                result.setMsg("access denied : maybe captured or faked token");
                break;
        }

        return result;
    }
}
