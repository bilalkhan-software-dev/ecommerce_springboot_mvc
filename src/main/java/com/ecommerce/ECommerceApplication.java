package com.ecommerce;

import com.ecommerce.Entity.User;
import com.ecommerce.Helpers.AppConstant;
import com.ecommerce.Helpers.Provider;
import com.ecommerce.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ECommerceApplication implements CommandLineRunner {


	private final PasswordEncoder passwordEncoder ;
	private final UserRepo userRepo;

	@Autowired
	public ECommerceApplication(PasswordEncoder passwordEncoder, UserRepo userRepo) {
		this.passwordEncoder = passwordEncoder;
		this.userRepo = userRepo;
	}

	public static void main(String[] args) {
		SpringApplication.run(ECommerceApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {

		User admin = new User() ;
		admin.setFullName("Main Admin");
		admin.setEmail("admin@admin.com");
		admin.setAddress("Admin Address");
		admin.setState("Admin is from Punjab");
		admin.setCity("Admin is from Gujranwala");
		admin.setPinCode("Admin 1234");
		admin.setMobileNumber("03**-*******");
		admin.setRole(AppConstant.ROLE_ADMIN);
		admin.setIsEnable(true);
		admin.setEmailVerificationCode(null);
		admin.setProvider(Provider.Self);
		admin.setPassword(passwordEncoder.encode("admin"));
		admin.setImageName("default.jpg");

		userRepo.findByEmail("admin@admin.com").ifPresentOrElse((user)->{},() ->{
			userRepo.save(admin);
			System.out.println("Admin Is Created Successfully");
		});

	}
}
