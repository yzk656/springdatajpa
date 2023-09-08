package com.example.springdatajpa.controller;


import com.example.springdatajpa.Repository.PersonRepository;
import com.example.springdatajpa.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
