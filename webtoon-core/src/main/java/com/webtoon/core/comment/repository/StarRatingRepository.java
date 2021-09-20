package com.webtoon.core.comment.repository;


import com.webtoon.core.comment.domain.StarRating;
import com.webtoon.core.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StarRatingRepository extends JpaRepository<StarRating, Long> {
    boolean existsByEpIdxAndUser(@Param("epIdx") Long epIdx, @Param("userIdx") User user);

    @Query(value = " SELECT AVG(s.rating) " +
            "FROM star_rating s " +
            "GROUP BY s.ep_idx " +
            "HAVING s.ep_idx = :epIdx", nativeQuery = true)
    float getRatingAvgByEpIdx(@Param("epIdx") Long epIdx);
}
