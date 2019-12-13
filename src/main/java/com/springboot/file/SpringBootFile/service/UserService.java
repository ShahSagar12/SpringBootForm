package com.springboot.file.SpringBootFile.service;

import java.util.List;

import com.springboot.file.SpringBootFile.model.User;
import com.springboot.file.SpringBootFile.model.UserFiles;

public interface UserService {

	List<User> getAll();

	User save(User user);

	User findById(Long id);

	List<UserFiles> findFilesByUserId(Long id);

	User update(User user);

}
