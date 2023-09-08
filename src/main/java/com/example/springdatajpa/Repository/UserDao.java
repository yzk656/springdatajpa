package com.example.springdatajpa.Repository;

import com.example.springdatajpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaSpecificationExecutor<User>, JpaRepository<User, Long> {

}
