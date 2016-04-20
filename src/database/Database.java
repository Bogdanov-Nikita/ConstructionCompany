/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package database;

/**
 *
 * @author Nik
 */
//указание таблиц и полей баз данных, в коментариях указанны правила и типы данных double это REAL
public  final class Database {
    
    //Primary Key (PK)
    //Foreign key (FK)
    
    public final static class Order{
        public final static String Table = "Order";
        public final static String id = "id";//PK
        public final static String number = "number";//уникальный INT
        public final static String client_id = "client_id";//FK by Client
        public final static String manager_id = "manager_id";//FK by Manager
        public final static String status = "status";//INT
        public final static String current_coast = "current_coast";//double >= 0
        public final static String create_date = "create_date";//timestamp
        public final static String update_date = "update_date";//timestamp
        public final static String end_date = "end_date";//timestamp
    }
    
    public final static class StorageInformation{
        public final static String Table = "StorageInformation";
        public final static String id = "id";//PK
        public final static String location = "location";//char
    }
    
    public final static class Storage{
        public final static String Table = "Storage";
        public final static String id = "id";//FK by StorageInfo
        public final static String resource_id = "resource_id";//FK by Resource
        public final static String amount = "amount";//INT >= 0
    }
    
    public final static class Work{
        public final static String Table = "Work";
        public final static String id = "id";//PK
        public final static String description = "description";//char
        public final static String service_coast = "service_coast";//double >= 0
    }
    
    public final static class Resource{
        public final static String Table = "Resource";
        public final static String id = "id";//PK
        public final static String type = "type";//INT
        public final static String name = "name";//char
        public final static String coast = "coast";//double >= 0
    }
    
    public final static class Estimate{
        public final static String Table = "Estimate";
        public final static String id = "id";//PK
        public final static String order_id = "order_id";
        public final static String type = "type";//INT
        public final static String coast = "coast";//double >= 0
        public final static String paid = "paid";//boolean
    }
    
    public final static class EstimateWorks{
        public final static String Table = "EstimateWorks";
        public final static String estimate_id = "estimate_id";//FK by Estimate
        public final static String work_id = "work_id";//FK by Work        
        public final static String master_id = "master_id"; //FK by Master
        public final static String finish = "finish";//boolean
    }
    
    public final static class WorksAndResource{
        public final static String Table = "WorksAndResource";
        public final static String work_id = "work_id";//FK by Work 
        public final static String resource_id = "resource_id";//FK by Resource
        public final static String amount = "amount";//INT >= 0
    }
    
    public final static class Manager{
        public final static String Table = "Manager";
        public final static String id = "id";//PK
        public final static String name = "name";//char
        public final static String phone_number = "phone_number";//char
        public final static String office_address = "office_address";//char
    }
    
    public final static class Client{
        public final static String Table = "Client";
        public final static String id = "id";//PK
        public final static String name = "name";//char
        public final static String phone_number = "phone_number";//char
        public final static String type = "type";//INT > 0
        public final static String addres = "addres";//char
    }
    
    public final static class Master{
        public final static String Table = "Master";
        public final static String id = "id";//PK
        public final static String name = "name";//char
        public final static String phone_number = "phone_number";//char
    }
    public final static class StorageView{
        public final static String View = "StorageView";
        public final static String id = "id";//PK
        public final static String location = "location";//char
        public final static String resource_id = "resource_id";//FK by Resource
        public final static String type = "type";//INT
        public final static String name = "name";//char
        public final static String coast = "coast";//double >= 0
        public final static String amount = "amount";//INT >= 0
    }
    public final static class WorkView{
        public final static String View = "WorkView";
        public final static String id = "id";//PK
        public final static String description = "description";//char
        public final static String service_coast = "service_coast";//double >= 0
        public final static String resource_id = "resource_id";//FK by Resource
        public final static String type = "type";//INT
        public final static String name = "name";//char
        public final static String coast = "coast";//double >= 0
        public final static String amount = "amount";//INT >= 0
    }
    public final static class EstimateView{
        public final static String View = "EstimateView";
        public final static String id = "id";//PK
        public final static String order_id = "order_id";
        public final static String type = "type";//INT
        public final static String coast = "coast";//double >= 0
        public final static String paid = "paid";//boolean
        public final static String master_id = "master_id"; //FK by Master
        public final static String work_id = "work_id";//FK by Work
        public final static String finish = "finish";//boolean
    }
}
