package com.reto.usuario.domain.utils;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.reto.usuario.domain.exceptions.AuthenticationFailedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TokenUtils {

    private static final Long ACCESS_TOKEN_VALIDITY_SECONDS = 2_592_000L;
    private static final String ACCESS_TOKEN_SECRET = "8y/B?E(G+KbPeShVmYq3t6w9z$C&F)J@";

    private TokenUtils() {}

    public static String createToken(String email, List<String> rol, String name, String lastName) {
        long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1000;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
        Map<String, Object> extra = new HashMap<>();
        extra.put("name", name);
        extra.put("lastName", lastName);
        extra.put("rol", rol);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .addClaims(extra)
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public static UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            JWT jwt = JWTParser.parse(token);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            String email = claims.getSubject();
            List<String> rol = claims.getStringListClaim("rol");
            return new UsernamePasswordAuthenticationToken(email, null,
                    rol.stream().map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList()));
        } catch (ParseException e) {
            throw new AuthenticationFailedException("Error token could not be read");
        }
    }

    public static String getUsername(String tokenWithBearerPrefix) {
        try {
            String username;
            if (tokenWithBearerPrefix.startsWith("Bearer ")) {
                tokenWithBearerPrefix = tokenWithBearerPrefix.replace("Bearer ", "");
            }
            JWT jwt = JWTParser.parse(tokenWithBearerPrefix);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            username = claims.getSubject();
            return username;
        } catch (ParseException e) {
            throw new AuthenticationFailedException("Error token could not be read");
        }
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(ACCESS_TOKEN_SECRET.getBytes()).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException | UnsupportedJwtException
                 | ExpiredJwtException | IllegalArgumentException | SignatureException e) {
            return false;
        }
    }
}
