package com.placement.placement_platform.service;

import com.placement.placement_platform.entity.Admin;
import com.placement.placement_platform.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin registerAdmin(Admin admin) {

        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new IllegalArgumentException(
                    "Admin email already registered."
            );
        }

        admin.setPassword(
                passwordEncoder.encode(admin.getPassword())
        );

        return adminRepository.save(admin);
    }
}