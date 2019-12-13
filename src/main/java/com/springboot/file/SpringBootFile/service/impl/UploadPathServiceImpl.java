package com.springboot.file.SpringBootFile.service.impl;

import java.io.File;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.file.SpringBootFile.service.UploadPathService;
@Service
@Transactional
public class UploadPathServiceImpl implements UploadPathService{
	
	@Autowired
	ServletContext context;
	
	@Override
	public File setFilePath(String modifiedFileName, String filePath) {
		String path2 = "/"+filePath+"/";
		String path = context.getRealPath(path2);
		boolean exists=new File(path).exists();
		if(!exists) {
			new File(path).mkdir();
		}
		String modifiedFilePath=context.getRealPath(path2+File.separator+modifiedFileName);
		return new File(modifiedFilePath);
	}
	

}
