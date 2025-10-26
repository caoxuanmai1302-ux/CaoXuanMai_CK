package com.example.caoxuanmai_ck.util;
public class ValidateUtils {
    public static boolean isEmail(String s){ return android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches(); }
    public static boolean isPhone(String s){ return android.util.Patterns.PHONE.matcher(s).matches() && s.replaceAll("\\D","").length()>=9; }
    public static boolean strongPassword(String s){
        return s!=null && s.length()>=8 && s.matches(".*[A-Z].*") && s.matches(".*\\d.*")
                && s.matches(".*[~!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/\\\\].*");
    }
}
