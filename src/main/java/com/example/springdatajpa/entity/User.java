package com.example.springdatajpa.entity;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false)
    private String password;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false,unique = true)
    private String nickName;
    @Column(nullable = false)
    private String regTime;


    public User(){
    }

    public User(String username, String password, String email, String nickName, String regTime) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickName = nickName;
        this.regTime = regTime;
    }
}
