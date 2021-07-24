package com.webtoon.core.user.service;

import com.webtoon.core.user.dto.Tokens;
import com.webtoon.core.user.dto.UserDto;
import com.webtoon.core.user.dto.UserInfoModifiedDto;
import com.webtoon.core.user.dto.UserLoginDto;
import com.webtoon.core.user.dto.UserRegisterDto;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UserService {

	UserRepository userRepository;
	PasswordEncoder passwordEncoder;
	JwtTokenProvider jwtTokenProvider;

	/**
	 * REGISTER 회원가입
	 * @param userRegisterDto
	 * @return result error code 
	 */
	public int register(UserRegisterDto user) {
		// id 중복체크
		if (userRepository.existsByAccount(user.getAccount())) {
			return 1; //insert fail
		}
		// email 중복체크
		if (userRepository.existsByEmail(user.getEmail())) {
			return 3;
		}
		// pw encoding
		String encodedpw = passwordEncoder.encode(user.getPw());

		userRepository.save(user.toEntity(encodedpw));
		// insert complete
		return 0; 
	}

	/**
	 * LOGIN 로그인
	 * @param userlogindto (id/pw)
	 * @return tokens (access/refresh)
	 */
	public Tokens login(UserLoginDto user) {
		Tokens tokens = new Tokens();
		// user login
		// id not exist
		if (!userRepository.existsByAccount(user.getAccount())) {
			return tokens; 
		}
		// pw matching
		User info = userRepository.findByAccount(user.getAccount());
		if (passwordEncoder.matches(user.getPw(), info.getPw())) {
			System.out.println("============"+info.getName());
			tokens.setAccessToken(jwtTokenProvider.createAccessToken(info.getIdx(),info.getName()));
			System.out.println("access token:"+tokens.getAccessToken());
			tokens.setRefreshToken(jwtTokenProvider.createRefreshToken(info.getAccount()));
            System.out.println("refresh token:"+tokens.getRefreshToken());
			//login date time
            LocalDateTime now = LocalDateTime.now();
            userRepository.updateLoginDate(now, info.getIdx());
		} 
		return tokens;
	}
	
	/**
	 * 회원정보 수정
	 * @param info
	 * @return
	 */
	public int modifyInfo(Long userIdx, UserInfoModifiedDto info) {
		//pw encoding
		String encoded_pw = passwordEncoder.encode(info.getPw());
		//update
		int update_result = userRepository.updateUserInfo(encoded_pw, info.getGender(),
				info.getName(), info.getBirth(), userIdx);
		LocalDateTime now = LocalDateTime.now();
		if(update_result==1)
			userRepository.updateUpdatedDate(now, userIdx);
		return update_result;
	}
	
	/**
	 * 회원정보 삭제
	 * @param user_idx
	 * @return
	 */
	public void deleteInfo(Long user_idx) {
		userRepository.deleteById(user_idx);
	}
	
	public UserDto getUserDto(String user_id) {
		UserDto userDto = new UserDto();
		User info = userRepository.findByAccount(user_id);
		userDto.setBirth(info.getBirth());
		userDto.setGender(info.getGender());
		userDto.setName(info.getName());
		userDto.setAccount(user_id);
		userDto.setEmail(info.getEmail());
		return userDto;
	}
}
