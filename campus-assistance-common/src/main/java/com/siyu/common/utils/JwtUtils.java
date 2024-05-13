package com.siyu.common.utils;

import com.siyu.common.constants.GlobalConstants;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.common.utils.BeanUtils;
import com.siyu.common.utils.WebUtils;
import io.jsonwebtoken.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    public static String getToken(ServletRequest request) {
        String token = WebUtils.getHeader(WebUtils.AUTHENTICATION_HEADER, (HttpServletRequest) request);
        return token;
    }

    public static String generateToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + GlobalConstants.JWT_EXPIRE))
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, GlobalConstants.JWT_SECRET)
                .compact();
        return token;
    }

    public static String generateToken(Object holder) {
        Map<String, Object> map = BeanUtils.obj2Map(holder);
        return generateToken(map);
    }

    public static boolean validateToken(String token) {
        try{
            Jwts.parser().setSigningKey(GlobalConstants.JWT_SECRET).parse(token);
            return true;
        } catch (MalformedJwtException e) {
            throw new BusinessException(ErrorStatus.TOKEN_ERROR, "Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorStatus.TOKEN_ERROR, "JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new BusinessException(ErrorStatus.TOKEN_ERROR, "JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorStatus.TOKEN_ERROR, "JWT claims string is empty: " + e.getMessage());
        }
    }

    public static Claims parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(GlobalConstants.JWT_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (MalformedJwtException e) {
            throw new BusinessException(ErrorStatus.TOKEN_ERROR, "Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorStatus.TOKEN_ERROR, "JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new BusinessException(ErrorStatus.TOKEN_ERROR, "JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorStatus.TOKEN_ERROR, "JWT claims string is empty: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException(ErrorStatus.SYS_ERROR);
        }
    }
    public static <T> T parseToken(String token, Class<T> clazz) {
        Claims claims = parseToken(token);
        return BeanUtils.map2Obj(claims, clazz);
    }

    public static Date parseExpiration(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(GlobalConstants.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

    public static boolean needRefresh(String token) {
        Date expireDate = parseExpiration(token);
        Date now = new Date();
        return now.getTime() > (expireDate.getTime() - GlobalConstants.JWT_REFRESH);
    }
}
