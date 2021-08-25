package com.webtoon.core.episode.repository;

import com.webtoon.core.episode.domain.Episode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
	Page<Episode> findAllByWebtoonIdx(Pageable pageable, @Param("webtoonIdx") Long webtoonIdx);

	Optional<Episode> findByWebtoonIdxAndEpNo(@Param("webtoonIdx") Long webtoonIdx, @Param("epNo") int epNo);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Transactional
	@Query(value = "UPDATE Episode e " +
			"SET e.rating_avg = (SELECT AVG(rating) FROM star_rating s WHERE s.ep_idx = :epIdx)," +
			"e.rating_person_total = e.rating_person_total + 1 " +
			"WHERE e.idx = :epIdx", nativeQuery = true)
	void updateRatingAvgAndPersonTotal(@Param("epIdx") Long epIdx);
}
