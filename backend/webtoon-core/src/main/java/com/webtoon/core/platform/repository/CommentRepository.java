package com.webtoon.core.platform.repository;

import com.webtoon.core.platform.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByEpIdx(Pageable pageable, @Param("epIdx") Long epIdx);

    Page<Comment> findAllByUserIdx(Pageable pageable, @Param("userIdx") Long userIdx);

    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM Comment c " +
                    "WHERE c.like_count >= c.dislike_count + 5 AND c.ep_idx = :epIdx " +
                    "ORDER BY (c.like_count - c.dislike_count) DESC, c.like_count DESC " +
                    "LIMIT 5")
    Stream<Comment> findBestCommentsByEpIdx(@Param("epIdx") Long epIdx);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Comment c " +
            "SET c.likeCount = c.likeCount + :addCnt " +
            "WHERE c.idx = :commentIdx")
    void updateLikeCount(@Param("commentIdx") Long commentIdx, @Param("addCnt") int addCnt);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Comment c " +
            "SET c.dislikeCount = c.dislikeCount + :addCnt " +
            "WHERE c.idx = :commentIdx")
    void updateDislikeCount(@Param("commentIdx") Long commentIdx, @Param("addCnt") int addCnt);
}
