package com.webhotel.webhotel.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.webhotel.webhotel.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);
}