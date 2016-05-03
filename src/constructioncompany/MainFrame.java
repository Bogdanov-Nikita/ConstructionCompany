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
import businesslogic.Role;
import businesslogic.model.BehaviorModel;
import database.ClientMapper;
import database.DatabaseManager;
import database.EstimateMapper;
import database.ManagerMapper;
import database.MasterMapper;
import database.OrderMapper;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import service.Authorization;
import service.config.Config;
import service.config.ConfigDatabase;
import service.config.ConfigRole;
import service.config.ConfigXmlParser;
/**
 *
 * @author Nik
 */
public class MainFrame extends javax.swing.JFrame {

    boolean CONNECTED = false;
    int index = -1;//номер текушего пользователя в конфигурационном файле
    int CurrentId = 0;//id текущего пользователя в базе
    Config config;
    Role CurrentRole;
    BehaviorModel BusinessModel;
    DatabaseManager DBManager;
    
    ArrayList<Order> ClientOrderList;
    ArrayList<Estimate> MasterEstimateList;
    
    SimpleDateFormat DateFormat;
    
    /**
     * Creates new form NewJFrame
     */
    public MainFrame() {
        
        //TODO: 1.порписать кнопочки и прочую часть графики
        //TВыполненно 2.прописать аутентификацию и подключение к базе
        //Выполненно 3.Прописать проверку конфига.
        //Выполненно 5.прописать конфиг и добавить новые поля
        //Выполненно 6.прописать бизнес логику.
        //Выполненно 7.написать тесты.        
        //Выполненно 8.вставить диалог для запоминания перехода.
        DateFormat = new SimpleDateFormat ("dd.MM.yyyy HH:mm:ss");
        ClientOrderList = null;
        MasterEstimateList = null;
        initComponents();
        initLocation();
    }
    
    private void initLocation(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        ClientView.setLocation(dim.width/2-ClientView.getSize().width/2, dim.height/2-ClientView.getSize().height/2);
        MasterView.setLocation(dim.width/2-MasterView.getSize().width/2, dim.height/2-MasterView.getSize().height/2);
        ManagerView.setLocation(dim.width/2-ManagerView.getSize().width/2, dim.height/2-ManagerView.getSize().height/2);
        AskDialog.setLocation(dim.width/2-AskDialog.getSize().width/2, dim.height/2-AskDialog.getSize().height/2);
        ViewOrderDialog.setLocation(dim.width/2-ViewOrderDialog.getSize().width/2, dim.height/2-ViewOrderDialog.getSize().height/2);
    }
    
    private void initConfig(){
        //получение данных из конфигурационного файла
        ConfigXmlParser configInfo = new ConfigXmlParser();
        if(configInfo.OpenConfig(R.FileName.Config) == ConfigXmlParser.CONFIG_SUCCESS){  
            config = configInfo.getConfig();           
        }else{
            config = null;
        }
    }
    
    private void initAuth(){
        if(config != null){
            String role = null;
            switch(RoleComboBox.getSelectedIndex()+1){
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
            index = Authorization.ConfigRoleAuth(role, LoginTextField.getText(), new String(PasswordFieldText.getPassword()), config);
        }
    }
    
    private void initDatabase(){
        if(config != null && index >= 0){
            //Запуск и проверка коннекта до базы, незадыть вызвать диалог проверки доступа к базе в случае не доступности
            if(config.getConfigItem(index).isValid()){
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
                    }catch(SQLException ex){
                        CONNECTED = false;
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Can't connected to database", ex);
                        ErrorMassage(ex.toString());
                    }
            }else{
                ErrorMassage(R.ErrMsg.ConfigError_2);
            }
        }
    }
    
