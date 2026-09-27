package com.enerpulse.service;

import com.enerpulse.common.api.PageResult;
import com.enerpulse.common.exception.BusinessException;
import com.enerpulse.entity.User;
import com.enerpulse.entity.UserRole;
import com.enerpulse.repository.UserRepository;
import com.enerpulse.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Long DEFAULT_TENANT_ID = 1L;

    public PageResult<User> list(int page, int pageSize, String keyword, String status) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<User> p = userRepository.findAll(pageable);
        List<User> content = p.getContent().stream()
                .filter(u -> !StringUtils.hasText(keyword)
                        || u.getUsername().contains(keyword)
                        || (u.getNickname() != null && u.getNickname().contains(keyword)))
                .filter(u -> !StringUtils.hasText(status) || status.equals(u.getStatus()))
                .peek(u -> u.setPasswordHash(null))
                .toList();
        return new PageResult<>(content, page, pageSize, p.getTotalElements());
    }

    public User create(User user, List<Long> roleIds) {
        user.setTenantId(DEFAULT_TENANT_ID);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash() != null ? user.getPasswordHash() : "123456"));
        User saved = userRepository.save(user);
        bindRoles(saved.getId(), roleIds);
        saved.setPasswordHash(null);
        return saved;
    }

    public User update(Long id, User user) {
        User existing = get(id);
        existing.setNickname(user.getNickname());
        existing.setMobile(user.getMobile());
        existing.setEmail(user.getEmail());
        existing.setStatus(user.getStatus());
        User saved = userRepository.save(existing);
        saved.setPasswordHash(null);
        return saved;
    }

    public void delete(Long id) {
        User u = get(id);
        u.setDeletedAt(OffsetDateTime.now());
        u.setStatus("INACTIVE");
        userRepository.save(u);
    }

    public void resetPassword(Long id) {
        User u = get(id);
        u.setPasswordHash(passwordEncoder.encode("123456"));
        userRepository.save(u);
    }

    @Transactional
    public void bindRoles(Long userId, List<Long> roleIds) {
        userRoleRepository.deleteByUserId(userId);
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                userRoleRepository.save(ur);
            }
        }
    }

    public User get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40401, "用户不存在"));
    }
}
