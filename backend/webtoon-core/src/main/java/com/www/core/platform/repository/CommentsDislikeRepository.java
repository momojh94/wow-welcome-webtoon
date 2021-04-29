package com.www.core.platform.repository;

import com.www.core.platform.entity.CommentsDislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentsDislikeRepository extends JpaRepository<CommentsDislike, Long> {
    boolean existsByCommentIdxAndUserIdx(Long commentIdx, Long userIdx);

    CommentsDislike findByCommentIdxAndUserIdx(Long commentIdx, Long userIdx);

    @Modifying
    @Transactional
    @Query("DELETE FROM CommentsDislike " +
            "WHERE idx = :idx")
    void deleteByIdx(@Param("idx") Long idx);

    @Modifying
    @Transactional
    void deleteAllBycommentIdx(@Param("commentIdx") Long commentIdx);
}
