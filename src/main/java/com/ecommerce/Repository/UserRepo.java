package com.ecommerce.Repository;

import com.ecommerce.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<User,Integer> {


    Optional<User> findByEmail(String email);
    Optional<User> findByEmailVerificationCode(String token);
    Optional<User> findById(int id);
    boolean existsByEmail(String email);

    List<User> findByRole(String role);

    Page<User> findByRole(String role,Pageable pageable);
}
