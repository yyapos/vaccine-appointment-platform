package com.vaccine.auth.security;

import com.vaccine.auth.entity.User;
import com.vaccine.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthService authService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== UserDetailsServiceImpl.loadUserByUsername ===");
        System.out.println("用户名: " + username);

        User user = authService.getUserByUsername(username);

        System.out.println("查询到的用户: " + (user != null ? user.getUsername() : "null"));
        if (user != null) {
            System.out.println("密码: " + user.getPassword());
        }

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (user.getStatus() != 1) {
            throw new UsernameNotFoundException("账号已被禁用: " + username);
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(new ArrayList<>())
                .build();

        System.out.println("返回的UserDetails: " + userDetails.getUsername());
        System.out.println("UserDetails密码: " + userDetails.getPassword());

        return userDetails;
    }
}