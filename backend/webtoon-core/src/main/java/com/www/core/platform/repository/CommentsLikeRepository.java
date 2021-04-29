package com.www.core.platform.repository;

import com.www.core.platform.entity.CommentsLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentsLikeRepository extends JpaRepository<CommentsLike, Long> {
    boolean existsByCommentIdxAndUserIdx(Long commentIdx, Long userIdx);

    CommentsLike findByCommentIdxAndUserIdx(Long commentIdx, Long userIdx);

    @Modifying
    @Transactional
    @Query("DELETE FROM CommentsLike " +
            "WHERE idx = :idx")
    void deleteByIdx(@Param("idx") Long idx);

    @Modifying
    @Transactional
    void deleteAllByCommentIdx(@Param("commentIdx") Long commentIdx);
}