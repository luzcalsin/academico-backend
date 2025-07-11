package com.springboot.backend.academico.users.api.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.springboot.backend.academico.users.api.entities.User;

public interface UserRepository extends CrudRepository<User, Long>{
    Page<User> findAll(Pageable pageable);

    Optional<User> findByUsername(String name);
}
