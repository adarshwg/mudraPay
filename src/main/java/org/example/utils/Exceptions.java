package org.example.utils;
public class Exceptions {
    public static class InvalidCredentials extends Exception{
        public InvalidCredentials(String str){
            super(str);
        }
    }
    public static class UserNotFoundException extends Exception{
        public UserNotFoundException(String str){
            super(str);
        }
    }
    public static class InvalidTokenException extends Exception{
        public InvalidTokenException(String str){
            super(str);
        }
    }
    public static class UserAlreadyExistsException extends Exception{
        public UserAlreadyExistsException(String str){
            super(str);
        }
    }
    public static class DatabaseException extends Exception{
        public DatabaseException(String str){
            super(str);
        }
    }
    public static class  LowBalanceException extends Exception{
        public LowBalanceException (String str){
            super(str);
        }
    }

}