    private void initDatabaseRoleAuth(){        
        try {
            CurrentId = Authorization.DatabaseRoleAuth(config.getConfigItem(index).getRole(), DBManager);
            if(CurrentId > 0){                
                switch(RoleComboBox.getSelectedIndex()+1){
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
            }else{
                CurrentRole = null;
            }
        } catch (SQLException ex) {
            CurrentRole = null;
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            ErrorMassage(ex.toString());
        }
    }

    private void initBusinessModel(){
        BusinessModel = new BehaviorModel(RoleComboBox.getSelectedIndex()+1, CurrentRole);       
    }
    
    private void ErrorMassage(String message){
        String Title = "Ошибка";
        JOptionPane.showConfirmDialog(null, message, Title, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null);
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
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        ManagerOrderTable = new javax.swing.JTable();
        jScrollPane9 = new javax.swing.JScrollPane();
        ManagerEstimateTable = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        ManagerWorkTable = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        ManagerWorkResourceTable = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        ManagerResourceTable = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        ManagerStorageTable = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        ManagerStorageResourceTable = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        ManagerNameTextField = new javax.swing.JTextField();
        ManagerPhoneTextField = new javax.swing.JTextField();
        ManagerAddressTextField = new javax.swing.JTextField();
        SaveManagerProfileButton = new javax.swing.JButton();
        ManagerExitButton = new javax.swing.JButton();
        ClientView = new javax.swing.JFrame();
        ClientExitButton = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ClientOrderTable = new javax.swing.JTable();
        ClientPayButton = new javax.swing.JButton();
        ClientAcceptWorkButton = new javax.swing.JButton();
        ClientOrderDetailButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
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
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        MasterWorksTable = new javax.swing.JTable();
        MasterFinishWorkButton = new javax.swing.JButton();
        SaveWorkMasterButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
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
        OkButton = new javax.swing.JButton();
        CanselButton = new javax.swing.JButton();
        PasswordFieldText = new javax.swing.JPasswordField();
        RoleComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        LoginTextField = new javax.swing.JTextField();

        ManagerView.setMinimumSize(new java.awt.Dimension(700, 500));
        ManagerView.setPreferredSize(new java.awt.Dimension(700, 500));
        ManagerView.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                ManagerViewWindowClosing(evt);
            }
        });

        jTabbedPane3.setPreferredSize(new java.awt.Dimension(700, 500));

        ManagerOrderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
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
        jScrollPane5.setViewportView(ManagerOrderTable);

        ManagerEstimateTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
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
        jScrollPane9.setViewportView(ManagerEstimateTable);

        jLabel17.setText("Заказы:");

        jLabel18.setText("Сметы:");

        jButton7.setText("Редактировать");

        jButton8.setText("Добавить");

        jButton9.setText("Удалить");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
                    .addComponent(jScrollPane9)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jButton8)
                                .addGap(18, 18, 18)
                                .addComponent(jButton7)
                                .addGap(18, 18, 18)
                                .addComponent(jButton9)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jButton7)
                    .addComponent(jButton9))
                .addGap(18, 18, 18)
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane3.addTab("Заказы", jPanel5);

        ManagerWorkTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
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
        jScrollPane7.setViewportView(ManagerWorkTable);

        ManagerWorkResourceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Тип", "Описание", "Стоимость"
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
        jScrollPane8.setViewportView(ManagerWorkResourceTable);

        jButton4.setText("Редактировать");

        jButton5.setText("Добавить");

        jButton6.setText("Удалить");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4)
                        .addGap(18, 18, 18)
                        .addComponent(jButton6)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Работы", jPanel6);

        ManagerResourceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Тип", "Описание", "Стоимость"
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
        jScrollPane6.setViewportView(ManagerResourceTable);

        jButton3.setText("Редактировать");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton3)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Ресурсы", jPanel7);

        ManagerStorageTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
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
        jScrollPane3.setViewportView(ManagerStorageTable);

        ManagerStorageResourceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Название", "Количество", "Цена за шт./ед.изм."
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
        jScrollPane4.setViewportView(ManagerStorageResourceTable);

        jLabel12.setText("Склды:");

        jLabel13.setText("Ресурсы:");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Склад", jPanel8);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 675, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 371, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab("Графики", jPanel9);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 675, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 409, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab("Заказчики", jPanel11);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 675, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 409, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab("Прорабы", jPanel12);

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

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(18, 18, 18)
                        .addComponent(ManagerAddressTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(ManagerNameTextField))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(ManagerPhoneTextField))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(SaveManagerProfileButton)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(ManagerNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(ManagerPhoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(ManagerAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 230, Short.MAX_VALUE)
                .addComponent(SaveManagerProfileButton)
                .addContainerGap())
        );

        jTabbedPane3.addTab("Профиль", jPanel10);

        ManagerExitButton.setText("Выход");
        ManagerExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ManagerExitButtonActionPerformed(evt);
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
                        .addComponent(ManagerExitButton)))
                .addContainerGap())
        );
        ManagerViewLayout.setVerticalGroup(
            ManagerViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerViewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(ManagerExitButton)
                .addContainerGap())
        );

        ClientView.setMinimumSize(new java.awt.Dimension(700, 500));
        ClientView.setPreferredSize(new java.awt.Dimension(700, 500));
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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ClientOrderDetailButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ClientPayButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ClientAcceptWorkButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(ClientAcceptWorkButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ClientPayButton)
                        .addGap(18, 18, 18)
                        .addComponent(ClientOrderDetailButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Заказы", jPanel3);

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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(ClientNameTextField))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(ClientPhoneTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(ClientAddressTextField))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(ClientTypeLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(SaveClientProfileButton)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(ClientNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(ClientPhoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(ClientAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(ClientTypeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 236, Short.MAX_VALUE)
                .addComponent(SaveClientProfileButton)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Профиль", jPanel4);

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
        MasterView.setPreferredSize(new java.awt.Dimension(700, 500));
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(MasterFinishWorkButton, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                    .addComponent(SaveWorkMasterButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(MasterFinishWorkButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SaveWorkMasterButton)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Работы", jPanel1);

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(39, 39, 39)
                        .addComponent(MasterNameTextField))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(MasterPhoneTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(SaveMasterProfileButton)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(MasterNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(MasterPhoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 237, Short.MAX_VALUE)
                .addComponent(SaveMasterProfileButton)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Профиль", jPanel2);

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
                "Тип", "Описание", "Количество", "Стоимость"
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
        initConfig();
        //проверка на загрузку конфига конфиг != null
        if(config != null){
            initAuth();
            //проверка соответствия роли логиа и пароля код индекса >= 0
            if(index >= 0){
                initDatabase();
                //проверка подключения базы CONECTED = true
                if(CONNECTED){
                    initDatabaseRoleAuth();
                    //проверка для роли внутри базы.
                    if(CurrentId > 0 && CurrentRole != null){
                        initBusinessModel();                    
                        setVisible(false);
                        ConfigRole RoleInfo = config.getConfigItem(index).getRole();
                        ConfigDatabase ConfigInfo = config.getConfigItem(index).getDatabase();
                        switch(RoleComboBox.getSelectedIndex()+1){
                            case R.ModelType.ManagerModel://Менеджер
                                ManagerView.setTitle(
                                        R.RoleType.Manager + " : " + RoleInfo.getLogin() + " - " +
                                        "Connected to: "+ ConfigInfo.getHost() + " ["+ConfigInfo.getPath()+"]");
                                ManagerView.setVisible(true);
                                ManagerNameTextField.setText(CurrentRole.getName());
                                ManagerPhoneTextField.setText(CurrentRole.getPhoneNumber());
                                ManagerAddressTextField.setText(((Manager)CurrentRole).getOfficeAddress());
                                break;
                            case R.ModelType.MasterModel://Прораб
                                MasterView.setTitle(
                                        R.RoleType.Master + " : " + RoleInfo.getLogin() + " - " +
                                        "Connected to: "+ ConfigInfo.getHost() + " ["+ConfigInfo.getPath()+"]");
                                MasterView.setVisible(true);
                                MasterNameTextField.setText(CurrentRole.getName());
                                MasterPhoneTextField.setText(CurrentRole.getPhoneNumber());
                                
                                javax.swing.table.DefaultTableModel MasterWorksTableModel = 
                                        (javax.swing.table.DefaultTableModel)MasterWorksTable.getModel();                     
                                try {
                                    MasterEstimateList = new EstimateMapper().loadListbyMaster(CurrentId, DBManager);
                                    for (Estimate MasterEstimateList1 : MasterEstimateList) {
                                        Client cl = new OrderMapper().loadClientByOrderId(MasterEstimateList1.getOrderId(), DBManager);
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
                                    }
                                    MasterWorksTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
                                        if(MasterWorksTable.getSelectedRow() >= 0){
                                            int WorkIndex = MasterWorksTable.getSelectedRow();
                                            int EstimateIndex = 0;
                                            for (Estimate es : MasterEstimateList) {
                                                int size = es.getWorkList().size();
                                                if(WorkIndex >= size){
                                                    EstimateIndex++;
                                                    WorkIndex = WorkIndex - size;
                                                }else{
                                                    break;
                                                }
                                            }
                                            MasterFinishWorkButton.setEnabled(
                                                    !MasterEstimateList
                                                            .get(EstimateIndex)
                                                            .getWork(WorkIndex)
                                                            .isFinish()
                                            );
                                        }
                                    });
                                } catch (SQLException ex) {
                                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                            case R.ModelType.ClientModel://Клиент
                                ClientView.setTitle(
                                        R.RoleType.Client + " : " + RoleInfo.getLogin() + " - " +
                                        "Connected to: "+ ConfigInfo.getHost() + " ["+ConfigInfo.getPath()+"]");
                                ClientView.setVisible(true);
                                ClientNameTextField.setText(CurrentRole.getName());
                                ClientPhoneTextField.setText(CurrentRole.getPhoneNumber());
                                ClientAddressTextField.setText(((Client)CurrentRole).getAddres());
                                ClientTypeLabel.setText( R.Client.CLIENT_TYPE + " " + 
                                        R.Client.ClientTypeName(((Client)CurrentRole).getType()));
                                try {
                                    ClientOrderList = new OrderMapper().loadListbyClient(CurrentId, DBManager);
                                    javax.swing.table.DefaultTableModel ClientOrderTableModel = 
                                            (javax.swing.table.DefaultTableModel)ClientOrderTable.getModel();
                                    ClientOrderList.stream().forEach((list1) -> {
                                        ClientOrderTableModel.addRow(new Object[]{
                                            String.valueOf(list1.getNumber()),
                                            R.Order.StatusName(list1.getStatus()), 
                                            String.valueOf(list1.getCurrentCoast()), 
                                            DateFormat.format(list1.getCreate()), 
                                            DateFormat.format(list1.getLastUpdate()),
                                            (list1.getEnd()!= null) ? DateFormat.format(list1.getEnd()):""});
                                        list1.CoastCalculation();
                                    });                                    
                                    ClientOrderTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
                                        if(ClientOrderTable.getSelectedRow() >= 0){
                                            switch(ClientOrderList.get(ClientOrderTable.getSelectedRow()).getStatus()){
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
                                } catch (SQLException ex) {
                                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                        }
                    }else{//отсутствие роли в базе данных.
                        ErrorMassage(R.ErrMsg.AuthDatabaseError);
                    }
                }else{//ошибка подключения к базе данных
                    ErrorMassage(R.ErrMsg.DatabaseError);
                }
            }else{//ошибка соответствия логина и пароля
                switch(index){
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
            config.getConfigItem(index).getSettings().setAskDialog(false);
            config.getConfigItem(index).getSettings().setExitOperation(false);
        }
        //Сохранение конфигурации
        config.writetoFile(R.FileName.Config);
        AskDialog.setVisible(false);
        setVisible(true);
    }//GEN-LAST:event_NotExitButtonActionPerformed

    private void ExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitButtonActionPerformed
        // выход
        if(AskCheckBox.isSelected()){
            config.getConfigItem(index).getSettings().setAskDialog(false);
            config.getConfigItem(index).getSettings().setExitOperation(true);
        }
        AskDialog.setVisible(false);
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));//закрыть приложение
    }//GEN-LAST:event_ExitButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //Сохранение конфигурации
        if(config != null){
            config.writetoFile(R.FileName.Config);
        }
    }//GEN-LAST:event_formWindowClosing

    private void AskDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_AskDialogWindowClosing
        AskDialog.setVisible(false);
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));//закрыть приложение
    }//GEN-LAST:event_AskDialogWindowClosing

    private void SaveManagerProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveManagerProfileButtonActionPerformed
        Manager role = (Manager)CurrentRole;
        if(DBManager != null && CurrentRole != null){
            if(!ManagerNameTextField.getText().trim().equals("")){
                if(!ManagerPhoneTextField.getText().trim().equals("")){
                    if(!ManagerAddressTextField.getText().trim().equals("")){
                        role.setName(ManagerNameTextField.getText().trim());
                        role.setPhoneNumber(ManagerPhoneTextField.getText().trim());
                        role.setOfficeAddress(ManagerAddressTextField.getText().trim());
                        try {
                            new ManagerMapper().save(role,DBManager);
                        } catch (SQLException ex) {                
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            ErrorMassage(ex.toString());
                        }
                    }else{ErrorMassage(R.ErrMsg.AddressError);}
                }else{ErrorMassage(R.ErrMsg.PhoneError);}
            }else{ErrorMassage(R.ErrMsg.NaneError);}
        }
    }//GEN-LAST:event_SaveManagerProfileButtonActionPerformed

    private void SaveClientProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveClientProfileButtonActionPerformed
        Client role = (Client)CurrentRole;
        if(DBManager != null && CurrentRole != null){
            if(!ClientNameTextField.getText().trim().equals("")){
                if(!ClientPhoneTextField.getText().trim().equals("")){
                    if(!ClientAddressTextField.getText().trim().equals("")){
                        role.setName(ClientNameTextField.getText().trim());
                        role.setPhoneNumber(ClientPhoneTextField.getText().trim());
                        role.setAddres(ClientAddressTextField.getText().trim());
                        try {
                            new ClientMapper().save(role,DBManager);
                        } catch (SQLException ex) {                
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            ErrorMassage(ex.toString());
                        }
                    }else{ErrorMassage(R.ErrMsg.AddressError);}
                }else{ErrorMassage(R.ErrMsg.PhoneError);}
            }else{ErrorMassage(R.ErrMsg.NaneError);}
        }
    }//GEN-LAST:event_SaveClientProfileButtonActionPerformed

    private void SaveMasterProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveMasterProfileButtonActionPerformed
        Master role = (Master)CurrentRole;
        if(DBManager != null && CurrentRole != null){
            if(!MasterNameTextField.getText().trim().equals("")){
                if(!MasterPhoneTextField.getText().trim().equals("")){            
                    role.setName(MasterNameTextField.getText().trim());
                    role.setPhoneNumber(MasterPhoneTextField.getText().trim());
                    try {
                        new MasterMapper().save(role,DBManager);
                    } catch (SQLException ex) {                
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        ErrorMassage(ex.toString());
                    }
                }else{ErrorMassage(R.ErrMsg.AddressError);}
            }else{ErrorMassage(R.ErrMsg.PhoneError);}
        }else{ErrorMassage(R.ErrMsg.NaneError);}
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
            if(ClientOrderList != null){
                if(ClientOrderList.get(SelectedRow).getStatus() == Order.WAITING_ACKNOWLEDGMENT_TAKE){
                    ClientOrderList.get(SelectedRow).setStatus(Order.WAITING_PAY);
                    javax.swing.table.DefaultTableModel ClientOrderTableModel = 
                                            (javax.swing.table.DefaultTableModel)ClientOrderTable.getModel();
                    ClientOrderTableModel.setValueAt(R.Order.StatusName(ClientOrderList.get(SelectedRow).getStatus()), SelectedRow, 1);
                }
            }
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_ClientAcceptWorkButtonActionPerformed

    private void ClientPayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClientPayButtonActionPerformed
        int SelectedRow = ClientOrderTable.getSelectedRow();
        if(SelectedRow >= 0){
            if(ClientOrderList != null){
                if(ClientOrderList.get(SelectedRow).getStatus() == Order.WAITING_PAY){
                    //Диалог с запросом числа.
                    try{
                        double pay = Double.parseDouble(JOptionPane.showInputDialog(R.Dialog.ClientPayInputMsg));
                        if(pay < 0){
                            ErrorMassage(R.ErrMsg.InputPayError_2);
                        }else{                            
                            if(((Client)CurrentRole).PayEstimatePart(ClientOrderList.get(SelectedRow),pay)){
                                String Title = "Оплата";
                                JOptionPane.showConfirmDialog(null, 
                                        R.Dialog.ClienPayAccept, 
                                        Title, 
                                        JOptionPane.DEFAULT_OPTION, 
                                        JOptionPane.PLAIN_MESSAGE, null);
                                javax.swing.table.DefaultTableModel ClientOrderTableModel = 
                                            (javax.swing.table.DefaultTableModel)ClientOrderTable.getModel();
                                ClientOrderTableModel.setValueAt(String.valueOf(ClientOrderList.get(SelectedRow).getCurrentCoast()), SelectedRow, 2);
                            }else{
                                ErrorMassage(R.ErrMsg.InputPayError_3);
                            }
                        } 
                    }catch(NumberFormatException ex){
                        ErrorMassage(R.ErrMsg.InputPayError_1);
                    }
                }
            }
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_ClientPayButtonActionPerformed

    private void ClientOrderDetailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClientOrderDetailButtonActionPerformed
        int SelectedRow = ClientOrderTable.getSelectedRow();
        if(SelectedRow >= 0){
            OrderNumberLabel.setText(R.Dialog.Order + " №" + 
                    String.valueOf(ClientOrderList.get(SelectedRow).getNumber()));
            OrderStatusLabel.setText(R.Dialog.Status + " " +
                    R.Order.StatusName(ClientOrderList.get(SelectedRow).getStatus()));
            OrderClientNameLabel.setText(R.Dialog.Client + " " + 
                    CurrentRole.getName());
            OrderTotalCoastLabel.setText(R.Dialog.TotalCoast + " " +
                    String.valueOf(ClientOrderList.get(SelectedRow).getTotalCoast()));
            OrderCurrentCoastLabel.setText(R.Dialog.CurrentCoast + " " +
                    String.valueOf(ClientOrderList.get(SelectedRow).getCurrentCoast()));
            OrderCreateDateLabel.setText(R.Dialog.Create + " " + 
                    DateFormat.format(ClientOrderList.get(SelectedRow).getCreate()));
            OrderLastUpdateDateLabel.setText(R.Dialog.LastUpdate + " " + 
                    DateFormat.format(ClientOrderList.get(SelectedRow).getLastUpdate()));            
            OrderEndDateLabel.setText(
                    R.Dialog.End + " " + 
                    ((ClientOrderList.get(SelectedRow).getEnd() != null) ? 
                    DateFormat.format(ClientOrderList.get(SelectedRow).getEnd()):" "));
            
            javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel)OrderEstimateTable.getModel();
            while(m.getRowCount() > 0){m.removeRow(0);}
            ClientOrderList.get(SelectedRow).getEstimateList().stream().forEach((Elist1) -> {
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
                    ClientOrderList.get(SelectedRow).getEstimate(OrderEstimateTable.getSelectedRow()).getWorkList().stream().forEach((e) -> {
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
                    ClientOrderList
                            .get(SelectedRow)
                            .getEstimate(OrderEstimateTable.getSelectedRow())
                            .getWork(OrderWorkTable.getSelectedRow())
                            .getResources().stream().forEach((res) -> {
                        resourceModel.addRow(new Object[]{
                            String.valueOf(res.getType()),
                            res.getName(),
                            String.valueOf(res.getCoast()),
                            String.valueOf(res.getAmount())
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
            for (Estimate es : MasterEstimateList) {
                int size = es.getWorkList().size();
                if(WorkIndex >= size){
                    EstimateIndex++;
                    WorkIndex = WorkIndex - size;
                }else{
                    break;
                }
            }
            MasterEstimateList.get(EstimateIndex).getWork(WorkIndex).setFinish(true);
            javax.swing.table.DefaultTableModel MasterWorksTableModel = 
                                        (javax.swing.table.DefaultTableModel)MasterWorksTable.getModel();
            MasterWorksTableModel.setValueAt(R.Work.StatusName(MasterEstimateList.get(EstimateIndex).getWork(WorkIndex).isFinish()),SelectedRow,1);
        }else{
            ErrorMassage(R.ErrMsg.CollumSelectionError);
        }
    }//GEN-LAST:event_MasterFinishWorkButtonActionPerformed

    private void RoleViewWindowClosing(int RoleType){
        //TODO сохранить значения в базу
        //Закрытие окна роли (менеджера,прораба,клиента)
        switch(RoleType){
            case R.ModelType.ManagerModel:
                ManagerView.setVisible(false);
                break;
            case R.ModelType.ClientModel:
                ClientView.setVisible(false);
                if(ClientOrderList != null){                    
                    try {
                        new OrderMapper().saveArray(ClientOrderList, DBManager);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        ErrorMassage(ex.toString());
                    }
                }            
                break;
            case R.ModelType.MasterModel:
                MasterView.setVisible(false);
                if(MasterEstimateList != null){  
                    try {
                        new EstimateMapper().saveArray(MasterEstimateList, DBManager);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        ErrorMassage(ex.toString());
                    }                    
                }
                break;
        }
        
        //Закрытие текущего соединения
        try {
            DBManager.closeConnection();
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Can't close database connection", ex);
        }
        DBManager.close();
        
        if(config.getConfigItem(index).getSettings().isAskDialog()){//покывает диалог с вопросом
            AskDialog.setVisible(true);
        }else{
            if(config.getConfigItem(index).getSettings().isExitOperation()){
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
    private javax.swing.JCheckBox AskCheckBox;
    private javax.swing.JDialog AskDialog;
    private javax.swing.JButton CanselButton;
    private javax.swing.JButton ClientAcceptWorkButton;
    private javax.swing.JTextField ClientAddressTextField;
    private javax.swing.JButton ClientExitButton;
    private javax.swing.JTextField ClientNameTextField;
    private javax.swing.JButton ClientOrderDetailButton;
    private javax.swing.JTable ClientOrderTable;
    private javax.swing.JButton ClientPayButton;
    private javax.swing.JTextField ClientPhoneTextField;
    private javax.swing.JLabel ClientTypeLabel;
    private javax.swing.JFrame ClientView;
    private javax.swing.JButton ExitButton;
    private javax.swing.JTextField LoginTextField;
    private javax.swing.JTextField ManagerAddressTextField;
    private javax.swing.JTable ManagerEstimateTable;
    private javax.swing.JButton ManagerExitButton;
    private javax.swing.JTextField ManagerNameTextField;
    private javax.swing.JTable ManagerOrderTable;
    private javax.swing.JTextField ManagerPhoneTextField;
    private javax.swing.JTable ManagerResourceTable;
    private javax.swing.JTable ManagerStorageResourceTable;
    private javax.swing.JTable ManagerStorageTable;
    private javax.swing.JFrame ManagerView;
    private javax.swing.JTable ManagerWorkResourceTable;
    private javax.swing.JTable ManagerWorkTable;
    private javax.swing.JButton MasterExitButton;
    private javax.swing.JButton MasterFinishWorkButton;
    private javax.swing.JTextField MasterNameTextField;
    private javax.swing.JTextField MasterPhoneTextField;
    private javax.swing.JFrame MasterView;
    private javax.swing.JTable MasterWorksTable;
    private javax.swing.JButton NotExitButton;
    private javax.swing.JButton OkButton;
    private javax.swing.JButton OkViewButton;
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
    private javax.swing.JComboBox RoleComboBox;
    private javax.swing.JButton SaveClientProfileButton;
    private javax.swing.JButton SaveManagerProfileButton;
    private javax.swing.JButton SaveMasterProfileButton;
    private javax.swing.JButton SaveWorkMasterButton;
    private javax.swing.JDialog ViewOrderDialog;
    private javax.swing.JLabel WorkMasterSumLabel;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
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
