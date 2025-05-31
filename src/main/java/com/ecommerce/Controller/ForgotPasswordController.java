package com.ecommerce.Controller;


import com.ecommerce.Entity.User;
import com.ecommerce.Helpers.Message;
import com.ecommerce.Helpers.MessageType;
import com.ecommerce.Repository.UserRepo;
import com.ecommerce.Services.EmailService;
import com.ecommerce.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.util.UUID;

@Controller
public class ForgotPasswordController {

    private final UserService userService;
    private final EmailService emailService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;;


    private final SecureRandom random = new SecureRandom();


    Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    private int generateOTP() {
        return 100000 + random.nextInt(900000);
    }

    @Autowired
    public ForgotPasswordController(UserService userService, EmailService emailService, UserRepo userRepo,PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailService = emailService;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/forgotPassword")
    public String forgotPasswordPage() {
        return "user/forgot_Password";
    }

    @GetMapping("/resetPassword")
    public String resetPasswordPage() {
        return "user/reset_password";
    }


    @PostMapping("/do-generateLink")
    public String sendResetPasswordLink(@RequestParam("email") String email, HttpSession session) {
        User requestedUser = userService.getUserByEmail(email);

        if (!ObjectUtils.isEmpty(requestedUser)) {
            int otp = generateOTP();
            session.setAttribute("otp", otp);
            logger.info("OTP for {} is {}", email, otp);
            session.setAttribute("email", email);
            emailService.sendEmail(requestedUser.getEmail(), "Password Reset", templateForPasswordReset(otp));
            session.setAttribute("message", Message.builder()
                    .content("Password Reset OTP Sent to your Email")
                    .messageColorType(MessageType.green)
                    .build());
            return "user/verifyingOTP";

        } else {
            session.setAttribute("message", Message.builder()
                    .content("Email is not registered")
                    .messageColorType(MessageType.red).build());
            return "redirect:/forgotPassword";
        }
    }


    @PostMapping("/do-verifyOTP")
    public String verifyOTP(@RequestParam("otp") int otp, HttpSession session) {

        int storedOtp = (Integer) session.getAttribute("otp");

        if (storedOtp == otp) {
            session.setAttribute("message", Message.builder()
                    .content("OTP is correct")
                    .messageColorType(MessageType.green)
                    .build());
            return "user/reset_password";
        } else {
            session.setAttribute("message", Message.builder()
                    .content("OTP is not correct")
                    .messageColorType(MessageType.red).build());
            return "user/verifyingOTP";
        }
    }

    @PostMapping("/do-resetPassword")
    public String resetPassword( @RequestParam("password") String password, @RequestParam("confirmPassword") String confirmPassword, HttpSession session) {

       String email = (String)  session.getAttribute("email");

        User user = userService.getUserByEmail(email);

    if (!ObjectUtils.isEmpty(user)) {

            user.setPassword(passwordEncoder.encode(password));
            userRepo.save(user);
            session.setAttribute("message", Message.builder()
                    .content("Password Reset Successfully").messageColorType(MessageType.green).build());
            session.removeAttribute("otp");
            session.removeAttribute("email");
            return "redirect:/login";

    }
    else {
        session.setAttribute("message", Message.builder()
                .content("Email is not registered")
                .messageColorType(MessageType.red).build());
        return "redirect:/forgotPassword";
    }
    }

    private String templateForPasswordReset(Integer OTP) {
        return """
                 <!DOCTYPE html>
                 <html lang="en">
                 <head>
                     <meta charset="UTF-8">
                     <meta http-equiv="X-UA-Compatible" content="IE=edge">
                     <meta name="viewport" content="width=device-width, initial-scale=1.0">
                     <title>Password Reset Request</title>
                     <style>
                         body {
                             font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                             background-color: #f8f9fa;
                             margin: 0;
                             padding: 0;
                         }
                         .container {
                             max-width: 600px;
                             margin: 40px auto;
                             background-color: #ffffff;
                             padding: 30px;
                             border-radius: 8px;
                             box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                             text-align: center;
                         }
                         h1 {
                             color: #212529;
                             margin-bottom: 20px;
                         }
                         p {
                             font-size: 16px;
                             color: #495057;
                             line-height: 1.5;
                         }
                         .otp-box {
                             display: inline-block;
                             margin: 20px 0;
                             padding: 15px 25px;
                             background-color: #007bff;
                             color: #ffffff;
                             font-size: 20px;
                             border-radius: 5px;
                             font-weight: bold;
                             letter-spacing: 2px;
                         }
                         .footer {
                             font-size: 14px;
                             color: #868e96;
                             margin-top: 30px;
                         }
                     </style>
                 </head>
                 <body>
                     <div class="container">
                         <h1>Password Reset Request</h1>
                         <p>We've received a request to reset your password.<br>Your One-Time Password (OTP) is:</p>
                         <div class="otp-box">%d</div>
                         <p>Please enter this OTP to complete the password reset process.<br>This code will expire shortly for security reasons.</p>
                         <p class="footer">If you didn't request a password reset, you can safely ignore this email.</p>
                     </div>
                 </body>
                 </html>
                \s""".formatted(OTP);
    }


}
