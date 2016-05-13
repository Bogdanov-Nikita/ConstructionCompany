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
    public final static String DataFormat = "dd.MM.yyyy HH:mm:ss";
    
    public static final class FileName{
        public final static String Config = "config.xml";
    }
    public static final class ErrMsg{
        //All error massage
        public final static String DatabaseError = "Невозможно подключится к базе данных.";
        
        public final static String AuthError_1 = "Данной записи не найдено.";
        public final static String AuthError_2 = "Не соответствие логина и роли.";
        public final static String AuthError_3 = "Неверный пароль";  
        
        public final static String AuthDatabaseError = "Не найденно роли в базе данных";
        
        public final static String ConfigError_1 = "Конфигурационный файл не найден или повреждён";
        public final static String ConfigError_2 = "Запись конфигурационнго файла не корректна или отсутствует.\n"
                + "Для дополнительной информации смотри лог.";
        
        public final static String NaneError = "Не заполненно поле Ф.И.О.!";
        public final static String PhoneError = "Не заполненно поле телефонного номера!";
        public final static String AddressError = "Не заполненно поле адреса!";
        
        public final static String CollumSelectionError = "Выделите строку";
        public final static String DeleteError = "Невозможно удалить";
        
        public final static String InputDataError_1 = "Введённое значение не корректно";
        public final static String InputDataError_2 = "Ведённое число не должно быть отрицательно";
        public final static String InputPayError = "Введеное число больше необходимого";
        public final static String InputResourceNameError = "Не заполненно поле название ресурса";
        public final static String InputResourceCoastError_1 = "Не заполненно поле стоимости ресурса";
        public final static String InputResourceCoastError_2 = "Стоимость ресурса не может быть отрицательна";
        public final static String InputResourceTypeError_1 = "Не заполненно поле типа ресурса";
        public final static String InputResourceTypeError_2 = "Типа ресурса не может быть отрицательным числом";
        public final static String InputStorageError = "Не заполненно поле адреса склада";
        public final static String InputWorkDescriptionError = "Не заполненно поле описание работы";
        public final static String InputWorkCoastError_1 = "Не заполненно поле стоимости работы";
        public final static String InputWorkCoastError_2 = "Стоимость работы не может быть отрицательна";
        public final static String InputWorkAmountError_1 = "Не заполненно поле количества";
        public final static String InputWorkAmountError_2 = "Количество ресурсов неможет быть отрицательннм";
        
        public final static String StorageAmountError = "Нельзя взять больше чем есть есть на складе";
    }
    public static final class RoleType{
        public final static String Defaul = "Default";        
        public final static String ConfigManager = "Manager";
        public final static String ConfigClient  = "Client";
        public final static String ConfigMaster  = "Master";
        public final static String Manager = "Менеджер";
        public final static String Client  = "Заказчик";
        public final static String Master  = "Прораб";
        
    }
    public static final class ModelType {
        public final static int DefaultModel = 0x0;
        public final static int ManagerModel = 0x1;
        public final static int MasterModel  = 0x2;
        public final static int ClientModel  = 0x3;        
    }
    public static final class ModelEvent {
        public final static int DefaultEvent  = 0x0;
        public final static int onClickOk     = 0x1;
        public final static int onClickCansel = 0x2;
        public final static int onClickAction = 0x3;
    }
    public static final class Client{
        public final static String CLIENT_TYPE = "Тип:";
        public final static String DEFAULT = "Неизвстен";
        public final static String PHYSICAL = "Физический";
        public final static String LEGAL = "Юридический";
        
        public static String ClientTypeName(int type){
            String str = DEFAULT;
            switch(type){
                case businesslogic.Client.PHYSICAL: str = PHYSICAL; break;
                case businesslogic.Client.LEGAL: str = LEGAL; break;
            }
            return str;
        }
    }
    public static final class Order{
        public final static String DEFAULT = "Неизвстен";
        public final static String OPEN = "открыт";
        public final static String INPROGRESS = "отправлен на исполнение";
        public final static String WAITING_ACKNOWLEDGMENT_TAKE = "отправлен на подтверждение, клиенту";
        public final static String WAITING_PAY = "ожидание оплаты от клиента";
        public final static String WAITING_ACKNOWLEDGMENT_PAY = "на подтверждение оплаты, менеджеру";
        public final static String CLOSE = "закрыт";
        
        public static String StatusName(int Status){
            String str = DEFAULT;
            switch(Status){
                case businesslogic.Order.OPEN: str = OPEN; break;
                case businesslogic.Order.INPROGRESS: str = INPROGRESS; break;
                case businesslogic.Order.WAITING_ACKNOWLEDGMENT_TAKE: str = WAITING_ACKNOWLEDGMENT_TAKE; break;
                case businesslogic.Order.WAITING_PAY: str = WAITING_PAY; break;
                case businesslogic.Order.WAITING_ACKNOWLEDGMENT_PAY: str = WAITING_ACKNOWLEDGMENT_PAY; break;
                case businesslogic.Order.CLOSE: str = CLOSE; break;  
            }
            return str;
        }        
    }
    
    public static final class Estimate{
        public final static String DEFAULT = "неизвстен";
        public final static String PAID = "оплачена";
        public final static String FINISH = "завершена";
        public final static String NOTPAID = "не оплачена";
        public final static String NOTFINISH = "не завершена";
        public final static String MAIN = "основная";
        public final static String ADDITIONAL = "дополнительная";
        
        public static String TypeName(int type){
            String str = DEFAULT;
            switch(type){
                case businesslogic.Estimate.MAIN: str = MAIN; break;
                case businesslogic.Estimate.ADDITIONAL: str =ADDITIONAL; break;
            }
            return str;
        }
        
        public static String StatusName(boolean isPaid,boolean isFinish){
            return ((isPaid)? (PAID) : (NOTPAID)) + " | " + ((isFinish) ? (FINISH) : (NOTFINISH));
        }
    }
    public static final class Work{        
        public final static String FINISH = "завершена";
        public final static String NOTFINISH = "не завершена";
        
        public static String StatusName(boolean isFinish){         
            return (isFinish) ? (FINISH) : (NOTFINISH);
        }
    }
    public static final class Dialog{
        //Dialog Action
        public final static int AddAction = 1;
        public final static int EditAction = 2;
        public final static int SendAction = 3;
        public final static int TakeAction = 4;
        //Title
        public final static String Send = "Отправить ресурс";
        public final static String Take = "Взять ресурс";
        public final static String Add = "Добавить";
        public final static String Edit = "Редактировать";
        public final static String Delete = "Удалить";
        public final static String Update = "Обновление";
        //View
        public final static String Order = "Заказ:";
        public final static String Client = "Клиент:";
        public final static String Manager = "Менеджер:";
        public final static String Status = "Статус:";
        public final static String TotalCoast = "Общая стоимость:";
        public final static String CurrentCoast = "Текущая стоимость:";
        public final static String Create  = "Дата создания:";
        public final static String LastUpdate = "Дата последнего обновления:";
        public final static String End = "Дата завершения:";
        //Input
        public final static String ClientPayInputMsg = "Введите сколько хотите заплатить";
        public final static String ClienPayAccept = "Оплата проведена успешно";
        //Info        
        public final static String UpdateMsg = "Нет данных для обновления";
    }
}
