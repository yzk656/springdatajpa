package com.example.springdatajpa.Repository;

import com.example.springdatajpa.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PersonRepository extends JpaRepository<Person,Long> {


    @Query(value = "select * from person where name = :name",nativeQuery = true)
    Person findByName(@Param("name") String name);

}
