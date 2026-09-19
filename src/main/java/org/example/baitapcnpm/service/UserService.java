package org.example.baitapcnpm.service;

import lombok.RequiredArgsConstructor;
import org.example.baitapcnpm.model.Role;
import org.example.baitapcnpm.model.User;
import org.example.baitapcnpm.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Transactional
    public User register(String username, String password, String fullName, String email, String phone, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Tên đăng nhập '" + username + "' đã tồn tại!");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email '" + email + "' đã được sử dụng!");
        }

        User user = User.builder()
                .username(username)
                .password(password) // Trong hệ thống giáo dục/demo lưu text hoặc hash
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .role(role != null ? role : Role.ROLE_CANDIDATE)
                .build();

        return userRepository.save(user);
    }

    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User updateUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id));
        user.setRole(newRole);
        return userRepository.save(user);
    }
}
