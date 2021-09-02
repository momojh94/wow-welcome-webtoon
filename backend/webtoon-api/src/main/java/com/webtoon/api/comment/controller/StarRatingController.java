package com.webtoon.api.comment.controller;

import com.webtoon.core.common.ApiResponse;
import com.webtoon.core.comment.dto.EpisodeStarRatingRequest;
import com.webtoon.core.comment.dto.EpisodeStarRatingResponse;
import com.webtoon.core.comment.service.StarRatingService;

import com.webtoon.core.user.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class StarRatingController {

    private final StarRatingService starRatingService;

    public StarRatingController(StarRatingService starRatingService) {
        this.starRatingService = starRatingService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/episodes/{epIdx}/rating")
    public ApiResponse<EpisodeStarRatingResponse> create(@AuthenticationPrincipal User user,
                                                         @PathVariable("epIdx") Long epIdx,
                                                         @RequestBody @Valid EpisodeStarRatingRequest request) {
        return ApiResponse.succeed(starRatingService.create(epIdx, user, request.getRating()));
    }
}