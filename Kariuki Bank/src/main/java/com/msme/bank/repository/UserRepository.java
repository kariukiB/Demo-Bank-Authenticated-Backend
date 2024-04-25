package com.msme.bank.repository;

import com.msme.bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    Boolean existsByAccountNumber(String accountNumber);

    User findByAccountNumber(String accountNumber);

    Optional<User> findByEmail(String email);

    Optional<User> findByFirstName(String name);

    List<User> findAllByFirstNameContainsIgnoreCase(String name);
}
