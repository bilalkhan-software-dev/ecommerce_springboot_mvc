package com.ecommerce.Config;

import com.ecommerce.Entity.User;
import com.ecommerce.Helpers.AppConstant;
import com.ecommerce.Helpers.Provider;
import com.ecommerce.Repository.UserRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> roles = AuthorityUtils.authorityListToSet(authorities);

        log.info("Authentication type: {}", authentication.getClass().getName());

        // Check if it's OAuth2 login
        if (authentication instanceof OAuth2AuthenticationToken principal) {
            String authorizedClientRegisteredId = principal.getAuthorizedClientRegistrationId();
            log.info("OAuth2 Provider: {}", authorizedClientRegisteredId);

            // Get OAuth user attributes
            var oAuthUser = (DefaultOAuth2User) principal.getPrincipal();

            log.info("OAuth2 User Attributes:");
            oAuthUser.getAttributes().forEach((key, value) -> log.info("{} : {}", key, value));

            User user = new User();
            user.setIsEnable(true);
            user.setEmailVerificationCode(null);
            user.setRole(AppConstant.ROLE_USER);
            user.setPassword("******");
            String needToBeUpdate = "Need To be Update";
            user.setPinCode(needToBeUpdate);
            user.setCity(needToBeUpdate);
            user.setState(needToBeUpdate);
            user.setMobileNumber(needToBeUpdate);
            user.setAddress(needToBeUpdate);

            // Set attributes based on provider
            switch (authorizedClientRegisteredId.toLowerCase()) {
                case "google" -> {
                    user.setEmail(oAuthUser.getAttribute("email"));
                    user.setImageName(oAuthUser.getAttribute("picture"));
                    user.setProvider(Provider.Google);
                    user.setFullName(oAuthUser.getAttribute("name"));
                }
                case "github" -> {
                    //  GitHub logic
                }
                case "linkedin" -> log.info("Handle LinkedIn login");
                case "facebook" -> log.info("Handle Facebook login");
                default -> log.warn("Unknown OAuth2 provider: {}", authorizedClientRegisteredId);
            }


            if (userRepo.findByEmail(user.getEmail()).isEmpty()) {
                log.info("New OAuth2 user: {}", user.getEmail());
            }

            // Save user if not exists
            User userFromDB = userRepo.findByEmail(user.getEmail()).orElse(null);
            if (userFromDB == null) {
                userRepo.save(user);
                log.info("User saved successfully: {}", user.getEmail());
            }
        }

        String redirectURL = roles.contains(AppConstant.ROLE_ADMIN) ? "/admin/dashboard" : "/home";
        new DefaultRedirectStrategy().sendRedirect(request, response, redirectURL);
    }

}
