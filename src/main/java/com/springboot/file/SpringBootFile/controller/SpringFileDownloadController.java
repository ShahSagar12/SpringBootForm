package com.springboot.file.SpringBootFile.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.springboot.file.SpringBootFile.model.UserFiles;
import com.springboot.file.SpringBootFile.service.UserService;

@Controller
public class SpringFileDownloadController {
	@Autowired
	private ServletContext context;
	@Autowired
	private UserService userService;
	
	@GetMapping(value="/downloadfile/{fileName}/{modifiedFileName}")
	public void downloadFile(@PathVariable String fileName,@PathVariable String modifiedFileName,HttpServletResponse response) {
		String fullPath=context.getRealPath("/images/"+File.separator+modifiedFileName);
		File file=new File(fullPath);
		final int BUFFER_SIZE=4096;
		if(file.exists()) {
			try {
				FileInputStream inputStream=new FileInputStream(file);
				String mimeType=context.getMimeType(fullPath);
				response.setContentType(mimeType);
				response.setHeader("Content-disposition", "attachment;filename="+fileName);
				OutputStream outputStream=response.getOutputStream();
				byte[] buffer=new byte[BUFFER_SIZE];
				int byteRead=-1;
				while((byteRead=inputStream.read(buffer))!=-1) {
					outputStream.write(buffer, 0, byteRead);
				}
				inputStream.close();
			} catch (Exception e) {
				e.getMessage();
			}
		}
	}
	
	@GetMapping(value="/downloadfileaszip/{id}")
	public void downloadfileaszip(@PathVariable Long id,HttpServletResponse response) {
		List<UserFiles> userFiles=userService.findFilesByUserId(id);
		if(userFiles!=null && userFiles.size()>0) {
			downloadZipFiles(userFiles,"files.zip",response);
		}
	}

	private void downloadZipFiles(List<UserFiles> userFiles, String fileName, HttpServletResponse response) {
		try {
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			ZipOutputStream zos=new ZipOutputStream(baos);
			byte bytes[]=new byte[122048];
			for(UserFiles file:userFiles) {
				if(file!=null && StringUtils.hasText(file.getModifiedFileName())) {
					FileInputStream fis=new FileInputStream(context.getRealPath("/images/"+File.separator+file.getModifiedFileName()));
					BufferedInputStream bis=new BufferedInputStream(fis);
					zos.putNextEntry(new ZipEntry(file.getFileName()));
					int byteread;
					while((byteread=bis.read(bytes))!=-1) {
						zos.write(bytes, 0, byteread);
					}
					zos.closeEntry();;
					bis.close();
					fis.close();
				
				}
			}
			zos.flush();
			baos.flush();
			zos.close();
			baos.close();
			
			byte[] zip=baos.toByteArray();
			ServletOutputStream sos=response.getOutputStream();
			response.setContentType("application/zip");
			response.setHeader("Content-disposition", "attachment;filename="+fileName);
			sos.write(zip);
			sos.flush();
			sos.close();
			
		} catch (Exception e) {
			e.getMessage();
		}
		
	}	

}
