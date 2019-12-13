package com.springboot.file.SpringBootFile.service.impl;



import java.io.File;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.file.SpringBootFile.model.User;
import com.springboot.file.SpringBootFile.model.UserFiles;
import com.springboot.file.SpringBootFile.repository.UserFileRepository;
import com.springboot.file.SpringBootFile.repository.UserRepository;
import com.springboot.file.SpringBootFile.service.UploadPathService;
import com.springboot.file.SpringBootFile.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService{
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UploadPathService uploadPathService;
	@Autowired
	private UserFileRepository userFileRepository;
	@Autowired
	private ServletContext context;

	@Override
	@ReadOnlyProperty
	public List<User> getAll() {

		return (List<User>) userRepository.findAll();
	}

	@Override
	public User save(User user) {
		user.setCreatedDate(new Date());
		User dbUser=userRepository.save(user);
		
		if(dbUser!=null && user.getFiles()!=null && user.getFiles().size()>0) {
			for(MultipartFile file:user.getFiles()) {
				if(file!=null && StringUtils.hasText(file.getOriginalFilename())) {
					String fileName=file.getOriginalFilename();
					String modifiedFileName=FilenameUtils.getBaseName(fileName)+"_"+System.currentTimeMillis()+"."+FilenameUtils.getExtension(fileName);
					File storeFile=uploadPathService.setFilePath(modifiedFileName,"images");
					if(storeFile!=null) {
						try{
							FileUtils.writeByteArrayToFile(storeFile, file.getBytes());
						}catch(Exception exp) {
							exp.getMessage();
						}
					}
					UserFiles userFile=new UserFiles();
					userFile.setFileName(fileName);
					userFile.setFileExtension(FilenameUtils.getExtension(fileName));
					userFile.setModifiedFileName(modifiedFileName);
					userFile.setUser(dbUser);
					userFileRepository.save(userFile);
					
				}
				
			}
		}
		return dbUser;
	}

	@Override
	public User findById(Long id) {
		
		return userRepository.findById(id).get();
	}

	@Override
	public List<UserFiles> findFilesByUserId(Long id) {
		
		return userFileRepository.findFilesByUserId(id);
	}

	@Override
	public User update(User user) {
		user.setUpdatedDate(new Date());
		User dbUser=userRepository.save(user);
		if(user!=null && user.getRemoveImages()!=null && user.getRemoveImages().size()>0) {
			userFileRepository.deleteFilesByUserIdAndImageNames(user.getId(),user.getRemoveImages());
			for(String file:user.getRemoveImages()) {
				File dbFile=new File(context.getRealPath("/images/"+File.separator+file));
				if(dbFile.exists()) {
					dbFile.delete();
				}
				
			}
		}
		if(dbUser!=null && user.getFiles()!=null && user.getFiles().size()>0) {
			for(MultipartFile file:user.getFiles()) {
				if(file!=null && StringUtils.hasText(file.getOriginalFilename())) {
					String fileName=file.getOriginalFilename();
					String modifiedFileName=FilenameUtils.getBaseName(fileName)+"_"+System.currentTimeMillis()+"."+FilenameUtils.getExtension(fileName);
					File storeFile=uploadPathService.setFilePath(modifiedFileName,"images");
					if(storeFile!=null) {
						try{
							FileUtils.writeByteArrayToFile(storeFile, file.getBytes());
						}catch(Exception exp) {
							exp.getMessage();
						}
					}
					UserFiles userFile=new UserFiles();
					userFile.setFileName(fileName);
					userFile.setFileExtension(FilenameUtils.getExtension(fileName));
					userFile.setModifiedFileName(modifiedFileName);
					userFile.setUser(dbUser);
					
					userFileRepository.save(userFile);
				}
			}
		}
		return dbUser;
		
		
	}

	@Override
	public void deleteFilesByUserId(Long id) {
		List<UserFiles> userFiles=userFileRepository.findFilesByUserId(id);
		if(userFiles!=null && userFiles.size()>0) {
			for(UserFiles dbFile:userFiles) {
				File dbStoreFile=new File(context.getRealPath("images"+File.separator+dbFile.getModifiedFileName()));
				if(dbStoreFile.exists()) {
					dbStoreFile.delete();
				}
			}
			userFileRepository.deleteFilesByUserId(id);
		}
		
	}

	@Override
	public void deleteUserByUserId(Long id) {
		userRepository.deleteById(id);
	}

	



}
