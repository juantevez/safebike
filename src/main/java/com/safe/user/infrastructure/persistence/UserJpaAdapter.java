package com.safe.user.infrastructure.persistence;


import com.safe.user.domain.model.User;
import com.safe.user.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component // o @Repository, si es más específico
public class UserJpaAdapter implements UserRepositoryPort {

    private final UserRepository userRepository; // ¡Inyección aquí!

    public UserJpaAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}