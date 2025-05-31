package com.ecommerce.Services;

import com.ecommerce.Entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    User saveUser(User user);

    User saveAdmin(User user) ;

    User getUserById(int id);

    void deleteUser(int id);

    User updateUserProfile(User user,String imageName);
    User updateUserPassword(User user);

    User getUserByEmail(String email);

    Boolean isUserExists(String email);

    List<User> getAllUser(String role) ;

    Boolean updateAccountStatus(int id, Boolean status);


    //pagination
    Page<User> getAllUserAndRoleWithPagination(String role,int pageNo, int pageSize, String sortBy, String direction) ;



}
