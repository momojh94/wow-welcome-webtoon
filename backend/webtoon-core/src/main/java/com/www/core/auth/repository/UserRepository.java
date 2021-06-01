package com.www.core.auth.repository;

import com.www.core.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;

/**
 * Repository : JPA connects DB
 * @author bjiso
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {

	User findByAccount(String account);
	User findByIdx(Long idx);
	
	boolean existsByAccount(String account);
	boolean existsByEmail(String email);
	
	@Modifying
	@Transactional
	@Query("UPDATE User SET login_date = :loginDate WHERE idx = :idx")
	int updateLoginDate(@Param("loginDate") LocalDateTime loginDate, @Param("idx") Long idx);
	
	@Modifying
	@Transactional
	@Query("UPDATE User "
			+ "SET "
			+ "pw = :encodedPw,"
			+ "gender = :gender,"
			+ "name = :name,"
			+ "birth = :birth "
			+ "WHERE idx = :idx")
	int updateUserInfo(@Param("encodedPw") String encodedPw, @Param("gender") int gender,
					   @Param("name") String name, @Param("birth") Date birth, @Param("idx") Long userIdx);
	
	@Modifying
	@Transactional
	@Query("UPDATE User SET updated_date = :updatedDate WHERE idx = :idx")
	int updateUpdatedDate(@Param("updatedDate") LocalDateTime updatedDate, @Param("idx") Long idx);
}
