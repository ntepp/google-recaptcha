package com.duocode.util;

public class StringUtils {

	public static boolean isNotEmpty(String captchaVerifyMessage) {
		// TODO Auto-generated method stub
		if(captchaVerifyMessage == null) return false;
		return captchaVerifyMessage.length()==0?false:true;
	}

}
