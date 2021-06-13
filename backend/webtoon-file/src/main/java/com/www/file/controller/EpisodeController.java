package com.www.file.controller;

import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.file.dto.EpisodeContents;
import com.www.file.dto.EpisodeDto;
import com.www.file.dto.EpisodePage;
import com.www.file.service.EpisodeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class EpisodeController {
	private final EpisodeService episodeService;
	private final TokenChecker tokenChecker;

	public EpisodeController(EpisodeService episodeService, TokenChecker tokenChecker) {
		this.episodeService = episodeService;
		this.tokenChecker = tokenChecker;
	}

	// 에피소드 등록
	@PostMapping("/webtoons/{webtoonIdx}/episodes")
	public Response<EpisodeDto> createEpisode(@RequestHeader("Authorization") String accessToken,
											  @PathVariable("webtoonIdx") Long webtoonIdx, @RequestPart("thumbnail") MultipartFile thumbnail,
											  @RequestPart("manuscript") MultipartFile[] manuscripts, @RequestParam("title") String title,
											  @RequestParam("author_comment") String authorComment) throws IllegalStateException, IOException {
		EpisodeDto episodeDto = new EpisodeDto(title, authorComment);
		Response<EpisodeDto> res = new Response<EpisodeDto>();
		int n = tokenChecker.validateToken(accessToken);

		switch(n) {
			case 0: //유효한 토큰
				return episodeService.addEpisode(webtoonIdx, thumbnail, manuscripts, episodeDto);
			case 1: //만료된 토큰
				res.setCode(40);
				res.setMsg("reissue tokens");
				break;
			case 2: //에러,올바르지 않은 토큰
				res.setCode(42);
				res.setMsg("access denied : maybe captured or faked token");
				break;
		}

		return res;
	}

	// 해당 웹툰의 에피소드 목록 출력
	@GetMapping("/webtoons/{webtoonIdx}/episodes")
	public Response<EpisodePage> getEpisodes( @PathVariable("webtoonIdx") Long webtoonIdx,
											  @RequestParam(value="page", defaultValue = "1") Integer page){
		Response<EpisodePage> res = new Response<EpisodePage>();
		EpisodePage episodePage = new EpisodePage();

		switch (res.getCode()) {
			case 0:
				return episodeService.getEpisodeList(webtoonIdx, page, -1L);
			case 1:
				break;
		}
		return res;
	}

	// 해당 웹툰의 에피소드 출력
	@GetMapping("/webtoons/{webtoonIdx}/episodes/{epNo}/detail")
	public Response<EpisodeContents> getEpisode(@PathVariable("webtoonIdx") Long webtoonIdx,
												@PathVariable("epNo") int epNo) throws IOException {
		return episodeService.showEpisode(webtoonIdx, epNo);
	}

	// 내 웹툰의 에피소드 목록 출력
	@GetMapping("/users/webtoons/{webtoonIdx}/episodes")
	public Response<EpisodePage> getMyEpisodes(@RequestHeader("Authorization") String accessToken,
												 @PathVariable("webtoonIdx") Long webtoonIdx,
												 @RequestParam(value = "page", defaultValue = "1") Integer page) {
		Response<EpisodePage> res = new Response<EpisodePage>();
		int n = tokenChecker.validateToken(accessToken);
		Long userIdx = tokenChecker.getUserIdx(accessToken);

		switch(n) {
			case 0: //유효한 토큰
				switch (res.getCode()) {
					case 0:
						return episodeService.getEpisodeList(webtoonIdx, page, userIdx);
					case 1:
						res.setData(null);
						break;
				}
				return res;

			case 1: //만료된 토큰
				res.setCode(40);
				res.setMsg("reissue tokens");
				break;
			case 2: //에러,올바르지 않은 토큰
				res.setCode(42);
				res.setMsg("access denied : maybe captured or faked token");
				break;
		}

		return res;
	}

	// 해당 에피소드 정보 출력 (에피소드 수정 전 기존 정보 get)
	@GetMapping("/users/webtoons/{webtoonIdx}/episodes/{epNo}")
	public Response<EpisodeDto> getOriginalEpisode(@RequestHeader("Authorization") String accessToken,
												   @PathVariable("webtoonIdx") Long webtoonIdx,
												   @PathVariable("epNo") int epNo) throws IOException {
		Response<EpisodeDto> res = new Response<EpisodeDto>();
		int tk = tokenChecker.validateToken(accessToken);

		switch(tk) {
			case 0: //유효한 토큰
				return episodeService.getEpisodeInfo(webtoonIdx, epNo);
			case 1: //만료된 토큰
				res.setCode(40);
				res.setMsg("reissue tokens");
				break;
			case 2: //에러,올바르지 않은 토큰
				res.setCode(42);
				res.setMsg("access denied : maybe captured or faked token");
				break;
		}

		return res;
	}

	// 에피소드 정보 수정
	@PutMapping("/webtoons/{webtoonIdx}/episodes/{epNo}")
	public Response<EpisodeDto> editEpisode(@RequestHeader("Authorization") String accessToken,
											@PathVariable("webtoonIdx") Long webtoonIdx, @PathVariable("epNo") int epNo,
											@RequestPart("thumbnail") MultipartFile thumbnail, @RequestPart("manuscript") MultipartFile[] manuscripts,
											@RequestParam("title") String title, @RequestParam("author_comment") String authorComment) throws IOException {
		EpisodeDto episodeDto = new EpisodeDto(title, authorComment);
		Response<EpisodeDto> res = new Response<EpisodeDto>();
		int n = tokenChecker.validateToken(accessToken);
		
		switch(n) {
			case 0: //유효한 토큰
				return episodeService.editEpisode(webtoonIdx, epNo, thumbnail, manuscripts, episodeDto);
			case 1: //만료된 토큰
				res.setCode(40);
				res.setMsg("reissue tokens");
				break;
			case 2: //에러,올바르지 않은 토큰
				res.setCode(42);
				res.setMsg("access denied : maybe captured or faked token");
				break;
		}
		return res;
	}
	
	// 에피소드 삭제
	@DeleteMapping("/webtoons/{webtoonIdx}/episodes/{epNo}")
	public Response<Long> deleteEpisode(@RequestHeader("Authorization") String accessToken,
										@PathVariable("webtoonIdx") Long webtoonIdx,
										@PathVariable("epNo") int epNo){
		Response<Long> res = new Response<Long>();
		int tk = tokenChecker.validateToken(accessToken);
		Long userIdx = tokenChecker.getUserIdx(accessToken);
		
		switch(tk) {
			case 0: //유효한 토큰
				return episodeService.deleteEpisode(webtoonIdx, epNo, userIdx);
			case 1: //만료된 토큰
				res.setCode(40);
				res.setMsg("reissue tokens");
				break;
			case 2: //에러,올바르지 않은 토큰
				res.setCode(42);
				res.setMsg("access denied : maybe captured or faked token");
				break;
		}
		return res;
	}
}
