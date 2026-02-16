package com.app.dhcp.jwt;

import com.app.dhcp.enums.HttpStatusError;
import com.app.dhcp.enums.JwtValidationError;
import com.app.dhcp.exeptionsHandler.HandleException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final String secretKey;
    private final String timeExpiration;
    private final JwtParser jwtParseBuilder;

    public JwtUtil( @Value("${jwt.secret.key}") String secretKey,
                    @Value("${jwt.time.expiration}")String timeExpiration) {
        this.secretKey = secretKey;
        this.timeExpiration = timeExpiration;

        // Parser builder helps to compare and check the sign
        this.jwtParseBuilder =
                Jwts.parserBuilder().setSigningKey(getJwtKey()).build();
    }

     /*
     * 1- the secret key is decoded in base 64
     * 2- it´s generated a key with hmac algorithm * is used only for private key does not have public key
     */

    private Key getJwtKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

     /*
     * Generate token
     * 1- the function receive a username to add in payload
     * 2- with jwts.builder the token is generated
     */

    public String generateToken(String username, String role) {

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expireDate = new Date(now + Long.parseLong(timeExpiration));

        // Adding extra claims
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role);

        // Create token
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expireDate)
                .signWith(getJwtKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // validate access token
    public boolean validateToken(String token)  {
        try {
            jwtParseBuilder.parseClaimsJws(token);
            return true;
        }catch (ExpiredJwtException e) {
            throw new HandleException(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, JwtValidationError.TOKEN_HAS_EXPIRED.toString());

        } catch (MalformedJwtException e) {
            throw new HandleException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, JwtValidationError.TOKEN_HAS_WRONG_FORMAT.toString());

        } catch (UnsupportedJwtException e) {
            throw new HandleException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, JwtValidationError.TOKEN_NO_COMPATIBLE.toString());

        } catch (IllegalArgumentException e) {
            throw new HandleException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, JwtValidationError.TOKEN_NOT_VALID.toString());

        } catch (Exception e) {
            throw new HandleException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, JwtValidationError.TOKEN_COULD_NOT_VALIDATE.toString());
        }
    }

    //Get information from token
    public Claims extractAllClaims(String token) {
        return jwtParseBuilder
                .parseClaimsJws(token)
                .getBody();
    }

    //Get one specific claim
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Get subject in token
    public String getSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

}
