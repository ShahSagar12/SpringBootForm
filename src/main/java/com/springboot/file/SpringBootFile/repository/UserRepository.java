package com.springboot.file.SpringBootFile.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.springboot.file.SpringBootFile.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

}
