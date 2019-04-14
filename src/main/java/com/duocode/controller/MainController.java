package com.duocode.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.duocode.entities.User;
import com.duocode.repositories.UserRepository;
import com.duocode.service.RecaptchaService;
import com.duocode.util.StringUtils;

@Controller
public class MainController {

	@Autowired
	UserRepository userRepository;
	
	RecaptchaService captchaService =new RecaptchaService();
	
	@PostMapping("/api/signup")
	public ResponseEntity<?> signup(@Valid User user, @RequestParam(name="g-recaptcha-response") String recaptchaResponse,
	  HttpServletRequest request){
		System.out.println("Recaptcha response: "+recaptchaResponse+" name: "+user.getFirstName());


	  String ip = request.getRemoteAddr();
	  String captchaVerifyMessage = 
	      captchaService.verifyRecaptcha(ip, recaptchaResponse);
	 
	  if ( StringUtils.isNotEmpty(captchaVerifyMessage)) {
	    Map<String, Object> response = new HashMap<>();
	    response.put("message", captchaVerifyMessage);
	    return ResponseEntity.badRequest()
	      .body(response);
	  }
	  userRepository.save(user);
	  return ResponseEntity.ok().build();
	}
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("user", new User());
		return "index";
	}
}
