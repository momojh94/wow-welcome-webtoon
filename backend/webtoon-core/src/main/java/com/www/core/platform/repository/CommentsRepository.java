package com.www.core.platform.repository;

import com.www.core.platform.entity.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

public interface CommentsRepository extends JpaRepository<Comments, Long> {

    Page<Comments> findAllByEpIdx(Pageable pageable, @Param("epIdx") Long epIdx);

    Page<Comments> findAllByUserIdx(Pageable pageable, @Param("userIdx") Long userIdx);

    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM Comments c " +
                    "WHERE c.like_count >= c.dislike_count + 5 AND c.ep_idx = :epIdx " +
                    "ORDER BY (c.like_count - c.dislike_count) DESC, c.like_count DESC " +
                    "LIMIT 5")
    Stream<Comments> findBestCommentsByEpIdx(@Param("epIdx") Long epIdx);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Comments c " +
            "SET c.likeCount = c.likeCount + :addCnt " +
            "WHERE c.idx = :commentIdx")
    void updateLikeCount(@Param("commentIdx") Long commentIdx, @Param("addCnt") int addCnt);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Comments c " +
            "SET c.dislikeCount = c.dislikeCount + :addCnt " +
            "WHERE c.idx = :commentIdx")
    void updateDislikeCount(@Param("commentIdx") Long commentIdx, @Param("addCnt") int addCnt);
}
