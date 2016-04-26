/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Estimate;
import businesslogic.Order;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Nik
 */
public class OrderMapper extends Mapper<Order, DatabaseManager>{

    @Override
    public Order load(int Id, DatabaseManager db) throws SQLException {        
        db.startTransaction();
        String columns[] = {
            Database.Order.id,//1            
            Database.Order.number,//2
            Database.Order.client_id,//3
            Database.Order.manager_id,//4
            Database.Order.status,//5
            Database.Order.current_coast,//6
            Database.Order.create_date,//7
            Database.Order.update_date,//8
            Database.Order.end_date//9
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.Order.Table,
                        columns,
                        "\"" + Database.Order.id + "\"=?",
                        new String[]{String.valueOf(Id)},
                        null,
                        Database.Order.id
                )
        );
        rs.next();
        int number = rs.getInt(2);
        int client_id = rs.getInt(3);
        int manager_id = rs.getInt(4);
        int status = rs.getInt(5);
        double coast = rs.getDouble(6);
        Date start = rs.getDate(7);
        Date update = rs.getDate(8);
        Date end = rs.getDate(9);
        
        String Vcolumns[] ={
            Database.EstimateView.order_id,
            Database.EstimateView.id            
        };
        ResultSet Vrs = db.executeQuery(
                QueryBilder.select(
                        Database.EstimateView.View,
                        Vcolumns,
                        "\"" + Database.EstimateView.order_id + "\"=?",
                        new String[]{String.valueOf(Id)},
                        null,
                        Database.EstimateView.id
                )
        );
        ArrayList<Integer> list = new ArrayList<>();
        int LastId = 0;
        int CurrerntId = 0;
        while(Vrs.next()){
            CurrerntId = Vrs.getInt(2);
            if(CurrerntId != LastId){
                if(LastId > 0){
                    list.add(LastId);
                }
                LastId = CurrerntId;
            }
        }
        list.add(CurrerntId);
        db.commitTransaction();
        ArrayList<Estimate> EstimateList = new ArrayList<>();
        for(Integer e : list){
            Estimate est = new EstimateMapper().load(e, db);
            if(est != null){
                EstimateList.add(est);
            }
        }
        return new Order(Id,number, status, client_id, manager_id, coast, 0, start, update, end, EstimateList);
    }

    @Override
    public ArrayList<Order> loadList(DatabaseManager db) throws SQLException {        
        ArrayList<Integer> IdList = new ArrayList<>();
        db.startTransaction();
        String columns[] = {
            Database.Order.id  
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.Order.Table,
                        columns,
                        null,
                        null,
                        null,
                        Database.Order.id
                )
        );        
        while(rs.next()){
            IdList.add(rs.getInt(1));
        }
        db.commitTransaction();        
        ArrayList<Order> list = new ArrayList<>();
        //возможно стоит заменить на более легковесный.
        for (Integer IdList1 : IdList) {
            list.add(load(IdList1, db));
        }
        return list;
    }

    @Override
    public boolean save(Order e, DatabaseManager db) throws SQLException {
        boolean flag;
        SimpleDateFormat ft = new SimpleDateFormat ("dd.MM.yyyy HH:mm:ss");
        ContentValues value = new ContentValues();
        value.put(Database.Order.Table + "\".\"" + Database.Order.number,String.valueOf(e.getNumber()));
        value.put(Database.Order.Table + "\".\"" + Database.Order.status,String.valueOf(e.getStatus()));
        value.put(Database.Order.Table + "\".\"" + Database.Order.current_coast,String.valueOf(e.getCurrentCoast()));
        value.put(Database.Order.Table + "\".\"" + Database.Order.client_id,String.valueOf(e.getClientID()));
        value.put(Database.Order.Table + "\".\"" + Database.Order.manager_id,String.valueOf(e.getManagerID()));        
        value.put(Database.Order.Table + "\".\"" + Database.Order.create_date,String.valueOf(ft.format(e.getCreate())));
        value.put(Database.Order.Table + "\".\"" + Database.Order.update_date,String.valueOf(ft.format(e.getLastUpdate())));
        if(e.getEnd() != null){
            value.put(Database.Order.Table + "\".\"" + Database.Order.end_date,String.valueOf(ft.format(e.getEnd())));
        }
        if(e.getId() == 0||e.getId() == -1){
            //insert
            int nextID = generateIDs(1, db);
            e.setId(nextID);
            value.put(Database.Order.Table + "\".\"" + Database.Order.id,String.valueOf(nextID));
            flag = db.execute(QueryBilder.insert(Database.Order.Table, value));
            db.commitTransaction();
        }else{
            //update
            String whereClause = "\"" + Database.Order.Table + "\".\"" +Database.Order.id + "\"=?";
            String args[] = new String[]{String.valueOf(e.getId())};
            flag = db.execute(QueryBilder.update(Database.Order.Table,value,whereClause,args));
            db.commitTransaction();
        }
        for(int i = 0; i < e.getEstimateList().size(); i++){
            e.getEstimate(i).setOrderId(e.getId());
        }
        //isert/update Estimate
        new EstimateMapper().saveArray(e.getEstimateList(), db);
        return flag;
    }

    @Override
    public boolean saveArray(ArrayList<Order> list, DatabaseManager db) throws SQLException {
        boolean flag = false;
        for (Order list1 : list) {
            flag = save(list1, db);
        }
        return flag;
    }

    @Override
    public void delete(int id, DatabaseManager db) throws SQLException {
        clearEstimates(id,db);
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put("\"" + Database.Order.Table + "\".\"" + Database.Order.id +"\"", String.valueOf(id));
        db.execute(QueryBilder.delete(Database.Order.Table, value));
        db.commitTransaction();
    }

    public void clearEstimates(int Id, DatabaseManager db) throws SQLException{
        db.commitTransaction();
        ArrayList<Integer> list = new ArrayList<>();
        String columns[] = {
            Database.EstimateView.id,//1            
            Database.EstimateView.order_id,//2
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.EstimateView.View,
                        columns,
                        "\"" + Database.EstimateView.order_id + "\"=?",
                        new String[]{String.valueOf(Id)},
                        null,
                        Database.EstimateView.id
                )
        );
        while(rs.next()){
            list.add(rs.getInt(1));
        }        
        db.commitTransaction();
        for(Integer deleteId:list){
            new EstimateMapper().delete(deleteId, db);
        }
    }
    
    @Override
    public int generateIDs(int size, DatabaseManager db) throws SQLException {
        ResultSet Rset = db.executeQuery("SELECT GEN_ID( ORDER_ID_GENERATOR, " + String.valueOf(size) + " ) FROM RDB$DATABASE;"); 
        Rset.next();        
        return Rset.getInt(1);    
    }
    
}
