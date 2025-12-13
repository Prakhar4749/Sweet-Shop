package com.sweetShop.Backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This utility class is the "Engine" behind our security tokens.
 * It handles creating new tokens (printing ID cards) and reading existing ones.
 */
@Component
public class JwtUtil {

    // A secret code used to digitally sign the tokens.
    // Like a watermark or wax seal—if anyone tries to fake a token without this key, we will know.
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    /**
     * Prints a new ID card (Token) for a user.
     * @param userName The name of the person (e.g., "admin").
     * @param role The job title (e.g., "ROLE_ADMIN"). We bake this INTO the card.
     * @return The long string of gibberish that is the actual Token.
     */
    public String generateToken(String userName, String role) {
        Map<String, Object> claims = new HashMap<>();
        // We add the "role" as extra data (Claim) inside the token.
        claims.put("role", role);

        return createToken(claims, userName);
    }

    // Internal helper to actually build the token string using the library.
    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims) // Add the extra data (Role)
                .setSubject(userName) // Add the main owner's name
                .setIssuedAt(new Date(System.currentTimeMillis())) // Print today's date
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // Set expiration (30 mins from now)
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Sign it with our SECRET key
                .compact(); // Compress it into a string
    }

    // Decodes our secret string into a format the crypto library understands.
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Reads the token and pulls out the username (Subject).
     * Like reading the name printed on the front of the ID card.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Reads the token and pulls out the Role.
     * Useful if we want to know if they are an Admin without asking the database.
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Reads the expiration date stamped on the card.
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // A generic helper method that knows how to open the token and find ANY piece of data you ask for.
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // The technical heavy lifter: It uses the SECRET key to unlock the token and read all the data inside.
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Checks if the current time is AFTER the expiration date on the card.
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * The final check: Is this ID card valid?
     * 1. Does the name on the card match the user we are checking?
     * 2. Has the card expired?
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}