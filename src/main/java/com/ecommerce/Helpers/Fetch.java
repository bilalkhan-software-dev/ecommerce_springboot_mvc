package com.ecommerce.Helpers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Fetch {

    public  String LoggedInUserEmail(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken auth) {
            String clientRegistrationId = auth.getAuthorizedClientRegistrationId();
            var oAuth2User = (OAuth2User) authentication.getPrincipal();
            if (clientRegistrationId.equalsIgnoreCase("google")) {
                log.info("Provider  : {}",Provider.Google);
                return oAuth2User.getAttribute("email").toString();
            }
//            else if (clientRegistrationId.equalsIgnoreCase("github")) {
//                // same as for others with different attributes according to their attribute name
//            }
        }

            log.info("User Provider : {} ",Provider.Self);
            return authentication.getName();
        }
}
