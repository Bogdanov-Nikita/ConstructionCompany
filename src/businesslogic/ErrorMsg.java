/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic;

/**
 * Тип ошибки и некотроая числовая информация создан специально для обработки ошибок от склада при работе менеджера
 * @author Nik
 */
public class ErrorMsg {
    int code;
    int other;

    public ErrorMsg(int code, int other) {
        this.code = code;
        this.other = other;
    }
    
}
