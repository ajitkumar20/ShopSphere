package com.ajit.shopsphere.auth.config;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JWTTokenHelper {

    @Value("${jwt.auth.app}")
    private String appName;

    @Value("${jwt.auth.secret_key}")
    private String secretKey;

    @Value("${jwt.auth.expires_in}")
    private int expiresIn;

    public String generateToken(String userName){
        return Jwts.builder()
                    .issuer(appName)
                    .subject(userName)
                    .issuedAt(new Date())
                    .expiration(generateExpirationDate())
                    .signWith(getSigningKey())
                    .compact();
    }

    private Key getSigningKey() {
        byte[] keyBites = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBites);
    }

    private Date generateExpirationDate() {
        return new Date(new Date().getTime() + expiresIn * 1000L);
    }

    public String getToken(HttpServletRequest request) {
        
        String authHeader = getAuthHeaderFromHeader( request );
        if ( authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return authHeader;
    }

    public Boolean validateToken(String authToken, UserDetails userDetails) {
        final String username = getUserNameFromToken(authToken);
        return (
                username != null &&
                        username.equals(userDetails.getUsername()) &&
                        !isTokenExpired(authToken)
        );
    }

    private boolean isTokenExpired(String authToken) {
        Date expireDate = getExpirationDate(authToken);
        return expireDate.before(new Date());
    }

    private Date getExpirationDate(String authToken) {
        Date expireDate;
        try {
            final Claims claims = this.getAllClaimsFromToken(authToken);
            expireDate = claims.getExpiration();
        } catch(Exception e){
            expireDate = null;
        }
        return expireDate;
    }

    private String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public String getUserNameFromToken(String authToken) {
        String username;
        try {
            final Claims claims = this.getAllClaimsFromToken(authToken);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    private Claims getAllClaimsFromToken(String authToken) {
        Claims claims;
        try {
            claims = Jwts.parser()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(authToken)
                        .getBody();
        } catch(Exception e){
            claims = null;
        }
        return claims;
    }
    
}
