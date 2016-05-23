/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package constructioncompany;
import Resources.R;
import businesslogic.Client;
import businesslogic.Estimate;
import businesslogic.Manager;
import businesslogic.Master;
import businesslogic.Order;
import businesslogic.Resource;
import businesslogic.Storage;
import businesslogic.Work;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import service.Authorization;
import service.BuisnessService;
import service.ExitMsg;
import service.config.ConfigDatabase;
import service.config.ConfigRole;
/**
 *
 * @author Nik
 */
public class MainFrame extends javax.swing.JFrame {
    
    boolean Master = false;         //Выбор для обобщенного диалога добавления/редактирования для какой роли  Master - true , Client - false.
    int DialogAction = 0;           //Действие диалогового окна добавить/редактировать для Склада отправить/получиить 
    boolean UPDATED = false;        //Флаг обновлениея данных
    BuisnessService service;        //Слой служб управляющий бизнеслогикой и слоем хранения
    
    /**
     * Инициализация диалогового окна
     */
    public MainFrame() {
        //TODO прописать въюшку админа, окно для паролей
        //TODO добавить позицию распределения ресурсов
        //Выполненно прописать диалоги для добавления элементов: заказов, смет, работ, складов, ресурсов 
        //Выполненно 1.порписать кнопочки и прочую часть графики
        //Выполненно 2.прописать аутентификацию и подключение к базе
        //Выполненно 3.Прописать проверку конфига.
        //Выполненно 5.прописать конфиг и добавить новые поля
        //Выполненно 6.прописать бизнес логику.
        //Выполненно 7.написать тесты.        
        //Выполненно 8.вставить диалог для запоминания перехода.
        /*DateFormat = new SimpleDateFormat (R.DataFormat);
        ManagerOrderList = null;
        ManagerWorkList = null;        
        ManagerResourceList = null;
        ManagerStorageList = null;
        ManagerClientList = null;
        ManagerMasterList = null;
        ClientOrderList = null;
        MasterEstimateList = null;
        TempEstimateWorkList = null;*/
        service = new BuisnessService();
        initComponents();
        initLocation();
    }
    
    private void initLocation(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        java.awt.Window Windows[] = {
            this,
            ClientView,MasterView,ManagerView,
            AskDialog,ViewOrderDialog,TakeSendResourceDialog,
            AddEstimateWorkDialog,AddEditClientMasterDialog,AddEditEstimateDialog,
            AddEditResourceDialog,AddEditStorageDialog,AddEditWorkDialog,AddOrderDialog
        };
        for(java.awt.Window elem:Windows){
            elem.setLocation(dim.width/2-elem.getSize().width/2, dim.height/2-elem.getSize().height/2);
        }
    }
     
    private void initRoleViewBlock(){
        ConfigRole RoleInfo = service.getCurrentConfigRole();
        ConfigDatabase ConfigInfo = service.getCurrentConfigDatabase();
        switch(RoleComboBox.getSelectedIndex()+1){
            case R.ModelType.ManagerModel://Менеджер
                ManagerView.setTitle(
                        R.RoleType.Manager + " : " + RoleInfo.getLogin() + " - " +
                        "Connected to: "+ ConfigInfo.getHost() + " ["+ConfigInfo.getPath()+"]");
                ManagerView.setVisible(true);
                //Вкладка профиль
                ManagerNameTextField.setText(service.getCurrentRole().getName());
                ManagerPhoneTextField.setText(service.getCurrentRole().getPhoneNumber());
                ManagerAddressTextField.setText(((Manager)service.getCurrentRole()).getOfficeAddress());                               
                initManagerViewModel();
                break;
            case R.ModelType.MasterModel://Прораб
                MasterView.setTitle(
                        R.RoleType.Master + " : " + RoleInfo.getLogin() + " - " +
                        "Connected to: "+ ConfigInfo.getHost() + " ["+ConfigInfo.getPath()+"]");
                MasterView.setVisible(true);
                //Вкладка профиль
                MasterNameTextField.setText(service.getCurrentRole().getName());
                MasterPhoneTextField.setText(service.getCurrentRole().getPhoneNumber());
                initMasterViewModel();
                break;
            case R.ModelType.ClientModel://Клиент
                ClientView.setTitle(
                        R.RoleType.Client + " : " + RoleInfo.getLogin() + " - " +
                        "Connected to: "+ ConfigInfo.getHost() + " ["+ConfigInfo.getPath()+"]");
                ClientView.setVisible(true);
                //Вкладка профиль
                ClientNameTextField.setText(service.getCurrentRole().getName());
                ClientPhoneTextField.setText(service.getCurrentRole().getPhoneNumber());
                ClientAddressTextField.setText(((Client)service.getCurrentRole()).getAddres());
                ClientTypeLabel.setText( R.Client.CLIENT_TYPE + " " + 
                        R.Client.ClientTypeName(((Client)service.getCurrentRole()).getType()));
                initClientViewModel();
                break;
        }
    }
    
