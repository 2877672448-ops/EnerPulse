package com.enerpulse.service;

import com.enerpulse.common.exception.BusinessException;
import com.enerpulse.dto.request.LoginRequest;
import com.enerpulse.dto.response.LoginResponse;
import com.enerpulse.entity.User;
import com.enerpulse.repository.UserRepository;
import com.enerpulse.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new BusinessException(40101, "用户名或密码错误"));
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(40301, "账号已停用");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(40101, "用户名或密码错误");
        }
        user.setLastLoginAt(OffsetDateTime.now());
        userRepository.save(user);
        String token = jwtUtil.generate(user.getId(), user.getUsername());
        return new LoginResponse(token, jwtUtil.getExpirationSeconds(),
                new LoginResponse.UserInfo(user.getId(), user.getUsername(), user.getNickname()));
    }

    public User getCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(40401, "用户不存在"));
    }
}
