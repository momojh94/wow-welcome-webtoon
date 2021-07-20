package com.www.core.platform.repository;

import com.www.core.platform.entity.StarRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StarRatingRepository extends JpaRepository<StarRating, Long> {
    boolean existsByEpIdxAndUserIdx(@Param("epIdx") Long epIdx, @Param("userIdx") Long userIdx);

    StarRating findByEpIdxAndUserIdx(@Param("epIdx") Long epIdx, @Param("userIdx") Long userIdx);


    @Query(value = " SELECT AVG(s.rating) " +
            "FROM star_rating s " +
            "GROUP BY s.ep_idx " +
            "HAVING s.ep_idx = :epIdx", nativeQuery = true)
    float getRatingAvgByEpIdx(@Param("epIdx") Long epIdx);
}