    private void initManagerViewModel(){
        ExitMsg ex;
        //Вкладка заказов.
        ex = service.loadOrderListbyManager();
        if(ex.getCode() == ExitMsg.SUCCESS){
            javax.swing.table.DefaultTableModel ManagerOrderTableModel = 
                    (javax.swing.table.DefaultTableModel)ManagerOrderTable.getModel();
            while(ManagerOrderTableModel.getRowCount() > 0){ManagerOrderTableModel.removeRow(0);}
            service.getManagerOrderList().stream().forEach((list1) -> {
                ManagerOrderTableModel.addRow(new Object[]{
                    String.valueOf(list1.getNumber()),
                    R.Order.StatusName(list1.getStatus()), 
                    String.valueOf(list1.getCurrentCoast()), 
                    service.getDateFormat().format(list1.getCreate()), 
                    service.getDateFormat().format(list1.getLastUpdate()),
                    (list1.getEnd()!= null) ? service.getDateFormat().format(list1.getEnd()):""});
                list1.CoastCalculation();
            });                                    
            ManagerOrderTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
                int SelectedRow = ManagerOrderTable.getSelectedRow();
                if(SelectedRow >= 0){
                    switch(service.getManagerOrderAt(SelectedRow).getStatus()){
                        case Order.OPEN:
                            ChangeStatusOrderButton.setText("Изменить статус на: " + R.Order.StatusName(Order.INPROGRESS));
                            ChangeStatusOrderButton.setEnabled(true);
                        break;
                        case Order.WAITING_ACKNOWLEDGMENT_PAY:
                            ChangeStatusOrderButton.setText("Изменить статус на: " + R.Order.StatusName(Order.CLOSE));
                            ChangeStatusOrderButton.setEnabled(true);
                        break;
                        default:
                            ChangeStatusOrderButton.setText("Изменить статус");
                            ChangeStatusOrderButton.setEnabled(false);
                            break;
                    }
                    
                    javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel)ManagerEstimateTable.getModel();
                    while(m.getRowCount() > 0){m.removeRow(0);}
                    service.getManagerOrderAt(SelectedRow).getEstimateList().stream().forEach((Elist1) -> {
                        m.addRow(new Object[]{
                                    R.Estimate.TypeName(Elist1.getType()), 
                                    R.Estimate.StatusName(Elist1.isPaid(), Elist1.isFinish()),
                                    String.valueOf(Elist1.getCoast())
                                }
                        );
                    });
                }
            });
        }else{ErrorMassage(ex.getMassage());}
        //Вкладка работ.
        ex = service.loadManagerWorkList();
        if(ex.getCode() == ExitMsg.SUCCESS){
            javax.swing.table.DefaultTableModel ManagerWorkTableModel = 
                    (javax.swing.table.DefaultTableModel) ManagerWorkTable.getModel();
            while(ManagerWorkTableModel.getRowCount() > 0){ManagerWorkTableModel.removeRow(0);}
            service.getManagerWorkList().stream().forEach((Work1) -> {
                ManagerWorkTableModel.addRow(new Object[]{
                    Work1.getDescription(),
                    Work1.getServiceCoast(),
                    Work1.CoastCalculation()
                });
            });
            ManagerWorkTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
                int SelectedRow = ManagerWorkTable.getSelectedRow();
                if(SelectedRow >= 0){
                    javax.swing.table.DefaultTableModel m = 
                    (javax.swing.table.DefaultTableModel)ManagerWorkResourceTable.getModel();
                    while(m.getRowCount() > 0){m.removeRow(0);}
                    service.getManagerWorkAt(SelectedRow).getResources().stream().forEach((Res1) -> {                                                
                        m.addRow(new Object[]{
                                    Res1.getName(),
                                    String.valueOf(Res1.getAmount()),
                                    String.valueOf(Res1.getCoast()),
                                    String.valueOf(Res1.getType())
                                }
                        );
                    });
                }
            });
        }else{ErrorMassage(ex.getMassage());}
        //Вкладка Ресурсы.                             
        ex = service.loadManagerResourceList();
        if(ex.getCode() == ExitMsg.SUCCESS){
            javax.swing.table.DefaultTableModel ManagerStorageTableModel = 
                    (javax.swing.table.DefaultTableModel)ManagerResourceTable.getModel();
            while(ManagerStorageTableModel.getRowCount() > 0 ){ManagerStorageTableModel.removeRow(0);}
            service.getManagerResourceList().stream().forEach((Res1) -> {
                ManagerStorageTableModel.addRow(new Object[]{
                    Res1.getName(),
                    String.valueOf(Res1.getCoast()),
                    String.valueOf(Res1.getType())
                });
            });
        }else{ErrorMassage(ex.getMassage());}
        //Вкладка Склад.
        ex = service.loadManagerStorageList();
        if(ex.getCode() == ExitMsg.SUCCESS){
            javax.swing.table.DefaultTableModel ManagerStorageTableModel = 
                    (javax.swing.table.DefaultTableModel)ManagerStorageTable.getModel();
            while(ManagerStorageTableModel.getRowCount() > 0){ManagerStorageTableModel.removeRow(0);}
            service.getManagerStorageList().stream().forEach((Storege1) -> {
                ManagerStorageTableModel.addRow(new Object[]{Storege1.getLocation()});
            });
            ManagerStorageTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
                int SelectedRow = ManagerStorageTable.getSelectedRow();
                if(SelectedRow >= 0){
                    javax.swing.table.DefaultTableModel m = 
                    (javax.swing.table.DefaultTableModel)ManagerStorageResourceTable.getModel();
                    while(m.getRowCount() > 0){m.removeRow(0);}
                    service.getManagerStorageAt(SelectedRow).getResources().stream().forEach((Res1) -> {                                                
                        m.addRow(new Object[]{
                                    Res1.getName(),
                                    String.valueOf(Res1.getAmount()),
                                    String.valueOf(Res1.getCoast()),
                                    String.valueOf(Res1.getType())
                                }
                        );
                    });
                }
            });
        }else{ErrorMassage(ex.getMassage());}
        //Вкладка заказчики
        ex = service.loadManagerClientList();
        if(ex.getCode() == ExitMsg.SUCCESS){
            javax.swing.table.DefaultTableModel ManagerClientTableModel = 
                    (javax.swing.table.DefaultTableModel)ManagerClientTable.getModel();
            while(ManagerClientTableModel.getRowCount() > 0){ManagerClientTableModel.removeRow(0);}
            service.getManagerClientList().stream().forEach((Client1) -> {
                ManagerClientTableModel.addRow(new Object[]{
                    Client1.getName(),
                    Client1.getPhoneNumber(),
                    Client1.getAddres(),
                    R.Client.ClientTypeName(Client1.getType())
                });
            });
        }else{ErrorMassage(ex.getMassage());}
        //Вкладка прорабы
        ex = service.loadManagerMasterList();
        if(ex.getCode() == ExitMsg.SUCCESS){
            javax.swing.table.DefaultTableModel ManagerMasterTableModel = 
                    (javax.swing.table.DefaultTableModel)ManagerMasterTable.getModel();
            while(ManagerMasterTableModel.getRowCount() > 0){ManagerMasterTableModel.removeRow(0);}
            service.getManagerMasterList().stream().forEach((Master1) -> {
                ManagerMasterTableModel.addRow(new Object[]{
                    Master1.getName(),
                    Master1.getPhoneNumber()
                });
            });
        }else{ErrorMassage(ex.getMassage());}
    }
    
    private void initMasterViewModel(){
        ExitMsg ex = service.loadMasterEstimateList();
        if(ex.getCode() == ExitMsg.SUCCESS){
            javax.swing.table.DefaultTableModel MasterWorksTableModel = 
                                            (javax.swing.table.DefaultTableModel)MasterWorksTable.getModel();
            while(MasterWorksTableModel.getRowCount() > 0){MasterWorksTableModel.removeRow(0);}
            for (Estimate MasterEstimateList1 : service.getMasterEstimateList()) {
                Client cl = service.loadClientByOrderId(MasterEstimateList1.getOrderId()); 
                if(cl != null){
                    MasterEstimateList1.getWorkList().stream().forEach((work1) -> {
                        MasterWorksTableModel.addRow(new Object[]{
                            work1.getDescription(),
                            R.Work.StatusName(work1.isFinish()),
                            String.valueOf(work1.getServiceCoast()),
                            String.valueOf(work1.CoastCalculation()),
                            cl.getName(),
                            cl.getPhoneNumber(),
                            cl.getAddres()}
                        );  });
                }else{
                    ex = new ExitMsg(ExitMsg.DATABASE_LOAD_ERROR, null);
                    break;
                }
            }
            if(ex.getCode() == ExitMsg.SUCCESS){
                MasterWorksTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
                    if(MasterWorksTable.getSelectedRow() >= 0){
                        int WorkIndex = MasterWorksTable.getSelectedRow();
                        int EstimateIndex = 0;
                        for (Estimate es : service.getMasterEstimateList()) {
                            int size = es.getWorkList().size();
                            if(WorkIndex >= size){
                                EstimateIndex++;
                                WorkIndex = WorkIndex - size;
                            }else{
                                break;
                            }
                        }
                        MasterFinishWorkButton.setEnabled(
                                !service.getMasterEstimateList()
                                        .get(EstimateIndex)
                                        .getWork(WorkIndex)
                                        .isFinish()
                        );
                    }
                });
            }else{ErrorMassage(R.ErrMsg.DatabaseLoadError);}
        }else{ErrorMassage(ex.getMassage());}
    }
    
    private void initClientViewModel(){
        ExitMsg ex = service.loadClientOrderList();
        if(ex.getCode() == ExitMsg.SUCCESS){
            javax.swing.table.DefaultTableModel ClientOrderTableModel = 
                    (javax.swing.table.DefaultTableModel)ClientOrderTable.getModel();
            while(ClientOrderTableModel.getRowCount() > 0){ClientOrderTableModel.removeRow(0);}
            service.getClientOrderList().stream().forEach((list1) -> {
                ClientOrderTableModel.addRow(new Object[]{
                    String.valueOf(list1.getNumber()),
                    R.Order.StatusName(list1.getStatus()), 
                    String.valueOf(list1.getCurrentCoast()), 
                    service.getDateFormat().format(list1.getCreate()), 
                    service.getDateFormat().format(list1.getLastUpdate()),
                    (list1.getEnd()!= null) ? service.getDateFormat().format(list1.getEnd()):""});
                list1.CoastCalculation();
            });                                    
            ClientOrderTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
                if(ClientOrderTable.getSelectedRow() >= 0){
                    switch(service.getClientOrderAt(ClientOrderTable.getSelectedRow()).getStatus()){
                        case Order.WAITING_PAY:
                            ClientAcceptWorkButton.setEnabled(false);
                            ClientPayButton.setEnabled(true);
                            break;
                        case Order.WAITING_ACKNOWLEDGMENT_TAKE:
                            ClientAcceptWorkButton.setEnabled(true);
                            ClientPayButton.setEnabled(false);
                            break;
                        default:
                            ClientAcceptWorkButton.setEnabled(false);
                            ClientPayButton.setEnabled(false);
                            break;
                    }
                }
            });
        }else{ErrorMassage(ex.getMassage());}
    }
    
    private void ErrorMassage(String message){
        JOptionPane.showConfirmDialog(null, message, R.ErrorDialogTitle, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("Convert2Lambda")//генерируемый код так что можно и отключить предупреждение
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ManagerView = new javax.swing.JFrame();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        ManagerOrderPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        ManagerOrderTable = new javax.swing.JTable();
        jScrollPane9 = new javax.swing.JScrollPane();
        ManagerEstimateTable = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        ChangeStatusOrderButton = new javax.swing.JButton();
        AddOrderButton = new javax.swing.JButton();
        DeleteOrderButton = new javax.swing.JButton();
        AddEstimateButton = new javax.swing.JButton();
        EditEstimateButton = new javax.swing.JButton();
        DeleteEstimateButton = new javax.swing.JButton();
        ManagerWorksPanel = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        ManagerWorkTable = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        ManagerWorkResourceTable = new javax.swing.JTable();
        EditWorkButton = new javax.swing.JButton();
        AddWorkButton = new javax.swing.JButton();
        DeleteWorkButton = new javax.swing.JButton();
        AddWorkResourceButton = new javax.swing.JButton();
        DeleteWorkResourceButton = new javax.swing.JButton();
        EditWorkResourceButton = new javax.swing.JButton();
        ManagerResourcePanel = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        ManagerResourceTable = new javax.swing.JTable();
        AddResourceButton = new javax.swing.JButton();
        EditResourceButton = new javax.swing.JButton();
        DeleteResourceButton = new javax.swing.JButton();
        ManagerStoragePanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        ManagerStorageTable = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        ManagerStorageResourceTable = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        TakeResourceFromStorageButton = new javax.swing.JButton();
        SendResourceToStorageButton = new javax.swing.JButton();
        EditStorageButton = new javax.swing.JButton();
        AddStorageButton = new javax.swing.JButton();
        DeleteStorageButton = new javax.swing.JButton();
        ManagerClientsPanel = new javax.swing.JPanel();
        jScrollPane13 = new javax.swing.JScrollPane();
        ManagerClientTable = new javax.swing.JTable();
        AddClientButton = new javax.swing.JButton();
        EditClientButton = new javax.swing.JButton();
        DeleteClientButton = new javax.swing.JButton();
        ManagerMastersPanel = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        ManagerMasterTable = new javax.swing.JTable();
        AddMasterButton = new javax.swing.JButton();
        EditMasterButton = new javax.swing.JButton();
        DeleteMasterButton = new javax.swing.JButton();
        ManagerProfilePanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        ManagerNameTextField = new javax.swing.JTextField();
        ManagerPhoneTextField = new javax.swing.JTextField();
        ManagerAddressTextField = new javax.swing.JTextField();
        SaveManagerProfileButton = new javax.swing.JButton();
        ManagerExitButton = new javax.swing.JButton();
        ManagerUpdateButton = new javax.swing.JButton();
        ClientView = new javax.swing.JFrame();
        ClientExitButton = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        ClientOrderPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ClientOrderTable = new javax.swing.JTable();
        ClientPayButton = new javax.swing.JButton();
        ClientAcceptWorkButton = new javax.swing.JButton();
        ClientOrderDetailButton = new javax.swing.JButton();
        ClientProfilePanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ClientNameTextField = new javax.swing.JTextField();
        ClientPhoneTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        ClientAddressTextField = new javax.swing.JTextField();
        ClientTypeLabel = new javax.swing.JLabel();
        SaveClientProfileButton = new javax.swing.JButton();
        MasterView = new javax.swing.JFrame();
        MasterExitButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        MasterWorksPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        MasterWorksTable = new javax.swing.JTable();
        MasterFinishWorkButton = new javax.swing.JButton();
        SaveWorkMasterButton = new javax.swing.JButton();
        MasterProfilePanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        MasterNameTextField = new javax.swing.JTextField();
        MasterPhoneTextField = new javax.swing.JTextField();
        SaveMasterProfileButton = new javax.swing.JButton();
        WorkMasterSumLabel = new javax.swing.JLabel();
        AskDialog = new javax.swing.JDialog();
        jLabel4 = new javax.swing.JLabel();
        NotExitButton = new javax.swing.JButton();
        ExitButton = new javax.swing.JButton();
        AskCheckBox = new javax.swing.JCheckBox();
        ViewOrderDialog = new javax.swing.JDialog();
        jScrollPane10 = new javax.swing.JScrollPane();
        OrderResourceTable = new javax.swing.JTable();
        jScrollPane11 = new javax.swing.JScrollPane();
        OrderEstimateTable = new javax.swing.JTable();
        OrderNumberLabel = new javax.swing.JLabel();
        OrderClientNameLabel = new javax.swing.JLabel();
        OrderManagerNameLabel = new javax.swing.JLabel();
        OrderTotalCoastLabel = new javax.swing.JLabel();
        OrderCurrentCoastLabel = new javax.swing.JLabel();
        OrderCreateDateLabel = new javax.swing.JLabel();
        OrderLastUpdateDateLabel = new javax.swing.JLabel();
        OrderEndDateLabel = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        OrderWorkTable = new javax.swing.JTable();
        jLabel29 = new javax.swing.JLabel();
        OkViewButton = new javax.swing.JButton();
        OrderStatusLabel = new javax.swing.JLabel();
        TakeSendResourceDialog = new javax.swing.JDialog();
        OkTakeSendResourceDialogButton = new javax.swing.JButton();
        CanselTakeSendResourceDialogButton = new javax.swing.JButton();
        TypeNameComboBox = new javax.swing.JComboBox();
        NameRadio = new javax.swing.JRadioButton();
        TypeRadio = new javax.swing.JRadioButton();
        AmountTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        TypeNameRadioGroup = new javax.swing.ButtonGroup();
        AddEditStorageDialog = new javax.swing.JDialog();
        OkStorageDialogButton = new javax.swing.JButton();
        CanselStorageDialogButton = new javax.swing.JButton();
        StorageLocationTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        AddEditWorkDialog = new javax.swing.JDialog();
        OkWorkDialogButton = new javax.swing.JButton();
        CanselWorkDialogButton = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        DescriptionTextField = new javax.swing.JTextField();
        WorkCoastTextField = new javax.swing.JTextField();
        AddEditResourceDialog = new javax.swing.JDialog();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        OkResourceDialogButton = new javax.swing.JButton();
        CanselResourceDialogButton = new javax.swing.JButton();
        NameTextField = new javax.swing.JTextField();
        ResourceCoastTextField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        TypeTextField = new javax.swing.JTextField();
        AddEditClientMasterDialog = new javax.swing.JDialog();
        OkClientMasterDialogButton = new javax.swing.JButton();
        CanselClientMasterDialogButton = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        NameClientMasterTextField = new javax.swing.JTextField();
        PhoneTextField = new javax.swing.JTextField();
        AddressTextField = new javax.swing.JTextField();
        TypeComboBox = new javax.swing.JComboBox();
        AddEditEstimateDialog = new javax.swing.JDialog();
        jLabel31 = new javax.swing.JLabel();
        EstimateTypeComboBox = new javax.swing.JComboBox();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        EstimateWorksDialogTable = new javax.swing.JTable();
        AddEstimateDialogButton = new javax.swing.JButton();
        DeleteEstimateDialogButton = new javax.swing.JButton();
        CanselEstimateDialogButton = new javax.swing.JButton();
        OkEstimateDialogButton = new javax.swing.JButton();
        AddEstimateWorkDialog = new javax.swing.JDialog();
        jLabel33 = new javax.swing.JLabel();
        WorkComboBox = new javax.swing.JComboBox();
        OkAddEstimateWorkDialogButton = new javax.swing.JButton();
        CanselAddDeleteWorkDialogButton = new javax.swing.JButton();
        AddOrderDialog = new javax.swing.JDialog();
        OkOrderDialogButton = new javax.swing.JButton();
        CanselOrderDialogButton = new javax.swing.JButton();
        DateCreateTextField = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        ManagerClientComboBox = new javax.swing.JComboBox();
        OkButton = new javax.swing.JButton();
        CanselButton = new javax.swing.JButton();
        PasswordFieldText = new javax.swing.JPasswordField();
        RoleComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        LoginTextField = new javax.swing.JTextField();

        ManagerView.setMinimumSize(new java.awt.Dimension(700, 500));
        ManagerView.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                ManagerViewWindowClosing(evt);
            }
        });

        jTabbedPane3.setPreferredSize(new java.awt.Dimension(700, 500));

        ManagerOrderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Номер", "Статус", "Стоимость", "Дата создания", "Дата изменения", "Дата закрытия"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ManagerOrderTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(ManagerOrderTable);

        ManagerEstimateTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Тип", "Статус", "Стоимость"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ManagerEstimateTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane9.setViewportView(ManagerEstimateTable);

        jLabel17.setText("Заказы:");

        jLabel18.setText("Сметы:");

        ChangeStatusOrderButton.setText("Изменить статус");
        ChangeStatusOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeStatusOrderButtonActionPerformed(evt);
            }
        });

        AddOrderButton.setText("Добавить");
        AddOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddOrderButtonActionPerformed(evt);
            }
        });

        DeleteOrderButton.setText("Удалить");
        DeleteOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteOrderButtonActionPerformed(evt);
            }
        });

        AddEstimateButton.setText("Добавить");
        AddEstimateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddEstimateButtonActionPerformed(evt);
            }
        });

        EditEstimateButton.setText("Редактировать");
        EditEstimateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditEstimateButtonActionPerformed(evt);
            }
        });

        DeleteEstimateButton.setText("Удалить");
        DeleteEstimateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteEstimateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ManagerOrderPanelLayout = new javax.swing.GroupLayout(ManagerOrderPanel);
        ManagerOrderPanel.setLayout(ManagerOrderPanelLayout);
        ManagerOrderPanelLayout.setHorizontalGroup(
            ManagerOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerOrderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ManagerOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
                    .addComponent(jScrollPane9)
                    .addGroup(ManagerOrderPanelLayout.createSequentialGroup()
                        .addGroup(ManagerOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18)
                            .addGroup(ManagerOrderPanelLayout.createSequentialGroup()
                                .addComponent(AddOrderButton)
                                .addGap(18, 18, 18)
                                .addComponent(ChangeStatusOrderButton)
                                .addGap(18, 18, 18)
                                .addComponent(DeleteOrderButton))
                            .addGroup(ManagerOrderPanelLayout.createSequentialGroup()
                                .addComponent(AddEstimateButton)
                                .addGap(18, 18, 18)
                                .addComponent(EditEstimateButton)
                                .addGap(18, 18, 18)
                                .addComponent(DeleteEstimateButton)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        ManagerOrderPanelLayout.setVerticalGroup(
            ManagerOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerOrderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(ManagerOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddOrderButton)
                    .addComponent(ChangeStatusOrderButton)
                    .addComponent(DeleteOrderButton))
                .addGap(18, 18, 18)
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(ManagerOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddEstimateButton)
                    .addComponent(EditEstimateButton)
                    .addComponent(DeleteEstimateButton))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Заказы", ManagerOrderPanel);

        ManagerWorkTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Описание", "Стоимость работаты", "Полная стоимость"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ManagerWorkTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane7.setViewportView(ManagerWorkTable);

        ManagerWorkResourceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                " Название", "Количство", "Цена за шт./ед.изм.", "Тип"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ManagerWorkResourceTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane8.setViewportView(ManagerWorkResourceTable);

        EditWorkButton.setText("Редактировать");
        EditWorkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditWorkButtonActionPerformed(evt);
            }
        });

        AddWorkButton.setText("Добавить");
        AddWorkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddWorkButtonActionPerformed(evt);
            }
        });

        DeleteWorkButton.setText("Удалить");
        DeleteWorkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteWorkButtonActionPerformed(evt);
            }
        });

        AddWorkResourceButton.setText("Добавить ресурс");
        AddWorkResourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddWorkResourceButtonActionPerformed(evt);
            }
        });

        DeleteWorkResourceButton.setText("Удалить ресурс");
        DeleteWorkResourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteWorkResourceButtonActionPerformed(evt);
            }
        });

        EditWorkResourceButton.setText("Редактировать ресурс");
        EditWorkResourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditWorkResourceButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ManagerWorksPanelLayout = new javax.swing.GroupLayout(ManagerWorksPanel);
        ManagerWorksPanel.setLayout(ManagerWorksPanelLayout);
        ManagerWorksPanelLayout.setHorizontalGroup(
            ManagerWorksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerWorksPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(ManagerWorksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(DeleteWorkButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(EditWorkButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AddWorkButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AddWorkResourceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(DeleteWorkResourceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(EditWorkResourceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        ManagerWorksPanelLayout.setVerticalGroup(
            ManagerWorksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerWorksPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ManagerWorksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                    .addGroup(ManagerWorksPanelLayout.createSequentialGroup()
                        .addComponent(AddWorkButton)
                        .addGap(18, 18, 18)
                        .addComponent(EditWorkButton)
                        .addGap(18, 18, 18)
                        .addComponent(DeleteWorkButton)
                        .addGap(18, 18, 18)
                        .addComponent(AddWorkResourceButton)
                        .addGap(18, 18, 18)
                        .addComponent(EditWorkResourceButton)
                        .addGap(18, 18, 18)
                        .addComponent(DeleteWorkResourceButton))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Работы", ManagerWorksPanel);

        ManagerResourceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Название", "Цена за шт./ед.изм.", "Тип"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ManagerResourceTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane6.setViewportView(ManagerResourceTable);

        AddResourceButton.setText("Добавить");
        AddResourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddResourceButtonActionPerformed(evt);
            }
        });

        EditResourceButton.setText("Редактировать");
        EditResourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditResourceButtonActionPerformed(evt);
            }
        });

        DeleteResourceButton.setText("Удалить");
        DeleteResourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteResourceButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ManagerResourcePanelLayout = new javax.swing.GroupLayout(ManagerResourcePanel);
        ManagerResourcePanel.setLayout(ManagerResourcePanelLayout);
        ManagerResourcePanelLayout.setHorizontalGroup(
            ManagerResourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerResourcePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(ManagerResourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(AddResourceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(EditResourceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(DeleteResourceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        ManagerResourcePanelLayout.setVerticalGroup(
            ManagerResourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerResourcePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ManagerResourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ManagerResourcePanelLayout.createSequentialGroup()
                        .addComponent(AddResourceButton)
                        .addGap(18, 18, 18)
                        .addComponent(EditResourceButton)
                        .addGap(18, 18, 18)
                        .addComponent(DeleteResourceButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Ресурсы", ManagerResourcePanel);

        ManagerStorageTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Адрес"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ManagerStorageTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(ManagerStorageTable);

        ManagerStorageResourceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Название", "Количество", "Цена за шт./ед.изм.", "Тип"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ManagerStorageResourceTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(ManagerStorageResourceTable);

        jLabel12.setText("Склды:");

        jLabel13.setText("Ресурсы:");

        TakeResourceFromStorageButton.setText("Взять ресурсы");
        TakeResourceFromStorageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TakeResourceFromStorageButtonActionPerformed(evt);
            }
        });

        SendResourceToStorageButton.setText("Добавить ресурсы");
        SendResourceToStorageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendResourceToStorageButtonActionPerformed(evt);
            }
        });

        EditStorageButton.setText("Редактировать");
        EditStorageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditStorageButtonActionPerformed(evt);
            }
        });

        AddStorageButton.setText("Добавить склад");
        AddStorageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddStorageButtonActionPerformed(evt);
            }
        });

        DeleteStorageButton.setText("Удалить склад");
        DeleteStorageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteStorageButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ManagerStoragePanelLayout = new javax.swing.GroupLayout(ManagerStoragePanel);
        ManagerStoragePanel.setLayout(ManagerStoragePanelLayout);
        ManagerStoragePanelLayout.setHorizontalGroup(
            ManagerStoragePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerStoragePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ManagerStoragePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(ManagerStoragePanelLayout.createSequentialGroup()
                        .addGroup(ManagerStoragePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                            .addComponent(jLabel12))
                        .addGap(18, 18, 18)
                        .addGroup(ManagerStoragePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ManagerStoragePanelLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)))
                    .addGroup(ManagerStoragePanelLayout.createSequentialGroup()
                        .addComponent(AddStorageButton)
                        .addGap(18, 18, 18)
                        .addComponent(EditStorageButton)
                        .addGap(18, 18, 18)
                        .addComponent(DeleteStorageButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SendResourceToStorageButton)
                        .addGap(18, 18, 18)
                        .addComponent(TakeResourceFromStorageButton)))
                .addContainerGap())
        );
        ManagerStoragePanelLayout.setVerticalGroup(
            ManagerStoragePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerStoragePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ManagerStoragePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addGroup(ManagerStoragePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ManagerStoragePanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE))
                    .addGroup(ManagerStoragePanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(ManagerStoragePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TakeResourceFromStorageButton)
                    .addComponent(SendResourceToStorageButton)
                    .addComponent(EditStorageButton)
                    .addComponent(AddStorageButton)
                    .addComponent(DeleteStorageButton))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Склад", ManagerStoragePanel);

        ManagerClientTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ф.И.О.", "Телефон", "Адрес", "Тип клиента"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ManagerClientTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane13.setViewportView(ManagerClientTable);

        AddClientButton.setText("Добавить");
        AddClientButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddClientButtonActionPerformed(evt);
            }
        });

        EditClientButton.setText("Редактировать");
        EditClientButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditClientButtonActionPerformed(evt);
            }
        });

        DeleteClientButton.setText("Удалить");
        DeleteClientButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteClientButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ManagerClientsPanelLayout = new javax.swing.GroupLayout(ManagerClientsPanel);
        ManagerClientsPanel.setLayout(ManagerClientsPanelLayout);
        ManagerClientsPanelLayout.setHorizontalGroup(
            ManagerClientsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerClientsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(ManagerClientsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(EditClientButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AddClientButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(DeleteClientButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        ManagerClientsPanelLayout.setVerticalGroup(
            ManagerClientsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerClientsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ManagerClientsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ManagerClientsPanelLayout.createSequentialGroup()
                        .addComponent(AddClientButton)
                        .addGap(18, 18, 18)
                        .addComponent(EditClientButton)
                        .addGap(18, 18, 18)
                        .addComponent(DeleteClientButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Заказчики", ManagerClientsPanel);

        ManagerMasterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ф.И.О.", "Телефон"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ManagerMasterTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane14.setViewportView(ManagerMasterTable);

        AddMasterButton.setText("Добавить");
        AddMasterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddMasterButtonActionPerformed(evt);
            }
        });

        EditMasterButton.setText("Редактировать");
        EditMasterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditMasterButtonActionPerformed(evt);
            }
        });

        DeleteMasterButton.setText("Удалить");
        DeleteMasterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteMasterButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ManagerMastersPanelLayout = new javax.swing.GroupLayout(ManagerMastersPanel);
        ManagerMastersPanel.setLayout(ManagerMastersPanelLayout);
        ManagerMastersPanelLayout.setHorizontalGroup(
            ManagerMastersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerMastersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(ManagerMastersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(EditMasterButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AddMasterButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(DeleteMasterButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        ManagerMastersPanelLayout.setVerticalGroup(
            ManagerMastersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerMastersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ManagerMastersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ManagerMastersPanelLayout.createSequentialGroup()
                        .addComponent(AddMasterButton)
                        .addGap(18, 18, 18)
                        .addComponent(EditMasterButton)
                        .addGap(18, 18, 18)
                        .addComponent(DeleteMasterButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Прорабы", ManagerMastersPanel);

        jLabel14.setText("ФИО");

        jLabel15.setText("Телефон");

        jLabel16.setText("Адрес офиса");

        ManagerNameTextField.setText("ФИО");

        ManagerPhoneTextField.setText("Телефон");

        ManagerAddressTextField.setText("Адрес офиса");

        SaveManagerProfileButton.setText("Сохранить");
        SaveManagerProfileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveManagerProfileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ManagerProfilePanelLayout = new javax.swing.GroupLayout(ManagerProfilePanel);
        ManagerProfilePanel.setLayout(ManagerProfilePanelLayout);
        ManagerProfilePanelLayout.setHorizontalGroup(
            ManagerProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerProfilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ManagerProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ManagerProfilePanelLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(18, 18, 18)
                        .addComponent(ManagerAddressTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE))
                    .addGroup(ManagerProfilePanelLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(ManagerNameTextField))
                    .addGroup(ManagerProfilePanelLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(ManagerPhoneTextField))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ManagerProfilePanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(SaveManagerProfileButton)))
                .addContainerGap())
        );
        ManagerProfilePanelLayout.setVerticalGroup(
            ManagerProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerProfilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ManagerProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(ManagerNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ManagerProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(ManagerPhoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ManagerProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(ManagerAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 268, Short.MAX_VALUE)
                .addComponent(SaveManagerProfileButton)
                .addContainerGap())
        );

        jTabbedPane3.addTab("Профиль", ManagerProfilePanel);

        ManagerExitButton.setText("Выход");
        ManagerExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ManagerExitButtonActionPerformed(evt);
            }
        });

        ManagerUpdateButton.setText("Обновить");
        ManagerUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ManagerUpdateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ManagerViewLayout = new javax.swing.GroupLayout(ManagerView.getContentPane());
        ManagerView.getContentPane().setLayout(ManagerViewLayout);
        ManagerViewLayout.setHorizontalGroup(
            ManagerViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerViewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ManagerViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ManagerViewLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(ManagerUpdateButton)
                        .addGap(18, 18, 18)
                        .addComponent(ManagerExitButton)))
                .addContainerGap())
        );
        ManagerViewLayout.setVerticalGroup(
            ManagerViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerViewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(ManagerViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ManagerExitButton)
                    .addComponent(ManagerUpdateButton))
                .addContainerGap())
        );

        ClientView.setMinimumSize(new java.awt.Dimension(700, 500));
        ClientView.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                ClientViewWindowClosing(evt);
            }
        });

        ClientExitButton.setText("Выход");
        ClientExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClientExitButtonActionPerformed(evt);
            }
        });

        ClientOrderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Номер", "Статус", "Стоимость", "Дата создания", "Дата изменения", "Дата закрытия"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ClientOrderTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(ClientOrderTable);

        ClientPayButton.setText("Оплатить");
        ClientPayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClientPayButtonActionPerformed(evt);
            }
        });

        ClientAcceptWorkButton.setText("Принять работу");
        ClientAcceptWorkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClientAcceptWorkButtonActionPerformed(evt);
            }
        });

        ClientOrderDetailButton.setText("Подробно");
        ClientOrderDetailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClientOrderDetailButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ClientOrderPanelLayout = new javax.swing.GroupLayout(ClientOrderPanel);
        ClientOrderPanel.setLayout(ClientOrderPanelLayout);
        ClientOrderPanelLayout.setHorizontalGroup(
            ClientOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ClientOrderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(ClientOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ClientOrderDetailButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ClientPayButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ClientAcceptWorkButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        ClientOrderPanelLayout.setVerticalGroup(
            ClientOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ClientOrderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ClientOrderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ClientOrderPanelLayout.createSequentialGroup()
                        .addComponent(ClientAcceptWorkButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ClientPayButton)
                        .addGap(18, 18, 18)
                        .addComponent(ClientOrderDetailButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Заказы", ClientOrderPanel);

        jLabel6.setText("ФИО");

        jLabel8.setText("Телефон");

        ClientNameTextField.setText("ФИО");

        ClientPhoneTextField.setText("Телефон");

        jLabel10.setText("Адрес");

        ClientAddressTextField.setText("Ардес");

        ClientTypeLabel.setText("Тип:");

        SaveClientProfileButton.setText("Сохранить");
        SaveClientProfileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveClientProfileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ClientProfilePanelLayout = new javax.swing.GroupLayout(ClientProfilePanel);
        ClientProfilePanel.setLayout(ClientProfilePanelLayout);
        ClientProfilePanelLayout.setHorizontalGroup(
            ClientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ClientProfilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ClientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ClientProfilePanelLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(ClientNameTextField))
                    .addGroup(ClientProfilePanelLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(ClientPhoneTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE))
                    .addGroup(ClientProfilePanelLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(ClientAddressTextField))
                    .addGroup(ClientProfilePanelLayout.createSequentialGroup()
                        .addComponent(ClientTypeLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ClientProfilePanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(SaveClientProfileButton)))
                .addContainerGap())
        );
        ClientProfilePanelLayout.setVerticalGroup(
            ClientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ClientProfilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ClientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(ClientNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ClientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(ClientPhoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ClientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(ClientAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(ClientTypeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 236, Short.MAX_VALUE)
                .addComponent(SaveClientProfileButton)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Профиль", ClientProfilePanel);

        javax.swing.GroupLayout ClientViewLayout = new javax.swing.GroupLayout(ClientView.getContentPane());
        ClientView.getContentPane().setLayout(ClientViewLayout);
        ClientViewLayout.setHorizontalGroup(
            ClientViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ClientViewLayout.createSequentialGroup()
                .addGroup(ClientViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(ClientViewLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane2))
                    .addGroup(ClientViewLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(ClientExitButton)))
                .addContainerGap())
        );
        ClientViewLayout.setVerticalGroup(
            ClientViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ClientViewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addGap(18, 18, 18)
                .addComponent(ClientExitButton)
                .addContainerGap())
        );

        MasterView.setMinimumSize(new java.awt.Dimension(700, 500));
        MasterView.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                MasterViewWindowClosing(evt);
            }
        });

        MasterExitButton.setText("Выход");
        MasterExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MasterExitButtonActionPerformed(evt);
            }
        });

        MasterWorksTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Работа", "Статус", "Стоимость", "Полная стоимость", "Ф.И.О. клиента", "Телефон клиента", "Адрес клиента"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        MasterWorksTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(MasterWorksTable);

        MasterFinishWorkButton.setText("Выполнить");
        MasterFinishWorkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MasterFinishWorkButtonActionPerformed(evt);
            }
        });

        SaveWorkMasterButton.setText("Сохранить");

        javax.swing.GroupLayout MasterWorksPanelLayout = new javax.swing.GroupLayout(MasterWorksPanel);
        MasterWorksPanel.setLayout(MasterWorksPanelLayout);
        MasterWorksPanelLayout.setHorizontalGroup(
            MasterWorksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MasterWorksPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(MasterWorksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(MasterFinishWorkButton, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                    .addComponent(SaveWorkMasterButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );
        MasterWorksPanelLayout.setVerticalGroup(
            MasterWorksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MasterWorksPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MasterWorksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                    .addGroup(MasterWorksPanelLayout.createSequentialGroup()
                        .addComponent(MasterFinishWorkButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SaveWorkMasterButton)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Работы", MasterWorksPanel);

        jLabel5.setText("ФИО");

        jLabel7.setText("Телефон");

        MasterNameTextField.setText("ФИО");

        MasterPhoneTextField.setText("Телфон");

        SaveMasterProfileButton.setText("Сохранить");
        SaveMasterProfileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveMasterProfileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MasterProfilePanelLayout = new javax.swing.GroupLayout(MasterProfilePanel);
        MasterProfilePanel.setLayout(MasterProfilePanelLayout);
        MasterProfilePanelLayout.setHorizontalGroup(
            MasterProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MasterProfilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MasterProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MasterProfilePanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(39, 39, 39)
                        .addComponent(MasterNameTextField))
                    .addGroup(MasterProfilePanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(MasterPhoneTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MasterProfilePanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(SaveMasterProfileButton)))
                .addContainerGap())
        );
        MasterProfilePanelLayout.setVerticalGroup(
            MasterProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MasterProfilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MasterProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(MasterNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(MasterProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(MasterPhoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 237, Short.MAX_VALUE)
                .addComponent(SaveMasterProfileButton)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Профиль", MasterProfilePanel);

        WorkMasterSumLabel.setText("Итого:");

        javax.swing.GroupLayout MasterViewLayout = new javax.swing.GroupLayout(MasterView.getContentPane());
        MasterView.getContentPane().setLayout(MasterViewLayout);
        MasterViewLayout.setHorizontalGroup(
            MasterViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MasterViewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MasterViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MasterViewLayout.createSequentialGroup()
                        .addComponent(WorkMasterSumLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(MasterExitButton)))
                .addContainerGap())
        );
        MasterViewLayout.setVerticalGroup(
            MasterViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MasterViewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addGap(18, 18, 18)
                .addGroup(MasterViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(WorkMasterSumLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(MasterExitButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        AskDialog.setTitle("Выход?");
        AskDialog.setMinimumSize(new java.awt.Dimension(365, 145));
        AskDialog.setModal(true);
        AskDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                AskDialogWindowClosing(evt);
            }
        });

        jLabel4.setText("Выйти из программы или вернутся к окну аутентификации?");

        NotExitButton.setText("Аутентификация");
        NotExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NotExitButtonActionPerformed(evt);
            }
        });

        ExitButton.setText("Выход");
        ExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitButtonActionPerformed(evt);
            }
        });

        AskCheckBox.setText("Запомнить выбор и больше не спрашивать об этом");

        javax.swing.GroupLayout AskDialogLayout = new javax.swing.GroupLayout(AskDialog.getContentPane());
        AskDialog.getContentPane().setLayout(AskDialogLayout);
        AskDialogLayout.setHorizontalGroup(
            AskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AskDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(AskCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AskDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(NotExitButton)
                .addGap(18, 18, 18)
                .addComponent(ExitButton)
                .addContainerGap())
        );
        AskDialogLayout.setVerticalGroup(
            AskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AskDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(AskCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(AskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ExitButton)
                    .addComponent(NotExitButton))
                .addContainerGap())
        );

        ViewOrderDialog.setTitle("Подробно");
        ViewOrderDialog.setMinimumSize(new java.awt.Dimension(700, 500));

        OrderResourceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Название", "Количество", "Цена за шт./ед.изм.", "Тип"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        OrderResourceTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane10.setViewportView(OrderResourceTable);

        OrderEstimateTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Тип", "Статус"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        OrderEstimateTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane11.setViewportView(OrderEstimateTable);

        OrderNumberLabel.setText("Заказ:<номер>");

        OrderClientNameLabel.setText("Клиент:<ФИО>");

        OrderManagerNameLabel.setText("Менеджер:<ФИО>");

        OrderTotalCoastLabel.setText("Общая стоимость:<calculate coast>");

        OrderCurrentCoastLabel.setText("Текущая стоимость:<current coast>");

        OrderCreateDateLabel.setText("Дата создания:<create date>");

        OrderLastUpdateDateLabel.setText("Дата последнего обновления:<update date>");

        OrderEndDateLabel.setText("Дата завершения:<end date>");

        jLabel27.setText("Сметы:");

        jLabel28.setText("Ресурсы");

        OrderWorkTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Описание", "Статус", "Стоимость", "Полная стоимость"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        OrderWorkTable.setName(""); // NOI18N
        OrderWorkTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane12.setViewportView(OrderWorkTable);

        jLabel29.setText("Работы:");

        OkViewButton.setText("Ок");
        OkViewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkViewButtonActionPerformed(evt);
            }
        });

        OrderStatusLabel.setText("Статус:<status>");

        javax.swing.GroupLayout ViewOrderDialogLayout = new javax.swing.GroupLayout(ViewOrderDialog.getContentPane());
        ViewOrderDialog.getContentPane().setLayout(ViewOrderDialogLayout);
        ViewOrderDialogLayout.setHorizontalGroup(
            ViewOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ViewOrderDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ViewOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ViewOrderDialogLayout.createSequentialGroup()
                        .addGroup(ViewOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(OrderEndDateLabel)
                            .addComponent(OrderLastUpdateDateLabel)
                            .addComponent(OrderManagerNameLabel)
                            .addComponent(OrderTotalCoastLabel)
                            .addComponent(OrderCurrentCoastLabel)
                            .addComponent(OrderCreateDateLabel)
                            .addComponent(OrderClientNameLabel)
                            .addComponent(jLabel29)
                            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE))
                        .addGap(18, 18, 18))
                    .addGroup(ViewOrderDialogLayout.createSequentialGroup()
                        .addComponent(OrderNumberLabel)
                        .addGap(18, 18, 18)
                        .addComponent(OrderStatusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(ViewOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(OkViewButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(ViewOrderDialogLayout.createSequentialGroup()
                        .addGroup(ViewOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28)
                            .addComponent(jLabel27))
                        .addGap(0, 198, Short.MAX_VALUE)))
                .addContainerGap())
        );
        ViewOrderDialogLayout.setVerticalGroup(
            ViewOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ViewOrderDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ViewOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OrderNumberLabel)
                    .addComponent(jLabel27)
                    .addComponent(OrderStatusLabel))
                .addGap(18, 18, 18)
                .addGroup(ViewOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ViewOrderDialogLayout.createSequentialGroup()
                        .addComponent(OrderClientNameLabel)
                        .addGap(18, 18, 18)
                        .addComponent(OrderManagerNameLabel)
                        .addGap(18, 18, 18)
                        .addComponent(OrderTotalCoastLabel)
                        .addGap(18, 18, 18)
                        .addComponent(OrderCurrentCoastLabel)
                        .addGap(18, 18, 18)
                        .addComponent(OrderCreateDateLabel)
                        .addGap(18, 18, 18)
                        .addComponent(OrderLastUpdateDateLabel)
                        .addGap(22, 22, 22)
                        .addComponent(OrderEndDateLabel))
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ViewOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29)
                    .addComponent(jLabel28))
                .addGap(18, 18, 18)
                .addGroup(ViewOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(16, 16, 16)
                .addComponent(OkViewButton)
                .addContainerGap())
        );

        TakeSendResourceDialog.setMinimumSize(new java.awt.Dimension(250, 200));
        TakeSendResourceDialog.setModal(true);

        OkTakeSendResourceDialogButton.setText("Ок");
        OkTakeSendResourceDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkTakeSendResourceDialogButtonActionPerformed(evt);
            }
        });

        CanselTakeSendResourceDialogButton.setText("Отмена");
        CanselTakeSendResourceDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanselTakeSendResourceDialogButtonActionPerformed(evt);
            }
        });

        TypeNameComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Название или тип" }));

        TypeNameRadioGroup.add(NameRadio);
        NameRadio.setText("Название");
        NameRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NameRadioActionPerformed(evt);
            }
        });

        TypeNameRadioGroup.add(TypeRadio);
        TypeRadio.setText("Тип");
        TypeRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TypeRadioActionPerformed(evt);
            }
        });

        AmountTextField.setText("кол-во");

        jLabel9.setText("Количество");

        javax.swing.GroupLayout TakeSendResourceDialogLayout = new javax.swing.GroupLayout(TakeSendResourceDialog.getContentPane());
        TakeSendResourceDialog.getContentPane().setLayout(TakeSendResourceDialogLayout);
        TakeSendResourceDialogLayout.setHorizontalGroup(
            TakeSendResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TakeSendResourceDialogLayout.createSequentialGroup()
                .addGroup(TakeSendResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TakeSendResourceDialogLayout.createSequentialGroup()
                        .addGap(0, 104, Short.MAX_VALUE)
                        .addComponent(OkTakeSendResourceDialogButton)
                        .addGap(18, 18, 18)
                        .addComponent(CanselTakeSendResourceDialogButton))
                    .addGroup(TakeSendResourceDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(TakeSendResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(TakeSendResourceDialogLayout.createSequentialGroup()
                                .addComponent(NameRadio)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(TypeRadio)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(TypeNameComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(TakeSendResourceDialogLayout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(AmountTextField)))))
                .addContainerGap())
        );
        TakeSendResourceDialogLayout.setVerticalGroup(
            TakeSendResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TakeSendResourceDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TakeSendResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NameRadio)
                    .addComponent(TypeRadio))
                .addGap(18, 18, 18)
                .addComponent(TypeNameComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(TakeSendResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AmountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addGroup(TakeSendResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OkTakeSendResourceDialogButton)
                    .addComponent(CanselTakeSendResourceDialogButton))
                .addContainerGap())
        );

        AddEditStorageDialog.setMinimumSize(new java.awt.Dimension(230, 115));
        AddEditStorageDialog.setModal(true);

        OkStorageDialogButton.setText("Ок");
        OkStorageDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkStorageDialogButtonActionPerformed(evt);
            }
        });

        CanselStorageDialogButton.setText("Отмена");
        CanselStorageDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanselStorageDialogButtonActionPerformed(evt);
            }
        });

        StorageLocationTextField.setText("адрес");

        jLabel11.setText("Расположение склада:");

        javax.swing.GroupLayout AddEditStorageDialogLayout = new javax.swing.GroupLayout(AddEditStorageDialog.getContentPane());
        AddEditStorageDialog.getContentPane().setLayout(AddEditStorageDialogLayout);
        AddEditStorageDialogLayout.setHorizontalGroup(
            AddEditStorageDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddEditStorageDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(OkStorageDialogButton)
                .addGap(18, 18, 18)
                .addComponent(CanselStorageDialogButton)
                .addContainerGap())
            .addGroup(AddEditStorageDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(StorageLocationTextField)
                .addGap(11, 11, 11))
        );
        AddEditStorageDialogLayout.setVerticalGroup(
            AddEditStorageDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddEditStorageDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddEditStorageDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StorageLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(AddEditStorageDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CanselStorageDialogButton)
                    .addComponent(OkStorageDialogButton))
                .addContainerGap())
        );

        AddEditWorkDialog.setMinimumSize(new java.awt.Dimension(210, 160));
        AddEditWorkDialog.setModal(true);

        OkWorkDialogButton.setText("Ок");
        OkWorkDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkWorkDialogButtonActionPerformed(evt);
            }
        });

        CanselWorkDialogButton.setText("Отмена");
        CanselWorkDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanselWorkDialogButtonActionPerformed(evt);
            }
        });

        jLabel19.setText("Описание");

        jLabel20.setText("Стоимость");

        DescriptionTextField.setText("Описание");

        WorkCoastTextField.setText("Стоиммость");

        javax.swing.GroupLayout AddEditWorkDialogLayout = new javax.swing.GroupLayout(AddEditWorkDialog.getContentPane());
        AddEditWorkDialog.getContentPane().setLayout(AddEditWorkDialogLayout);
        AddEditWorkDialogLayout.setHorizontalGroup(
            AddEditWorkDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddEditWorkDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddEditWorkDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddEditWorkDialogLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(23, 23, 23)
                        .addComponent(DescriptionTextField)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddEditWorkDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(OkWorkDialogButton)
                        .addGap(18, 18, 18)
                        .addComponent(CanselWorkDialogButton)
                        .addGap(10, 10, 10))
                    .addGroup(AddEditWorkDialogLayout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(18, 18, 18)
                        .addComponent(WorkCoastTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        AddEditWorkDialogLayout.setVerticalGroup(
            AddEditWorkDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddEditWorkDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddEditWorkDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(DescriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddEditWorkDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(WorkCoastTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addGroup(AddEditWorkDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OkWorkDialogButton)
                    .addComponent(CanselWorkDialogButton))
                .addContainerGap())
        );

        AddEditResourceDialog.setMinimumSize(new java.awt.Dimension(300, 200));
        AddEditResourceDialog.setModal(true);

        jLabel21.setText("Название ресурса:");

        jLabel22.setText("Цена за шт./ед. изм.:");

        OkResourceDialogButton.setText("Ок");
        OkResourceDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkResourceDialogButtonActionPerformed(evt);
            }
        });

        CanselResourceDialogButton.setText("Отмена");
        CanselResourceDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanselResourceDialogButtonActionPerformed(evt);
            }
        });

        NameTextField.setText("название");

        ResourceCoastTextField.setText("шт./ед. изм.");

        jLabel23.setText("Тип:");

        TypeTextField.setText("число");

        javax.swing.GroupLayout AddEditResourceDialogLayout = new javax.swing.GroupLayout(AddEditResourceDialog.getContentPane());
        AddEditResourceDialog.getContentPane().setLayout(AddEditResourceDialogLayout);
        AddEditResourceDialogLayout.setHorizontalGroup(
            AddEditResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddEditResourceDialogLayout.createSequentialGroup()
                .addGroup(AddEditResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddEditResourceDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(AddEditResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23)
                            .addComponent(jLabel21))
                        .addGap(18, 18, 18)
                        .addGroup(AddEditResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(NameTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ResourceCoastTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(TypeTextField)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddEditResourceDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(OkResourceDialogButton)
                        .addGap(18, 18, 18)
                        .addComponent(CanselResourceDialogButton)))
                .addContainerGap())
        );
        AddEditResourceDialogLayout.setVerticalGroup(
            AddEditResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddEditResourceDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddEditResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddEditResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(ResourceCoastTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddEditResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(TypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addGroup(AddEditResourceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CanselResourceDialogButton)
                    .addComponent(OkResourceDialogButton))
                .addContainerGap())
        );

        AddEditClientMasterDialog.setMinimumSize(new java.awt.Dimension(250, 235));
        AddEditClientMasterDialog.setModal(true);

        OkClientMasterDialogButton.setText("Ок");
        OkClientMasterDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkClientMasterDialogButtonActionPerformed(evt);
            }
        });

        CanselClientMasterDialogButton.setText("Отмена");
        CanselClientMasterDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanselClientMasterDialogButtonActionPerformed(evt);
            }
        });

        jLabel24.setText("Ф.И.О.:");

        jLabel25.setText("Телефон:");

        jLabel26.setText("Адрес:");

        jLabel30.setText("Тип");

        NameClientMasterTextField.setText("Ф.И.О.");

        PhoneTextField.setText("Телефон");

        AddressTextField.setText("Адрес");

        TypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { R.Client.PHYSICAL, R.Client.LEGAL }));

        javax.swing.GroupLayout AddEditClientMasterDialogLayout = new javax.swing.GroupLayout(AddEditClientMasterDialog.getContentPane());
        AddEditClientMasterDialog.getContentPane().setLayout(AddEditClientMasterDialogLayout);
        AddEditClientMasterDialogLayout.setHorizontalGroup(
            AddEditClientMasterDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddEditClientMasterDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddEditClientMasterDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddEditClientMasterDialogLayout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addGap(18, 18, 18)
                        .addComponent(TypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(AddEditClientMasterDialogLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(18, 18, 18)
                        .addComponent(NameClientMasterTextField))
                    .addGroup(AddEditClientMasterDialogLayout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(18, 18, 18)
                        .addComponent(AddressTextField)))
                .addGap(10, 10, 10))
            .addGroup(AddEditClientMasterDialogLayout.createSequentialGroup()
                .addGroup(AddEditClientMasterDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddEditClientMasterDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel25)
                        .addGap(18, 18, 18)
                        .addComponent(PhoneTextField))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddEditClientMasterDialogLayout.createSequentialGroup()
                        .addGap(0, 104, Short.MAX_VALUE)
                        .addComponent(OkClientMasterDialogButton)
                        .addGap(18, 18, 18)
                        .addComponent(CanselClientMasterDialogButton)))
                .addContainerGap())
        );
        AddEditClientMasterDialogLayout.setVerticalGroup(
            AddEditClientMasterDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddEditClientMasterDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddEditClientMasterDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(NameClientMasterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddEditClientMasterDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(PhoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddEditClientMasterDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(AddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(AddEditClientMasterDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(TypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addGroup(AddEditClientMasterDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CanselClientMasterDialogButton)
                    .addComponent(OkClientMasterDialogButton))
                .addContainerGap())
        );

        AddEditEstimateDialog.setMinimumSize(new java.awt.Dimension(420, 340));
        AddEditEstimateDialog.setModal(true);

        jLabel31.setText("Тип сметы:");

        EstimateTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { R.Estimate.MAIN, R.Estimate.ADDITIONAL }));

        jLabel32.setText("Работы:");

        EstimateWorksDialogTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Описание", "Стоимость", "Полная стоимость"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane15.setViewportView(EstimateWorksDialogTable);

        AddEstimateDialogButton.setText("Добавить");
        AddEstimateDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddEstimateDialogButtonActionPerformed(evt);
            }
        });

        DeleteEstimateDialogButton.setText("Удалить");
        DeleteEstimateDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteEstimateDialogButtonActionPerformed(evt);
            }
        });

        CanselEstimateDialogButton.setText("Отмена");
        CanselEstimateDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanselEstimateDialogButtonActionPerformed(evt);
            }
        });

        OkEstimateDialogButton.setText("Ок");
        OkEstimateDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkEstimateDialogButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout AddEditEstimateDialogLayout = new javax.swing.GroupLayout(AddEditEstimateDialog.getContentPane());
        AddEditEstimateDialog.getContentPane().setLayout(AddEditEstimateDialogLayout);
        AddEditEstimateDialogLayout.setHorizontalGroup(
            AddEditEstimateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddEditEstimateDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddEditEstimateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddEditEstimateDialogLayout.createSequentialGroup()
                        .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(AddEditEstimateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(AddEstimateDialogButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(DeleteEstimateDialogButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(AddEditEstimateDialogLayout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(AddEditEstimateDialogLayout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addGap(18, 18, 18)
                        .addComponent(EstimateTypeComboBox, 0, 306, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddEditEstimateDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(OkEstimateDialogButton)
                        .addGap(18, 18, 18)
                        .addComponent(CanselEstimateDialogButton)))
                .addContainerGap())
        );
        AddEditEstimateDialogLayout.setVerticalGroup(
            AddEditEstimateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddEditEstimateDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddEditEstimateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EstimateTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addGap(18, 18, 18)
                .addComponent(jLabel32)
                .addGap(18, 18, 18)
                .addGroup(AddEditEstimateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddEditEstimateDialogLayout.createSequentialGroup()
                        .addComponent(AddEstimateDialogButton)
                        .addGap(18, 18, 18)
                        .addComponent(DeleteEstimateDialogButton)
                        .addGap(0, 103, Short.MAX_VALUE))
                    .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(AddEditEstimateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CanselEstimateDialogButton)
                    .addComponent(OkEstimateDialogButton))
                .addContainerGap())
        );

        AddEstimateWorkDialog.setMinimumSize(new java.awt.Dimension(225, 120));
        AddEstimateWorkDialog.setModal(true);

        jLabel33.setText("Название:");

        WorkComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Описание работы" }));

        OkAddEstimateWorkDialogButton.setText("Ок");
        OkAddEstimateWorkDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkAddEstimateWorkDialogButtonActionPerformed(evt);
            }
        });

        CanselAddDeleteWorkDialogButton.setText("Отмена");
        CanselAddDeleteWorkDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanselAddDeleteWorkDialogButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout AddEstimateWorkDialogLayout = new javax.swing.GroupLayout(AddEstimateWorkDialog.getContentPane());
        AddEstimateWorkDialog.getContentPane().setLayout(AddEstimateWorkDialogLayout);
        AddEstimateWorkDialogLayout.setHorizontalGroup(
            AddEstimateWorkDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddEstimateWorkDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddEstimateWorkDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddEstimateWorkDialogLayout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addGap(18, 18, 18)
                        .addComponent(WorkComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddEstimateWorkDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(OkAddEstimateWorkDialogButton)
                        .addGap(18, 18, 18)
                        .addComponent(CanselAddDeleteWorkDialogButton)))
                .addContainerGap())
        );
        AddEstimateWorkDialogLayout.setVerticalGroup(
            AddEstimateWorkDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddEstimateWorkDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddEstimateWorkDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(WorkComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(AddEstimateWorkDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OkAddEstimateWorkDialogButton)
                    .addComponent(CanselAddDeleteWorkDialogButton))
                .addContainerGap())
        );

        AddOrderDialog.setMinimumSize(new java.awt.Dimension(220, 155));
        AddOrderDialog.setModal(true);
        AddOrderDialog.setPreferredSize(new java.awt.Dimension(220, 120));

        OkOrderDialogButton.setText("Ок");
        OkOrderDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkOrderDialogButtonActionPerformed(evt);
            }
        });

        CanselOrderDialogButton.setText("Отмена");
        CanselOrderDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanselOrderDialogButtonActionPerformed(evt);
            }
        });

        DateCreateTextField.setText("Дата создания");

        jLabel35.setText("Дата:");

        ManagerClientComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ф.И.О. клиента" }));

        javax.swing.GroupLayout AddOrderDialogLayout = new javax.swing.GroupLayout(AddOrderDialog.getContentPane());
        AddOrderDialog.getContentPane().setLayout(AddOrderDialogLayout);
        AddOrderDialogLayout.setHorizontalGroup(
            AddOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddOrderDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ManagerClientComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(AddOrderDialogLayout.createSequentialGroup()
                        .addGap(0, 64, Short.MAX_VALUE)
                        .addComponent(OkOrderDialogButton)
                        .addGap(18, 18, 18)
                        .addComponent(CanselOrderDialogButton))
                    .addGroup(AddOrderDialogLayout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addGap(18, 18, 18)
                        .addComponent(DateCreateTextField)))
                .addContainerGap())
        );
        AddOrderDialogLayout.setVerticalGroup(
            AddOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddOrderDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AddOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DateCreateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addGap(18, 18, 18)
                .addComponent(ManagerClientComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(AddOrderDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OkOrderDialogButton)
                    .addComponent(CanselOrderDialogButton))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Аутентификация");
        setMinimumSize(new java.awt.Dimension(200, 200));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        OkButton.setText("Ок");
        OkButton.setMaximumSize(new java.awt.Dimension(71, 23));
        OkButton.setMinimumSize(new java.awt.Dimension(71, 23));
        OkButton.setName(""); // NOI18N
        OkButton.setPreferredSize(new java.awt.Dimension(71, 23));
        OkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkButtonActionPerformed(evt);
            }
        });

        CanselButton.setText("Отмена");
        CanselButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanselButtonActionPerformed(evt);
            }
        });

        RoleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { R.RoleType.Manager,R.RoleType.Master,R.RoleType.Client }));
        RoleComboBox.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                RoleComboBoxMouseWheelMoved(evt);
            }
        });

        jLabel1.setText("Роль");

        jLabel2.setText("Логин");

        jLabel3.setText("Пароль");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(RoleComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(LoginTextField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(PasswordFieldText))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(OkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(CanselButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(RoleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(LoginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(PasswordFieldText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CanselButton)
                    .addComponent(OkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkButtonActionPerformed
        //Проверка Авторизации и запуск окна для конкретной роли.
        service.initConfig();
        //проверка на загрузку конфига. конфиг != null
        if(service.getConfig() != null){
            service.initAuth((RoleComboBox.getSelectedIndex() + 1),LoginTextField.getText(),new String(PasswordFieldText.getPassword()));
            //проверка соответствия роли логиа и пароля код индекса >= 0
            if(service.getIndex() >= 0){
                //проверка валидности данной конфигурационной записи.
                if(service.isConfigValid()){
                    service.initDatabase();
                    //проверка подключения базы CONECTED = true
                    if(service.isCONNECTED()){
                        ExitMsg m = service.initDatabaseRoleAuth(RoleComboBox.getSelectedIndex()+1);
                        //проверка для роли внутри базы.
                        if(m.getCode() == ExitMsg.SUCCESS){
                            setVisible(false);
                            initRoleViewBlock();
                        }else{//отсутствие роли в базе данных.
                            String msg = ((m.getMassage()  != null) ? (m.getMassage()+"\n") : "");
                            ErrorMassage(R.ErrMsg.AuthDatabaseError + msg);
                        }
                    }else{//ошибка подключения к базе данных
                        ErrorMassage(R.ErrMsg.DatabaseError);
                    }
                }else{//ошибка валидности данной конфигурационной записи.
                    ErrorMassage(R.ErrMsg.ConfigError_2);
                }
            }else{//ошибка соответствия логина и пароля
                switch(service.getIndex()){
                    case Authorization.NOT_FOUND:
                        ErrorMassage(R.ErrMsg.AuthError_1);
                        break;
                    case Authorization.ROLE_NOT_COMPARE_LOGIN:
                        ErrorMassage(R.ErrMsg.AuthError_2);
                        break;
                    case Authorization.NOT_CORRECT_PASSWORD:
                        ErrorMassage(R.ErrMsg.AuthError_3);
                        break;
                }
            }
        }else{//ошибка поиска конфигурационного файла
            ErrorMassage(R.ErrMsg.ConfigError_1);
        }        
    }//GEN-LAST:event_OkButtonActionPerformed

    private void CanselButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CanselButtonActionPerformed
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));//закрыть приложение
    }//GEN-LAST:event_CanselButtonActionPerformed

    private void ManagerViewWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_ManagerViewWindowClosing
        RoleViewWindowClosing(R.ModelType.ManagerModel);
    }//GEN-LAST:event_ManagerViewWindowClosing

    private void RoleComboBoxMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_RoleComboBoxMouseWheelMoved
        //Прокрутка вариантов мышью
        int i = RoleComboBox.getSelectedIndex() + evt.getWheelRotation();        
        if(i > -1 && i < RoleComboBox.getItemCount()){
            RoleComboBox.setSelectedIndex(i);
        }
    }//GEN-LAST:event_RoleComboBoxMouseWheelMoved

    private void ClientViewWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_ClientViewWindowClosing
        RoleViewWindowClosing(R.ModelType.ClientModel);
    }//GEN-LAST:event_ClientViewWindowClosing

    private void MasterViewWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_MasterViewWindowClosing
        RoleViewWindowClosing(R.ModelType.MasterModel);
    }//GEN-LAST:event_MasterViewWindowClosing

    private void NotExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NotExitButtonActionPerformed
        //переход к окну с аутентификацикей
        if(AskCheckBox.isSelected()){
            service.saveAskDialogProperties(false,false);
        }
        //Сохранение конфигурации
        ExitMsg m = service.saveConfig();
        if(m.getCode() != ExitMsg.SUCCESS){
            ErrorMassage(m.getMassage());
        }
        AskDialog.setVisible(false);
        setVisible(true);
    }//GEN-LAST:event_NotExitButtonActionPerformed

    private void ExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitButtonActionPerformed
        // выход
        if(AskCheckBox.isSelected()){
            service.saveAskDialogProperties(false, true);
        }
        AskDialog.setVisible(false);
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));//закрыть приложение
    }//GEN-LAST:event_ExitButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //Сохранение конфигурации
        if(service.getConfig() != null){
            service.saveConfig();
        }
    }//GEN-LAST:event_formWindowClosing

    private void AskDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_AskDialogWindowClosing
        AskDialog.setVisible(false);
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));//закрыть приложение
    }//GEN-LAST:event_AskDialogWindowClosing

    private void SaveManagerProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveManagerProfileButtonActionPerformed
        ExitMsg m = service.saveManagerProfile(
                ManagerNameTextField.getText(), 
                ManagerPhoneTextField.getText(), 
                ManagerAddressTextField.getText()
        );
        if(m.getCode() == ExitMsg.SUCCESS){
            UPDATED = true;
        }else{
            ErrorMassage(m.getMassage());
        }        
    }//GEN-LAST:event_SaveManagerProfileButtonActionPerformed

    private void SaveClientProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveClientProfileButtonActionPerformed
        ExitMsg m = service.saveClientProfile(
                ClientNameTextField.getText(), 
                ClientPhoneTextField.getText(), 
                ClientAddressTextField.getText()
        );
        if(m.getCode() == ExitMsg.SUCCESS){
            UPDATED = true;
        }else{
            ErrorMassage(m.getMassage());
        }
    }//GEN-LAST:event_SaveClientProfileButtonActionPerformed

    private void SaveMasterProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveMasterProfileButtonActionPerformed
        ExitMsg m = service.saveMasterProfile(MasterNameTextField.getText(), MasterPhoneTextField.getText());
        if(m.getCode() == ExitMsg.SUCCESS){
            UPDATED = true;
        }else{
            ErrorMassage(m.getMassage());
        }
    }//GEN-LAST:event_SaveMasterProfileButtonActionPerformed

    private void ManagerExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ManagerExitButtonActionPerformed
        RoleViewWindowClosing(R.ModelType.ManagerModel);
    }//GEN-LAST:event_ManagerExitButtonActionPerformed

    private void ClientExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClientExitButtonActionPerformed
        RoleViewWindowClosing(R.ModelType.ClientModel);
    }//GEN-LAST:event_ClientExitButtonActionPerformed

    private void MasterExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MasterExitButtonActionPerformed
        RoleViewWindowClosing(R.ModelType.MasterModel);
    }//GEN-LAST:event_MasterExitButtonActionPerformed

    private void ClientAcceptWorkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClientAcceptWorkButtonActionPerformed
        int SelectedRow = ClientOrderTable.getSelectedRow();
        if(SelectedRow >= 0){
            if(service.getClientOrderList() != null){
                if(service.getClientOrderAt(SelectedRow).getStatus() == Order.WAITING_ACKNOWLEDGMENT_TAKE){
                    service.getClientOrderAt(SelectedRow).setStatus(Order.WAITING_PAY);
                    javax.swing.table.DefaultTableModel ClientOrderTableModel = 
                                            (javax.swing.table.DefaultTableModel)ClientOrderTable.getModel();
                    ClientOrderTableModel.setValueAt(R.Order.StatusName(service.getClientOrderAt(SelectedRow).getStatus()), SelectedRow, 1);
                }
            }
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_ClientAcceptWorkButtonActionPerformed

    private void ClientPayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClientPayButtonActionPerformed
        int SelectedRow = ClientOrderTable.getSelectedRow();
        ExitMsg ex = service.clientPayLogic(SelectedRow, new BuisnessService.ClienPayListener(){

            @Override
            public String askDialog() {
                return JOptionPane.showInputDialog(R.Dialog.ClientPayInputMsg);
            }

            @Override
            public void acceptDialog() {
                String Title = "Оплата";
                                JOptionPane.showConfirmDialog(null, 
                                        R.Dialog.ClienPayAccept, 
                                        Title, 
                                        JOptionPane.DEFAULT_OPTION, 
                                        JOptionPane.PLAIN_MESSAGE, null);                   
            }
            
        });
        
        if(ex.getCode() == ExitMsg.SUCCESS){
            javax.swing.table.DefaultTableModel ClientOrderTableModel = 
                                            (javax.swing.table.DefaultTableModel)ClientOrderTable.getModel();
                                ClientOrderTableModel.setValueAt(String.valueOf(service.getClientOrderList().get(SelectedRow).getCurrentCoast()), SelectedRow, 2);
        }else{
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_ClientPayButtonActionPerformed

    private void ClientOrderDetailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClientOrderDetailButtonActionPerformed
        int SelectedRow = ClientOrderTable.getSelectedRow();
        if(SelectedRow >= 0){
            OrderNumberLabel.setText(service.getOrderNumberText(SelectedRow));
            OrderStatusLabel.setText(service.getOrderStatusText(SelectedRow));
            OrderClientNameLabel.setText(service.getOrderClientNameText());
            OrderTotalCoastLabel.setText(service.getOrderTotalCoastText(SelectedRow));
            OrderCurrentCoastLabel.setText(service.getOrderCurrentCoastText(SelectedRow));
            OrderCreateDateLabel.setText(service.getOrderCreateDateText(SelectedRow));
            OrderLastUpdateDateLabel.setText(service.getOrderLastUpdateDateText(SelectedRow));            
            OrderEndDateLabel.setText(service.getOrderEndDateText(SelectedRow));
            
            javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel)OrderEstimateTable.getModel();
            while(m.getRowCount() > 0){m.removeRow(0);}
            service.getClientOrderAt(SelectedRow).getEstimateList().stream().forEach((Elist1) -> {
                m.addRow(new Object[]{
                            R.Estimate.TypeName(Elist1.getType()), 
                            R.Estimate.StatusName(Elist1.isPaid(), Elist1.isFinish())
                        }
                );
            });
            
            OrderEstimateTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
                if(OrderEstimateTable.getSelectedRow() >= 0){
                    javax.swing.table.DefaultTableModel workModel = (javax.swing.table.DefaultTableModel)OrderWorkTable.getModel();                 
                    while(workModel.getRowCount() > 0){workModel.removeRow(0);}
                    service.getClientOrderAt(SelectedRow).getEstimate(OrderEstimateTable.getSelectedRow()).getWorkList().stream().forEach((e) -> {
                        workModel.addRow(new Object[]{
                            e.getDescription(), 
                            R.Work.StatusName(e.isFinish()),
                            String.valueOf(e.getServiceCoast()),
                            String.valueOf(e.CoastCalculation())
                        });
                    });
                }
            });
            
            OrderWorkTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
                if(OrderEstimateTable.getSelectedRow() >= 0 ){
                    if(OrderWorkTable.getSelectedRow() >= 0 ){
                    javax.swing.table.DefaultTableModel resourceModel = (javax.swing.table.DefaultTableModel)OrderResourceTable.getModel();
                    while(resourceModel.getRowCount() > 0){resourceModel.removeRow(0);}
                    service.getClientOrderList()
                            .get(SelectedRow)
                            .getEstimate(OrderEstimateTable.getSelectedRow())
                            .getWork(OrderWorkTable.getSelectedRow())
                            .getResources().stream().forEach((res) -> {
                        resourceModel.addRow(new Object[]{
                            res.getName(),
                            String.valueOf(res.getAmount()),
                            String.valueOf(res.getCoast()),
                            String.valueOf(res.getType())
                        });
                    });
                    }
                }
            });
            
            ViewOrderDialog.setVisible(true);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_ClientOrderDetailButtonActionPerformed

    private void OkViewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkViewButtonActionPerformed
        ViewOrderDialog.setVisible(false);
    }//GEN-LAST:event_OkViewButtonActionPerformed

    private void MasterFinishWorkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MasterFinishWorkButtonActionPerformed
        int SelectedRow = MasterWorksTable.getSelectedRow();
        if(SelectedRow >= 0){
            int WorkIndex = SelectedRow;
            int EstimateIndex = 0;
            for (Estimate es : service.getMasterEstimateList()) {
                int size = es.getWorkList().size();
                if(WorkIndex >= size){
                    EstimateIndex++;
                    WorkIndex = WorkIndex - size;
                }else{
                    break;
                }
            }
            service.getMasterEstimateAt(EstimateIndex).getWork(WorkIndex).setFinish(true);
            javax.swing.table.DefaultTableModel MasterWorksTableModel = 
                                        (javax.swing.table.DefaultTableModel)MasterWorksTable.getModel();
            MasterWorksTableModel.setValueAt(R.Work.StatusName(service.getMasterEstimateAt(EstimateIndex).getWork(WorkIndex).isFinish()),SelectedRow,1);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_MasterFinishWorkButtonActionPerformed

    private void ManagerUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ManagerUpdateButtonActionPerformed
        //обновление позиции
        if(UPDATED){
            initManagerViewModel();
            UPDATED = false;
        }else{
            JOptionPane.showConfirmDialog(null,
                    R.Dialog.UpdateMsg,
                    R.Dialog.Update,
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null);
        }
    }//GEN-LAST:event_ManagerUpdateButtonActionPerformed

    private void AddOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddOrderButtonActionPerformed
        DateCreateTextField.setText(service.getDateFormat().format(new Date()));
        ManagerClientComboBox.removeAllItems();
        service.getManagerClientList().stream().forEach((Client1) -> {
            ManagerClientComboBox.addItem(Client1.getName());
        });
        AddOrderDialog.setVisible(true);
    }//GEN-LAST:event_AddOrderButtonActionPerformed

    private void ChangeStatusOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeStatusOrderButtonActionPerformed
        int SelectedRow = ManagerOrderTable.getSelectedRow();
        ExitMsg ex = service.changeOrderStatus(SelectedRow);
        if(ex.getCode() == ExitMsg.SUCCESS){
            ManagerOrderTable.getModel().setValueAt(R.Order.StatusName(service.getManagerOrderAt(SelectedRow).getStatus()), SelectedRow, 1);
        }else{
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_ChangeStatusOrderButtonActionPerformed

    private void DeleteOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteOrderButtonActionPerformed
        int SelectedRow = ManagerOrderTable.getSelectedRow();
        ExitMsg ex = service.deleteOrder(SelectedRow);
        if(ex.getCode() == ExitMsg.SUCCESS){
            ((javax.swing.table.DefaultTableModel)ManagerOrderTable.getModel()).removeRow(SelectedRow);
        }else{
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_DeleteOrderButtonActionPerformed

    private void AddWorkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddWorkButtonActionPerformed
        DialogAction = R.Dialog.AddAction;
        DescriptionTextField.setText("");
        WorkCoastTextField.setText("");
        AddEditWorkDialog.setTitle(R.Dialog.Add);
        AddEditWorkDialog.setVisible(true);
    }//GEN-LAST:event_AddWorkButtonActionPerformed

    private void EditWorkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditWorkButtonActionPerformed
        int SelectedRow = ManagerWorkTable.getSelectedRow();
        if(SelectedRow >= 0){
            DialogAction = R.Dialog.EditAction;
            DescriptionTextField.setText(service.getManagerWorkAt(SelectedRow).getDescription());
            WorkCoastTextField.setText(String.valueOf(service.getManagerWorkAt(SelectedRow).getServiceCoast()));
            AddEditWorkDialog.setTitle(R.Dialog.Edit);
            AddEditWorkDialog.setVisible(true);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_EditWorkButtonActionPerformed

    private void DeleteWorkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteWorkButtonActionPerformed
        int SelectedRow = ManagerWorkTable.getSelectedRow();
        ExitMsg ex = service.deleteWork(SelectedRow);
        if(ex.getCode() == ExitMsg.SUCCESS){
            ((javax.swing.table.DefaultTableModel)ManagerWorkTable.getModel()).removeRow(SelectedRow);
            UPDATED = true;
        }else{    
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_DeleteWorkButtonActionPerformed

    private void AddResourceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddResourceButtonActionPerformed
        DialogAction = R.Dialog.AddAction;
        NameTextField.setText("");
        ResourceCoastTextField.setText("");
        TypeTextField.setText("");
        AddEditResourceDialog.setTitle(R.Dialog.Add);
        AddEditResourceDialog.setVisible(true);
    }//GEN-LAST:event_AddResourceButtonActionPerformed

    private void EditResourceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditResourceButtonActionPerformed
        int SelectedRow = ManagerResourceTable.getSelectedRow();
        if(SelectedRow >= 0){
            DialogAction = R.Dialog.EditAction;
            NameTextField.setText(service.getManagerResourceAt(SelectedRow).getName());
            ResourceCoastTextField.setText(String.valueOf(service.getManagerResourceAt(SelectedRow).getCoast()));
            TypeTextField.setText(String.valueOf(service.getManagerResourceAt(SelectedRow).getType()));
            AddEditResourceDialog.setTitle(R.Dialog.Edit);
            AddEditResourceDialog.setVisible(true);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_EditResourceButtonActionPerformed

    private void DeleteResourceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteResourceButtonActionPerformed
        int SelectedRow = ManagerResourceTable.getSelectedRow();        
        ExitMsg ex = service.deleteResource(SelectedRow);
        if(ex.getCode() == ExitMsg.SUCCESS){
            ((javax.swing.table.DefaultTableModel)ManagerResourceTable.getModel()).removeRow(SelectedRow);
            UPDATED = true;
        }else{    
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_DeleteResourceButtonActionPerformed

    private void AddStorageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddStorageButtonActionPerformed
        DialogAction = R.Dialog.AddAction;
        StorageLocationTextField.setText("");
        AddEditStorageDialog.setTitle(R.Dialog.Add);
        AddEditStorageDialog.setVisible(true);
    }//GEN-LAST:event_AddStorageButtonActionPerformed

    private void EditStorageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditStorageButtonActionPerformed
        int SelectedRow = ManagerStorageTable.getSelectedRow();
        if(SelectedRow >= 0){
            DialogAction = R.Dialog.EditAction;
            StorageLocationTextField.setText(service.getManagerStorageAt(SelectedRow).getLocation());
            AddEditStorageDialog.setTitle(R.Dialog.Edit);
            AddEditStorageDialog.setVisible(true);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_EditStorageButtonActionPerformed

    private void DeleteStorageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteStorageButtonActionPerformed
        int SelectedRow = ManagerStorageTable.getSelectedRow();      
        ExitMsg ex = service.deleteStorage(SelectedRow);
        if(ex.getCode() == ExitMsg.SUCCESS){
            ((javax.swing.table.DefaultTableModel)ManagerStorageTable.getModel()).removeRow(SelectedRow);
            UPDATED = true;
        }else{    
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_DeleteStorageButtonActionPerformed

    private void SendResourceToStorageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendResourceToStorageButtonActionPerformed
        int SelectedRow = ManagerStorageTable.getSelectedRow();
        if(SelectedRow >= 0){
            DialogAction = R.Dialog.SendAction;
            TypeNameComboBox.removeAllItems();
            if(service.getManagerResourceList() != null){
                service.getManagerResourceList().stream().forEach((e) -> {
                    TypeNameComboBox.addItem(e.getName());
                });
            }
            TypeNameComboBox.setEnabled(true);
            NameRadio.setSelected(true);        
            AmountTextField.setText("");
            TakeSendResourceDialog.setTitle(R.Dialog.Send);
            TakeSendResourceDialog.setVisible(true);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_SendResourceToStorageButtonActionPerformed

    private void TakeResourceFromStorageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TakeResourceFromStorageButtonActionPerformed
        int SelectedRow = ManagerStorageTable.getSelectedRow();
        int SelectedRowResource = ManagerStorageResourceTable.getSelectedRow();
        if(SelectedRow >= 0 && SelectedRowResource >= 0){
            DialogAction = R.Dialog.TakeAction;
            TypeNameComboBox.removeAllItems();
            if(service.getManagerStorageAt(SelectedRow).getResources() != null){
                service.getManagerStorageAt(SelectedRow).getResources().stream().forEach((e) -> {
                    TypeNameComboBox.addItem(e.getName());
                });
            }
            TypeNameComboBox.setSelectedIndex(SelectedRowResource);
            TypeNameComboBox.setEnabled(false);
            NameRadio.setSelected(true);        
            AmountTextField.setText("");
            TakeSendResourceDialog.setTitle(R.Dialog.Send);
            TakeSendResourceDialog.setVisible(true);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_TakeResourceFromStorageButtonActionPerformed

    private void AddClientButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddClientButtonActionPerformed
        Master = false;
        DialogAction = R.Dialog.AddAction;
        NameClientMasterTextField.setText("");
        PhoneTextField.setText("");
        AddressTextField.setText("");
        jLabel26.setVisible(true);
        AddressTextField.setVisible(true);
        jLabel30.setVisible(true);
        TypeComboBox.setVisible(true);
        AddEditClientMasterDialog.setTitle(R.Dialog.Add);
        AddEditClientMasterDialog.setVisible(true);
    }//GEN-LAST:event_AddClientButtonActionPerformed

    private void EditClientButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditClientButtonActionPerformed
        int SelectedRow = ManagerClientTable.getSelectedRow();
        if(SelectedRow >= 0){
            Master = false;
            DialogAction = R.Dialog.EditAction;
            NameClientMasterTextField.setText(service.getManagerClientAt(SelectedRow).getName());
            PhoneTextField.setText(service.getManagerClientAt(SelectedRow).getPhoneNumber());
            AddressTextField.setText(service.getManagerClientAt(SelectedRow).getAddres());
            TypeComboBox.setSelectedIndex(service.getManagerClientAt(SelectedRow).getType()-1);
            jLabel26.setVisible(true);
            AddressTextField.setVisible(true);
            jLabel30.setVisible(true);
            TypeComboBox.setVisible(true);
            AddEditClientMasterDialog.setTitle(R.Dialog.Edit);
            AddEditClientMasterDialog.setVisible(true);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_EditClientButtonActionPerformed

    private void DeleteClientButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteClientButtonActionPerformed
        int SelectedRow = ManagerClientTable.getSelectedRow();
        ExitMsg ex = service.deleteClient(SelectedRow);
        if(ex.getCode() == ExitMsg.SUCCESS){
            ((javax.swing.table.DefaultTableModel)ManagerClientTable.getModel()).removeRow(SelectedRow);
            UPDATED = true;
        }else{    
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_DeleteClientButtonActionPerformed

    private void AddMasterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddMasterButtonActionPerformed
        Master = true;
        DialogAction = R.Dialog.AddAction;
        NameClientMasterTextField.setText("");
        PhoneTextField.setText("");
        jLabel26.setVisible(false);
        AddressTextField.setVisible(false);
        jLabel30.setVisible(false);
        TypeComboBox.setVisible(false);
        AddEditClientMasterDialog.setTitle(R.Dialog.Add);
        AddEditClientMasterDialog.setVisible(true);
    }//GEN-LAST:event_AddMasterButtonActionPerformed

    private void EditMasterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditMasterButtonActionPerformed
        int SelectedRow = ManagerMasterTable.getSelectedRow();
        if(SelectedRow >= 0){
            Master = true;
            DialogAction = R.Dialog.EditAction;
            NameClientMasterTextField.setText(service.getManagerMasterAt(SelectedRow).getName());
            PhoneTextField.setText(service.getManagerMasterAt(SelectedRow).getPhoneNumber());
            jLabel26.setVisible(false);
            AddressTextField.setVisible(false);
            jLabel30.setVisible(false);
            TypeComboBox.setVisible(false);
            AddEditClientMasterDialog.setTitle(R.Dialog.Edit);
            AddEditClientMasterDialog.setVisible(true);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_EditMasterButtonActionPerformed

    private void DeleteMasterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteMasterButtonActionPerformed
        int SelectedRow = ManagerMasterTable.getSelectedRow();
        ExitMsg ex = service.deleteMaster(SelectedRow);
        if(ex.getCode() == ExitMsg.SUCCESS){
            ((javax.swing.table.DefaultTableModel)ManagerMasterTable.getModel()).removeRow(SelectedRow);
            UPDATED = true;
        }else{    
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_DeleteMasterButtonActionPerformed

    private void AddEstimateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddEstimateButtonActionPerformed
        int SelectedRow = ManagerOrderTable.getSelectedRow();
        if(SelectedRow >= 0){
            javax.swing.table.DefaultTableModel m = 
                    (javax.swing.table.DefaultTableModel) EstimateWorksDialogTable.getModel();
            while(m.getRowCount() > 0){m.removeRow(0);}
            DialogAction = R.Dialog.AddAction;
            WorkComboBox.removeAllItems();
            AddEditEstimateDialog.setTitle(R.Dialog.Add);        
            AddEditEstimateDialog.setVisible(true);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_AddEstimateButtonActionPerformed

    private void EditEstimateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditEstimateButtonActionPerformed
        int SelectedRow = ManagerOrderTable.getSelectedRow();
        int SelectedRowEstimate = ManagerEstimateTable.getSelectedRow();
        if(SelectedRow >= 0 && SelectedRowEstimate >= 0){
            javax.swing.table.DefaultTableModel m = 
                    (javax.swing.table.DefaultTableModel) EstimateWorksDialogTable.getModel();
            while(m.getRowCount() > 0){m.removeRow(0);}
            DialogAction = R.Dialog.EditAction;
            WorkComboBox.removeAllItems();
            service.getManagerOrderAt(SelectedRow).getEstimate(SelectedRowEstimate).getWorkList().stream().forEach((e) -> {
                m.addRow(new Object[]{
                    e.getDescription(),
                    e.getServiceCoast(),
                    e.CoastCalculation()
                });
            });
            EstimateTypeComboBox.setSelectedIndex(service.getManagerOrderAt(SelectedRow).getEstimate(SelectedRowEstimate).getType()-1);
            AddEditEstimateDialog.setTitle(R.Dialog.Edit);
            AddEditEstimateDialog.setVisible(true);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_EditEstimateButtonActionPerformed

    private void DeleteEstimateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteEstimateButtonActionPerformed
        int SelectedRow = ManagerOrderTable.getSelectedRow();
        int EstimateSelectedRow = ManagerEstimateTable.getSelectedRow();
        ExitMsg ex = service.deleteEstimate(SelectedRow,EstimateSelectedRow);
        if(ex.getCode() == ExitMsg.SUCCESS){
            ((javax.swing.table.DefaultTableModel)ManagerEstimateTable.getModel()).removeRow(EstimateSelectedRow);
            UPDATED = true;
        }else{    
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_DeleteEstimateButtonActionPerformed

    private void AddWorkResourceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddWorkResourceButtonActionPerformed
        int SelectedRow = ManagerWorkTable.getSelectedRow();
        if(SelectedRow >= 0){
            DialogAction = R.Dialog.AddAction;
            TypeNameComboBox.removeAllItems();
            service.getManagerResourceList().stream().forEach((e) -> {
                TypeNameComboBox.addItem(e.getName());
            });
            TypeNameComboBox.setEnabled(true);
            NameRadio.setSelected(true);
            AmountTextField.setText("");
            TakeSendResourceDialog.setTitle(R.Dialog.Add);
            TakeSendResourceDialog.setVisible(true);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_AddWorkResourceButtonActionPerformed

    private void EditWorkResourceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditWorkResourceButtonActionPerformed
        int SelectedRow = ManagerWorkTable.getSelectedRow();
        int SelectedRowResource = ManagerWorkResourceTable.getSelectedRow();
        if(SelectedRow >= 0 && SelectedRowResource >= 0){
            DialogAction = R.Dialog.EditAction;
            TypeNameComboBox.removeAllItems();
            if(service.getManagerWorkAt(SelectedRow).getResources() != null){
                service.getManagerWorkAt(SelectedRow).getResources().stream().forEach((e) -> {
                    TypeNameComboBox.addItem(e.getName());
                });
            }
            TypeNameComboBox.setSelectedIndex(SelectedRowResource);
            TypeNameComboBox.setEnabled(false);
            NameRadio.setSelected(true);
            AmountTextField.setText(String.valueOf(service.getManagerWorkAt(SelectedRow).getResource(SelectedRowResource).getAmount()));
            TakeSendResourceDialog.setTitle(R.Dialog.Edit);
            TakeSendResourceDialog.setVisible(true);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_EditWorkResourceButtonActionPerformed

    private void DeleteWorkResourceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteWorkResourceButtonActionPerformed
        int SelectedRow = ManagerWorkTable.getSelectedRow();
        int SelectedRowResource = ManagerWorkResourceTable.getSelectedRow();
        ExitMsg ex = service.deleteWorkResource(SelectedRow,SelectedRowResource);
        if(ex.getCode() == ExitMsg.SUCCESS){
            ((javax.swing.table.DefaultTableModel)ManagerWorkResourceTable.getModel()).removeRow(SelectedRowResource);
            UPDATED = true;
        }else{    
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_DeleteWorkResourceButtonActionPerformed

    private void OkTakeSendResourceDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkTakeSendResourceDialogButtonActionPerformed
        int SelectedWorkRow = ManagerWorkTable.getSelectedRow();
        int SelectedStorageRow = ManagerStorageTable.getSelectedRow();
        int SelectedIndex = TypeNameComboBox.getSelectedIndex();
        int SelectedWorkResourceRow = ManagerWorkResourceTable.getSelectedRow();
        int SelectedStorageResourceRow = ManagerStorageResourceTable.getSelectedRow();        
        ExitMsg ex = service.saveTakeSendResourceDialog(
            DialogAction,
            AmountTextField.getText(),
            SelectedWorkRow,
            SelectedStorageRow,
            SelectedIndex,
            SelectedWorkResourceRow,
            SelectedStorageResourceRow,
            new BuisnessService.CallBackSaveTakeSendResourceDialog(){

                @Override
                public void addAction(Resource WorkResource) {
                    ((javax.swing.table.DefaultTableModel)ManagerWorkResourceTable.getModel())
                        .addRow(new Object[]{
                            WorkResource.getName(),
                            String.valueOf(WorkResource.getAmount()),
                            String.valueOf(WorkResource.getCoast()),
                            String.valueOf(WorkResource.getType())
                    });
                }

                @Override
                public void addAction(Resource WorkResource,int WorkResPosition) {
                    javax.swing.table.DefaultTableModel m = 
                        ((javax.swing.table.DefaultTableModel)ManagerWorkResourceTable.getModel());
                    m.setValueAt(WorkResource.getName(), WorkResPosition, 0);
                    m.setValueAt(String.valueOf(WorkResource.getAmount()), WorkResPosition, 1);
                    m.setValueAt(String.valueOf(WorkResource.getCoast()), WorkResPosition, 2);
                    m.setValueAt(String.valueOf(WorkResource.getType()), WorkResPosition, 3);
                }

                @Override
                public void WorkFinishAction() {
                    ((javax.swing.table.DefaultTableModel)ManagerWorkTable.getModel())
                        .setValueAt(String.valueOf(service.getManagerWorkAt(SelectedWorkRow).CoastCalculation()),SelectedWorkRow, 2);
                }

                @Override
                public void sendAction(Resource StorageResource) {
                    ((javax.swing.table.DefaultTableModel)ManagerStorageResourceTable.getModel())
                        .addRow(new Object[]{
                            StorageResource.getName(),
                            String.valueOf(StorageResource.getAmount()),
                            String.valueOf(StorageResource.getCoast()),
                            String.valueOf(StorageResource.getType())
                    });
                }

                @Override
                public void sendAction(Resource StorageResource, int StorResPosition) {
                    javax.swing.table.DefaultTableModel m = 
                        ((javax.swing.table.DefaultTableModel)ManagerStorageResourceTable.getModel());
                    m.setValueAt(service.getManagerResourceAt(SelectedIndex).getName(), SelectedIndex, 0);
                    m.setValueAt(String.valueOf(StorageResource.getAmount()), StorResPosition, 1);
                    m.setValueAt(String.valueOf(StorageResource.getCoast()), StorResPosition, 2);
                    m.setValueAt(String.valueOf(StorageResource.getType()), StorResPosition, 3);
                }

                @Override
                public void takeAction() {
                    ((javax.swing.table.DefaultTableModel)ManagerStorageResourceTable.getModel())
                        .setValueAt(String.valueOf(service.getManagerStorageAt(SelectedStorageRow)
                                        .getResource(SelectedStorageResourceRow).getAmount()),
                                SelectedStorageResourceRow, 1);
                }

                @Override
                public void takeAction2() {
                    ((javax.swing.table.DefaultTableModel)ManagerStorageResourceTable.getModel())
                        .removeRow(SelectedStorageResourceRow);
                }
                
            }
        );
        
        if(ex.getCode() == ExitMsg.SUCCESS){
            UPDATED = true;
            TakeSendResourceDialog.setVisible(false);
        }else{
            if(ex.getCode() != ExitMsg.INPUT_ERROR){
                TakeSendResourceDialog.setVisible(false);
            }
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_OkTakeSendResourceDialogButtonActionPerformed

    private void CanselTakeSendResourceDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CanselTakeSendResourceDialogButtonActionPerformed
        TakeSendResourceDialog.setVisible(false);
    }//GEN-LAST:event_CanselTakeSendResourceDialogButtonActionPerformed

    private void OkStorageDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkStorageDialogButtonActionPerformed
        int SelectedRow =  ManagerStorageTable.getSelectedRow();
        ExitMsg ex = service.saveStorage(
                DialogAction,
                StorageLocationTextField.getText(),
                SelectedRow,
                new BuisnessService.CallBackSaveStorageDialog(){

                    @Override
                    public void addAction(Storage stor) {
                        ((javax.swing.table.DefaultTableModel)ManagerStorageTable.getModel())
                            .addRow(new Object[]{stor.getLocation()});
                    }

                    @Override
                    public void editAction(Storage stor) {
                        javax.swing.table.DefaultTableModel m = 
                                (javax.swing.table.DefaultTableModel)ManagerStorageTable.getModel();
                        m.setValueAt(stor.getLocation(), SelectedRow, 0);
                    }

                }
        );
        
        if(ex.getCode() == ExitMsg.SUCCESS){
            UPDATED = true;
            AddEditStorageDialog.setVisible(false);
        }else{
            if(ex.getCode() != ExitMsg.INPUT_ERROR){
                AddEditStorageDialog.setVisible(false);
            }
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_OkStorageDialogButtonActionPerformed

    private void CanselStorageDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CanselStorageDialogButtonActionPerformed
        AddEditStorageDialog.setVisible(false);
    }//GEN-LAST:event_CanselStorageDialogButtonActionPerformed

    private void OkWorkDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkWorkDialogButtonActionPerformed
        int SelectedRow = ManagerWorkTable.getSelectedRow();
        ExitMsg ex = service.saveWork(
            DialogAction,
            DescriptionTextField.getText(),
            WorkCoastTextField.getText(),
            SelectedRow,
            new BuisnessService.CallBackSaveWorkDialog(){

                @Override
                public void addAction(Work wr) {
                    ((javax.swing.table.DefaultTableModel)ManagerWorkTable.getModel())
                        .addRow(new Object[]{
                            wr.getDescription(),
                            String.valueOf(wr.getServiceCoast()),
                            String.valueOf(wr.CoastCalculation())
                    });
                }

                @Override
                public void editAction(Work wr) {
                    javax.swing.table.DefaultTableModel m = 
                                            (javax.swing.table.DefaultTableModel)ManagerWorkTable.getModel();
                    m.setValueAt(wr.getDescription(), SelectedRow, 0);
                    m.setValueAt(String.valueOf(wr.getServiceCoast()), SelectedRow, 1);
                    m.setValueAt(String.valueOf(wr.CoastCalculation()), SelectedRow, 2);                                    
                }
                
            }
        );
        
        if(ex.getCode() == ExitMsg.SUCCESS){
            UPDATED = true;
            AddEditWorkDialog.setVisible(false);
        }else{
            if(ex.getCode() != ExitMsg.INPUT_ERROR){
                AddEditWorkDialog.setVisible(false);
            }
            ErrorMassage(ex.getMassage());
        }    
    }//GEN-LAST:event_OkWorkDialogButtonActionPerformed

    private void CanselWorkDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CanselWorkDialogButtonActionPerformed
        AddEditWorkDialog.setVisible(false);
    }//GEN-LAST:event_CanselWorkDialogButtonActionPerformed

    private void OkResourceDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkResourceDialogButtonActionPerformed
        int SelectedRow = ManagerResourceTable.getSelectedRow();
        ExitMsg ex = service.saveResource(
                DialogAction, 
                NameTextField.getText(), 
                ResourceCoastTextField.getText(), 
                TypeTextField.getText(), 
                SelectedRow,
                new BuisnessService.CallBackSaveResourceDialog(){

                    @Override
                    public void addAction(Resource res) {
                        ((javax.swing.table.DefaultTableModel)ManagerResourceTable.getModel())
                            .addRow(new Object[]{
                                res.getName(),
                                String.valueOf(res.getCoast()),
                                String.valueOf(res.getType())
                            });
                    }

                    @Override
                    public void editAction(Resource res) {
                        javax.swing.table.DefaultTableModel m = 
                                (javax.swing.table.DefaultTableModel)ManagerResourceTable.getModel();
                        m.setValueAt(res.getName(), SelectedRow, 0);
                        m.setValueAt(String.valueOf(res.getCoast()), SelectedRow, 1);
                        m.setValueAt(String.valueOf(res.getType()), SelectedRow, 2);
                    }

                }
        );
        
        if(ex.getCode() == ExitMsg.SUCCESS){
            UPDATED = true;
            AddEditResourceDialog.setVisible(false);
        }else{
            if(ex.getCode() != ExitMsg.INPUT_ERROR){
                AddEditResourceDialog.setVisible(false);
            }
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_OkResourceDialogButtonActionPerformed

    private void CanselResourceDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CanselResourceDialogButtonActionPerformed
        AddEditResourceDialog.setVisible(false);
    }//GEN-LAST:event_CanselResourceDialogButtonActionPerformed

    private void OkClientMasterDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkClientMasterDialogButtonActionPerformed
        int MasterSelectedRow = ManagerMasterTable.getSelectedRow();
        int ClientSelectedRow = ManagerClientTable.getSelectedRow();
        ExitMsg ex  = service.saveClientMaster(
                DialogAction,
                NameClientMasterTextField.getText(),
                PhoneTextField.getText(),
                AddressTextField.getText(),
                (TypeComboBox.getSelectedIndex() + 1),
                Master,
                MasterSelectedRow,
                ClientSelectedRow,
                new BuisnessService.CallBackSaveClientMaster(){
                    
                    @Override
                    public void addMaster(Master mr) {
                        ((javax.swing.table.DefaultTableModel)ManagerMasterTable.getModel()).addRow(
                        new Object[]{
                            mr.getName(),
                            mr.getPhoneNumber()
                        });
                    }
                    
                    @Override
                    public void editMaster(Master mr) {
                        javax.swing.table.DefaultTableModel m = 
                            (javax.swing.table.DefaultTableModel)ManagerMasterTable.getModel();
                        m.setValueAt(mr.getName(), MasterSelectedRow, 0);
                        m.setValueAt(mr.getPhoneNumber(), MasterSelectedRow, 1);
                    }
                    
                    @Override
                    public void addClient(Client cl) {
                        ((javax.swing.table.DefaultTableModel)ManagerClientTable.getModel()).addRow(
                            new Object[]{
                                cl.getName(),
                                cl.getPhoneNumber(),
                                cl.getAddres(),
                                R.Client.ClientTypeName(cl.getType())
                            }
                        );
                    }
                    
                    @Override
                    public void editClient(Client cl) {
                        javax.swing.table.DefaultTableModel m = 
                            (javax.swing.table.DefaultTableModel)ManagerClientTable.getModel();
                        m.setValueAt(cl.getName(), ClientSelectedRow, 0);
                        m.setValueAt(cl.getPhoneNumber(), ClientSelectedRow, 1);
                        m.setValueAt(cl.getAddres(), ClientSelectedRow, 2);
                        m.setValueAt(R.Client.ClientTypeName(cl.getType()), ClientSelectedRow, 3);
                    }
                    
                }
        );
        if(ex.getCode() == ExitMsg.SUCCESS){
            UPDATED = true;
            AddEditClientMasterDialog.setVisible(false);
        }else{
            if(ex.getCode() != ExitMsg.INPUT_ERROR){
                AddEditClientMasterDialog.setVisible(false);
            }
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_OkClientMasterDialogButtonActionPerformed

    private void CanselClientMasterDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CanselClientMasterDialogButtonActionPerformed
        AddEditClientMasterDialog.setVisible(false);
    }//GEN-LAST:event_CanselClientMasterDialogButtonActionPerformed

    private void OkEstimateDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkEstimateDialogButtonActionPerformed
        //создать или редактировать смету
        int SelectedRow = ManagerOrderTable.getSelectedRow();
        int SelectedRowEstimate = ManagerEstimateTable.getSelectedRow();
        ExitMsg ex  = service.saveEstimate(
                DialogAction,
                SelectedRow,(EstimateTypeComboBox.getSelectedIndex() + 1),
                SelectedRowEstimate,
                new BuisnessService.CallBackSaveEstimateDialog(){

                    @Override
                    public void addAction(Estimate es) {
                        ((javax.swing.table.DefaultTableModel)ManagerEstimateTable.getModel())
                            .addRow(new Object[]{
                                R.Estimate.TypeName(es.getType()), 
                                R.Estimate.StatusName(es.isPaid(), es.isFinish()),
                                String.valueOf(es.getCoast())
                            });
                        ((javax.swing.table.DefaultTableModel)ManagerOrderTable.getModel())
                                .setValueAt(String.valueOf(service.getManagerOrderAt(SelectedRow).getCurrentCoast()), SelectedRow, 2);
                    }

                    @Override
                    public void editAction(Estimate es) {
                        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel)ManagerEstimateTable.getModel();
                        m.setValueAt(R.Estimate.TypeName(es.getType()), SelectedRowEstimate, 0);
                        m.setValueAt(R.Estimate.StatusName(es.isPaid(), es.isFinish()), SelectedRowEstimate, 1);
                        m.setValueAt(String.valueOf(es.getCoast()), SelectedRowEstimate, 2);
                        ((javax.swing.table.DefaultTableModel)ManagerOrderTable.getModel())
                            .setValueAt(String.valueOf(service.getManagerOrderAt(SelectedRow).getCurrentCoast()), SelectedRow, 2);
                    }

                }
        );
        if(ex.getCode() == ExitMsg.SUCCESS){
            UPDATED = true;
            AddEditEstimateDialog.setVisible(false);
        }else{
            if(ex.getCode() != ExitMsg.INPUT_ERROR){
                AddEditEstimateDialog.setVisible(false);
            }
            ErrorMassage(ex.getMassage());
        }
        
    }//GEN-LAST:event_OkEstimateDialogButtonActionPerformed

    private void CanselEstimateDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CanselEstimateDialogButtonActionPerformed
        AddEditEstimateDialog.setVisible(false);
    }//GEN-LAST:event_CanselEstimateDialogButtonActionPerformed

    private void OkAddEstimateWorkDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkAddEstimateWorkDialogButtonActionPerformed
        int SelectedIndex = WorkComboBox.getSelectedIndex();
        ExitMsg ex = service.addEstimate(SelectedIndex);
        if(ex.getCode() == ExitMsg.SUCCESS){
            ((javax.swing.table.DefaultTableModel)EstimateWorksDialogTable.getModel())
                    .addRow(new Object[]{
                        service.getManagerWorkAt(SelectedIndex).getDescription(),
                        String.valueOf(service.getManagerWorkAt(SelectedIndex).getServiceCoast()),
                        String.valueOf(service.getManagerWorkAt(SelectedIndex).CoastCalculation())
                    }
            );
            AddEstimateWorkDialog.setVisible(false);
        }
    }//GEN-LAST:event_OkAddEstimateWorkDialogButtonActionPerformed

    private void CanselAddDeleteWorkDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CanselAddDeleteWorkDialogButtonActionPerformed
        AddEstimateWorkDialog.setVisible(false);
    }//GEN-LAST:event_CanselAddDeleteWorkDialogButtonActionPerformed

    private void OkOrderDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkOrderDialogButtonActionPerformed
        
        ExitMsg ex = service.saveOrder(ManagerClientComboBox.getSelectedIndex(), DateCreateTextField.getText().trim(),(Order ord) -> {
            javax.swing.table.DefaultTableModel ManagerOrderTableModel=
                    (javax.swing.table.DefaultTableModel) ManagerOrderTable.getModel();
            ManagerOrderTableModel.addRow(new Object[]{
                String.valueOf(ord.getNumber()),
                R.Order.StatusName(ord.getStatus()),
                String.valueOf(ord.getCurrentCoast()),
                service.getDateFormat().format(ord.getCreate()),
                service.getDateFormat().format(ord.getLastUpdate()),
                ""});
        });
        
        if(ex.getCode() == ExitMsg.SUCCESS){            
            UPDATED = true;
        }else{
            ErrorMassage(ex.toString());
        }
        AddEstimateWorkDialog.setVisible(false);
        
    }//GEN-LAST:event_OkOrderDialogButtonActionPerformed

    private void CanselOrderDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CanselOrderDialogButtonActionPerformed
        AddOrderDialog.setVisible(false);
    }//GEN-LAST:event_CanselOrderDialogButtonActionPerformed

    private void NameRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NameRadioActionPerformed
        NameOrTypeRadioActionPerformed(true);
    }//GEN-LAST:event_NameRadioActionPerformed

    private void TypeRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TypeRadioActionPerformed
        NameOrTypeRadioActionPerformed(false);        
    }//GEN-LAST:event_TypeRadioActionPerformed

    private void AddEstimateDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddEstimateDialogButtonActionPerformed
        //вызов диалога для добавления работы в смету
        service.getManagerWorkList().stream().forEach((ManagerWorkList1) -> {
            WorkComboBox.addItem(ManagerWorkList1);
        });
        AddEstimateWorkDialog.setVisible(true);
    }//GEN-LAST:event_AddEstimateDialogButtonActionPerformed

    private void DeleteEstimateDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteEstimateDialogButtonActionPerformed
        //удаления работы из сметы
        int SelectedRow = EstimateWorksDialogTable.getSelectedRow();
        ExitMsg ex = service.deleteTempEstimateWork(SelectedRow);
        if(ex.getCode() == ExitMsg.SUCCESS){
            ((javax.swing.table.DefaultTableModel)EstimateWorksDialogTable.getModel()).removeRow(SelectedRow);
        }else{    
            ErrorMassage(ex.getMassage());
        }
    }//GEN-LAST:event_DeleteEstimateDialogButtonActionPerformed
 
    /**
     * @param flag true - Name, false - type
    */
    private void NameOrTypeRadioActionPerformed(boolean flag){
        int SelectedRow;
        int SelectedIndex = TypeNameComboBox.getSelectedIndex();
            switch(DialogAction){
                case R.Dialog.AddAction:
                    TypeNameComboBox.removeAllItems();
                    service.getManagerResourceList().stream().forEach((e) -> {
                            TypeNameComboBox.addItem((flag) ? e.getName() : String.valueOf(e.getType()));
                    });
                    break;
                case R.Dialog.EditAction:
                    TypeNameComboBox.removeAllItems();
                    SelectedRow = ManagerWorkTable.getSelectedRow();
                    if(service.getManagerWorkAt(SelectedRow).getResources() != null){
                        service.getManagerWorkAt(SelectedRow).getResources().stream().forEach((e) -> {
                            TypeNameComboBox.addItem((flag) ? e.getName() : String.valueOf(e.getType()));
                        });
                    }
                    break;
                case R.Dialog.SendAction:
                    TypeNameComboBox.removeAllItems();
                    if(service.getManagerResourceList() != null){
                        service.getManagerResourceList().stream().forEach((e) -> {
                            TypeNameComboBox.addItem((flag) ? e.getName() : String.valueOf(e.getType()));
                        });
                    }
                    break;
                case R.Dialog.TakeAction:
                    TypeNameComboBox.removeAllItems();
                    SelectedRow = ManagerStorageTable.getSelectedRow();
                    if(service.getManagerStorageAt(SelectedRow).getResources() != null){
                        service.getManagerStorageAt(SelectedRow).getResources().stream().forEach((e) -> {
                            TypeNameComboBox.addItem((flag) ? e.getName() : String.valueOf(e.getType()));
                        });
                    }
                    break;
            }
        TypeNameComboBox.setSelectedIndex(SelectedIndex);
    }
    
    private void RoleViewWindowClosing(int RoleType){
        //Закрытие окна роли (менеджера,прораба,клиента)
        switch(RoleType){
            case R.ModelType.ManagerModel:
                ManagerView.setVisible(false);
                //TODO не нужно добвлений
                //т.к. для менеджера происходит запись сразу или по кнопке обновления
                //таже стот задуматся о том нужно ли это для других ролей
                break;
            case R.ModelType.ClientModel:
                ClientView.setVisible(false);               
                ExitMsg ex = service.saveMasterEstimateArray();
                if(ex.getCode() == ExitMsg.DATABASE_SAVE_ERROR){
                    ErrorMassage(ex.toString());
                }            
                break;
            case R.ModelType.MasterModel:
                MasterView.setVisible(false);
                //
                ExitMsg ex2 = service.saveMasterEstimateArray();
                if(ex2.getCode() == ExitMsg.DATABASE_SAVE_ERROR){  
                    ErrorMassage(ex2.toString());
                }
                break;
        }
        
        //Закрытие текущего соединения
        service.closeDatabaseConnection();
        
        if(service.isAskDialog()){//покывает диалог с вопросом
            AskDialog.setVisible(true);
        }else{
            if(service.isExitOperation()){
                //выход
                this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));//закрыть приложение
            }else{
                //переход к окну с аутентификацикей
                setVisible(true);
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddClientButton;
    private javax.swing.JDialog AddEditClientMasterDialog;
    private javax.swing.JDialog AddEditEstimateDialog;
    private javax.swing.JDialog AddEditResourceDialog;
    private javax.swing.JDialog AddEditStorageDialog;
    private javax.swing.JDialog AddEditWorkDialog;
    private javax.swing.JButton AddEstimateButton;
    private javax.swing.JButton AddEstimateDialogButton;
    private javax.swing.JDialog AddEstimateWorkDialog;
    private javax.swing.JButton AddMasterButton;
    private javax.swing.JButton AddOrderButton;
    private javax.swing.JDialog AddOrderDialog;
    private javax.swing.JButton AddResourceButton;
    private javax.swing.JButton AddStorageButton;
    private javax.swing.JButton AddWorkButton;
    private javax.swing.JButton AddWorkResourceButton;
    private javax.swing.JTextField AddressTextField;
    private javax.swing.JTextField AmountTextField;
    private javax.swing.JCheckBox AskCheckBox;
    private javax.swing.JDialog AskDialog;
    private javax.swing.JButton CanselAddDeleteWorkDialogButton;
    private javax.swing.JButton CanselButton;
    private javax.swing.JButton CanselClientMasterDialogButton;
    private javax.swing.JButton CanselEstimateDialogButton;
    private javax.swing.JButton CanselOrderDialogButton;
    private javax.swing.JButton CanselResourceDialogButton;
    private javax.swing.JButton CanselStorageDialogButton;
    private javax.swing.JButton CanselTakeSendResourceDialogButton;
    private javax.swing.JButton CanselWorkDialogButton;
    private javax.swing.JButton ChangeStatusOrderButton;
    private javax.swing.JButton ClientAcceptWorkButton;
    private javax.swing.JTextField ClientAddressTextField;
    private javax.swing.JButton ClientExitButton;
    private javax.swing.JTextField ClientNameTextField;
    private javax.swing.JButton ClientOrderDetailButton;
    private javax.swing.JPanel ClientOrderPanel;
    private javax.swing.JTable ClientOrderTable;
    private javax.swing.JButton ClientPayButton;
    private javax.swing.JTextField ClientPhoneTextField;
    private javax.swing.JPanel ClientProfilePanel;
    private javax.swing.JLabel ClientTypeLabel;
    private javax.swing.JFrame ClientView;
    private javax.swing.JTextField DateCreateTextField;
    private javax.swing.JButton DeleteClientButton;
    private javax.swing.JButton DeleteEstimateButton;
    private javax.swing.JButton DeleteEstimateDialogButton;
    private javax.swing.JButton DeleteMasterButton;
    private javax.swing.JButton DeleteOrderButton;
    private javax.swing.JButton DeleteResourceButton;
    private javax.swing.JButton DeleteStorageButton;
    private javax.swing.JButton DeleteWorkButton;
    private javax.swing.JButton DeleteWorkResourceButton;
    private javax.swing.JTextField DescriptionTextField;
    private javax.swing.JButton EditClientButton;
    private javax.swing.JButton EditEstimateButton;
    private javax.swing.JButton EditMasterButton;
    private javax.swing.JButton EditResourceButton;
    private javax.swing.JButton EditStorageButton;
    private javax.swing.JButton EditWorkButton;
    private javax.swing.JButton EditWorkResourceButton;
    private javax.swing.JComboBox EstimateTypeComboBox;
    private javax.swing.JTable EstimateWorksDialogTable;
    private javax.swing.JButton ExitButton;
    private javax.swing.JTextField LoginTextField;
    private javax.swing.JTextField ManagerAddressTextField;
    private javax.swing.JComboBox ManagerClientComboBox;
    private javax.swing.JTable ManagerClientTable;
    private javax.swing.JPanel ManagerClientsPanel;
    private javax.swing.JTable ManagerEstimateTable;
    private javax.swing.JButton ManagerExitButton;
    private javax.swing.JTable ManagerMasterTable;
    private javax.swing.JPanel ManagerMastersPanel;
    private javax.swing.JTextField ManagerNameTextField;
    private javax.swing.JPanel ManagerOrderPanel;
    private javax.swing.JTable ManagerOrderTable;
    private javax.swing.JTextField ManagerPhoneTextField;
    private javax.swing.JPanel ManagerProfilePanel;
    private javax.swing.JPanel ManagerResourcePanel;
    private javax.swing.JTable ManagerResourceTable;
    private javax.swing.JPanel ManagerStoragePanel;
    private javax.swing.JTable ManagerStorageResourceTable;
    private javax.swing.JTable ManagerStorageTable;
    private javax.swing.JButton ManagerUpdateButton;
    private javax.swing.JFrame ManagerView;
    private javax.swing.JTable ManagerWorkResourceTable;
    private javax.swing.JTable ManagerWorkTable;
    private javax.swing.JPanel ManagerWorksPanel;
    private javax.swing.JButton MasterExitButton;
    private javax.swing.JButton MasterFinishWorkButton;
    private javax.swing.JTextField MasterNameTextField;
    private javax.swing.JTextField MasterPhoneTextField;
    private javax.swing.JPanel MasterProfilePanel;
    private javax.swing.JFrame MasterView;
    private javax.swing.JPanel MasterWorksPanel;
    private javax.swing.JTable MasterWorksTable;
    private javax.swing.JTextField NameClientMasterTextField;
    private javax.swing.JRadioButton NameRadio;
    private javax.swing.JTextField NameTextField;
    private javax.swing.JButton NotExitButton;
    private javax.swing.JButton OkAddEstimateWorkDialogButton;
    private javax.swing.JButton OkButton;
    private javax.swing.JButton OkClientMasterDialogButton;
    private javax.swing.JButton OkEstimateDialogButton;
    private javax.swing.JButton OkOrderDialogButton;
    private javax.swing.JButton OkResourceDialogButton;
    private javax.swing.JButton OkStorageDialogButton;
    private javax.swing.JButton OkTakeSendResourceDialogButton;
    private javax.swing.JButton OkViewButton;
    private javax.swing.JButton OkWorkDialogButton;
    private javax.swing.JLabel OrderClientNameLabel;
    private javax.swing.JLabel OrderCreateDateLabel;
    private javax.swing.JLabel OrderCurrentCoastLabel;
    private javax.swing.JLabel OrderEndDateLabel;
    private javax.swing.JTable OrderEstimateTable;
    private javax.swing.JLabel OrderLastUpdateDateLabel;
    private javax.swing.JLabel OrderManagerNameLabel;
    private javax.swing.JLabel OrderNumberLabel;
    private javax.swing.JTable OrderResourceTable;
    private javax.swing.JLabel OrderStatusLabel;
    private javax.swing.JLabel OrderTotalCoastLabel;
    private javax.swing.JTable OrderWorkTable;
    private javax.swing.JPasswordField PasswordFieldText;
    private javax.swing.JTextField PhoneTextField;
    private javax.swing.JTextField ResourceCoastTextField;
    private javax.swing.JComboBox RoleComboBox;
    private javax.swing.JButton SaveClientProfileButton;
    private javax.swing.JButton SaveManagerProfileButton;
    private javax.swing.JButton SaveMasterProfileButton;
    private javax.swing.JButton SaveWorkMasterButton;
    private javax.swing.JButton SendResourceToStorageButton;
    private javax.swing.JTextField StorageLocationTextField;
    private javax.swing.JButton TakeResourceFromStorageButton;
    private javax.swing.JDialog TakeSendResourceDialog;
    private javax.swing.JComboBox TypeComboBox;
    private javax.swing.JComboBox TypeNameComboBox;
    private javax.swing.ButtonGroup TypeNameRadioGroup;
    private javax.swing.JRadioButton TypeRadio;
    private javax.swing.JTextField TypeTextField;
    private javax.swing.JDialog ViewOrderDialog;
    private javax.swing.JTextField WorkCoastTextField;
    private javax.swing.JComboBox WorkComboBox;
    private javax.swing.JLabel WorkMasterSumLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    // End of variables declaration//GEN-END:variables
}
