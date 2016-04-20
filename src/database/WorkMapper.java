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
            Database.Work.id,
            Database.Work.description,
            Database.Work.service_coast,
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                Database.Work.Table,
                columns,
                Database.Work.id+"=?",
                new String[]{String.valueOf(Id)},
                null,
                Database.Work.id)
        );
        double ServiceCoast = rs.getDouble(2);
        String Description = rs.getString(3).trim();
        rs.next();
        db.commitTransaction();
        //загрузка таблицы ресурсов
        db.startTransaction();
        String Wcolumns[] = {
            Database.WorkView.id,
            Database.WorkView.resource_id,
            Database.WorkView.type,
            Database.WorkView.name,
            Database.WorkView.coast,
            Database.WorkView.amount
        };
        ResultSet Rrs = db.executeQuery(
                QueryBilder.select(
                        Database.WorkView.View,
                        Wcolumns,
                        Database.WorkView.id+"=?",
                        new String[]{String.valueOf(Id)},
                        null,
                        Database.WorkView.resource_id
                )
        );        
        ArrayList<Resource> ResourceList = new ArrayList<>();
        while(Rrs.next()){
            ResourceList.add(new Resource(rs.getInt(2),rs.getInt(6),rs.getInt(3),rs.getDouble(5),rs.getString(4).trim()));
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
                        Database.WorkView.resource_id
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
        db.commitTransaction();
        return WorkList;       
    }

    @Override
    public boolean save(Work e, DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean saveArray(ArrayList<Work> list, DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(int id, DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int generateIDs(int size, DatabaseManager db) throws SQLException {
        ResultSet Rset = db.executeQuery("SELECT GEN_ID( WORK_ID_GENERATOR, " + String.valueOf(size) + " ) FROM RDB$DATABASE;"); 
        Rset.next();        
        return Rset.getInt(1);
    }
    
}
