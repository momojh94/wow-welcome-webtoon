package com.webtoon.core.user.service;

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

@Service
public class JwtTokenProvider {

	private String secretKey = "secretkeyhelloimjwtsecretkeysecretkeyhelloimkeyjwtplz";

	private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	private byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
	private Key KEY = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
	private long tokenValidMilisecond = 1000L * 60 * 60; // 1000ms(1sec), 1000*60*60==한시간


	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	@Autowired
	UserRepository userRepository;

	// access jwt 발급
	public String createAccessToken(Long user_idx, String user_name) {
		// payload
		Claims claims = Jwts.claims();
		claims.put("user_idx", user_idx);
		claims.put("user_name", user_name);

		Date now = new Date();
		return Jwts.builder().setHeaderParam("typ", "jwt").setClaims(claims).setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + tokenValidMilisecond)).signWith(KEY, signatureAlgorithm)
				.compact();
	}

	// refresh token 발급
	public String createRefreshToken(String user_id) {
		System.out.println("redis template:" + redisTemplate);

		Date now = new Date();
		String refreshToken = Jwts.builder().setHeaderParam("typ", "jwt").setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + tokenValidMilisecond * 24)) // �ϴ� �Ϸ縸 ��ȿ
				.signWith(KEY, signatureAlgorithm).compact();
		// redis set
		ValueOperations<String, Object> vop = redisTemplate.opsForValue();
		vop.set(user_id, refreshToken);
		// 24시간 후에 만료
		redisTemplate.expire(user_id, tokenValidMilisecond * 24, TimeUnit.MILLISECONDS);

		return refreshToken;
	}

	// access token에서 user idx 파싱
	public int getUserIdx(String token) {
		try {
			return (int) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("user_idx");
		} catch (ExpiredJwtException e) {
			return (int) e.getClaims().get("user_idx");
		} catch (Exception e) { // error
			return -1;
		}
	}

	// access token에서 user name 파싱
	public String getUserName(String token) {
		try {
			return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("user_name");
		} catch (ExpiredJwtException e) {
			return (String) e.getClaims().get("user_name");
		} catch (Exception e) { // error
			return e.getMessage();
		}
	}

	// token 유효성 검사
	public int validateToken(String jwtToken) {
		try {
			Date now = new Date();
			long time = (Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody().getExpiration()
					.getTime() - now.getTime());
			System.out.println("토큰 시간! : " + time);
			if (time < 1000L * 30) { //exp time이 30초미만으로 남았을 경우 만료로 간주
				return 1; //expired 
			}
			return 0; //valid
		} catch (ExpiredJwtException e) {
			return 1; //expired
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return 2; //invalid
		}
	}

	// refresh token
	public int checkRefreshToken(String accessT, String refreshT, Long idx) {
		int accessT_valid = validateToken(accessT);
		int refreshT_valid = validateToken(refreshT);
		System.out.println("accessT idx : "+getUserIdx(accessT));
		if (accessT_valid < 2 && refreshT_valid < 2 && getUserIdx(accessT) == idx) {
			try {
				ValueOperations<String, Object> vop = redisTemplate.opsForValue();
				String redis_T = (String) vop.get(userRepository.findByIdx(idx).getAccount());
				System.out.println("redis refresh token: " + redis_T);

				if (redis_T == null)
					return 41; //refresh token 만료 (로그아웃)
				if (redis_T.equals(refreshT)) {
					if(accessT_valid==1&&refreshT_valid==0)
						return 40; //access token 재발급 가능
					if(accessT_valid==0&&refreshT_valid==0)
						return 43; //logout필요,재발급불가
				}
				else
					return 42; //클라가 보낸 refresh token과 서버에 저장된 token이 다른 경우
			} catch (Exception e) {
				System.out.println("refreshT check error "+e);
				return 42; 
			}
		}
		return 42; //유효하지 않은 토큰들
	}

	// redis refresh token 체크 
	public void expireToken(Long idx) {
		try {
			redisTemplate.delete(userRepository.findByIdx(idx).getAccount());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
