package com.www.file.controller;

import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.file.dto.EpisodeContents;
import com.www.file.dto.EpisodeDto;
import com.www.file.dto.EpisodePage;
import com.www.file.service.EpisodeService;
import com.www.file.service.WebtoonService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class EpisodeController {
	private final WebtoonService webtoonService;
	private final EpisodeService episodeService;
	private final TokenChecker tokenChecker;

	public EpisodeController(WebtoonService webtoonService, EpisodeService episodeService,
							 TokenChecker tokenChecker) {
		this.webtoonService = webtoonService;
		this.episodeService = episodeService;
		this.tokenChecker = tokenChecker;
	}

	//회차 리스트 출력
	@GetMapping("/episode/{idx}")
	public Response<EpisodePage> showEpisodeList( @PathVariable("idx") Long idx,
												  @RequestParam(value="page", defaultValue = "1") Integer page){
		Response<EpisodePage> res = new Response<EpisodePage>();
		EpisodePage episodePage = new EpisodePage();

		switch (res.getCode()) {
			case 0:
				return episodeService.getEpisodeList(idx,page,-1L);
			case 1:
				break;
		}
		return res;

	}

	//회차 출력
	@GetMapping("/detail/{webtoonIdx}/{no}")
	public Response<EpisodeContents> showEpisode(@PathVariable("webtoonIdx") Long webtoonIdx,
												 @PathVariable("no") int no) throws IOException{
		return episodeService.showEpisode(webtoonIdx, no);
	}

	//회차 등록
	@PostMapping("/myArticleDetail/{idx}")
	public Response<EpisodeDto> addEpisode(@RequestHeader("Authorization") String accessToken,
			@PathVariable("idx") Long idx, @RequestPart("thumbnail") MultipartFile thumbnail, 
			@RequestPart("manuscript") MultipartFile[] manuscripts, @RequestParam("title") String title, 
			@RequestParam("author_comment") String authorComment) throws IllegalStateException, IOException {
		EpisodeDto episodeDto = new EpisodeDto(title, authorComment);
		Response<EpisodeDto> res = new Response<EpisodeDto>();
		int n = tokenChecker.validateToken(accessToken);
		
		switch(n) {
		case 0: //유효한 토큰
			return episodeService.addEpisode(idx, thumbnail, manuscripts, episodeDto);
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

	//회차 정보 수정
	@PutMapping("/myArticleDetail/{webtoonIdx}/{no}")
	public Response<EpisodeDto> editEpisode(@RequestHeader("Authorization") String accessToken,
			@PathVariable("webtoonIdx") Long webtoonIdx, @PathVariable("no") Long idx,
			@RequestPart("thumbnail") MultipartFile thumbnail, @RequestPart("manuscript") MultipartFile[] manuscripts, 
			@RequestParam("title") String title, @RequestParam("author_comment") String authorComment) throws IOException{
		EpisodeDto episodeDto = new EpisodeDto(title, authorComment);
		Response<EpisodeDto> res = new Response<EpisodeDto>();
		int n = tokenChecker.validateToken(accessToken);
		
		switch(n) {
			case 0: //유효한 토큰
				return episodeService.editEpisode(webtoonIdx, idx, thumbnail, manuscripts, episodeDto);
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
	
	//회차리스트 출력
	@GetMapping("/myArticleList/{idx}")
	public Response<EpisodePage> showEpisodeList(@RequestHeader("Authorization") String accessToken,
			@PathVariable("idx") Long idx, @RequestParam(value="page", defaultValue = "1") Integer page){
		Response<EpisodePage> res = new Response<EpisodePage>();
		
		int n = tokenChecker.validateToken(accessToken);
		Long user_idx = tokenChecker.getUserIdx(accessToken);
		switch(n) {
		case 0: //유효한 토큰
			switch (res.getCode()) {
			case 0:
				return episodeService.getEpisodeList(idx,page,user_idx);
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
	
	//회차 삭제 
	@DeleteMapping("/myArticleList/{webtoon_idx}/{no}")
	public Response<Long> deleteEpisode(@RequestHeader("Authorization") String accessToken,
			@PathVariable("webtoonIdx") Long webtoonIdx, @PathVariable("no") int epNo){
		
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
	
	//기존 회차 정보 가져오기
	@GetMapping("/myArticleDetail/{webtoon_idx}/{no}")
	public Response<EpisodeDto> getOriginalEpisode(@RequestHeader("Authorization") String accessToken,
			@PathVariable("webtoonIdx") Long webtoonIdx, @PathVariable("no") int no) throws IOException{
		Response<EpisodeDto> res = new Response<EpisodeDto>();
		int tk = tokenChecker.validateToken(accessToken);
		
		switch(tk) {
		case 0: //유효한 토큰
			return episodeService.getEpisodeInfo(webtoonIdx, no);
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
	
	//회차 출력
	@GetMapping("/mydetail/{webtoon_idx}/{no}")
	public Response<EpisodeContents> showMyEpisode(@RequestHeader("Authorization") String accessToken,
			@PathVariable("webtoon_idx") Long webtoonIdx, @PathVariable("no") int no) throws IOException{
		
		Response<EpisodeContents> res = new Response<EpisodeContents>();
		int tk = tokenChecker.validateToken(accessToken);
		
		switch(tk) {
		case 0: //유효한 토큰
			return episodeService.showEpisode(webtoonIdx, no);
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
