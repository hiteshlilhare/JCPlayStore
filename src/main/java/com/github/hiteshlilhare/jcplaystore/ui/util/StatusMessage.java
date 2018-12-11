package com.github.hiteshlilhare.jcplaystore.ui.util;

/**
 *
 * @author Hitesh
 */
public class StatusMessage {
    public enum Code{
      INITIALIZE,SUCCESS,NOTFOUND,INVAILD,ALREADYEXISTS, NOTEXIST, FAILURE;  
    };
    private Code code;
    private String message;
    
    public StatusMessage(){
        code = Code.INITIALIZE;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return code.toString() + ":" + message;
    }
    
    
    
}
