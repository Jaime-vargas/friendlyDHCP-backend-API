package com.app.dhcp.Jwt;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;


@RestController
@RequestMapping("/api/jwt")
public class JwtTestController {

    @Autowired
    public JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> jwt() {
        return ResponseEntity.ok().body(Map.of("Token", jwtUtil.generateToken("jaime","ADMIN")));
    }

    @GetMapping("/valid")
    public ResponseEntity<?> validJwt(@RequestBody String token) {
        return ResponseEntity.ok().body(Map.of("Token", jwtUtil.validateToken(token)));
    }

    @GetMapping("/claims")
    public ResponseEntity<?> claims(@RequestBody String token) {
        String role = jwtUtil.getClaim(token, c -> c.get("role").toString());
        Claims claims = jwtUtil.extractAllClaims(token);
        String subject = jwtUtil.getSubject(token);

        return ResponseEntity.ok().body(Map.of("role", role,
                "all claims", claims,
                "sub desde Jwt util", subject,
                "iat", claims.getIssuedAt()));
    }

}
