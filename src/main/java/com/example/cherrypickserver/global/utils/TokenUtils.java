package com.example.cherrypickserver.global.utils;

import com.example.cherrypickserver.member.domain.Member;
import com.example.cherrypickserver.member.exception.TokenExpirationException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenUtils {
  public static final String MEMBER_ID = "memberId";
  public static final String NICKNAME = "nickname";
  public static final String ONE_BLOCK = " ";
  public static final String COMMA = ",";

  public enum TYPE {
    REFRESH,
    ACCESS
  }

//  private final RedisTemplateService redisTemplateService;

  public static String accessKeyId;
  public static String secretKey;
  public static String tokenType;
  public static String accessName;
  public static String refreshName;
  public static String accessExTime;
  public static String refreshExTime;


  @Value("${jwt.secret}")
  public void accessKeyId(String value) {
    accessKeyId = value;
  }

  @Value("${jwt.secret}")
  public void setSecretKey(String value) {
    secretKey = value;
  }

  @Value("${jwt.token-type}")
  public void setTokenType(String value) {
    tokenType = value;
  }

  @Value("${jwt.access-name}")
  public void setAccessName(String value) {
    accessName = value;
  }

  @Value("${jwt.refresh-name}")
  public void setRefreshName(String value) {
    refreshName = value;
  }

  @Value("${jwt.access-expired-time}")
  public void setAccessExpiredTime(String value) {
    accessExTime = value;
  }

  @Value("${jwt.refresh-expired-time}")
  public void setRefreshExpireTime(String value) {
    refreshExTime = value;
  }

  public String createToken(Member member) {
    String access_token = this.createAccessToken(member.getId(), member.getName());
    String refresh_token = this.createRefreshToken(member.getId(), member.getName());
    return access_token + COMMA + refresh_token;
  }

  public String createAccessToken(Long memberId, String nickname) {
    Claims claims = Jwts.claims()
            .setSubject(accessName)
            .setIssuedAt(new Date());
    claims.put(MEMBER_ID, memberId);
    claims.put(NICKNAME, nickname);
    Date ext = new Date();
    ext.setTime(ext.getTime() + Long.parseLong(Objects.requireNonNull(accessExTime)));
    String accessToken = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setClaims(claims)
            .setExpiration(ext)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    return tokenType + ONE_BLOCK + accessToken;
  }

  public String createRefreshToken(Long userIdx, String nickname) {
    Claims claims = Jwts.claims()
            .setSubject(refreshName)
            .setIssuedAt(new Date());
    claims.put(MEMBER_ID, userIdx);
    claims.put(NICKNAME, nickname);
    Date ext = new Date();
    ext.setTime(ext.getTime() + Long.parseLong(Objects.requireNonNull(refreshExTime)));
    String refreshToken = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setClaims(claims)
            .setExpiration(ext)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
//    redisTemplateService.setUserRefreshToken(userIdx, tokenType + ONE_BLOCK + refreshToken);
    return tokenType + ONE_BLOCK + refreshToken;
  }

  public boolean isValidToken(String justToken) {
    if (justToken != null && justToken.split(ONE_BLOCK).length == 2)
      justToken = justToken.split(ONE_BLOCK)[1];
    try {
      Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(justToken).getBody();
      return true;
    } catch (ExpiredJwtException exception) {
      log.error("Token Tampered");
      return true;
    } catch (MalformedJwtException exception) {
      log.error("Token MalformedJwtException");
      return false;
    } catch (ClaimJwtException exception) {
      log.error("Token ClaimJwtException");
      return false;
    } catch (UnsupportedJwtException exception) {
      log.error("Token UnsupportedJwtException");
      return false;
    } catch (CompressionException exception) {
      log.error("Token CompressionException");
      return false;
    } catch (RequiredTypeException exception) {
      log.error("Token RequiredTypeException");
      return false;
    } catch (NullPointerException exception) {
      log.error("Token is null");
      return false;
    } catch (Exception exception) {
      log.error("Undefined ERROR");
      return false;
    }
  }

  private Claims getJwtBodyFromJustToken(String justToken) {
    try {
      return Jwts.parser()
              .setSigningKey(secretKey)
              .parseClaimsJws(justToken)
              .getBody();
    } catch (ExpiredJwtException e) {
      throw new TokenExpirationException();
    }
  }

  public boolean isTokenExpired(String justToken) {
    if (justToken != null && justToken.split(ONE_BLOCK).length == 2)
      justToken = justToken.split(ONE_BLOCK)[1];
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(justToken).getBody();
    } catch (ExpiredJwtException e) {
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return false;
  }

  public String getUserIdFromFullToken(String fullToken) {
    return String.valueOf(getJwtBodyFromJustToken(parseJustTokenFromFullToken(fullToken)).get(MEMBER_ID));
  }

  public String getNicknameFromFullToken(String fullToken) {
    return String.valueOf(getJwtBodyFromJustToken(parseJustTokenFromFullToken(fullToken)).get(NICKNAME));
  }

  // "Bearer eyi35..." 로 부터 "Bearer " 이하만 떼어내는 메서드
  public String parseJustTokenFromFullToken(String fullToken) {
    if (StringUtils.hasText(fullToken)
            &&
            fullToken.startsWith(Objects.requireNonNull(tokenType))
    )
      return fullToken.split(ONE_BLOCK)[1]; // e부터 시작하는 jwt 토큰
    return null;
  }

//    @Transactional
//    public String accessExpiration(UserAuthTokenReq userAuthTokenReq) {
//        String userRefreshToken = redisTemplateService.getUserRefreshToken(userAuthTokenReq.getUserIdx());
//        String refreshNickname = getNicknameFromFullToken(userRefreshToken);
//      if (userRefreshToken == null) throw new TokenExpirationException();
//      if (refreshNickname.isEmpty()) throw new TokenExpirationException();
//
//        //토큰이 만료되었을 경우.
//        return createAccessToken(userAuthTokenReq.getUserIdx(), refreshNickname);
//    }

}
