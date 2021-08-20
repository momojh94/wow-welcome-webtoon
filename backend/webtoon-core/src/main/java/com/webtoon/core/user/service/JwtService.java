package com.webtoon.core.user.service;

import com.webtoon.core.common.exception.ApplicationException;
import com.webtoon.core.common.exception.ErrorType;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.webtoon.core.common.exception.ErrorType.USER_NOT_FOUND;

@Service
public class JwtService {

	private String secretKey = "secretkeyhelloimjwtsecretkeysecretkeyhelloimkeyjwtplz";
	private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	private byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
	private Key KEY = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
	private long tokenValidMilisecond = 1000L * 60 * 60; // 1000ms(1sec), 1000 * 60 * 60 == 한시간

	private final RedisTemplate<String, Object> redisTemplate;
	private final UserRepository userRepository;

	public JwtService(RedisTemplate<String, Object> redisTemplate,
					  UserRepository userRepository) {
		this.redisTemplate = redisTemplate;
		this.userRepository = userRepository;
	}

	// access jwt 발급
	public String createAccessToken(Long userIdx, String userName) {
		// payload
		Claims claims = Jwts.claims();
		claims.put("userIdx", userIdx);
		claims.put("userName", userName);

		Date now = new Date();
		return Jwts.builder().setHeaderParam("typ", "jwt").setClaims(claims).setIssuedAt(now)
				   .setExpiration(new Date(now.getTime() + tokenValidMilisecond)).signWith(KEY, signatureAlgorithm)
				   .compact();
	}

	// refresh token 발급
	public String createRefreshToken(String account) {
		Date now = new Date();
		String refreshToken = Jwts.builder().setHeaderParam("typ", "jwt").setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + tokenValidMilisecond * 24)) //
				.signWith(KEY, signatureAlgorithm).compact();
		// redis set
		ValueOperations<String, Object> vop = redisTemplate.opsForValue();
		vop.set(account, refreshToken);
		// 24시간 후에 만료
		redisTemplate.expire(account, tokenValidMilisecond * 24, TimeUnit.MILLISECONDS);

		return refreshToken;
	}

	public Long getUserIdx(String token) {
		try {
			// access token bearer split
			token = token.substring(7);
			return Long.parseLong(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("userIdx").toString());
		} catch (ExpiredJwtException e) { //만료된 token이라도 user idx 반환
			return Long.parseLong(String.valueOf(e.getClaims().get("userIdx")));
		} catch (Exception e) {
			return -1L; //error
		}
	}

	public String getUserName(String token) {
		try {
			// access token bearer split
			token = token.substring(7);
			return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("user_name");
		} catch (ExpiredJwtException e) { //유효시간 만료된 경우에도 username 반환
			return (String) e.getClaims().get("userName");
		} catch (Exception e) { //error
			return e.getMessage();
		}
	}

	public int validateToken(String token) {
		try {
			// access token bearer split
			token = token.substring(7);
			Date now = new Date();
			long time = (Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration().getTime()
					- now.getTime());
			if (time < 1000L * 30) { //exp time 30초 미만일 경우 만료로 간주
				return 1;
			}
			return 0; // 유효
		} catch (ExpiredJwtException e) {
			return 1; // 만료
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return 2; // 올바르지않은 토큰
		}
	}

	// refresh token
	public int checkRefreshToken(String accessToken, String refreshToken, Long userIdx) {
		int accessTokenValidity = validateToken(accessToken);
		int refreshTokenValidity = validateToken(refreshToken);

		User user = userRepository.findById(userIdx)
								  .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));

		if (accessTokenValidity < 2 && refreshTokenValidity < 2 && getUserIdx(accessToken) == userIdx) {
			try {
				ValueOperations<String, Object> vop = redisTemplate.opsForValue();
				String redisToken = (String) vop.get(user.getAccount());

				if (redisToken == null) {
					return 41; //refresh token 만료 (로그아웃)
				}

				if (redisToken.equals(refreshToken)) {
					if (accessTokenValidity == 1 && refreshTokenValidity == 0) {
						return 40; //access token 재발급 가능
					}
					if (accessTokenValidity == 0 && refreshTokenValidity == 0) {
						return 43; //logout필요,재발급불가
					}
				}
				else {
					return 42; //클라가 보낸 refresh token과 서버에 저장된 token이 다른 경우
				}
			} catch (Exception e) {
				System.out.println("refreshT check error " + e);
				return 42;
			}
		}
		return 42; //유효하지 않은 토큰들
	}

	// redis refresh token 체크
	public void expireToken(Long idx) {
		User user = userRepository.findById(idx)
								  .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));

		redisTemplate.delete(user.getAccount());
	}
}
