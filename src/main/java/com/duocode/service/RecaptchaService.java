package com.duocode.service;

import java.io.Console;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.logging.log4j.MarkerManager.Log4jMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.duocode.repositories.UserRepository;
import com.duocode.security.ReCaptchaResponse;
import com.duocode.util.RecaptchaUtil;
import org.apache.commons.lang3.StringUtils;

//@Slf4j
@Service
public class RecaptchaService {
 
  	
  String recaptchaSecret ="6LdBEp4UAAAAAItGrg3HmpsUlS9IA9cUlcaF9oLE";
   
  private static final String GOOGLE_RECAPTCHA_VERIFY_URL =
    "https://www.google.com/recaptcha/api/siteverify";
  
  

  RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
 
  public String verifyRecaptcha(String ip, String recaptchaResponse){
	
    Map<String, String> body = new HashMap<>();
    body.put("secret", recaptchaSecret);
    body.put("response", recaptchaResponse);
    body.put("remoteip", ip);
    System.out.println("message");
    //log.debug("Request body for recaptcha: {}", body);
    ResponseEntity<Map> recaptchaResponseEntity = restTemplateBuilder.build()
        .postForEntity(GOOGLE_RECAPTCHA_VERIFY_URL+
          "?secret={secret}&response={response}&remoteip={remoteip}", 
          body, Map.class, body);
             
    System.out.println("Response from recaptcha: {}" +recaptchaResponseEntity);
    Map<String, Object> responseBody = 
      recaptchaResponseEntity.getBody();
       
    boolean recaptchaSucess = (Boolean)responseBody.get("success");
    System.out.println("Success= "+ recaptchaSucess);
    if ( !recaptchaSucess) {
      List<String> errorCodes = 
        (List<String>)responseBody.get("error-codes");
       
      String errorMessage = errorCodes.stream()
          .map(s -> RecaptchaUtil.RECAPTCHA_ERROR_CODE.get(s))
          .collect(Collectors.joining(", "));
      System.out.println(errorMessage);
      return errorMessage;
    }else {
    	
      return StringUtils.EMPTY;
    }
  }
  
  private static final Logger log = LoggerFactory.getLogger(RecaptchaService.class);

  @Autowired
  private RestOperations restTemplate;


  @Autowired
  private HttpServletRequest request;

  public boolean validate(String ip, String reCaptchaResponse){
      URI verifyUri = URI.create(String.format(
    		  GOOGLE_RECAPTCHA_VERIFY_URL + "?secret=%s&response=%s&remoteip=%s",
    		  recaptchaSecret,
              reCaptchaResponse,
              ip
      ));

      try {
    	  ReCaptchaResponse response = restTemplate.getForObject(verifyUri, ReCaptchaResponse.class);
          return response.isSuccess();
      } catch (Exception ignored){
          log.error("", ignored);
          // ignore when google services are not available
          // maybe add some sort of logging or trigger that'll alert the administrator
      }

      return true;
  }
  
  @Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}
 }


