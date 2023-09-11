package com.example.springdatajpa.Repository;

import com.example.springdatajpa.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email,Long> {

}
