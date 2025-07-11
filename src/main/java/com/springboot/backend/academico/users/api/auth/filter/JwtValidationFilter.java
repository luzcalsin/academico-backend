package com.springboot.backend.academico.users.api.auth.filter;

import static com.springboot.backend.academico.users.api.auth.TokenJwtConfig.*;

import java.beans.SimpleBeanInfo;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.web.WebProperties.Resources.Chain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.backend.academico.users.api.auth.SimpleGrantedAuthorityJsonCreated;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Arrays;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
       
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
       
                String header = request.getHeader(HEADER_AUTHORIZATION);
                if(header == null || !header.startsWith(PREFIX_TOKEN)){
                    chain.doFilter(request,response);
                    return;
                }
                String token = header.replace(PREFIX_TOKEN,"");
                

                try{
                    Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
                    String username = claims.getSubject();
                    //String username2 = (String) claims.get("username");
                    Object authoritiesClaims = claims.get("authorities");

                    Collection<? extends GrantedAuthority> roles = Arrays.asList(new ObjectMapper()
                                                                        .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreated.class)
                                                                        .readValue(authoritiesClaims.toString().getBytes(),SimpleGrantedAuthority[].class));

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,null,roles);

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    chain.doFilter(request, response);

                } catch(JwtException e){
                    Map<String, String> body = new HashMap<>();
                    body.put("error",e.getMessage());
                    body.put("error","El token es inválido!");

                    response.getWriter().write(new ObjectMapper().writeValueAsString(body));
                    response.setStatus(401);
                    response.setContentType(CONTENT_TYPE);
                    
                }
    }

}
