package murraco.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import murraco.model.Role;
import murraco.service.SigningKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    /**
     * 1小时
     */
    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 3600000;

    @Autowired
    private MyUserDetails myUserDetails;

    @Autowired
    private SigningKeyService signingKeyService;

    public String createToken(String username, List<Role> roles) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", roles.stream().map(s -> new SimpleGrantedAuthority(s.getAuthority())).collect(Collectors.toList()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, signingKeyService.getSigningKey())
                .compact();
    }

    Authentication getAuthentication(String token) {
        UserDetails userDetails = myUserDetails.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        try {
            return Jwts.parser().setSigningKey(signingKeyService.getSigningKey()).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            try {
                return Jwts.parser().setSigningKey(signingKeyService.getBackupSigningKey()).parseClaimsJws(token).getBody().getSubject();
            } catch (Exception e1) {
                log.error("second get user name failed {}", Arrays.toString(e1.getStackTrace()));
            }
        }
        return null;
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(signingKeyService.getSigningKey()).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.warn("The token may be signed use back up key");
            try {
                Jwts.parser().setSigningKey(signingKeyService.getBackupSigningKey()).parseClaimsJws(token);
                return true;
            } catch (Exception e1) {
                log.error("re validate token failed {}", Arrays.toString(e1.getStackTrace()));
            }
        }
        return false;
    }
}
