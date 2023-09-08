package com.example.springdatajpa.controller;

import com.example.springdatajpa.Repository.UserDao;
import com.example.springdatajpa.common.CommonResult;
import com.example.springdatajpa.entity.User;
import com.example.springdatajpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CommonResult commonResult;

    @Autowired
    private UserDao userDao;


    @PostMapping("/addUser")
    public CommonResult addUser(User user) {
        try {
            userService.addUser(user);
            return commonResult;
        } catch (Exception e) {
            e.printStackTrace();
            commonResult.setState(500);
            commonResult.setMsg("添加失败");
            return commonResult;
        }
    }


    /**
     * 使用JpaSpecificationExecutor进行数据的复杂查询
     * @param pageNo
     * @param pageSize
     * @param email
     * @param userName
     * @param regTime
     * @return
     */
    @GetMapping("/findUser")
    public List<User> queryUser(int pageNo, int pageSize, String email, String userName, String regTime) {
        List<User> result;
        Specification<User> userSpecification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                ArrayList<Predicate> predicates = new ArrayList<>();

                if (userName != null) {
                    predicates.add(criteriaBuilder.equal(root.get("username"), userName));
                }

                if (regTime != null) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("regTime"), regTime));
                }
                if (email != null) {
                    predicates.add(criteriaBuilder.equal(root.get("email"), email));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        if (pageNo == 0 && pageSize == 0) {
            result = userDao.findAll(userSpecification);
        } else {
            result = userDao.findAll(userSpecification, PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "regTime"))).getContent();
        }

        return result;
    }
}
