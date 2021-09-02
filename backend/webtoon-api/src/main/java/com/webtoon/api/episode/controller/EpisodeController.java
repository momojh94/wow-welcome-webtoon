package com.webtoon.api.episode.controller;

import com.webtoon.core.common.ApiResponse;
import com.webtoon.core.episode.dto.EpisodeCreateRequest;
import com.webtoon.core.episode.dto.EpisodeDetailResponse;
import com.webtoon.core.episode.dto.EpisodeResponse;
import com.webtoon.core.episode.dto.EpisodeUpdateRequest;
import com.webtoon.core.episode.dto.EpisodesViewPageResponse;
import com.webtoon.core.episode.service.EpisodeService;

import com.webtoon.core.user.domain.User;
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
public class EpisodeController {

	private final EpisodeService episodeService;

	public EpisodeController(EpisodeService episodeService) {
		this.episodeService = episodeService;
	}

	// 해당 에피소드를 실제로 보는 페이지 정보 출력
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/webtoons/{webtoonIdx}/episodes/{epNo}/detail")
	public ApiResponse<EpisodeDetailResponse> findEpisodeDetail(@PathVariable("webtoonIdx") Long webtoonIdx,
																@PathVariable("epNo") int epNo) {
		return ApiResponse.succeed(episodeService.findEpisodeDetail(webtoonIdx, epNo));
	}

	// 해당 에피소드 정보 출력(에피소드 update 하기 위해 기존 정보 얻기용)
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/webtoons/{webtoonIdx}/episodes/{epNo}")
	public ApiResponse<EpisodeResponse> findEpisode(@PathVariable("webtoonIdx") Long webtoonIdx,
													@PathVariable("epNo") int epNo) {
		return ApiResponse.succeed(episodeService.findEpisode(webtoonIdx, epNo));
	}

	// 해당 웹툰의 에피소드 목록 출력
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/webtoons/{webtoonIdx}/episodes")
	public ApiResponse<EpisodesViewPageResponse> findAllEpisode(@PathVariable("webtoonIdx") Long webtoonIdx,
																@RequestParam(value = "page", defaultValue = "1") int page) {
		return ApiResponse.succeed(episodeService.findAllEpisodeByPage(webtoonIdx, page));
	}

	// 에피소드 등록
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/webtoons/{webtoonIdx}/episodes")
	public ApiResponse<Void> create(@AuthenticationPrincipal User user, @PathVariable("webtoonIdx") Long webtoonIdx,
									@RequestPart("thumbnail") MultipartFile thumbnailFile, @RequestPart("manuscript") MultipartFile[] contentImages,
									@RequestParam("title") String title, @RequestParam("author_comment") String authorComment)
			throws IllegalStateException, IOException {
		EpisodeCreateRequest request = new EpisodeCreateRequest(title, authorComment);
		episodeService.create(webtoonIdx, user, request, thumbnailFile, contentImages);
		return ApiResponse.succeed();
	}

	// 에피소드 정보 수정
	@ResponseStatus(HttpStatus.OK)
	@PutMapping("/webtoons/{webtoonIdx}/episodes/{epNo}")
	public ApiResponse<Void> update(@AuthenticationPrincipal User user, @PathVariable("webtoonIdx") Long webtoonIdx, @PathVariable("epNo") int epNo,
									@RequestPart("thumbnail") MultipartFile thumbnailFile, @RequestPart("manuscript") MultipartFile[] contentImages,
									@RequestParam("title") String title, @RequestParam("author_comment") String authorComment) throws IOException {
		EpisodeUpdateRequest request = new EpisodeUpdateRequest(title, authorComment);
		episodeService.update(user, webtoonIdx, epNo, request, thumbnailFile, contentImages);
		return ApiResponse.succeed();
	}

	// 에피소드 삭제
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping("/webtoons/{webtoonIdx}/episodes/{epNo}")
	public ApiResponse<Void> delete(@AuthenticationPrincipal User user, @PathVariable("webtoonIdx") Long webtoonIdx,
									@PathVariable("epNo") int epNo){
		episodeService.delete(webtoonIdx, epNo, user);
		return ApiResponse.succeed();
	}
}
