package com.ecommerce.Config;

import com.ecommerce.Entity.User;
import com.ecommerce.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepo userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Find user by email
        String email = oAuth2User.getAttribute("email");
        String userRole = userRepo.findByEmail(email)
                .map(User::getRole)
                .orElse("ROLE_USER");


        // Adding a role from DB as granted authority
        var authorities = List.of(new SimpleGrantedAuthority(userRole));

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "email");
    }
}
