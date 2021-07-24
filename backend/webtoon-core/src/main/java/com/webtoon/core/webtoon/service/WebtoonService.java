package com.webtoon.core.webtoon.service;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.webtoon.core.common.exception.ApplicationException;
import com.webtoon.core.common.util.FileUploader;
import com.webtoon.core.webtoon.dto.MyWebtoonResponse;
import com.webtoon.core.webtoon.dto.MyWebtoonsResponse;
import com.webtoon.core.webtoon.dto.WebtoonCreateRequest;
import com.webtoon.core.webtoon.dto.WebtoonEditRequest;
import com.webtoon.core.webtoon.dto.WebtoonMainPageResponse;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.UserRepository;
import com.webtoon.core.webtoon.domain.Webtoon;
import com.webtoon.core.webtoon.domain.WebtoonRepository;
import com.webtoon.core.webtoon.dto.WebtoonResponse;
import com.webtoon.core.webtoon.dto.WebtoonsMainPageResponse;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.webtoon.core.common.exception.ErrorType.USER_IS_NOT_AUTHOR_OF_WEBTOON;
import static com.webtoon.core.common.exception.ErrorType.USER_NOT_FOUND;
import static com.webtoon.core.common.exception.ErrorType.WEBTOON_NOT_FOUND;

@Service
public class WebtoonService {
	private final WebtoonRepository webtoonRepository;
	private final UserRepository userRepository;

	public WebtoonService(WebtoonRepository webtoonRepository,
						  UserRepository userRepository) {
		this.webtoonRepository = webtoonRepository;
		this.userRepository = userRepository;
	}

	//한 블럭 내 최대 페이지 번호 수
	private static final int BLOCK_PAGE_NUM_COUNT = 5;
	//한 페이지 내 최대 웹툰 출력 갯수
	private static final int PAGE_WEBTOON_COUNT = 10;
	//한 페이지 내 최대 회차 출력 갯수
	private static final int PAGE_EPISODE_COUNT = 7;

	public WebtoonResponse findWebtoon(Long webtoonIdx){
		Webtoon webtoon = webtoonRepository.findById(webtoonIdx)
										   .orElseThrow(() -> new ApplicationException(WEBTOON_NOT_FOUND));
		return new WebtoonResponse(webtoon);
	}

	public WebtoonsMainPageResponse findAllWebtoon(Integer pageNum, int sort) {
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
				// TODO : fail : sortNum is not valid
				break;
		}

		List<Webtoon> webtoons = page.getContent();
		int totalPages = page.getTotalPages();

		//등록된 웹툰이 없을 경우
		if (totalPages == 0) {
			totalPages = 1;
		}

		// TODO : 요청한 페이지 번호 > totalPages 일 때 처리

		return new WebtoonsMainPageResponse(webtoons.stream()
													.map(WebtoonMainPageResponse::new)
													.collect(Collectors.toList()), totalPages);
	}

	public MyWebtoonsResponse findAllMyWebtoon(Integer pageNum, Long userIdx) {
		Pageable pageable = PageRequest.of(pageNum - 1, PAGE_WEBTOON_COUNT);
		Page<Webtoon> page = webtoonRepository.findAllByUserIdx(pageable, userIdx);

		List<Webtoon> webtoons = page.getContent();
		int totalPages = page.getTotalPages();
		if (totalPages == 0) {
			totalPages = 1;
		}

		// TODO : 요청한 페이지 번호 > totalPages 일 때 처리

		return new MyWebtoonsResponse(webtoons.stream()
											  .map(MyWebtoonResponse::new)
											  .collect(Collectors.toList()), totalPages);
	}

	@Transactional
	public void create(Long userIdx, MultipartFile thumbnailFile,
					   WebtoonCreateRequest request) throws IOException {
		User user = userRepository.findById(userIdx)
								  .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
		// TODO : file is empty
		String thumbnail = FileUploader.uploadWebtoonThumbnail(thumbnailFile);

		Webtoon webtoon = request.toEntityWith(thumbnail, user);
		webtoonRepository.save(webtoon);
	}

	@Transactional
	public void update(Long webtoonIdx, Long userIdx, MultipartFile thumbnailFile,
					   WebtoonEditRequest request) throws IOException {
		Webtoon webtoon = webtoonRepository.findById(webtoonIdx)
										   .orElseThrow(() -> new ApplicationException(WEBTOON_NOT_FOUND));

		if (!webtoon.wasDrawnBy(userIdx)) {
			throw new ApplicationException(USER_IS_NOT_AUTHOR_OF_WEBTOON);
		}

		// TODO : file is empty
		String thumbnail = FileUploader.uploadWebtoonThumbnail(thumbnailFile);

		webtoon.update(request.toEntityWtih(thumbnail));
	}

	@Transactional
	public void delete(Long webtoonIdx, Long userIdx) {
		Webtoon webtoon = webtoonRepository.findById(webtoonIdx)
										   .orElseThrow(() -> new ApplicationException(WEBTOON_NOT_FOUND));

		if (!webtoon.wasDrawnBy(userIdx)) {
			throw new ApplicationException(USER_IS_NOT_AUTHOR_OF_WEBTOON);
		}

		webtoonRepository.delete(webtoon);
	}
}
