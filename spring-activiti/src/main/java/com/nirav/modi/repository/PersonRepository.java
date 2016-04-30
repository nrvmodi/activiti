package com.nirav.modi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nirav.modi.entity.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

	Person findByUsername(String username);

}