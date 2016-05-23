/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

/**
 *
 * @author Nik
 */
public class ExitMsg {
    
    public static final int SUCCESS = 0x1; 
    public static final int FILE_WRITE_ERROR = -0x1;
    public static final int CONFIG_ERROR = - 0x2;
    public static final int CURRENT_ID_ERROR = - 0x3;
    public static final int DATABASE_ERROR = - 0x4;
    public static final int DATABASE_ROLE_ERROR = - 0x5;
    public static final int DATABASE_LOAD_ERROR = - 0x6;
    public static final int DATABASE_SAVE_ERROR = - 0x7;
    public static final int DATABASE_DELETE_ERROR = - 0x8;
    public static final int INPUT_ERROR = - 0x9;
    public static final int ROLE_ERROR = - 0xA;
    public static final int BUISNESS_LOGIC_ERROR = - 0xB;
    
    int code;
    String massage;

    public ExitMsg(int code, String massage) {
        this.code = code;
        this.massage = massage;
    }

    public int getCode() {
        return code;
    }

    public String getMassage() {
        return massage;
    }
    
    
    
}
