package com.ajit.shopsphere.auth.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ajit.shopsphere.auth.config.JWTTokenHelper;
import com.ajit.shopsphere.auth.entities.User;
import com.ajit.shopsphere.auth.services.OAuth2Service;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @Autowired
    OAuth2Service oAuth2Service;

    @Autowired
    private JWTTokenHelper jwtTokenHelper;
    
    @GetMapping("/success")
    public void callbackOAuth2(@AuthenticationPrincipal OAuth2User oAuth2User, HttpServletResponse response) throws IOException {

        String userName = oAuth2User.getAttribute("email");
        User user = oAuth2Service.getUser(userName);
        if(user == null){
            user = oAuth2Service.createUser(oAuth2User,"google");
        }
        String token = jwtTokenHelper.generateToken(user.getUsername());

        response.sendRedirect("http://localhost:5173/oauth2/callback?token="+token);
    }

}
