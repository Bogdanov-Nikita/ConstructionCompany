/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Resource;
import businesslogic.Work;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class WorkMapper extends Mapper<Work, DatabaseManager>{

    @Override
    public Work load(int Id, DatabaseManager db) throws SQLException {
        db.startTransaction();
        String columns[] = {
            Database.WorkView.id,//1            
            Database.WorkView.description,//2
            Database.WorkView.service_coast,//3
            Database.WorkView.resource_id,//4
            Database.WorkView.type,//5
            Database.WorkView.name,//6
            Database.WorkView.coast,//7
            Database.WorkView.amount//8
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.WorkView.View,
                        columns,
                        "\"" + Database.WorkView.id + "\"=?",
                        new String[]{String.valueOf(Id)},
                        null,
                        Database.WorkView.resource_id
                )
        );
        rs.next();
        String Description = rs.getString(2).trim(); 
        double ServiceCoast = rs.getDouble(3);        
        ArrayList<Resource> ResourceList = new ArrayList<>();
        ResourceList.add(new Resource(rs.getInt(4),rs.getInt(8),rs.getInt(5),rs.getDouble(7),rs.getString(6).trim()));
        while(rs.next()){
            ResourceList.add(new Resource(rs.getInt(4),rs.getInt(8),rs.getInt(5),rs.getDouble(7),rs.getString(6).trim()));
        }
        db.commitTransaction();        
        return new Work(Id,ResourceList,ServiceCoast,Description);
    }

    @Override
    public ArrayList<Work> loadList(DatabaseManager db) throws SQLException {
        db.startTransaction();
        String Wcolumns[] = {
            Database.WorkView.id,            
            Database.WorkView.description,
            Database.WorkView.service_coast,
            Database.WorkView.resource_id,
            Database.WorkView.type,
            Database.WorkView.name,
            Database.WorkView.coast,
            Database.WorkView.amount
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.WorkView.View,
                        Wcolumns,
                        null,
                        null,
                        null,
                        Database.WorkView.id+ "\",\"" + Database.WorkView.resource_id
                )
        );        
        
        ArrayList<Work> WorkList = new ArrayList<>();
        ArrayList<ArrayList<Resource>> listList = new ArrayList<>();
        
        String CurrentDescription;
        String LastDescription = null;
        double CurrentCoast;//service_coast
        double LastCoast = 0;
        int currentId;
        int lastId = 0;        
        int i = 0;
        
        while(rs.next()){
            currentId = rs.getInt(1);
            CurrentDescription = rs.getString(2).trim();
            CurrentCoast = rs.getDouble(3);
            if(currentId != lastId){
                if(lastId != 0){
                    WorkList.add(new Work(lastId,listList.get(i),LastCoast,LastDescription));
                    i++;
                }                                
                lastId = currentId;
                LastDescription = CurrentDescription;
                LastCoast = CurrentCoast;
                listList.add(new ArrayList<>());                
            }
            listList.get(i).add(new Resource(rs.getInt(4), rs.getInt(8), rs.getInt(5), rs.getDouble(7), rs.getString(6).trim()));
        }
        WorkList.add(new Work(lastId,listList.get(i),LastCoast,LastDescription));
        db.commitTransaction();
        return WorkList;       
    }

    @Override
    public boolean save(Work e, DatabaseManager db) throws SQLException {
        boolean flag;
        ArrayList<Integer> NewElement = new ArrayList<>();
        //создаём список добавляемых ресурсов.
        for(int j = 0; j < e.getResources().size(); j++){
            int id = e.getResource(j).getId();
            if(id == 0 || id == -1){
                NewElement.add(j);
            }
        }
        
        //insert/update Resource Table
        new ResourceMapper().saveArray(e.getResources(), db);
        //insert/update  Work Table
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put(Database.Work.Table + "\".\"" + Database.Work.description,e.getDescription());
        value.put(Database.Work.Table + "\".\"" + Database.Work.service_coast,String.valueOf(e.getServiceCoast()));
        if(e.getId() == 0||e.getId() == -1){
            //Get Store Id
            int nextID = generateIDs(1, db);
            e.setId(nextID);
            //запись в Work Table
            value.put(Database.Work.Table + "\".\"" + Database.Work.id, String.valueOf(nextID));
            flag = db.execute(QueryBilder.insert(Database.Work.Table,value));            
            db.commitTransaction();
            //insert в WorksAndResource Table
            db.startTransaction();
            for(int i = 0; i <  e.getResources().size(); i++){
                ContentValues ResourceValue = new ContentValues();
                // work_id - nextID
                // resource_id - (nextResID + i)
                //amount - e.getResource(i).getAmount()
                ResourceValue.put(Database.WorksAndResource.Table + "\".\"" + Database.WorksAndResource.work_id,String.valueOf(nextID) );
                ResourceValue.put(Database.WorksAndResource.Table + "\".\"" + Database.WorksAndResource.resource_id,String.valueOf(e.getResource(i).getId()));
                ResourceValue.put(Database.WorksAndResource.Table + "\".\"" + Database.WorksAndResource.amount,String.valueOf(e.getResource(i).getAmount()));
                flag = db.execute(QueryBilder.insert(Database.WorksAndResource.Table,ResourceValue));              
            }
        }else{
            //update
            value.put(Database.Work.id, String.valueOf(e.getId()));
            String whereClause = "\"" + Database.Work.Table + "\".\"" + Database.Work.id +"\"=?";
            String args[] = {String.valueOf(e.getId())};
            flag = db.execute(QueryBilder.update(Database.Work.Table, value, whereClause, args));
            db.commitTransaction();
            //insert/update в Storage Table
            db.startTransaction();
            int index = 0;
            for(int i = 0; i <  e.getResources().size(); i++){
                ContentValues ResourceValue = new ContentValues();
                // work_id - nextID
                // resource_id - (nextResID + i)
                //amount - e.getResource(i).getAmount()
                ResourceValue.put(Database.WorksAndResource.Table + "\".\"" + Database.WorksAndResource.work_id,String.valueOf(e.getId()) );
                ResourceValue.put(Database.WorksAndResource.Table + "\".\"" + Database.WorksAndResource.resource_id,String.valueOf(e.getResource(i).getId()));
                ResourceValue.put(Database.WorksAndResource.Table + "\".\"" + Database.WorksAndResource.amount,String.valueOf(e.getResource(i).getAmount()));
                if(!NewElement.isEmpty() && (NewElement.get(index) == e.getResource(i).getId())){
                    //insert
                    flag = db.execute(QueryBilder.insert(Database.WorksAndResource.Table,ResourceValue));
                    index++;
                }else{
                    //update
                    String whereClauseR = "\"" + Database.WorksAndResource.Table + "\".\"" + Database.WorksAndResource.work_id +"\"=? AND " + 
                    "\"" + Database.WorksAndResource.Table + "\".\"" + Database.WorksAndResource.resource_id +"\"=?";
                    String argsR[] = {String.valueOf(e.getId()),String.valueOf(e.getResource(i).getId())};
                    flag = db.execute(QueryBilder.update(Database.WorksAndResource.Table,ResourceValue,whereClauseR,argsR));
                }
            }
        }
        db.commitTransaction();        
        return flag;
    }

    @Override
    public boolean saveArray(ArrayList<Work> list, DatabaseManager db) throws SQLException {
        boolean flag = false;
        for (Work list1 : list) {
            flag = save(list1, db);
        }
        return flag;
    }

    @Override
    public void delete(int id, DatabaseManager db) throws SQLException {
        clearWorkResources(id, db);
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put("\"" + Database.Work.Table + "\".\"" + Database.Work.id +"\"", String.valueOf(id));
        db.execute(QueryBilder.delete(Database.Work.Table, value));
        db.commitTransaction();
    }

    public void clearWorkResources(int id,DatabaseManager db) throws SQLException{
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put("\"" + Database.WorksAndResource.Table + "\".\"" + Database.WorksAndResource.work_id +"\"", String.valueOf(id));
        db.execute(QueryBilder.delete(Database.WorksAndResource.Table, value));
        db.commitTransaction();
    }
    
    @Override
    public int generateIDs(int size, DatabaseManager db) throws SQLException {
        ResultSet Rset = db.executeQuery("SELECT GEN_ID( WORK_ID_GENERATOR, " + String.valueOf(size) + " ) FROM RDB$DATABASE;"); 
        Rset.next();        
        return Rset.getInt(1);
    }
    
}
