package com.springboot.file.SpringBootFile.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.file.SpringBootFile.model.User;
import com.springboot.file.SpringBootFile.model.UserFiles;
import com.springboot.file.SpringBootFile.service.UserService;

@Controller
public class SpringBootFileController {
	
	@Autowired
	UserService userService;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("users",userService.getAll());
		model.addAttribute("user",new User());
		model.addAttribute("isAdd",true);
		model.addAttribute("files",new ArrayList<UserFiles>());
		return "view/user";
	}

	@PostMapping("/save")
	public String save(Model model,@ModelAttribute User user,RedirectAttributes redirectAttributes) {
		User dbUser=userService.save(user);
		if(dbUser!=null) {
			redirectAttributes.addFlashAttribute("successMessage","User Saved Successfully");
			return "redirect:/";
		}else {
			redirectAttributes.addFlashAttribute("errorMessage","User Cannot be Saved! Please Try Again Later");
			model.addAttribute("user",user);
			return "user/view";
		}	
	}
	
	@GetMapping("/edituser/{id}")
	public String edit(Model model,@PathVariable Long id) {
		model.addAttribute("users",userService.getAll());
		model.addAttribute("user",userService.findById(id));
		model.addAttribute("isAdd",false);
		model.addAttribute("files",userService.findFilesByUserId(id));
		return "view/user";
	}
	
	
	@PostMapping("/update")
	public String update(Model model,@ModelAttribute User user,RedirectAttributes redirectAttributes) {
		User dbUser=userService.update(user);
		if(dbUser!=null) {
			redirectAttributes.addFlashAttribute("successMessage","User Updated Successfully");
			return "redirect:/";
		}else {
			redirectAttributes.addFlashAttribute("errorMessage","User Cannot be Updated! Please Try Again Later");
			model.addAttribute("user",user);
			return "user/view";
		}	
	}
	
	@GetMapping("/deleteuser/{id}")
	public String delete(Model model,@PathVariable Long id,RedirectAttributes redirectAttributes) {
		userService.deleteFilesByUserId(id);
		userService.deleteUserByUserId(id);
		redirectAttributes.addFlashAttribute("successMessage","User deleted Successfully");
		return "redirect:/";
	}
	
	@GetMapping("/viewuser/{id}")
	public String viewuser(Model model,@PathVariable Long id) {
		model.addAttribute("users",userService.getAll());
		model.addAttribute("user",userService.findById(id));
		model.addAttribute("files",userService.findFilesByUserId(id));
		return "view/viewuser";
	}
	

}
