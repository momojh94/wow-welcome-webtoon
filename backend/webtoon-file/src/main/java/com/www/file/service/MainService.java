package com.www.file.service;

import com.www.core.auth.repository.UserRepository;
import com.www.core.common.Response;
import com.www.core.file.entity.Episode;
import com.www.core.file.entity.Webtoon;
import com.www.core.file.repository.EpisodeRepository;
import com.www.core.file.repository.WebtoonRepository;
import com.www.file.dto.EpisodeContents;
import com.www.file.dto.MainWebtoonDto;
import com.www.file.dto.MainWebtoonPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MainService {
	@Autowired
	private WebtoonRepository webtoonRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EpisodeRepository episodeRepository;
	
	//한 블럭 내 최대 페이지 번호 수
	private static final int BLOCK_PAGE_NUM_COUNT = 5;
	//한 페이지 내 최대 웹툰 출력 갯수
	private static final int PAGE_WEBTOON_COUNT = 10;
	//한 페이지 내 최대 회차 출력 갯수
	private static final int PAGE_EPISODE_COUNT = 7;
	
	public MainService(WebtoonRepository webtoonRepository) {
		this.webtoonRepository = webtoonRepository;
	}
	
	@Transactional
	public MainWebtoonPage getWebtoonList(Integer pageNum, Response<MainWebtoonPage> res, int sort) {
		
		Page<Webtoon> page = null;
		switch(sort) {
		//기본정렬
		case 0:
			page = webtoonRepository.findAll(PageRequest.of(pageNum-1, PAGE_EPISODE_COUNT));
			break;
		//조회순 정렬
		case 1:
			page = webtoonRepository.findAll(PageRequest.of(pageNum-1, PAGE_EPISODE_COUNT, 
					Sort.by(Sort.Direction.DESC,"hits")));
			break;
		//별점 정렬
		case 2:
			page = webtoonRepository.findAll(PageRequest.of(pageNum-1, PAGE_EPISODE_COUNT, 
					Sort.by(Sort.Direction.DESC,"epRatingAvg")));
			break;
		default:
			res.setCode(1);
	    	res.setMsg("fail : sortNum is not valid");
	    	break;
		}
		
		List<Webtoon> webtoons = page.getContent();
		List<MainWebtoonDto> webtoonListDto = new ArrayList<>();
		int totalpages = page.getTotalPages();
		
		//등록된 웹툰이 없을 경우
		if(totalpages == 0 ) totalpages = 1;
		
		//요청한 페이지 번호가 유효한 범위인지 체크
		if(pageNum>0 && pageNum<=totalpages) {
			for(Webtoon webtoon : webtoons) {
				MainWebtoonDto webtoonDto = MainWebtoonDto.builder()
						.idx(webtoon.getIdx())
						.title(webtoon.getTitle())
						.thumbnail("http://localhost:8081/static/web_thumbnail/"+webtoon.getThumbnail())
						.genre1(webtoon.getStoryGenre1())
						.genre2(webtoon.getStoryGenre2())
						.author(webtoon.getUser().getName())
						.hits(webtoon.getHits())
						.epRatingAvg(webtoon.getRatingAvg())
						.build();
				webtoonListDto.add(webtoonDto);
			}
			res.setCode(0);
		    res.setMsg("show complete");
		}
		
		else {
	    	res.setCode(1);
	    	res.setMsg("fail : pageNum is not in valid range");
		}
		
		MainWebtoonPage mainWebtoonPage = new MainWebtoonPage(webtoonListDto, totalpages);
	    return mainWebtoonPage;
	}
	
	public Response<EpisodeContents> showEpisode(Long webtoonIdx, int no){
		
		Response<EpisodeContents> res = new Response<EpisodeContents>();
		Optional<Webtoon> webtoonWrapper = webtoonRepository.findById(webtoonIdx);
		Webtoon webtoon = webtoonWrapper.get();
		List<Episode> epList = webtoon.getEpisodes();
		Episode episode = new Episode();
		webtoon.setHits(webtoon.getHits()+1);
		webtoonRepository.save(webtoon);
		
		for(Episode ep : epList) {
			if(no == ep.getEpNo()) {
				episode = ep;
				break;
			}
		}
		episode.setHits(episode.getHits()+1);
		episodeRepository.save(episode);
		
		EpisodeContents episodeContents = EpisodeContents.builder()
				.webtoonTitle(webtoon.getTitle())
				.title(episode.getTitle())
				.authorComment(episode.getAuthorComment())
				.author(webtoon.getUser().getName())
				.summary(webtoon.getSummary())
				.thumbnail("http://localhost:8081/static/web_thumbnail/"+webtoon.getThumbnail())
				.ratingPersonTotal(episode.getRatingPersonTotal())
				.ratingAvg(episode.getRatingAvg())
				.epHits(episode.getHits())
				.build();
		
		String content = episode.getContents();
		String[] contents = content.split(";");
		for(int i=0;i<contents.length;i++) {
			contents[i] = "http://localhost:8081/static/webtoon/"+contents[i];
			System.out.println(contents[i]);
		}
		episodeContents.setContents(contents);
		
		res.setData(episodeContents);
		res.setCode(0);
		res.setMsg("show complete");
		return res;
		
	}
	
	
}
