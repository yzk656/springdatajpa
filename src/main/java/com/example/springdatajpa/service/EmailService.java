package com.example.springdatajpa.service;


import com.example.springdatajpa.Repository.EmailRepository;
import com.example.springdatajpa.entity.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    /**
     * 查询全部数据
     * @return
     */
    public List<Email> list(){
        return emailRepository.findAll();
    }


    /*查询单个数据*/
    public Email detailById(Long id){
        return emailRepository.findById(id).get();
    }


}
