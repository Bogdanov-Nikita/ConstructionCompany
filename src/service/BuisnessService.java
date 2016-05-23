/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import Resources.R;
import businesslogic.Client;
import businesslogic.Estimate;
import businesslogic.Manager;
import businesslogic.Master;
import businesslogic.Order;
import businesslogic.Resource;
import businesslogic.Role;
import businesslogic.Storage;
import businesslogic.Work;
import database.ClientMapper;
import database.DatabaseManager;
import database.EstimateMapper;
import database.ManagerMapper;
import database.MasterMapper;
import database.OrderMapper;
import database.ResourceMapper;
import database.StorageMapper;
import database.WorkMapper;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import service.config.Config;
import service.config.ConfigDatabase;
import service.config.ConfigRole;
import service.config.ConfigXmlParser;

/**
 *
 * @author Nik
 */
public class BuisnessService {
    
    boolean CONNECTED = false;      //Флаг подключения к базе данных
    int index = -1;                 //номер текушего пользователя в конфигурационном файле
    int CurrentId = 0;              //id текущего пользователя в базе
    Config config;                  //Конфигурационный файл содержит пароли и доступ к базе
    Role CurrentRole;               //Роль текщего пользователя системы
    DatabaseManager DBManager;      //Управление базой данных
    SimpleDateFormat DateFormat;    //Формат времени и даты
    
    //Для роли менеджера
    ArrayList<Order> ManagerOrderList;          //Список заказов
    ArrayList<Work> ManagerWorkList;            //Список работ
    ArrayList<Resource> ManagerResourceList;    //Список ресурсов
    ArrayList<Storage> ManagerStorageList;      //Список Складов
    ArrayList<Master> ManagerMasterList;        //Список прорабов
    ArrayList<Client> ManagerClientList;        //Список клиентов данного менеджера
    ArrayList<Work> TempEstimateWorkList;       //Веременный список смет
    //Для роли мастера
    ArrayList<Estimate> MasterEstimateList;     //Список Смет для получения списка работ для данного мастера
    //Для роли клиента
    ArrayList<Order> ClientOrderList;           //Список заказов данного клиента

    public BuisnessService() {
        DateFormat = new SimpleDateFormat (R.DataFormat);
        ManagerOrderList = null;
        ManagerWorkList = null;        
        ManagerResourceList = null;
        ManagerStorageList = null;
        ManagerClientList = null;
        ManagerMasterList = null;
        ClientOrderList = null;
        MasterEstimateList = null;
        TempEstimateWorkList = null;
    }    
    
    public void initConfig(){
        //получение данных из конфигурационного файла
        ConfigXmlParser configInfo = new ConfigXmlParser();
        if(configInfo.OpenConfig(R.FileName.Config) == ConfigXmlParser.CONFIG_SUCCESS){  
            config = configInfo.getConfig();           
        }else{
            config = null;
        }
    }
    
    /**
     * @param RoleComboBoxIndex RoleComboBox.getSelectedIndex() + 1
     * @param Login LoginTextField.getText()
     * @param Password PasswordFieldText.getPassword()
    */
    public void initAuth(int RoleComboBoxIndex,String Login,String Password){
        if(config != null){
            String role = null;
            switch(RoleComboBoxIndex){
                case R.ModelType.ManagerModel:
                    role = R.RoleType.ConfigManager;
                    break;
                case R.ModelType.MasterModel:
                    role = R.RoleType.ConfigMaster;
                    break;
                case R.ModelType.ClientModel: 
                    role = R.RoleType.ConfigClient;
                    break;
            }
            index = Authorization.ConfigRoleAuth(role, Login, Password, config);
        }
    }
        
    public void initDatabase(){
        ConfigDatabase ConfigInfo = config.getConfigItem(index).getDatabase();
        CONNECTED = true;
        //подключение к базе данных
        DBManager = new DatabaseManager(
                ConfigInfo.getUser(), 
                ConfigInfo.getPassword(), 
                ConfigInfo.getHost(), 
                ConfigInfo.getPath(), 
                ConfigInfo.getEncoding(), 
                ConfigInfo.getType(), 
                DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
        try{
            DBManager.connect();
        }catch(SQLException|NullPointerException ex){
            CONNECTED = false;
            Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, "Can't connected to database", ex);
        }
    }
    
