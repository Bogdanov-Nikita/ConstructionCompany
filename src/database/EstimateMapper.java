/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Estimate;
import businesslogic.Work;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class EstimateMapper extends Mapper<Estimate, DatabaseManager>{

    @Override
    public Estimate load(int Id, DatabaseManager db) throws SQLException {
        db.startTransaction();
        String columns[] = {
            Database.EstimateView.id,//1            
            Database.EstimateView.order_id,//2
            Database.EstimateView.type,//3
            Database.EstimateView.coast,//4
            Database.EstimateView.paid,//5
            Database.EstimateView.master_id,//6
            Database.EstimateView.work_id,//7
            Database.EstimateView.finish//8
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.EstimateView.View,
                        columns,
                        "\"" + Database.EstimateView.id + "\"=?",
                        new String[]{String.valueOf(Id)},
                        null,
                        Database.EstimateView.work_id
                )
        );        
        rs.next();
        
        ArrayList<Work> WorkList = new ArrayList<>();
        ArrayList<Integer> IdList = new ArrayList<>();
        ArrayList<Integer> MasterIdList = new ArrayList<>();
        int Type = rs.getInt(3);
        boolean Paid = (rs.getInt(5)==1);
        double Coast = rs.getDouble(4);
        int OrderId = rs.getInt(2);
        MasterIdList.add(rs.getInt(6));
        IdList.add(rs.getInt(7));   
        while(rs.next()){
            MasterIdList.add(rs.getInt(6));
            IdList.add(rs.getInt(7));
        }
        db.commitTransaction();
        
        int index = 0;
        for (Integer IdList1 : IdList) {
            WorkList.add(new WorkMapper().load(IdList1, db));
            WorkList.get(index).setMasterId(MasterIdList.get(index));
            index++;
        }
        return new Estimate(Id,OrderId,Paid,Type,Coast,WorkList);
    }

    @Override
    public ArrayList<Estimate> loadList(DatabaseManager db) throws SQLException {
        ArrayList<Integer> IdList = new ArrayList<>();
        db.startTransaction();
        String columns[] = {
            Database.EstimateView.id,//1            
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.EstimateView.View,
                        columns,
                        null,
                        null,
                        null,
                        Database.EstimateView.id+ "\",\"" + Database.EstimateView.work_id
                )
        );        
        while(rs.next()){
            IdList.add(rs.getInt(1));
        }
        db.commitTransaction();        
        ArrayList<Estimate> list = new ArrayList<>();
        //возможно стоит заменить на более легковесный.
        for (Integer IdList1 : IdList) {
            list.add(load(IdList1, db));
        }
        return list;
    }

    @Override
    public boolean save(Estimate e,DatabaseManager db) throws SQLException {
        boolean flag;
        ArrayList<Integer> NewElement = new ArrayList<>();
        //создаём список добавляемых ресурсов.
        for(int j = 0; j < e.getWorkList().size(); j++){
            int id = e.getWorkList().get(j).getId();
            if(id == 0 || id == -1){
                NewElement.add(j);
            }
        }
        
        //isert/update Work
        new WorkMapper().saveArray(e.getWorkList(), db);
        //add Estimate Table
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put(Database.Estimate.Table + "\".\"" + Database.Estimate.coast,String.valueOf(e.getCoast()));
        value.put(Database.Estimate.Table + "\".\"" + Database.Estimate.paid,String.valueOf(e.isPaid()?1:0));
        value.put(Database.Estimate.Table + "\".\"" + Database.Estimate.type,String.valueOf(e.getType()));
        value.put(Database.Estimate.Table + "\".\"" + Database.Estimate.order_id,String.valueOf(e.getOrderId()));        
        if(e.getId() == 0||e.getId() == -1){
            int nextID = generateIDs(1, db);
            e.setId(nextID);
            value.put(Database.Estimate.Table + "\".\"" + Database.Estimate.id,String.valueOf(nextID));
            flag = db.execute(QueryBilder.insert(Database.Estimate.Table, value));
            db.commitTransaction();
            //insert в EstimateWorks Table
            db.startTransaction();
            for(int i = 0;i < e.getWorkList().size(); i++){
                ContentValues WorkValue = new ContentValues();
                WorkValue.put(Database.EstimateWorks.Table + "\".\"" + Database.EstimateWorks.estimate_id,String.valueOf(nextID));
                WorkValue.put(Database.EstimateWorks.Table + "\".\"" + Database.EstimateWorks.master_id,String.valueOf(e.getWork(i).getMasterId()));
                WorkValue.put(Database.EstimateWorks.Table + "\".\"" + Database.EstimateWorks.work_id,String.valueOf(e.getWorkList().get(i).getId()));
                WorkValue.put(Database.EstimateWorks.Table + "\".\"" + Database.EstimateWorks.finish,String.valueOf(e.getWorkList().get(i).isFinish()?1:0));
                flag = db.execute(QueryBilder.insert(Database.EstimateWorks.Table, WorkValue));
            }
        }else{
            //update
            value.put(Database.Estimate.Table + "\".\"" + Database.Estimate.id,String.valueOf(e.getId()));
            String whereClause = "\"" + Database.Estimate.id + "\"=?";
            String args[] = new String[]{String.valueOf(e.getId())};
            flag = db.execute(QueryBilder.update(Database.Estimate.Table,value,whereClause,args));
            db.commitTransaction();
            //insert/update в EstimateWorks Table
            int index = 0;
            for(int i = 0; i <  e.getWorkList().size(); i++){
                ContentValues WorkValue = new ContentValues();
                WorkValue.put(Database.EstimateWorks.Table + "\".\"" + Database.EstimateWorks.estimate_id,String.valueOf(e.getId()) );
                WorkValue.put(Database.EstimateWorks.Table + "\".\"" + Database.EstimateWorks.work_id,String.valueOf(e.getWork(i).getId()));
                WorkValue.put(Database.EstimateWorks.Table + "\".\"" + Database.EstimateWorks.master_id,String.valueOf(e.getWork(i).getMasterId()));
                WorkValue.put(Database.EstimateWorks.Table + "\".\"" + Database.EstimateWorks.finish,String.valueOf(e.getWork(i).isFinish()?1:0));
                if(!NewElement.isEmpty() && (NewElement.get(index) == e.getWork(i).getId())){
                    //insert
                    flag = db.execute(QueryBilder.insert(Database.EstimateWorks.Table,WorkValue));
                    index++;
                }else{
                    //update
                    String whereClauseR = "\"" + Database.EstimateWorks.Table + "\".\"" + Database.EstimateWorks.estimate_id +"\"=? AND " + 
                    "\"" + Database.EstimateWorks.Table + "\".\"" + Database.EstimateWorks.work_id +"\"=?";
                    String argsR[] = {String.valueOf(e.getId()),String.valueOf(e.getWork(i).getId())};
                    flag = db.execute(QueryBilder.update(Database.EstimateWorks.Table,WorkValue,whereClauseR,argsR));
                }
            }
        }      
        db.commitTransaction();
        return flag;
    }

    @Override
    public boolean saveArray(ArrayList<Estimate> list,DatabaseManager db) throws SQLException {
        boolean flag = false;
        for (Estimate list1 : list) {
            flag = save(list1, db);
        }
        return flag;
    }

    @Override
    public void delete(int id, DatabaseManager db) throws SQLException {
        clearEstimateWorks(id, db);
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put("\"" + Database.Estimate.Table + "\".\"" + Database.Estimate.id +"\"", String.valueOf(id));
        db.execute(QueryBilder.delete(Database.Estimate.Table, value));
        db.commitTransaction();
    }

    public void clearEstimateWorks(int id,DatabaseManager db) throws SQLException{
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put("\"" + Database.EstimateWorks.Table + "\".\"" + Database.EstimateWorks.estimate_id +"\"", String.valueOf(id));
        db.execute(QueryBilder.delete(Database.EstimateWorks.Table, value));
        db.commitTransaction();
    }
    
    @Override
    public int generateIDs(int size, DatabaseManager db) throws SQLException {
        ResultSet Rset = db.executeQuery("SELECT GEN_ID( ESTIMATE_ID_GENERATOR, " + String.valueOf(size) + " ) FROM RDB$DATABASE;"); 
        Rset.next();        
        return Rset.getInt(1);
    }
    
}
