package com.Omer.jwt.jwtfilter;

import com.Omer.jwt.service.TokenManager;
import com.Omer.jwt.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private TokenManager tokenManager;
	
	@Autowired
	private UserDetailServiceImpl userDetailServiceImpl;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;
		
		if(authorizationHeader !=null && authorizationHeader.startsWith("Bearer")) {

			token = authorizationHeader.substring(7);
			username = tokenManager.extractUsername(token);
		}
		
		if(username !=null && SecurityContextHolder.getContext().getAuthentication() == null) { // username null değil(username null olmaması için authorizationheaderın null olamaması ve token ile get isteği atılması gerekir)
				// ve kullanıcı daha önce bu token ile sisteme login olmamışsa
			UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(username);
			if(tokenManager.validateToken(token, userDetails)) {
				
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
					.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				
			}
			
		}
		
		filterChain.doFilter(request, response);// hiçbiri değilse işleme devam et
		
	}

}
