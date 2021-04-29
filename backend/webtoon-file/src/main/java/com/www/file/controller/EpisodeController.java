package com.www.file.controller;

import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.file.dto.EpisodeContents;
import com.www.file.dto.EpisodeDto;
import com.www.file.dto.EpisodePage;
import com.www.file.service.EpisodeService;
import com.www.file.service.MainService;
import com.www.file.service.WebtoonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class EpisodeController {
	@Autowired
	private WebtoonService webtoonService;
	@Autowired
	private EpisodeService episodeService;
	@Autowired
	private MainService mainService;
	
	private TokenChecker tokenchecker = new TokenChecker();
	//회차 등록
	@PostMapping("/myArticleDetail/{idx}")
	public Response<EpisodeDto> addEpisode(@RequestHeader("Authorization") String AccessToken,
			@PathVariable("idx") Long idx, @RequestPart("thumbnail") MultipartFile thumbnail, 
			@RequestPart("manuscript") MultipartFile[] manuscripts, @RequestParam("title") String title, 
			@RequestParam("author_comment") String author_comment) throws IllegalStateException, IOException {
		EpisodeDto episodeDto = new EpisodeDto(title, author_comment);
		Response<EpisodeDto> res = new Response<EpisodeDto>();
		int n = tokenchecker.validateToken(AccessToken);
		
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
	public Response<EpisodeDto> editEpisode(@RequestHeader("Authorization") String AccessToken,
			@PathVariable("webtoonIdx") Long webtoonIdx, @PathVariable("no") Long idx,
			@RequestPart("thumbnail") MultipartFile thumbnail, @RequestPart("manuscript") MultipartFile[] manuscripts, 
			@RequestParam("title") String title, @RequestParam("author_comment") String author_comment) throws IOException{
		EpisodeDto episodeDto = new EpisodeDto(title, author_comment);
		Response<EpisodeDto> res = new Response<EpisodeDto>();
		int n = tokenchecker.validateToken(AccessToken);
		
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
	public Response<EpisodePage> showEpisodeList(@RequestHeader("Authorization") String AccessToken,
			@PathVariable("idx") Long idx, @RequestParam(value="page", defaultValue = "1") Integer page){
		Response<EpisodePage> res = new Response<EpisodePage>();
		
		int n = tokenchecker.validateToken(AccessToken);
		Long user_idx = tokenchecker.getUserIdx(AccessToken);
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
	public Response<Long> deleteEpisode(@RequestHeader("Authorization") String AccessToken,
			@PathVariable("webtoon_idx") Long webtoonIdx, @PathVariable("no") int ep_no){
		
		Response<Long> res = new Response<Long>();
		int tk = tokenchecker.validateToken(AccessToken);
		Long user_idx = tokenchecker.getUserIdx(AccessToken);
		
		switch(tk) {
		case 0: //유효한 토큰
			return episodeService.deleteEpisode(webtoonIdx, ep_no, user_idx);
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
	public Response<EpisodeDto> getOriginalEpisode(@RequestHeader("Authorization") String AccessToken,
			@PathVariable("webtoon_idx") Long webtoonIdx, @PathVariable("no") int no) throws IOException{
		Response<EpisodeDto> res = new Response<EpisodeDto>();
		int tk = tokenchecker.validateToken(AccessToken);
		
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
	public Response<EpisodeContents> showMyEpisode(@RequestHeader("Authorization") String AccessToken,
			@PathVariable("webtoon_idx") Long webtoonIdx, @PathVariable("no") int no) throws IOException{
		
		Response<EpisodeContents> res = new Response<EpisodeContents>();
		int tk = tokenchecker.validateToken(AccessToken);
		
		switch(tk) {
		case 0: //유효한 토큰
			return mainService.showEpisode(webtoonIdx, no);
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
