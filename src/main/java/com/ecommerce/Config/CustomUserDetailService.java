package com.ecommerce.Config;

import com.ecommerce.Helpers.NotFoundException;
import com.ecommerce.Repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private UserRepo userRepo ;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new CustomUserDetail(userRepo.findByEmail(username).orElseThrow(()->new NotFoundException("User Not Found with email : " +username)));
    }
}
