package com.academicsaas.identity.infrastructure.persistence;

import com.academicsaas.identity.domain.model.User;
import com.academicsaas.identity.domain.model.valueobject.Email;
import com.academicsaas.identity.domain.model.valueobject.UserId;
import com.academicsaas.identity.domain.repository.UserRepository;
import com.academicsaas.identity.infrastructure.persistence.mapper.UserMapper;
import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataUserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;
    private final UserMapper mapper;

    public UserRepositoryAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
        this.mapper = new UserMapper();
    }

    @Override
    public User save(User user) {
        var jpa = mapper.toJpa(user);
        var saved = springDataUserRepository.save(jpa);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return springDataUserRepository.findById(id.value())
            .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return springDataUserRepository.findByEmail(email.value())
            .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return springDataUserRepository.existsByEmail(email.value());
    }

    @Override
    public List<User> findAll() {
        return springDataUserRepository.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public void delete(UserId id) {
        springDataUserRepository.deleteById(id.value());
    }
}
