package com.www.file.controller;

import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.file.dto.EpisodeContents;
import com.www.file.dto.EpisodePage;
import com.www.file.dto.MainWebtoonPage;
import com.www.file.service.EpisodeService;
import com.www.file.service.MainService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class MainController {
	@Autowired
	private MainService mainService;
	@Autowired
	private EpisodeService episodeService;
	
	private TokenChecker tokenChecker;
	
	//웹툰 리스트 출력 (한 페이지당 최대 20개)
	@GetMapping("/webtoonlist")
	public Response<MainWebtoonPage> showWebtoonList(@RequestParam(value="page", defaultValue = "1") Integer page,
			@RequestParam(value="sortBy", defaultValue = "0") int sort ){
		
		Response<MainWebtoonPage> res = new Response<MainWebtoonPage>();
		MainWebtoonPage webtoonpage =  mainService.getWebtoonList(page,res,sort);
		switch(res.getCode()) {
		case 0:
			res.setData(webtoonpage);
			break;
		case 1:
			break;
		}
		return res;
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
		
		return mainService.showEpisode(webtoonIdx, no);
		
	}
			
	
}
