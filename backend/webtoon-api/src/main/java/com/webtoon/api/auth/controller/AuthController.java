package com.webtoon.api.auth.controller;


import com.webtoon.core.common.ApiResponse;
import com.webtoon.core.security.dto.RefreshTokenRequest;
import com.webtoon.core.security.service.AuthService;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.security.dto.UserLoginResponse;
import com.webtoon.core.security.dto.UserLoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<String>> login(@RequestBody @Valid UserLoginRequest request) {
        UserLoginResponse userLoginResponse = authService.login(request.getAccount(), request.getPassword());

        return ResponseEntity.ok()
                             .header(AUTHORIZATION, userLoginResponse.getAccessToken())
                             .body(ApiResponse.succeed(userLoginResponse.getRefreshToken()));
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping()
    public ApiResponse<Void> logout(@AuthenticationPrincipal User user,
                                    @RequestBody @Valid RefreshTokenRequest request) {
        authService.logout(user, request.getRefreshToken());
        return ApiResponse.succeed();
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<Void>> reissueAccessToken(@RequestHeader(AUTHORIZATION) String accessToken,
                                                                @RequestBody @Valid RefreshTokenRequest request) {
        String reissuedAccessToken = authService.reissueAccessToken(accessToken, request.getRefreshToken());
        return ResponseEntity.ok()
                             .header(AUTHORIZATION, reissuedAccessToken)
                             .body(ApiResponse.succeed());
    }
}
