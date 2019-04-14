package com.duocode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duocode.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
