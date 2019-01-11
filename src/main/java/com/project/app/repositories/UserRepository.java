package com.project.app.repositories;

import com.project.app.models.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {

    boolean existsByUsername(String username);

}
