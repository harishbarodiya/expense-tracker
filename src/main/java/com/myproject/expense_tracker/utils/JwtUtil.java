package com.myproject.expense_tracker.utils;

import com.myproject.expense_tracker.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.util.Date;
//import java.util.Date;

//import javax.xml.crypto.Data;
//import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "ThIs+iS+mY+SuPeR+SeCrEt+kEyThIs+iS+mY+SuPeR+SeCrEt+kEy";

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))      //1 hour
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, String username){
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        Date expiration = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody().
                getExpiration();

        return expiration.before(new Date());
    }
}
