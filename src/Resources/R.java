/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Resources;

/**
 *
 * @author Nik
 */
//некоторые из значений могут быть заменены в дальнейшем.
//хранит общие идентификаторы и значения и enum для разных случаев, аналог в Android
public final class R{
    public static final class FileName{
        public final static String Config = "config.xml";
    }
    public static final class ErrMsg{
        public final static String AuthError = "Не соответствие пароля логину или роли.";    
    }
    public static final class RoleType{
        public final static String Defaul = "Default";
        public final static String Manager = "Менеджер";
        public final static String Client  = "Заказчик";
        public final static String Master  = "Прораб";
    }
    public static final class ModelType {
        public final static int DefaultModel = 0x0;
        public final static int ManagerModel = 0x1;
        public final static int ClientModel  = 0x2;
        public final static int MasterModel  = 0x3;
    }
    public static final class ModelEvent {
        public final static int DefaultEvent  = 0x0;
        public final static int onClickOk     = 0x1;
        public final static int onClickCansel = 0x2;
        public final static int onClickAction = 0x3;
    }
    
}
