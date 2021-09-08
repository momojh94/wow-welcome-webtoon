package com.webtoon.core.webtoon.service;

import com.webtoon.core.common.util.FileUploader;
import com.webtoon.core.webtoon.dto.MyWebtoonResponse;
import com.webtoon.core.webtoon.dto.MyWebtoonsResponse;
import com.webtoon.core.webtoon.dto.WebtoonCreateRequest;
import com.webtoon.core.webtoon.dto.WebtoonEditRequest;
import com.webtoon.core.webtoon.dto.WebtoonMainPageResponse;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.repository.UserRepository;
import com.webtoon.core.webtoon.domain.Webtoon;
import com.webtoon.core.webtoon.repository.WebtoonRepository;
import com.webtoon.core.webtoon.dto.WebtoonResponse;
import com.webtoon.core.webtoon.dto.WebtoonsMainPageResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.webtoon.core.common.exception.ExceptionType.USER_IS_NOT_AUTHOR_OF_WEBTOON;
import static com.webtoon.core.common.exception.ExceptionType.WEBTOON_NOT_FOUND;

@Service
public class WebtoonService {

	private final WebtoonRepository webtoonRepository;
	private final UserRepository userRepository;
	private final FileUploader fileUploader;

	public WebtoonService(WebtoonRepository webtoonRepository,
						  UserRepository userRepository,
						  FileUploader fileUploader) {
		this.webtoonRepository = webtoonRepository;
		this.userRepository = userRepository;
		this.fileUploader = fileUploader;
	}

	//한 페이지 내 최대 웹툰 출력 갯수
	private static final int PAGE_WEBTOON_COUNT = 10;
	//한 페이지 내 최대 회차 출력 갯수
	private static final int PAGE_EPISODE_COUNT = 7;

	public WebtoonResponse findWebtoon(Long webtoonIdx){
		Webtoon webtoon = webtoonRepository.findById(webtoonIdx)
										   .orElseThrow(WEBTOON_NOT_FOUND::getException);
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

		int totalPages = page.getTotalPages();

		//등록된 웹툰이 없을 경우
		if (totalPages == 0) {
			totalPages = 1;
		}

		List<WebtoonMainPageResponse> webtoons = page.getContent().stream()
													 .map(WebtoonMainPageResponse::of)
													 .collect(Collectors.toList());

		return WebtoonsMainPageResponse.of(webtoons, totalPages);
	}

	public MyWebtoonsResponse findAllMyWebtoon(Integer pageNum, User user) {
		Pageable pageable = PageRequest.of(pageNum - 1, PAGE_WEBTOON_COUNT);
		Page<Webtoon> page = webtoonRepository.findAllByUser(pageable, user);

		int totalPages = page.getTotalPages();
		if (totalPages == 0) {
			totalPages = 1;
		}

		List<MyWebtoonResponse> webtoons = page.getContent().stream()
											   .map(MyWebtoonResponse::of)
											   .collect(Collectors.toList());

		return MyWebtoonsResponse.of(webtoons, totalPages);
	}

	@Transactional
	public void create(User user, MultipartFile thumbnailFile,
					   WebtoonCreateRequest request) throws IOException {

		// TODO : file is empty
		String thumbnail = fileUploader.uploadWebtoonThumbnail(thumbnailFile);

		Webtoon webtoon = request.toWebtoon(thumbnail, user);
		webtoonRepository.save(webtoon);
	}

	@Transactional
	public void update(User user, Long webtoonIdx, MultipartFile thumbnailFile,
					   WebtoonEditRequest request) throws IOException {
		Webtoon webtoon = webtoonRepository.findById(webtoonIdx)
										   .orElseThrow(WEBTOON_NOT_FOUND::getException);

		if (!webtoon.wasDrawnBy(user)) {
			throw USER_IS_NOT_AUTHOR_OF_WEBTOON.getException();
		}

		// TODO : file is empty
		String thumbnail = fileUploader.uploadWebtoonThumbnail(thumbnailFile);

		webtoon.update(request.toWebtoon(thumbnail));
	}

	@Transactional
	public void delete(Long webtoonIdx, User user) {
		Webtoon webtoon = webtoonRepository.findById(webtoonIdx)
										   .orElseThrow(WEBTOON_NOT_FOUND::getException);

		if (!webtoon.wasDrawnBy(user)) {
			throw USER_IS_NOT_AUTHOR_OF_WEBTOON.getException();
		}

		webtoonRepository.delete(webtoon);
	}
}
