package com.webtoon.core.comment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentDislikeRepository extends JpaRepository<CommentDislike, Long> {
    boolean existsByCommentIdxAndUserIdx(Long commentIdx, Long userIdx);

    CommentDislike findByCommentIdxAndUserIdx(Long commentIdx, Long userIdx);

    @Modifying
    @Transactional
    @Query("DELETE FROM CommentDislike WHERE idx = :idx")
    void deleteByIdx(@Param("idx") Long idx);

    @Modifying
    @Transactional
    void deleteAllBycommentIdx(@Param("commentIdx") Long commentIdx);
}
