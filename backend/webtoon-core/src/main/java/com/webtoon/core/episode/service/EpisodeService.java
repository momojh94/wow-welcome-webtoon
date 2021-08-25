package com.webtoon.core.episode.service;

import com.webtoon.core.common.exception.ApplicationException;
import com.webtoon.core.common.util.FileUploader;
import com.webtoon.core.episode.domain.Episode;
import com.webtoon.core.episode.repository.EpisodeRepository;
import com.webtoon.core.episode.dto.EpisodeCreateRequest;
import com.webtoon.core.episode.dto.EpisodeDetailResponse;
import com.webtoon.core.episode.dto.EpisodeResponse;
import com.webtoon.core.episode.dto.EpisodeUpdateRequest;
import com.webtoon.core.episode.dto.EpisodeViewPageResponse;
import com.webtoon.core.episode.dto.EpisodesViewPageResponse;
import com.webtoon.core.webtoon.domain.Webtoon;
import com.webtoon.core.webtoon.repository.WebtoonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Collectors;

import static com.webtoon.core.common.exception.ErrorType.EPISODE_NOT_FOUND;
import static com.webtoon.core.common.exception.ErrorType.USER_IS_NOT_AUTHOR_OF_WEBTOON;
import static com.webtoon.core.common.exception.ErrorType.WEBTOON_NOT_FOUND;

@Service
public class EpisodeService {
	private final WebtoonRepository webtoonRepository;
	private final EpisodeRepository episodeRepository;
	private final FileUploader fileUploader;

	//한 블럭 내 최대 페이지 번호 수
	private static final int BLOCK_PAGE_NUM_COUNT = 5;
	//한 페이지 내 최대 회차 출력 갯수
	private static final int PAGE_EPISODE_COUNT = 7;

	public EpisodeService(WebtoonRepository webtoonRepository,
						  EpisodeRepository episodeRepository,
						  FileUploader fileUploader) {
		this.webtoonRepository = webtoonRepository;
		this.episodeRepository = episodeRepository;
		this.fileUploader = fileUploader;
	}

	@Transactional
	public EpisodeDetailResponse findEpisodeDetail(Long webtoonIdx, int epNo){
		Episode episode = episodeRepository.findByWebtoonIdxAndEpNo(webtoonIdx, epNo)
										   .orElseThrow(() -> new ApplicationException(EPISODE_NOT_FOUND));
		Webtoon webtoon = episode.getWebtoon();
		webtoon.increaseHits();
		episode.increaseHits();

		return EpisodeDetailResponse.of(episode);
	}

	public EpisodeResponse findEpisode(Long webtoonIdx, int epNo){
		Episode episode = episodeRepository.findByWebtoonIdxAndEpNo(webtoonIdx, epNo)
										   .orElseThrow(() -> new ApplicationException(EPISODE_NOT_FOUND));

		return EpisodeResponse.of(episode);
	}

	public EpisodesViewPageResponse findAllEpisodeByPage(Long webtoonIdx, int pageNum) {
		Webtoon webtoon = webtoonRepository.findById(webtoonIdx)
										   .orElseThrow(() -> new ApplicationException(WEBTOON_NOT_FOUND));

		pageNum = pageNum == 0 ? 1 : pageNum;
		Pageable pageable = PageRequest.of(pageNum - 1, PAGE_EPISODE_COUNT, Sort.Direction.DESC, "epNo");
		Page<Episode> page = episodeRepository.findAllByWebtoonIdx(pageable, webtoonIdx);

		int totalPages = page.getTotalPages() == 0 ? 1 : page.getTotalPages();

		return EpisodesViewPageResponse.of(page.stream()
											   .map(EpisodeViewPageResponse::of)
											   .collect(Collectors.toList()), webtoon, totalPages);
	}
	
	@Transactional
	public void create(Long webtoonIdx, Long userIdx, EpisodeCreateRequest request,
					   MultipartFile thumbnailFile, MultipartFile[] contentImages) throws IOException {
		Webtoon webtoon = webtoonRepository.findById(webtoonIdx)
										   .orElseThrow(() -> new ApplicationException(WEBTOON_NOT_FOUND));

		if(!webtoon.wasDrawnBy(userIdx)) {
			throw new ApplicationException(USER_IS_NOT_AUTHOR_OF_WEBTOON);
		}
        
        int newEpNo = webtoon.getNewEpNo();
		String thumbnail = fileUploader.uploadEpisodeThumbnail(thumbnailFile);
		String contents = fileUploader.uploadContentImages(contentImages);

		episodeRepository.save(request.toEpisode(newEpNo, thumbnail, contents, webtoon));
	}

	@Transactional
	public void update(Long userIdx, Long webtoonIdx, int epNo, EpisodeUpdateRequest request,
					   MultipartFile thumbnailFile, MultipartFile[] contentImages) throws IOException {
		Episode episode = episodeRepository.findByWebtoonIdxAndEpNo(webtoonIdx, epNo)
										   .orElseThrow(() -> new ApplicationException(EPISODE_NOT_FOUND));
		Webtoon webtoon = episode.getWebtoon();

		if (!webtoon.wasDrawnBy(userIdx)) {
			throw new ApplicationException(USER_IS_NOT_AUTHOR_OF_WEBTOON);
		}

		String thumbnail = fileUploader.uploadEpisodeThumbnail(thumbnailFile);
		String contents = fileUploader.uploadContentImages(contentImages);

		episode.update(request.toEpisode(thumbnail, contents, webtoon));
	}

	@Transactional
	public void delete(Long webtoonIdx, int epNo, Long userIdx) {
		Episode episode = episodeRepository.findByWebtoonIdxAndEpNo(webtoonIdx, epNo)
										   .orElseThrow(() -> new ApplicationException(EPISODE_NOT_FOUND));
		Webtoon webtoon = episode.getWebtoon();

		if (!webtoon.wasDrawnBy(userIdx)) {
			throw new ApplicationException(USER_IS_NOT_AUTHOR_OF_WEBTOON);
		}

		episodeRepository.delete(episode);
	}

}
