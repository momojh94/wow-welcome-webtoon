package com.www.file.service;

import com.www.core.auth.entity.User;
import com.www.core.auth.repository.UserRepository;
import com.www.core.common.Response;
import com.www.core.file.entity.Episode;
import com.www.core.file.entity.Webtoon;
import com.www.core.file.repository.WebtoonRepository;
import com.www.file.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WebtoonService {
	private final WebtoonRepository webtoonRepository;
	private final UserRepository userRepository;

	public WebtoonService(WebtoonRepository webtoonRepository, UserRepository userRepository) {
		this.webtoonRepository = webtoonRepository;
		this.userRepository = userRepository;
	}

	//한 블럭 내 최대 페이지 번호 수
	private static final int BLOCK_PAGE_NUM_COUNT = 5;
	//한 페이지 내 최대 웹툰 출력 갯수
	private static final int PAGE_WEBTOON_COUNT = 10;
	//한 페이지 내 최대 회차 출력 갯수
	private static final int PAGE_EPISODE_COUNT = 7;
	
	@Value("${custom.path.upload-images}")
	private String filePath;
	
	public void checkCondition(MultipartFile file,WebtoonDto webtoonDto,Response<WebtoonDto> res) {
		if (webtoonDto.getTitle() == null) {
			res.setCode(10);
			res.setMsg("insert fail: need to register title");
			return;
		}
		if (webtoonDto.getPlot() == null) {
			res.setCode(11);
			res.setMsg("insert fail: need to register plot");
			return;
		}
		if (webtoonDto.getSummary() == null) {
			res.setCode(12);
			res.setMsg("insert fail: need to register summary");
			return;
		}
		if (file.isEmpty()) {
			res.setCode(13);
			res.setMsg("insert fail: need to register thumbnail");
			return;
		}
		
		res.setCode(0);
		res.setMsg("insert complete");
	}

	@Transactional
	public Response<MainWebtoonPage> getWebtoons(Integer pageNum, int sort) {
		Response<MainWebtoonPage> res = new Response<>();
		Page<Webtoon> page = null;
		switch(sort) {
			//기본정렬
			case 0:
				page = webtoonRepository.findAll(PageRequest.of(pageNum - 1, PAGE_EPISODE_COUNT));
				break;
			//조회순 정렬
			case 1:
				page = webtoonRepository.findAll(PageRequest.of(pageNum - 1, PAGE_EPISODE_COUNT,
						Sort.by(Sort.Direction.DESC,"hits")));
				break;
			//별점 정렬
			case 2:
				page = webtoonRepository.findAll(PageRequest.of(pageNum - 1, PAGE_EPISODE_COUNT,
						Sort.by(Sort.Direction.DESC,"ratingAvg")));
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
		if (totalpages == 0) {
			totalpages = 1;
		}

		//요청한 페이지 번호가 유효한 범위인지 체크
		if (pageNum > 0 && pageNum <= totalpages) {
			for (Webtoon webtoon : webtoons) {
				MainWebtoonDto webtoonDto = MainWebtoonDto.builder()
						.idx(webtoon.getIdx())
						.title(webtoon.getTitle())
						.thumbnail("http://localhost:8081/static/web_thumbnail/" + webtoon.getThumbnail())
						.storyGenre1(webtoon.getStoryGenre1())
						.storyGenre2(webtoon.getStoryGenre2())
						.author(webtoon.getUser().getName())
						.hits(webtoon.getHits())
						.ratingAvg(webtoon.getRatingAvg())
						.build();
				webtoonListDto.add(webtoonDto);
			}
			res.setCode(0);
			res.setMsg("show complete");
		} else {
			res.setCode(1);
			res.setMsg("fail : pageNum is not in valid range");
		}

		MainWebtoonPage mainWebtoonPage = new MainWebtoonPage(webtoonListDto, totalpages);
		res.setData(mainWebtoonPage);
		return res;
	}
	
	@Transactional
	public Response<WebtoonDto> createWebtoon(MultipartFile file, WebtoonDto webtoonDto, Long userIdx) throws IOException {
		Response<WebtoonDto> res = new Response<WebtoonDto>();
		Optional<User> users = userRepository.findById(userIdx);
		User user = users.get();
		//필수 조건 체크
		checkCondition(file,webtoonDto,res);
		if (res.getCode() != 0) {
			return res;
		} else {
			//필수 입력 조건 만족시
			UUID uuid = UUID.randomUUID();
			String fileName = uuid + "_" + file.getOriginalFilename();
			webtoonDto.setThumbnail(fileName);

			//file 외부 폴더로 이동
			File destinationFile = new File(filePath + "/web_thumbnail/" + fileName);
			destinationFile.getParentFile().mkdir();
			file.transferTo(destinationFile);

			Webtoon webtoon = Webtoon.builder()
					.title(webtoonDto.getTitle())
					.storyType(webtoonDto.getStoryType())
					.storyGenre1(webtoonDto.getStoryGenre1())
					.storyGenre2(webtoonDto.getStoryGenre2())
					.summary(webtoonDto.getSummary())
					.plot(webtoonDto.getPlot())
					.endFlag(webtoonDto.getEndFlag())
					.user(user)
					.thumbnail(fileName)
					.build();
			webtoonRepository.save(webtoon);

			res.setData(webtoonDto);
			return res;
		}
		
	}
	
	@Transactional
	public Response<WebtoonPage> getMyWebtoons(Integer pageNum, Long userIdx) {
		Response<WebtoonPage> res = new Response<>();
		Pageable pageable = PageRequest.of(pageNum-1, PAGE_WEBTOON_COUNT);
		Page<Webtoon> page = webtoonRepository.findAllByUserIdx(pageable, userIdx);
	    List<WebtoonListDto> webtoonListDto = new ArrayList<>();
		WebtoonPage webtoonPage = null;
		int totalpages = page.getTotalPages();
		if (totalpages == 0) {
			totalpages = 1;
		}
		
		//요청한 페이지 번호가 유효한 범위인지 체크
		if (pageNum > 0 && pageNum <= totalpages) {
			List<Webtoon> webtoons = page.getContent();
			for (Webtoon webtoon : webtoons) {
				WebtoonListDto webtoonDto = WebtoonListDto.builder()
						.idx(webtoon.getIdx())
						.title(webtoon.getTitle())
						.thumbnail("http://localhost:8081/static/web_thumbnail/" + webtoon.getThumbnail())
						.createdDate(webtoon.getCreatedDate())
						.build();

				List<Episode> episodeList = webtoon.getEpisodes();

				//웹툰 업데이트일 필드 
				//회차가 1개 이상 등록된 경우 가장 최신 회차의 업데이트 시간으로 설정
				if (!episodeList.isEmpty()) {
					Episode e = episodeList.get(episodeList.size() - 1);
					LocalDateTime lastUpdate = e.getUpdatedDate();
					webtoonDto.setLastUpdated(lastUpdate);
				}

				//회차가 등록되어있지 않은 경우 웹툰 생성시간으로 설정
				else {
					webtoonDto.setLastUpdated(webtoon.getCreatedDate());
				}
				webtoonListDto.add(webtoonDto);
			}
			webtoonPage = new WebtoonPage(webtoonListDto, totalpages);
			res.setCode(0);
			res.setMsg("show complete");
		} else {
			res.setCode(1);
			res.setMsg("fail : pageNum is not in valid range");
		}

		res.setData(webtoonPage);
	    return res;
	}
	
	
	@Transactional
	public Response<WebtoonDto> editWebtoon(Long idx, MultipartFile file, WebtoonDto webtoonDto) throws IOException {
		Response<WebtoonDto> res = new Response<WebtoonDto>();
		
		if(!webtoonRepository.existsById(idx)) {
      		res.setCode(1);
      		res.setMsg("fail: Webtoon do not exists");
      		return res;
        }
		
		checkCondition(file, webtoonDto, res);

		if (res.getCode() != 0) {
			return res;
		} else {
			Optional<Webtoon> WebtoonEntityWrapper = webtoonRepository.findById(idx);
			Webtoon webtoon = WebtoonEntityWrapper.get();

			webtoon.setEndFlag(webtoonDto.getEndFlag());
			webtoon.setStoryGenre1(webtoonDto.getStoryGenre1());
			webtoon.setStoryGenre2(webtoonDto.getStoryGenre2());
			webtoon.setPlot(webtoonDto.getPlot());
			webtoon.setSummary(webtoonDto.getSummary());
			webtoon.setTitle(webtoonDto.getTitle());
			webtoon.setStoryType(webtoonDto.getStoryType());

			if (!file.isEmpty()) {
				UUID uuid = UUID.randomUUID();
				String fileName = uuid + "_" + file.getOriginalFilename();
				System.out.println(fileName);
				webtoonDto.setThumbnail(fileName);
				//file 외부 폴더로 이동
				File destinationFile = new File(filePath + "/web_thumbnail/" + fileName);
				destinationFile.getParentFile().mkdir();
				file.transferTo(destinationFile);
			}

			webtoonRepository.save(webtoon);
			res.setData(webtoonDto);
			return res;
		}
	}
	
	public Response<Long> deleteWebtoon(Long webtoonIdx, Long userIdx) {
		Response<Long> res = new Response<Long>();

        //해당 웹툰 idx가 유효한지 체크
		if (!webtoonRepository.existsById(webtoonIdx)) {
			res.setCode(1);
			res.setMsg("delete fail: Webtoon do not exists");
		} else {
			Optional<Webtoon> WebtoonEntityWrapper = webtoonRepository.findById(webtoonIdx);
			Webtoon webtoon = WebtoonEntityWrapper.get();
			if (webtoon.getUser().getIdx() != userIdx) {
				res.setMsg("delete fail: user do not have authority");
				res.setCode(1);
			} else {
				webtoonRepository.delete(webtoon);
				res.setMsg("delete complete");
				res.setCode(0);
			}
		}

        return res;
	}
	
	public Response<WebtoonDto> getWebtoon(Long webtoonIdx){
		Response<WebtoonDto> res = new Response<WebtoonDto>();

		if (!webtoonRepository.existsById(webtoonIdx)) {
			res.setCode(1);
			res.setMsg("Webtoon do not exist");
			return res;
		}

		Optional<Webtoon> WebtoonEntityWrapper = webtoonRepository.findById(webtoonIdx);
        Webtoon webtoon = WebtoonEntityWrapper.get();
        
		WebtoonDto webtoonDto = WebtoonDto.builder()
				.title(webtoon.getTitle())
				.storyType(webtoon.getStoryType())
				.storyGenre1(webtoon.getStoryGenre1())
				.storyGenre2(webtoon.getStoryGenre2())
				.summary(webtoon.getSummary())
				.plot(webtoon.getPlot())
				.endFlag(webtoon.getEndFlag())
				.thumbnail(webtoon.getThumbnail())
				.build();
		res.setData(webtoonDto);
		res.setCode(0);
		res.setMsg("get webtoon info");
		
		return res;
	}
}
