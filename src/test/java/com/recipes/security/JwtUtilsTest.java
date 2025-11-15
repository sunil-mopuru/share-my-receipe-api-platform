package com.recipes.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "app.jwt.secret=MySecretKeyForShareMyRecipePlatformWhichIsVerySecureAndLongEnough",
    "app.jwt.expirationMs=86400000"
})
public class JwtUtilsTest {

    private JwtUtils jwtUtils = new JwtUtils();

    @Test
    public void testGenerateJwtToken() {
        // This test would require mocking the Authentication object
        // For now, we'll test the validation methods
    }

    @Test
    public void testValidateJwtTokenValid() {
        // Given
        String secret = "MySecretKeyForShareMyRecipePlatformWhichIsVerySecureAndLongEnough";
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // When
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    public void testValidateJwtTokenInvalid() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    public void testGetEmailFromJwtToken() {
        // Given
        String secret = "MySecretKeyForShareMyRecipePlatformWhichIsVerySecureAndLongEnough";
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        String email = "test@example.com";
        
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // When
        String extractedEmail = jwtUtils.getEmailFromJwtToken(token);

        // Then
        assertEquals(email, extractedEmail);
    }
}