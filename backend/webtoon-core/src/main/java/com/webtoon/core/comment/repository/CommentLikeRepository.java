package com.webtoon.core.comment.repository;


import com.webtoon.core.comment.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByCommentIdxAndUserIdx(Long commentIdx, Long userIdx);

    CommentLike findByCommentIdxAndUserIdx(Long commentIdx, Long userIdx);

    @Modifying
    @Transactional
    @Query("DELETE FROM CommentLike WHERE idx = :idx")
    void deleteByIdx(@Param("idx") Long idx);

    @Modifying
    @Transactional
    void deleteAllByCommentIdx(@Param("commentIdx") Long commentIdx);
}