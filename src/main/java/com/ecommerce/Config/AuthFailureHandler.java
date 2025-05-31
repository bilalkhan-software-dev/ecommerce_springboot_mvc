package com.ecommerce.Config;


import com.ecommerce.Helpers.Message;
import com.ecommerce.Helpers.MessageType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof DisabledException){
            HttpSession session = request.getSession();
            session.setAttribute("message", Message.builder()
                    .content("User is disable!. Please contact with Admin to enable the user.If you already verified your email")
                    .messageColorType(MessageType.red).build());
            response.sendRedirect("/login");
        }else {
            response.sendRedirect("/login?error=true");
        }
    }
}
