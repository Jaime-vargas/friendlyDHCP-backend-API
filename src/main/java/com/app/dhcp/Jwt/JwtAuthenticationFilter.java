package com.app.dhcp.Jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.app.dhcp.enums.ErrorMessages;
import com.app.dhcp.model.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public JwtUtil JwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.JwtUtil = jwtUtil;
    }

    /*
    * Attempt to authenticate
    *
    * 1- Get data from request
    * 2- Cast data to a UserEntity
    * 3- Generate UsernamePasswordAuthenticationToken
    * 4- Execute LoadUserByUsername in UserDetailsServiceImpl
    * 5- Execute a function to authenticate
    */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){

        UserEntity userEntity;
        // Cast data to a UserEntity
        userEntity = castRequestToUserEntity(request);
        //Generate UsernamePasswordAuthenticationToken / This object represents the user credentials
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userEntity.getUsername(), userEntity.getPassword());

        Authentication auth;
        try {
             //At this time spring calls to UserDetailsService and execute LoadUserByUsername
             //this generates a UserDetails Object
            auth = getAuthenticationManager().authenticate(authenticationToken);
        }catch (Exception e){
            throw new BadCredentialsException(ErrorMessages.WRONG_USER_OR_PASSWORD.toString());
        }
        return auth;
    }

    /*
    * After successful authentication in database
    *
    * 1- Create a User object fron Spring
    * 2- Generate Token
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult){

        User user = (User) authResult.getPrincipal();

        String username = user.getUsername();
        // Get only one role per user
        String role = user.getAuthorities().iterator().next().getAuthority();
        // Generate token
        String token = JwtUtil.generateToken(username, role);

        // Generate response to client
        createResponseWithToken(response,token, user);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("message:", failed.getMessage());
        String json = new ObjectMapper().writeValueAsString(body);
        response.getWriter().write(json);
        response.getWriter().flush();
    }

    public UserEntity castRequestToUserEntity(HttpServletRequest request){
        UserEntity userEntity;
        try{
            userEntity = new ObjectMapper().readValue(request.getInputStream(),UserEntity.class);
        }catch(IOException e){
            throw new BadCredentialsException(ErrorMessages.WRONG_JSON_FORMAT.toString());
        }

        return userEntity;
    }

    public void createResponseWithToken (HttpServletResponse response, String token, User user){
        // LinkedHashMap for body response
        Map<String, Object> httpResponse = new LinkedHashMap<>();
        httpResponse.put("username", user.getUsername());
        httpResponse.put("role", user.getAuthorities().iterator().next().getAuthority());
        httpResponse.put("token", token);

        try {
            response.addHeader("Authorization", "Bearer " + token);
            response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json");
            response.getWriter().flush();
        }catch (IOException e){
            throw new BadCredentialsException(ErrorMessages.ERROR_GENERATING_RESPONSE.toString());
        }
    }
}
