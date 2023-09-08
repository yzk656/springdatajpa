package com.example.springdatajpa.service;

import com.example.springdatajpa.Repository.UserDao;
import com.example.springdatajpa.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class UserService {
    @Autowired
    private UserDao userDao;


    /**
     * 添加用户
     * @param user
     */
    public void addUser(User user){
        userDao.save(user);
    }

    /**
     * 根据Id修改用户数据
     * @param user
     */
    public void updateUser(User user){
        userDao.save(user);
    }


    /**
     * 删除用户数据
     * @param id
     */
    public void deleteUser(Long id){
        userDao.deleteById(id);
    }

    /**
     * 查询所有用户
     * @return
     */
    public List<User> listUser(){
        return userDao.findAll();
    }

    /**
     * 根据ID查询信息
     * @param id
     * @return
     */
    public User findById(Long id){
        return userDao.findById(id).get();
    }
}
