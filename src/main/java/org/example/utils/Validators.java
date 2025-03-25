package org.example.utils;

public class Validators {
    public static boolean matchRegexWithString(String rex, String inputString){
        return inputString.matches(rex);
    }
    public static boolean checkUsernameFormat(String username){
        return matchRegexWithString("^(?=.*[0-9])(?=.*[a-z])(?!.* ).{5,}$",username);
    }
    public static boolean checkPasswordFormat(String password){
        return matchRegexWithString("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{5,}$",password);
    }
    public static boolean checkPinFormat(String pin){
        return matchRegexWithString("^\\d{6}$",pin);
    }
    public static boolean checkCredentialsFormat(String enteredUsername,String enteredPassword,String enteredPin){
        return checkUsernameFormat(enteredUsername)&&checkPasswordFormat(enteredPassword)&&checkPinFormat(enteredPin);
    }
    public static boolean checkTokenFormat(String token) {
        String regex = "^Bearer\\s(?:[\\w-]*\\.){2}[\\w-]*$";
        return matchRegexWithString(regex, token);
    }

}
