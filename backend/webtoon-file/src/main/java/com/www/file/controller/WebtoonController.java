package com.www.file.controller;

import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.core.file.enums.EndFlag;
import com.www.core.file.enums.StoryGenre;
import com.www.core.file.enums.StoryType;
import com.www.file.dto.MainWebtoonPage;
import com.www.file.dto.WebtoonDto;
import com.www.file.dto.WebtoonPage;
import com.www.file.service.EpisodeService;
import com.www.file.service.WebtoonService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class WebtoonController {
	private final WebtoonService webtoonService;
	private final EpisodeService episodeService;
	private final TokenChecker tokenChecker;

	public WebtoonController(WebtoonService webtoonService, EpisodeService episodeService,
							 TokenChecker tokenChecker) {
		this.webtoonService = webtoonService;
		this.episodeService = episodeService;
		this.tokenChecker = tokenChecker;
	}

	//웹툰 리스트 출력 (한 페이지당 최대 20개)
	@GetMapping("/webtoonlist")
	public Response<MainWebtoonPage> showWebtoonList(@RequestParam(value="page", defaultValue = "1") Integer page,
													 @RequestParam(value="sortBy", defaultValue = "0") int sort ){

		Response<MainWebtoonPage> res = new Response<MainWebtoonPage>();
		MainWebtoonPage webtoonpage =  webtoonService.getWebtoonList(page,res,sort);
		switch(res.getCode()) {
			case 0:
				res.setData(webtoonpage);
				break;
			case 1:
				break;
		}
		return res;
	}

	//새 웹툰 등록
	@PostMapping("/myTitleDetail")
	public Response<WebtoonDto> createWebtoon(@RequestHeader("Authorization") String accessToken,
											  @RequestPart("thumbnail") MultipartFile file, @RequestParam("title") String title, @RequestParam("story_type") StoryType storyType,
											  @RequestParam("story_genre1") StoryGenre storyGenre1, @RequestParam("story_genre2") StoryGenre storyGenre2, @RequestParam("summary") String summary,
											  @RequestParam("plot") String plot, @RequestParam("end_flag") EndFlag endFlag) throws IOException {
		WebtoonDto webtoonDto = new WebtoonDto(title, storyType, storyGenre1, storyGenre2, summary, plot, endFlag);
		Response<WebtoonDto> res = new Response<WebtoonDto>();
		int n = tokenChecker.validateToken(accessToken);
		Long userIdx = tokenChecker.getUserIdx(accessToken);

		switch(n) {
			case 0: //유효한 토큰
				return webtoonService.createWebtoon(file, webtoonDto, userIdx);
			case 1: //만료된 토큰
				res.setCode(40);
				res.setMsg("reissue tokens");
				break;
			case 2: //유효하지 않은 토큰
				res.setCode(42);
				res.setMsg("access denied : maybe captured or faked token");
				break;
		}
		
		return res;
	}
	
	//내 웹툰 리스트 출력 (한 페이지당 최대 20개)
	@GetMapping("/myTitleList")
	public Response<WebtoonPage> showWebtoonList(@RequestHeader("Authorization") String accessToken,
												 @RequestParam(value="page", defaultValue = "1") Integer page){
		
		Response<WebtoonPage> res = new Response<WebtoonPage>();
		int n = tokenChecker.validateToken(accessToken);
		Long userIdx = tokenChecker.getUserIdx(accessToken);
		
		switch(n) {
			case 0: //유효한 토큰
				WebtoonPage webtoonpage = webtoonService.getWebtoonList(page, res, userIdx);

				switch(res.getCode()) {
				case 0:
					res.setData(webtoonpage);
					break;
				case 1:
					break;
				}
				return res;

			case 1:
				res.setCode(40);
				res.setMsg("reissue tokens");
				break;
			case 2:
				res.setCode(42);
				res.setMsg("access denied : maybe captured or faked token");
				break;
		}
		
		return res;
	}
	
	//내 웹툰 정보 수정
	@PutMapping("/myTitleDetail/{idx}")
	public Response<WebtoonDto> editWebtoon(@RequestHeader("Authorization") String accessToken, @PathVariable("idx") Long idx,
											@RequestPart("thumbnail") MultipartFile file, @RequestParam("title") String title, @RequestParam("story_type") StoryType storyType,
											@RequestParam("story_genre1") StoryGenre storyGenre1, @RequestParam("story_genre2") StoryGenre storyGenre2, @RequestParam("summary") String summary,
											@RequestParam("plot") String plot, @RequestParam("end_flag") EndFlag endFlag) throws IOException {
		
		WebtoonDto webtoonDto = new WebtoonDto(title, storyType, storyGenre1, storyGenre2, summary, plot, endFlag);
		Response<WebtoonDto> res = new Response<WebtoonDto>();
		int n = tokenChecker.validateToken(accessToken);
		
		switch(n) {
		case 0: 
			return webtoonService.editWebtoon(idx, file, webtoonDto);
		case 1: 
			res.setCode(40);
			res.setMsg("reissue tokens");
			break;
		case 2: 
			res.setCode(42);
			res.setMsg("access denied : maybe captured or faked token");
			break;
		}
		
		return res;
	}
	
	//기존 웹툰 정보 가져오기 
	@GetMapping("/myTitleDetail/{idx}")
	public Response<WebtoonDto> GetOriginalWebtoon(@RequestHeader("Authorization") String accessToken,
												   @PathVariable("idx") Long idx) throws IOException {
		
		Response<WebtoonDto> res = new Response<WebtoonDto>();
		int n = tokenChecker.validateToken(accessToken);
		
		switch(n) {
		case 0: 
			return webtoonService.getWebtoonInfo(idx);
		case 1: 
			res.setCode(40);
			res.setMsg("reissue tokens");
			break;
		case 2: 
			res.setCode(42);
			res.setMsg("access denied : maybe captured or faked token");
			break;
		}
		
		return res;
	}

	//내 웹툰 삭제 
	@DeleteMapping("/myArticleList/{idx}")
	public Response<Long> deleteWebtoon(@RequestHeader("Authorization") String accessToken,
										@PathVariable("idx") Long idx){
		
		Response<Long> res = new Response<Long>();
		int tk = tokenChecker.validateToken(accessToken);
		Long userIdx = tokenChecker.getUserIdx(accessToken);
		switch(tk) {
		case 0: 
			return  webtoonService.deleteWebtoon(idx, userIdx);
		case 1:
			res.setCode(40);
			res.setMsg("reissue tokens");
			break;
		case 2: 
			res.setCode(42);
			res.setMsg("access denied : maybe captured or faked token");
			break;
		}
		
		return res;
		
	}
}
