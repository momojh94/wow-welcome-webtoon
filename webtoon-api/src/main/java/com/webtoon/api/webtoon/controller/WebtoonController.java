package com.webtoon.api.webtoon.controller;

import com.webtoon.core.common.ApiResponse;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.webtoon.domain.enums.EndFlag;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import com.webtoon.core.webtoon.domain.enums.StoryType;
import com.webtoon.core.webtoon.dto.MyWebtoonsResponse;
import com.webtoon.core.webtoon.dto.WebtoonCreateRequest;
import com.webtoon.core.webtoon.dto.WebtoonEditRequest;
import com.webtoon.core.webtoon.dto.WebtoonResponse;
import com.webtoon.core.webtoon.dto.WebtoonsMainPageResponse;
import com.webtoon.core.webtoon.service.WebtoonService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class WebtoonController {

	private final WebtoonService webtoonService;

	public WebtoonController(WebtoonService webtoonService) {
		this.webtoonService = webtoonService;
	}

	// 해당 웹툰 정보 출력
	@GetMapping("/webtoons/{webtoonIdx}")
	public ApiResponse<WebtoonResponse> findWebtoon(@PathVariable("webtoonIdx") Long webtoonIdx) {
		return ApiResponse.succeed(webtoonService.findWebtoon(webtoonIdx));
	}

	// 웹툰 리스트 출력 (한 페이지당 최대 20개), 정렬 기준 마다 조회 방식 다름
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/webtoons")
	public ApiResponse<WebtoonsMainPageResponse> findAllWebtoon(@RequestParam(value="page", defaultValue = "1") Integer page,
																@RequestParam(value="sortBy", defaultValue = "0") int sort ){
		return ApiResponse.succeed(webtoonService.findAllWebtoon(page, sort));
	}

	//내 웹툰 목록 출력 (한 페이지당 최대 20개)
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/users/webtoons")
	public ApiResponse<MyWebtoonsResponse> findAllMyWebtoon(@AuthenticationPrincipal User user,
															@RequestParam(value="page", defaultValue = "1") Integer page) {
		return ApiResponse.succeed(webtoonService.findAllMyWebtoon(page, user));
	}

	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/webtoons")
	public ApiResponse<Void> create(@AuthenticationPrincipal User user, @RequestPart("thumbnail") MultipartFile thumbnailFile,
									@RequestParam("title") String title, @RequestParam("story_type") StoryType storyType,
									@RequestParam("story_genre") StoryGenre storyGenre, @RequestParam("summary") String summary,
									@RequestParam("plot") String plot, @RequestParam("end_flag") EndFlag endFlag) throws IOException {
		WebtoonCreateRequest request = new WebtoonCreateRequest(title, storyType, storyGenre,
				summary, plot, endFlag);
		webtoonService.create(user, thumbnailFile, request);
		return ApiResponse.succeed();
	}

	//내 웹툰 정보 수정
	@ResponseStatus(HttpStatus.OK)
	@PutMapping("/webtoons/{webtoonIdx}")
	public ApiResponse<Void> update(@AuthenticationPrincipal User user, @PathVariable("webtoonIdx") Long webtoonIdx,
									@RequestPart("thumbnail") MultipartFile thumbnailFile, @RequestParam("title") String title,
									@RequestParam("story_type") StoryType storyType, @RequestParam("story_genre") StoryGenre storyGenre,
									@RequestParam("summary") String summary, @RequestParam("plot") String plot,
									@RequestParam("end_flag") EndFlag endFlag) throws IOException {
		WebtoonEditRequest request = new WebtoonEditRequest(title, storyType, storyGenre,
				summary, plot, endFlag);
		webtoonService.update(user, webtoonIdx, thumbnailFile, request);
		return ApiResponse.succeed();
	}

	//내 웹툰 삭제
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping("/webtoons/{webtoonIdx}")
	public ApiResponse<Void> delete(@AuthenticationPrincipal User user,
									@PathVariable("webtoonIdx") Long webtoonIdx) {
		webtoonService.delete(webtoonIdx, user);
		return ApiResponse.succeed();
	}
}