package com.ecommerce.Services.Impl;

import com.ecommerce.Entity.User;
import com.ecommerce.Helpers.AppConstant;
import com.ecommerce.Helpers.NotFoundException;
import com.ecommerce.Helpers.Provider;
import com.ecommerce.Helpers.getVerificationLink;
import com.ecommerce.Repository.UserRepo;
import com.ecommerce.Services.EmailService;
import com.ecommerce.Services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder ;
    private final getVerificationLink getVerificationLink;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, getVerificationLink getVerificationLink, EmailService emailService)
    {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder ;
        this.getVerificationLink = getVerificationLink;
        this.emailService = emailService;
    }


    @Override
    public User saveUser(User user) {
        user.setRole(AppConstant.ROLE_USER);
        user.setIsEnable(false);
        user.setProvider(Provider.Self);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // verify email modules
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationCode(token);
        String url = getVerificationLink.forEmailVerify(token);
        emailService.sendEmail(user.getEmail(),"Verify Account : BK_QA Shopping Center",EmailVerifyTemplate(url));
        return userRepo.save(user);
    }

    @Override
    public User saveAdmin(User user) {

        user.setProvider(Provider.Self);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerificationCode(null);
        user.setIsEnable(true);
        user.setRole(AppConstant.ROLE_ADMIN);

        System.out.println("===============================================================================");
        System.out.println("===> New Admin  User id : " + user.getId() + "\n Roll : " + user.getRole()  + " <===");
        System.out.println("===============================================================================");

        return userRepo.save(user);
    }

    @Override
    public User getUserById(int id) {
        return userRepo.findById(id).orElse(null);
    }

    @Override
    public void deleteUser(int id) {
        User userById = getUserById(id);
        if (!ObjectUtils.isEmpty(userById)) {
            userRepo.deleteById(id);
        }
    }

    @Override
    public User updateUserProfile(User user,String imageName) {

        User userById = getUserById(user.getId());


        if(imageName.equalsIgnoreCase("default.jpg")){
            imageName = userById.getImageName();
            userById.setImageName(imageName);
        }

        userById.setFullName(user.getFullName());
        user.setImageName(imageName);
        userById.setCity(user.getCity());
        userById.setState(userById.getState());
        userById.setMobileNumber(user.getMobileNumber());
        userById.setAddress(user.getAddress());
        userById.setPinCode(user.getPinCode());

       return  userRepo.save(userById);
    }

    @Override
    public User updateUserPassword(User user) {
        return userRepo.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepo.findByEmail(email).orElse(null);
        if(!ObjectUtils.isEmpty(user)){
            return user ;
        }else {
            return null ;
        }
    }

    @Override
    public Boolean isUserExists(String email) {
        return userRepo.existsByEmail(email);
    }



    @Override
    public List<User> getAllUser(String role) {
        return userRepo.findByRole(role);
    }

    @Override
    public Boolean updateAccountStatus(int id, Boolean status) {

       Optional <User>  findByUserId = userRepo.findById(id);
        if (findByUserId.isPresent()) {
            User user = findByUserId.get();
            user.setIsEnable(status);
            userRepo.save(user);
            return true;
        }
        return false;
    }

    @Override
    public Page<User> getAllUserAndRoleWithPagination(String role,int pageNo, int pageSize, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return userRepo.findByRole(role,pageable);
    }

    private String EmailVerifyTemplate(String url) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Email Verification</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f4f4f4;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 50px auto;
                        background-color: #ffffff;
                        padding: 30px;
                        border-radius: 8px;
                        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                        text-align: center;
                    }
                    h1 {
                        color: #333333;
                    }
                    p {
                        font-size: 16px;
                        color: #555555;
                    }
                    a.button {
                        display: inline-block;
                        margin-top: 20px;
                        padding: 12px 24px;
                        background-color: #007bff;
                        color: #ffffff;
                        text-decoration: none;
                        border-radius: 4px;
                        font-weight: bold;
                    }
                    a.button:hover {
                        background-color: #0056b3;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Welcome to BK_QA Shopping Center</h1>
                    <p>Thank you for signing up. Please verify your email address by clicking the button below.</p>
                    <a href="%s" class="button">Verify Email</a>
                    <p>If you did not create an account, please ignore this email.</p>
                </div>
            </body>
            </html>
            """.formatted(url);
    }

}