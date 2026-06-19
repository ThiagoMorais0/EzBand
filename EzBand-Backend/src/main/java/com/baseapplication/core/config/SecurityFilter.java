package com.baseapplication.core.config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dto.UserPrincipal;
import com.baseapplication.core.model.Usuario;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final String CACHE_PREFIX = "auth:user:";
    private static final long CACHE_TTL_SECONDS = 300;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired(required = false)
    private RedisTemplate<String, UserPrincipal> userPrincipalRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var token = this.recoverToken(request);
        if (token != null) {
            var login = tokenService.validarToken(token);
            if (login != null && !login.isEmpty()) {
                UserPrincipal userPrincipal = resolveUserPrincipal(login);
                if (userPrincipal != null) {
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userPrincipal, null, userPrincipal.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private UserPrincipal resolveUserPrincipal(String email) {
        if (userPrincipalRedisTemplate != null) {
            try {
                String cacheKey = CACHE_PREFIX + email;
                UserPrincipal cached = userPrincipalRedisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    return cached;
                }
                UserPrincipal principal = loadFromDatabase(email);
                if (principal != null) {
                    userPrincipalRedisTemplate.opsForValue().set(cacheKey, principal, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
                }
                return principal;
            } catch (Exception e) {
                log.warn("Redis indisponível, usando banco de dados para autenticação: {}", e.getMessage());
            }
        }
        return loadFromDatabase(email);
    }

    private UserPrincipal loadFromDatabase(String email) {
        Usuario usuario = usuarioDao.findByEmail(email);
        return usuario != null ? UserPrincipal.fromUsuario(usuario) : null;
    }

    private String recoverToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
