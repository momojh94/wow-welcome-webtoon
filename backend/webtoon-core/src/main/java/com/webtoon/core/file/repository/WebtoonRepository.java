package com.webtoon.core.file.repository;

import com.webtoon.core.file.entity.Webtoon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {
    Page<Webtoon> findAllByUserIdx(Pageable pageable, @Param("userIdx") Long userIdx);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE webtoon w " +
            "SET w.rating_avg = (SELECT " +
            "SUM(e.rating_avg * e.rating_person_total) / SUM(e.rating_person_total) " +
            "FROM episode e " +
            "WHERE e.webtoon_idx = :webtoonIdx) " +
            "WHERE w.idx = :webtoonIdx", nativeQuery = true)
    void updateRatingAvg(@Param("webtoonIdx") Long webtoonIdx);

}
