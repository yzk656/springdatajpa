package com.example.springdatajpa.controller;


import com.example.springdatajpa.Repository.PersonRepository;
import com.example.springdatajpa.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @PostMapping("/addPerson")
    public void addPerson(Person person) {
        personRepository.save(person);
    }

    @DeleteMapping("/deletePerson")
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    @GetMapping("/getByName")
    public void findByName(Person person) {
        Person personRepositoryByName = personRepository.findByName(person.getName());
        System.out.println(personRepositoryByName.getId());
    }

    /**
     * 通过年龄进行排序查询
     * @param age
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/getByAge")
    public List<Person> getByAge(Integer age, int pageNo, int pageSize){
        Pageable pageable=PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.ASC, "age"));
        List<Person> page = personRepository.findByAgeGreaterThanEqual(age, pageable);
        return page;
    }

}
