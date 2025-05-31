package com.ecommerce.Entity;

import com.ecommerce.Helpers.Provider;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;


    @NotBlank
    @Column(nullable = false)
    private String fullName ;

    @NotBlank
    @Column(unique = true,nullable = false)
    private String email ;

    @NotBlank
    @Column(nullable = false)
    private String password ;

    private String role ;

    @Enumerated(EnumType.STRING)
    private Provider provider ;

    private Boolean isEnable ;

    private String imageName ;

    @NotBlank
    private String mobileNumber ;

    @NotBlank
    private String address ;

    @NotBlank
    private String city ;

    @NotBlank
    private String state ;

    @NotBlank
    private String pinCode ;


    private String emailVerificationCode ;



}
