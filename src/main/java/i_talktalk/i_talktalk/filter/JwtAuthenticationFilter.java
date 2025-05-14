package i_talktalk.i_talktalk.filter;

import i_talktalk.i_talktalk.service.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();
        if (uri.startsWith("/swagger-ui")
                 uri.startsWith("/v3/api-docs")
                 uri.startsWith("/swagger-resources")
                 uri.startsWith("/webjars")
                 uri.equals("/swagger-ui.html")
                 uri.equals("/api-test")
                 uri.equals("/quiz/create")
                 uri.equals("/sign-in")
                 uri.equals("/sign-up")) {
            chain.doFilter(request, response);
            return;
        }

        // 1. Request Header에서 JWT 토큰 추출
        String token = resolveToken((HttpServletRequest) request);
        log.info("token: {}", token);
        // 2. validateToken으로 토큰 유효성 검사
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                throw new JwtException("Invalid token");
            }
        }catch (JwtException e) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 반환
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Invalid JWT Token\"}");
            return;
        }



        chain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