    public void closeDatabaseConnection(){
        try {
            DBManager.closeConnection();
        } catch (SQLException ex) {
            Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, "Can't close database connection", ex);
        }
        DBManager.close();
    }
    
    /**
     * @param RoleComboBoxIndex RoleComboBox.getSelectedIndex()+1
     * @return Exit code and massage if error.
     */
    public ExitMsg initDatabaseRoleAuth(int RoleComboBoxIndex){        
        try {
            CurrentId = Authorization.DatabaseRoleAuth(config.getConfigItem(index).getRole(), DBManager);
            if(CurrentId > 0){
                switch(RoleComboBoxIndex){
                    case R.ModelType.ManagerModel://Менеджер
                        CurrentRole = new ManagerMapper().load(CurrentId, DBManager);
                        break;
                    case R.ModelType.MasterModel://Прораб
                        CurrentRole = new MasterMapper().load(CurrentId, DBManager);
                        break;
                    case R.ModelType.ClientModel://Клиент
                        CurrentRole = new ClientMapper().load(CurrentId, DBManager);
                        break;
                }
                return new ExitMsg(ExitMsg.SUCCESS, null);
            }else{
                CurrentRole = null;
                return new ExitMsg(ExitMsg.CURRENT_ID_ERROR,null);
            }
        } catch (SQLException ex) {
            CurrentRole = null;
            Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
            return new ExitMsg(ExitMsg.DATABASE_ROLE_ERROR, ex.toString());
        }
    }    
    
    public String getOrderNumberText(int SelectedRow){
        return R.Dialog.Order + " №" + 
                    String.valueOf(ClientOrderList.get(SelectedRow).getNumber());
    }
    
    public String getOrderStatusText(int SelectedRow){
        return R.Dialog.Status + " " + 
                R.Order.StatusName(ClientOrderList.get(SelectedRow).getStatus());
    }
    
    public String getOrderClientNameText(){
        return R.Dialog.Client + " " + CurrentRole.getName();
    }
    
    public String getOrderTotalCoastText(int SelectedRow){
        return R.Dialog.TotalCoast + " " + 
                String.valueOf(ClientOrderList.get(SelectedRow).getTotalCoast());
    }
    
    public String getOrderCurrentCoastText(int SelectedRow){
        return R.Dialog.CurrentCoast + " " +
                    String.valueOf(ClientOrderList.get(SelectedRow).getCurrentCoast());
    }
    
    public String getOrderCreateDateText(int SelectedRow){
        return R.Dialog.Create + " " + 
                DateFormat.format(ClientOrderList.get(SelectedRow).getCreate());
    }
    
    public String getOrderLastUpdateDateText(int SelectedRow){
        return R.Dialog.LastUpdate + " " + 
                DateFormat.format(ClientOrderList.get(SelectedRow).getLastUpdate());
    }
    
    public String getOrderEndDateText(int SelectedRow){
        return  R.Dialog.End + " " + 
                ((ClientOrderList.get(SelectedRow).getEnd() != null) ? 
                DateFormat.format(ClientOrderList.get(SelectedRow).getEnd()):" ");
    }
    
    public ExitMsg loadOrderListbyManager(){
        return loadList(R.Mapper.loadOrderListbyManager);
    }
    
    public ExitMsg loadManagerWorkList(){
        return loadList(R.Mapper.loadManagerWork);
    }
    
    public ExitMsg loadManagerResourceList(){
        return loadList(R.Mapper.loadManagerResourceList);
    }
    
    public ExitMsg loadManagerStorageList(){
        return loadList(R.Mapper.loadManagerStorageList);
    }
    
    public ExitMsg loadManagerClientList(){
        return loadList(R.Mapper.loadManagerClientList);
    }
    
    public ExitMsg loadManagerMasterList(){
        return loadList(R.Mapper.loadManagerMasterList);
    }
    
    public ExitMsg loadClientOrderList(){
        return loadList(R.Mapper.loadClientOrderList);
    }
    
    public ExitMsg loadMasterEstimateList(){
        return loadList(R.Mapper.loadMasterEstimateList);
    }
    
    private ExitMsg loadList(int Type){
        try {
            switch(Type){
                    case R.Mapper.loadOrderListbyManager:
                        ManagerOrderList = new OrderMapper().loadListbyManager(CurrentId, DBManager);
                        return new ExitMsg(ExitMsg.SUCCESS, null);
                    case R.Mapper.loadManagerWork:
                        ManagerWorkList = new WorkMapper().loadList(DBManager);
                        return new ExitMsg(ExitMsg.SUCCESS, null);
                    case R.Mapper.loadManagerResourceList:
                        ManagerResourceList = new ResourceMapper().loadList(DBManager);
                        return new ExitMsg(ExitMsg.SUCCESS, null);
                    case R.Mapper.loadManagerStorageList:
                        ManagerStorageList = new StorageMapper().loadList(DBManager);
                        return new ExitMsg(ExitMsg.SUCCESS, null);
                    case R.Mapper.loadManagerClientList:
                        ManagerClientList = new ClientMapper().loadList(DBManager);
                        return new ExitMsg(ExitMsg.SUCCESS, null);
                    case R.Mapper.loadManagerMasterList:
                        ManagerMasterList = new MasterMapper().loadList(DBManager);
                        return new ExitMsg(ExitMsg.SUCCESS, null);
                    case R.Mapper.loadClientOrderList:
                        ClientOrderList = new OrderMapper().loadListbyClient(CurrentId, DBManager);
                        return new ExitMsg(ExitMsg.SUCCESS, null);
                    case R.Mapper.loadMasterEstimateList:
                        MasterEstimateList = new EstimateMapper().loadListbyMaster(CurrentId, DBManager);
                        return new ExitMsg(ExitMsg.SUCCESS, null);
                    default:
                        return new ExitMsg(ExitMsg.DATABASE_LOAD_ERROR, null);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
            return new ExitMsg(ExitMsg.DATABASE_LOAD_ERROR, ex.toString());
        }
    }
    
    public Client loadClientByOrderId(int OrderId){
        try {
            return new OrderMapper().loadClientByOrderId(OrderId, DBManager);
        } catch (SQLException ex) {
            Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public ExitMsg saveClientOrderArray(){
        if(ClientOrderList != null){
            try {
                new OrderMapper().saveArray(ClientOrderList, DBManager);
                return new ExitMsg(ExitMsg.SUCCESS, null);
            } catch (SQLException ex) {
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR, ex.toString());
            }
        }else{
            return new ExitMsg(ExitMsg.DATABASE_LOAD_ERROR, null);
        }
    }
    
    public ExitMsg saveMasterEstimateArray(){
        if(MasterEstimateList != null){  
            try {
                new EstimateMapper().saveArray(MasterEstimateList, DBManager);
                return new ExitMsg(ExitMsg.SUCCESS, null);
            } catch (SQLException ex) {
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR, ex.toString());
            }                    
        }else{
            return new ExitMsg(ExitMsg.DATABASE_LOAD_ERROR, null);
        }
    }
    
    /**
     * @param SelectedIndex ManagerClientComboBox.getSelectedIndex()
     * @param Date DateCreateTextField.getText()
     * @param l возвращение значений Order для обновления в GUI
     * @return Exit code and massage if error.
    */
    public ExitMsg saveOrder(int SelectedIndex,String Date,CallBackSaveOrderDialog l){
        try {
            Order ord;
            int number = new OrderMapper().generateIDs(0, DBManager) + 1;
            ord = new Order(DateFormat.parse(Date), number);
            ord.setManagerID(CurrentId);
            ord.setClientID(ManagerClientList.get(SelectedIndex).getID());
            ord.setCurrentCoast(0);
            new OrderMapper().save(ord, DBManager);
            ManagerOrderList.add(ord);
            l.addRow(ord);
            return new ExitMsg(ExitMsg.SUCCESS, null);
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
            return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR,ex.toString());
        }
    }    
    
    /**
     * @param DialogAction
     * @param SelectedRow ManagerOrderTable.getSelectedRow()
     * @param EstimateType EstimateTypeComboBox.getSelectedIndex() + 1
     * @param SelectedRowEstimate ManagerEstimateTable.getSelectedRow()
     * @param l возвращение значений Estimate для обновления в GUI
     * @return Exit code and massage if error.
    */
    public ExitMsg saveEstimate(int DialogAction, int SelectedRow,int EstimateType, int SelectedRowEstimate, CallBackSaveEstimateDialog l){
        if(SelectedRow >= 0){
            Estimate es = new Estimate(EstimateType,(TempEstimateWorkList != null) ? new ArrayList<>(TempEstimateWorkList) : null);
            es.setOrderId(ManagerOrderList.get(SelectedRow).getId());
            es.CoastCalculation();
            try {
                //сохраняем оплату клиента.
                double ClientPay = ManagerOrderList.get(SelectedRow).CoastCalculation() - 
                            ManagerOrderList.get(SelectedRow).getCurrentCoast();
                if(DialogAction == R.Dialog.AddAction){                    
                    es.CoastCalculation();
                    new EstimateMapper().save(es, DBManager);
                    ManagerOrderList.get(SelectedRow).addEstimate(es);
                    //делаем перерасчёт
                    ManagerOrderList.get(SelectedRow).setCurrentCoast(ManagerOrderList.get(SelectedRow).CoastCalculation());
                    ManagerOrderList.get(SelectedRow).ClientPay(ClientPay);
                    //сохраняем изминения в заказе
                    new OrderMapper().save(ManagerOrderList.get(SelectedRow), DBManager);
                    l.editAction(es);
                    return new ExitMsg(ExitMsg.SUCCESS, null);
                }else{
                    if(DialogAction == R.Dialog.EditAction){
                        es.setId(ManagerOrderList.get(SelectedRow).getEstimate(SelectedRowEstimate).getId());
                        es.CoastCalculation();
                        new EstimateMapper().save(es, DBManager);
                        //делаем перерасчёт
                        ManagerOrderList.get(SelectedRow).setCurrentCoast(ManagerOrderList.get(SelectedRow).CoastCalculation());
                        ManagerOrderList.get(SelectedRow).ClientPay(ClientPay);
                        //сохраняем изминения в заказе
                        new OrderMapper().save(ManagerOrderList.get(SelectedRow), DBManager);
                        l.editAction(es);
                        return new ExitMsg(ExitMsg.SUCCESS, null);
                    }else{return new ExitMsg(ExitMsg.INPUT_ERROR, null);}
                }
            } catch (SQLException ex) {
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR,ex.toString());
            }
        }else{return new ExitMsg(ExitMsg.INPUT_ERROR, null);}
    }
    
    /**
     * @param DialogAction
     * @param Name NameTextField.getText()
     * @param ResourceCoast ResourceCoastTextField.getText()
     * @param ResourceType TypeTextField.getText()
     * @param SelectedRow ManagerResourceTable.getSelectedRow()
     * @param l возвращение значений Resource для обновления в GUI
     * @return Exit code and massage if error.
     */
    public ExitMsg saveResource(int DialogAction, String Name, String ResourceCoast, String ResourceType, int SelectedRow,CallBackSaveResourceDialog l){
        ExitMsg es = new ExitMsg(0,null);
        boolean flag = false;
        if(!Name.trim().equals("")){
        if(!ResourceCoast.trim().equals("")){            
        if(!ResourceType.trim().equals("")){ flag = true;}
        else{es = new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputResourceTypeError_1);}}
        else{es = new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputResourceCoastError_1);}}
        else{es = new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputResourceNameError);}
        if(flag){
            try{    
                double Сoast = Double.parseDouble(ResourceCoast.trim());
                int Type =  Integer.parseInt(ResourceType.trim());
                if(Сoast > 0){
                    if(Type > 0){
                        Resource res = new Resource(0,0,Type,Сoast,Name.trim());                        
                        try {
                            if(DialogAction == R.Dialog.AddAction){   
                                new ResourceMapper().save(res, DBManager);
                                ManagerResourceList.add(res);
                                l.addAction(res);
                                return new ExitMsg(ExitMsg.SUCCESS, null);
                            }else{
                                if(DialogAction == R.Dialog.EditAction){
                                    res.setId(ManagerResourceList.get(SelectedRow).getId());
                                    new ResourceMapper().save(res, DBManager);
                                    ManagerResourceList.set(SelectedRow, res);
                                    l.editAction(res);
                                    return new ExitMsg(ExitMsg.SUCCESS, null);
                                }else{return new ExitMsg(ExitMsg.INPUT_ERROR, null);}
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                            return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR,ex.toString());
                        }
                    }else{return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputResourceTypeError_2);}
                }else{return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputResourceCoastError_2);}
            }catch(NumberFormatException ex){
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputDataError_1);
            }
        }else{return es;}
    }
    
    /**
     * 
     * @param DialogAction
     * @param Description DescriptionTextField.getText()
     * @param WorkCoast WorkCoastTextField.getText()
     * @param SelectedRow ManagerWorkTable.getSelectedRow()
     * @param l возвращение значений Work для обновления в GUI
     * @return Exit code and massage if error.
    */
    public ExitMsg saveWork(int DialogAction, String Description, String WorkCoast, int SelectedRow, CallBackSaveWorkDialog l){
        if(!Description.trim().equals("")){
            if(!WorkCoast.trim().equals("")){
                try {
                    double ServiceCoast = Double.parseDouble(WorkCoast.trim());                
                    if(ServiceCoast > 0){
                        try {
                            Work wr = new Work(0, null,ServiceCoast,Description.trim());
                            if(DialogAction == R.Dialog.AddAction){
                                new WorkMapper().save(wr, DBManager);
                                ManagerWorkList.add(wr);
                                l.addAction(wr);
                                return new ExitMsg(ExitMsg.SUCCESS, null);
                            }else{
                                if(DialogAction == R.Dialog.EditAction){
                                    wr.setId(ManagerWorkList.get(SelectedRow).getId());
                                    wr.setResources(ManagerWorkList.get(SelectedRow).getResources());
                                    new WorkMapper().save(wr, DBManager);
                                    ManagerWorkList.set(SelectedRow, wr);
                                    l.editAction(wr);
                                    return new ExitMsg(ExitMsg.SUCCESS, null);
                                }else{return new ExitMsg(ExitMsg.INPUT_ERROR, null);}
                            }
                        } catch (SQLException ex) {
                                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                                return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR, ex.toString());
                        }
                    }else{return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputWorkCoastError_2);}
                } catch(NumberFormatException ex){
                    Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                    return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputDataError_1);
                }
            }else{
                return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputWorkCoastError_1);
            }
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputWorkDescriptionError);
        }
    }
    
    /**
     * @param DialogAction
     * @param Location StorageLocationTextField.getText()
     * @param SelectedRow ManagerStorageTable.getSelectedRow()
     * @param l возвращение значений Storage для обновления в GUI
     * @return Exit code and massage if error.
    */
    public ExitMsg saveStorage(int DialogAction, String Location, int SelectedRow, CallBackSaveStorageDialog l){
        if(!Location.trim().equals("")){
            try {
                Storage stor = new Storage(0,Location.trim(), null);
                if(DialogAction == R.Dialog.AddAction){
                    new StorageMapper().save(stor, DBManager);
                    ManagerStorageList.add(stor);
                    l.addAction(stor);
                    return new ExitMsg(ExitMsg.SUCCESS, null);
                }else{
                    if(DialogAction == R.Dialog.EditAction){
                        stor.setId(ManagerStorageList.get(SelectedRow).getId());
                        stor.setResources(ManagerStorageList.get(SelectedRow).getResources());
                        new StorageMapper().save(stor, DBManager);
                        ManagerStorageList.set(SelectedRow, stor);
                        l.editAction(stor);
                        return new ExitMsg(ExitMsg.SUCCESS, null);
                    }else{return new ExitMsg(ExitMsg.INPUT_ERROR, null);}
                }
            } catch (SQLException ex) {
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR, ex.toString());
            }
            
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputStorageError);
        }
    }
    
    /**
     * @param DialogAction
     * @param Name NameClientMasterTextField.getText()
     * @param Phone PhoneTextField.getText()
     * @param Address AddressTextField.getText()
     * @param Type (TypeComboBox.getSelectedIndex() + 1)
     * @param Master Master - true , Client - false.
     * @param MasterSelectedRow ManagerMasterTable.getSelectedRow()
     * @param ClientSelectedRow ManagerClientTable.getSelectedRow()
     * @param l для обновления значений в GUI
     * @return Exit code and massage if error.
    */
    public ExitMsg saveClientMaster(
            int DialogAction, 
            String Name, 
            String Phone, 
            String Address ,
            int Type, 
            boolean Master, 
            int MasterSelectedRow, 
            int ClientSelectedRow,
            CallBackSaveClientMaster l){
        ExitMsg es = null;
        boolean flag = false;
        if(!Name.trim().equals("")){
            if(!Phone.trim().equals("")){
                if(Master){
                    flag = true;
                }else{
                    if(!Address.trim().equals("")){
                        flag = true;
                    }else{
                        es = new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.NaneError);
                    }
                }
            }else{
                es = new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.PhoneError);
            }
        }else{
            es = new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.AddressError);
        }
        if(flag){
            if(Master){
                Master mr = new Master(0, Name.trim(), Phone.trim());
                try {
                    if(DialogAction == R.Dialog.AddAction){
                        new MasterMapper().save(mr, DBManager);
                        ManagerMasterList.add(mr);
                        l.addMaster(mr);
                        return new ExitMsg(ExitMsg.SUCCESS, null);
                    }else{
                        if(DialogAction == R.Dialog.EditAction){
                            mr.setID(ManagerMasterList.get(MasterSelectedRow).getID());
                            new MasterMapper().save(mr, DBManager);
                            ManagerMasterList.set(MasterSelectedRow, mr);
                            l.editMaster(mr);
                            return new ExitMsg(ExitMsg.SUCCESS, null);
                        }else{return new ExitMsg(ExitMsg.INPUT_ERROR, null);}
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                    return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR, ex.toString());
                }
            }else{
                Client cl = new Client(Type,
                        Address.trim(),
                        0,
                        Name.trim(),
                        Phone.trim()
                );
                try {
                    if(DialogAction == R.Dialog.AddAction){
                        new ClientMapper().save(cl, DBManager);
                        ManagerClientList.add(cl);
                        l.addClient(cl);
                        return new ExitMsg(ExitMsg.SUCCESS, null);
                    }else{
                        if(DialogAction == R.Dialog.EditAction){
                            cl.setID(ManagerClientList.get(ClientSelectedRow).getID());
                            new ClientMapper().save(cl, DBManager);
                            ManagerClientList.set(ClientSelectedRow, cl);
                            l.editClient(cl);
                            return new ExitMsg(ExitMsg.SUCCESS, null);
                        }else{return new ExitMsg(ExitMsg.INPUT_ERROR, null);}
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                    return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR, ex.toString());
                }
            }
        }else{return es;}
    }
    
    /**
     * @param DialogAction
     * @param ResourceAmount AmountTextField.getText()
     * @param SelectedWorkRow ManagerWorkTable.getSelectedRow()
     * @param SelectedStorageRow ManagerStorageTable.getSelectedRow();
     * @param SelectedIndex TypeNameComboBox.getSelectedIndex()
     * @param SelectedWorkResourceRow ManagerWorkResourceTable.getSelectedRow();
     * @param SelectedStorageResourceRow ManagerStorageResourceTable.getSelectedRow()
     * @param l для обновления значений в GUI
     * @return Exit code and massage if error.
    */
    public ExitMsg saveTakeSendResourceDialog(
            int DialogAction,
            String ResourceAmount,            
            int SelectedWorkRow,
            int SelectedStorageRow,
            int SelectedIndex,
            int SelectedWorkResourceRow,
            int SelectedStorageResourceRow,
            CallBackSaveTakeSendResourceDialog l
        ){
        
        if(!ResourceAmount.trim().equals("")){
            try {
                int Amount = Integer.parseInt(ResourceAmount.trim());
                if(Amount > 0){
                    switch(DialogAction){
                        case R.Dialog.AddAction:
                            Resource WorkResource = new Resource(
                                    ManagerResourceList.get(SelectedIndex).getId(),
                                    Amount,
                                    ManagerResourceList.get(SelectedIndex).getType(),
                                    ManagerResourceList.get(SelectedIndex).getCoast(),
                                    ManagerResourceList.get(SelectedIndex).getName());
                            try {
                                int WorkResPosition = AccumulateIfAlreadyExists(ManagerWorkList.get(SelectedWorkRow).getResources(), WorkResource);
                                if( WorkResPosition < 0){                                    
                                    ManagerWorkList.get(SelectedWorkRow).add(WorkResource);
                                    new WorkMapper().saveArray(ManagerWorkList, DBManager);
                                    l.addAction(WorkResource);
                                }else{
                                    new WorkMapper().saveArray(ManagerWorkList, DBManager);
                                    l.addAction(WorkResource,WorkResPosition);
                                }
                                l.WorkFinishAction();
                                return new ExitMsg(ExitMsg.SUCCESS, null);
                            } catch (SQLException ex) {
                                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                                return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR, ex.toString());
                            }
                        case R.Dialog.EditAction:
                            ManagerWorkList.get(SelectedWorkRow).getResource(SelectedWorkResourceRow).setAmount(Amount);
                            try {
                                new WorkMapper().saveArray(ManagerWorkList, DBManager);
                                return new ExitMsg(ExitMsg.SUCCESS, null);
                            } catch (SQLException ex) {
                                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                                return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR, ex.toString());
                            }
                        case R.Dialog.SendAction:
                            Resource StorageResource = new Resource(
                                    ManagerResourceList.get(SelectedIndex).getId(),
                                    Amount,
                                    ManagerResourceList.get(SelectedIndex).getType(),
                                    ManagerResourceList.get(SelectedIndex).getCoast(),
                                    ManagerResourceList.get(SelectedIndex).getName());
                            try {
                                int StorResPosition = AccumulateIfAlreadyExists(ManagerStorageList.get(SelectedStorageRow).getResources(), StorageResource);
                                if( StorResPosition < 0 ){
                                    ManagerStorageList.get(SelectedStorageRow).addResource(StorageResource);
                                    new StorageMapper().saveArray(ManagerStorageList, DBManager);
                                    l.sendAction(StorageResource);
                                    
                                }else{
                                    new StorageMapper().saveArray(ManagerStorageList, DBManager);
                                    l.sendAction(StorageResource, StorResPosition);
                                }
                                return new ExitMsg(ExitMsg.SUCCESS, null);
                            } catch (SQLException ex) {
                                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                                return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR, ex.toString());
                            }
                        case R.Dialog.TakeAction:
                            int tempAmount = ManagerStorageList.get(SelectedStorageRow).getResource(SelectedStorageResourceRow).getAmount() - Amount;
                            if(tempAmount < 0){
                                return new ExitMsg(ExitMsg.BUISNESS_LOGIC_ERROR, R.ErrMsg.StorageAmountError);
                            }else{
                                try {
                                    if(tempAmount > 0){
                                        ManagerStorageList.get(SelectedStorageRow).getResource(SelectedStorageResourceRow).setAmount(tempAmount);
                                        new StorageMapper().saveArray(ManagerStorageList, DBManager);
                                        l.takeAction();
                                    }else{
                                        if(tempAmount == 0){
                                            ManagerStorageList.get(SelectedStorageRow).getResource(SelectedStorageResourceRow).getId();
                                            new StorageMapper().clearStorageResource(
                                                    ManagerStorageList.get(SelectedStorageRow).getId(),
                                                    ManagerStorageList.get(SelectedStorageRow).getResource(SelectedStorageResourceRow).getId(),
                                                    DBManager);
                                            ManagerStorageList.get(SelectedStorageRow).getResources().remove(SelectedStorageResourceRow);
                                            l.takeAction2();
                                        }
                                    }
                                    return new ExitMsg(ExitMsg.SUCCESS, null);
                                } catch (SQLException ex) {
                                    Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                                    return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR, ex.toString());
                                }
                            }
                        default:
                            return new ExitMsg(ExitMsg.INPUT_ERROR, null);
                    }
                }else{return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputWorkAmountError_2);}
            }catch(NumberFormatException ex){
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputDataError_1);
            }
        }else{return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputWorkAmountError_1);}
    }
    
    private int AccumulateIfAlreadyExists(ArrayList<Resource> ResourceList, Resource res){        
        int flag = -1;
        int i = 0;
        
        for (Resource ResourceList1 : ResourceList) {
            if (ResourceList1.getType() == res.getType()) {
                res.setId(ResourceList1.getId());
                ResourceList1.setAmount(ResourceList1.getAmount() + res.getAmount());
                flag = i;
            }
            i++;
        }
        return flag;
    }
    
    public ExitMsg deleteOrder(int SelectedRow){
        if(SelectedRow >= 0){
            try {
                new OrderMapper().delete(ManagerOrderList.get(SelectedRow).getId(), DBManager);
                ManagerOrderList.remove(SelectedRow);
                return new ExitMsg(ExitMsg.SUCCESS, null);
            } catch (SQLException ex) {
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.DATABASE_DELETE_ERROR, R.ErrMsg.DeleteError);
            }
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.CollumSelectionError);
        }
    }
    
    public ExitMsg deleteWork(int SelectedRow){
        if(SelectedRow >= 0){
            try {
                new WorkMapper().delete(ManagerWorkList.get(SelectedRow).getId(), DBManager);
                ManagerWorkList.remove(SelectedRow);
                return new ExitMsg(ExitMsg.SUCCESS, null);
            } catch (SQLException ex) {
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.DATABASE_DELETE_ERROR, R.ErrMsg.DeleteError);
            }            
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.CollumSelectionError);
        }
    } 
    
    public ExitMsg deleteResource(int SelectedRow){
        if(SelectedRow >= 0){
            try {
                new ResourceMapper().delete(ManagerResourceList.get(SelectedRow).getId(), DBManager);
                ManagerResourceList.remove(SelectedRow);
                return new ExitMsg(ExitMsg.SUCCESS, null);
            } catch (SQLException ex) {
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.DATABASE_DELETE_ERROR, R.ErrMsg.DeleteError);
            }            
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.CollumSelectionError);
        }
    }
    
    public ExitMsg deleteStorage(int SelectedRow){
        if(SelectedRow >= 0){
            try {
                new StorageMapper().delete(ManagerStorageList.get(SelectedRow).getId(), DBManager);
                ManagerStorageList.remove(SelectedRow);
                return new ExitMsg(ExitMsg.SUCCESS, null);
            } catch (SQLException ex) {
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.DATABASE_DELETE_ERROR, R.ErrMsg.DeleteError);
            }            
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.CollumSelectionError);
        }
    }
    
    public ExitMsg deleteClient(int SelectedRow){
        if(SelectedRow >= 0){
            try {
                new ClientMapper().delete(ManagerClientList.get(SelectedRow).getID(), DBManager);
                ManagerClientList.remove(SelectedRow);
                return new ExitMsg(ExitMsg.SUCCESS, null);
            } catch (SQLException ex) {
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.DATABASE_DELETE_ERROR, R.ErrMsg.DeleteError);
            }            
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.CollumSelectionError);
        }
    }
    
    public ExitMsg deleteMaster(int SelectedRow){
        if(SelectedRow >= 0){
            try {
                new ManagerMapper().delete(ManagerMasterList.get(SelectedRow).getID(), DBManager);
                ManagerMasterList.remove(SelectedRow);
                return new ExitMsg(ExitMsg.SUCCESS, null);
            } catch (SQLException ex) {
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.DATABASE_DELETE_ERROR, R.ErrMsg.DeleteError);
            }            
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.CollumSelectionError);
        }
    }
    
    public ExitMsg deleteEstimate(int SelectedRow, int EstimateSelectedRow){
        if(SelectedRow >= 0 && EstimateSelectedRow >= 0){
            try {
                new EstimateMapper().delete(ManagerOrderList.get(SelectedRow).getEstimate(EstimateSelectedRow).getId(), DBManager);
                ManagerOrderList.get(SelectedRow).deleteEstimate(EstimateSelectedRow);
                return new ExitMsg(ExitMsg.SUCCESS, null);
            } catch (SQLException ex) {
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.DATABASE_DELETE_ERROR, R.ErrMsg.DeleteError);
            }
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.CollumSelectionError);
        }
    }
    
    public ExitMsg deleteWorkResource(int SelectedRow, int SelectedRowResource){
        if(SelectedRow >= 0 && SelectedRowResource >= 0){            
            try {                
                new WorkMapper().clearWorkResource(
                        ManagerWorkList.get(SelectedRow).getId(),
                        ManagerWorkList.get(SelectedRow).getResource(SelectedRowResource).getId(),
                        DBManager);
                ManagerWorkList.get(SelectedRow).getResources().remove(SelectedRowResource);
                return new ExitMsg(ExitMsg.SUCCESS, null);
            } catch (SQLException ex) {
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.DATABASE_DELETE_ERROR, R.ErrMsg.DeleteError);
            }
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.CollumSelectionError);
        }
    }
    
    public ExitMsg addEstimate(int SelectedIndex){
        if(SelectedIndex >= 0){
            if(TempEstimateWorkList == null){
                TempEstimateWorkList = new ArrayList<>();
            }
            TempEstimateWorkList.add(ManagerWorkList.get(SelectedIndex));
            return new ExitMsg(ExitMsg.SUCCESS, null);
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.CollumSelectionError);
        }
    }
    
    public ExitMsg deleteTempEstimateWork(int SelectedRow){
        if(SelectedRow >= 0 && TempEstimateWorkList != null){
            TempEstimateWorkList.remove(SelectedRow);
            if(TempEstimateWorkList.isEmpty()){
                TempEstimateWorkList = null;
            }
            return new ExitMsg(ExitMsg.SUCCESS, null);
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.CollumSelectionError);
        }
    }
    
    public ExitMsg clientPayLogic(int SelectedRow,ClienPayListener l){    
        if(SelectedRow >= 0){
            if(ClientOrderList != null){
                if(ClientOrderList.get(SelectedRow).getStatus() == Order.WAITING_PAY){
                    //Диалог с запросом числа.
                    try{
                        double pay = Double.parseDouble(l.askDialog().trim());
                        if(pay < 0){
                            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputDataError_2);
                        }else{                            
                            if(((Client)CurrentRole).PayEstimatePart(ClientOrderList.get(SelectedRow),pay)){
                                l.acceptDialog();
                                return new ExitMsg(ExitMsg.SUCCESS, null);
                            }else{
                                return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputPayError);
                            }
                        } 
                    }catch(NumberFormatException ex){
                        return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.InputDataError_1);
                    }
                }else{return new ExitMsg(ExitMsg.BUISNESS_LOGIC_ERROR, null);}
            }else{return new ExitMsg(ExitMsg.DATABASE_ERROR, null);}
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.CollumSelectionError);
        }
    }
    
    public ExitMsg changeOrderStatus(int SelectedRow){
        if(SelectedRow >= 0){//TODO: подумать по поводу склада 
            switch(ManagerOrderList.get(SelectedRow).getStatus()){
                case Order.OPEN:
                    ManagerOrderList.get(SelectedRow).setStatus(Order.INPROGRESS);
                    ManagerOrderList.get(SelectedRow).setCurrentCoast(ManagerOrderList.get(SelectedRow).CoastCalculation());
                break;
                case Order.WAITING_ACKNOWLEDGMENT_PAY:
                    ManagerOrderList.get(SelectedRow).setStatus(Order.CLOSE);
                break;
            }
            try {
                new OrderMapper().save(ManagerOrderList.get(SelectedRow), DBManager);
                return new ExitMsg(ExitMsg.SUCCESS, null);
            } catch (SQLException ex) {
                Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR, ex.toString());
            }
        }else{
            return new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.CollumSelectionError);
        }
    }
    
    /**
     * @param Name ManagerNameTextField.getText()
     * @param Phone ManagerPhoneTextField.getText()
     * @param Address ManagerAddressTextField.getText()
     * @return Exit code and massage if error.
     */
    public ExitMsg saveManagerProfile(String Name,String Phone,String Address){
        return saveProfie(Name,Phone,Address,R.ModelType.ManagerModel);
    }
    
    /**
     * 
     * @param Name ClientNameTextField.getText()
     * @param Phone ClientPhoneTextField.getText()
     * @param Address ClientAddressTextField.getText()
     * @return Exit code and massage if error.
    */
    public ExitMsg saveClientProfile(String Name,String Phone,String Address){
        return saveProfie(Name,Phone,Address,R.ModelType.ClientModel);
    }
    
    /**
     * @param Name MasterNameTextField.getText()
     * @param Phone MasterPhoneTextField.getText()
     * @return Exit code and massage if error.
    */
    public ExitMsg saveMasterProfile(String Name,String Phone){
        return saveProfie(Name,Phone,null,R.ModelType.MasterModel);
    }
    
    private ExitMsg saveProfie(String Name,String Phone,String Address,int Role){
        if(DBManager != null && CurrentRole != null){
            if(!Name.trim().equals("")){
                if(!Phone.trim().equals("")){
                    boolean flag = 
                            (Role == R.ModelType.ManagerModel||Role == R.ModelType.ClientModel ) ? 
                            (!Address.trim().equals("")) : 
                            true;
                    if(flag){
                        switch(Role){
                            case R.ModelType.ManagerModel:
                                Manager role = (Manager)CurrentRole;
                                role.setName(Name.trim());
                                role.setPhoneNumber(Phone.trim());
                                role.setOfficeAddress(Address.trim());
                                try {
                                    new ManagerMapper().save(role,DBManager);
                                    return new ExitMsg(ExitMsg.SUCCESS, null);
                                } catch (SQLException ex) {
                                    Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                                    return  new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR, ex.toString());
                                }
                            case R.ModelType.ClientModel:
                                Client role2 = (Client)CurrentRole;
                                role2.setName(Name.trim());
                                role2.setPhoneNumber(Phone.trim());
                                role2.setAddres(Address.trim());
                                try {
                                    new ClientMapper().save(role2,DBManager);
                                    return new ExitMsg(ExitMsg.SUCCESS, null);
                                } catch (SQLException ex) {                
                                    Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                                    return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR, ex.toString());
                                }
                            case R.ModelType.MasterModel:
                                Master role3 = (Master)CurrentRole;
                                role3.setName(Name.trim());
                                role3.setPhoneNumber(Phone.trim());
                                try {
                                    new MasterMapper().save(role3,DBManager);
                                    return new ExitMsg(ExitMsg.SUCCESS, null);
                                } catch (SQLException ex) {                
                                    Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, null, ex);
                                    return new ExitMsg(ExitMsg.DATABASE_SAVE_ERROR,ex.toString());
                                }
                            default:
                                return new ExitMsg(ExitMsg.ROLE_ERROR,null);
                        }
                    }else{return  new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.AddressError);}
                }else{return  new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.PhoneError);}
            }else{return  new ExitMsg(ExitMsg.INPUT_ERROR, R.ErrMsg.NaneError);}
        }else{return new ExitMsg(ExitMsg.DATABASE_ERROR, R.ErrMsg.DatabaseError);}
    }
    
    public ExitMsg saveConfig(){
        return config.writetoFile(R.FileName.Config); 
    }
    
    public void saveAskDialogProperties(boolean AskDialog,boolean ExitOperation){
        config.getConfigItem(index).getSettings().setAskDialog(AskDialog);
        config.getConfigItem(index).getSettings().setExitOperation(ExitOperation);
    }
            
    public ConfigRole getCurrentConfigRole(){
        return config.getConfigItem(index).getRole();
    }
    
    public ConfigDatabase getCurrentConfigDatabase(){
        return config.getConfigItem(index).getDatabase();
    }
    
    public Config getConfig() {
        return config;
    }

    public int getIndex() {
        return index;
    }

    public boolean isCONNECTED() {
        return CONNECTED;
    }
    
    public boolean isConfigValid(){
        return config.getConfigItem(index).isValid();
    }
    
    public boolean isAskDialog(){
        return config.getConfigItem(index).getSettings().isAskDialog();
    }
    
    public boolean isExitOperation(){
        return config.getConfigItem(index).getSettings().isExitOperation();
    }
    
    public int getCurrentId() {
        return CurrentId;
    }

    public Role getCurrentRole() {
        return CurrentRole;
    }
    
    public ArrayList<Order> getClientOrderList() {
        return ClientOrderList;
    }
    
    public Order getClientOrderAt(int i) {
        return ClientOrderList.get(i);
    }
    
    public ArrayList<Client> getManagerClientList() {
        return ManagerClientList;
    }
    
    public Client getManagerClientAt(int i) {
        return ManagerClientList.get(i);
    }
    
    public ArrayList<Master> getManagerMasterList() {
        return ManagerMasterList;
    }
    
    public Master getManagerMasterAt(int i) {
        return ManagerMasterList.get(i);
    }
    
    public ArrayList<Order> getManagerOrderList() {
        return ManagerOrderList;
    }

    public Order getManagerOrderAt(int i) {
        return ManagerOrderList.get(i);
    }
    
    public ArrayList<Resource> getManagerResourceList() {
        return ManagerResourceList;
    }

    public Resource getManagerResourceAt(int i) {
        return ManagerResourceList.get(i);
    }
    
    public ArrayList<Storage> getManagerStorageList() {
        return ManagerStorageList;
    }

    public Storage getManagerStorageAt(int i) {
        return ManagerStorageList.get(i);
    }
    
    public ArrayList<Work> getManagerWorkList() {
        return ManagerWorkList;
    }

    public Work getManagerWorkAt(int i) {
        return ManagerWorkList.get(i);
    }
    
    public ArrayList<Estimate> getMasterEstimateList() {
        return MasterEstimateList;
    }

    public Estimate getMasterEstimateAt(int i) {
        return MasterEstimateList.get(i);
    }
    
    public ArrayList<Work> getTempEstimateWorkList() {
        return TempEstimateWorkList;
    }         
    
    public Work getTempEstimateWorkAt(int i) {
        return TempEstimateWorkList.get(i);
    }

    public SimpleDateFormat getDateFormat() {
        return DateFormat;
    }
    
    public interface ClienPayListener{
        String askDialog();
        void acceptDialog();
    }
    
    public interface CallBackSaveOrderDialog{
        void addRow(Order ord);
    }
    
    public interface CallBackSaveEstimateDialog{
        void addAction(Estimate es);
        void editAction(Estimate es);
    }
    
    public interface CallBackSaveResourceDialog{
        void addAction(Resource res);
        void editAction(Resource res);
    }
    
    public interface CallBackSaveWorkDialog{
        void addAction(Work wr);
        void editAction(Work wr);
    }
    
    public interface CallBackSaveStorageDialog{
        void addAction(Storage stor);
        void editAction(Storage stor);
    }
    
    public interface CallBackSaveTakeSendResourceDialog{
        void addAction(Resource WorkResource);
        void addAction(Resource WorkResource, int WorkResPosition);
        void WorkFinishAction();
        void sendAction(Resource StorageResource);
        void sendAction(Resource StorageResource, int StorResPosition);
        void takeAction();
        void takeAction2();
    }
    
    public interface CallBackSaveClientMaster{
        void addMaster(Master mr);
        void editMaster(Master mr);
        void addClient(Client cl);
        void editClient(Client cl);
    }
}
