/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package constructioncompany;
import Resources.R;
import businesslogic.Role;
import businesslogic.model.BehaviorModel;
import database.ClientMapper;
import database.DatabaseManager;
import database.ManagerMapper;
import database.MasterMapper;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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
    
    /**
     * Creates new form NewJFrame
     */
    public MainFrame() {
        //TODO: 1.порписать кнопочки и прочую часть графики
        //TВыполненно 2.прописать аутентификацию и подключение к базе
        //Выполненно 3.Прописать проверку конфига.        
        //TODO: 4.написать графическое взаимодействие "адаптеры"
        //Выполненно 5.прописать конфиг и добавить новые поля
        //Выполненно 6.прописать бизнес логику.
        //Выполненно 7.написать тесты.        
        //Выполненно 8.вставить диалог для запоминания перехода.
        initComponents();
        initLocation();
    }
    
    private void initLocation(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        ClientView.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        MasterView.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        ManagerView.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        AskDialog.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
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
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        ClientView = new javax.swing.JFrame();
        jCheckBox1 = new javax.swing.JCheckBox();
        MasterView = new javax.swing.JFrame();
        jButton1 = new javax.swing.JButton();
        AskDialog = new javax.swing.JDialog();
        jLabel4 = new javax.swing.JLabel();
        NotExitButton = new javax.swing.JButton();
        ExitButton = new javax.swing.JButton();
        AskCheckBox = new javax.swing.JCheckBox();
        OkButton = new javax.swing.JButton();
        CanselButton = new javax.swing.JButton();
        PasswordFieldText = new javax.swing.JPasswordField();
        RoleComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        LoginTextField = new javax.swing.JTextField();

        ManagerView.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                ManagerViewWindowClosing(evt);
            }
        });

        jRadioButton1.setText("jRadioButton1");

        jRadioButton2.setText("jRadioButton2");

        javax.swing.GroupLayout ManagerViewLayout = new javax.swing.GroupLayout(ManagerView.getContentPane());
        ManagerView.getContentPane().setLayout(ManagerViewLayout);
        ManagerViewLayout.setHorizontalGroup(
            ManagerViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerViewLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(ManagerViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton1))
                .addContainerGap(251, Short.MAX_VALUE))
        );
        ManagerViewLayout.setVerticalGroup(
            ManagerViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManagerViewLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addContainerGap(184, Short.MAX_VALUE))
        );

        ClientView.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                ClientViewWindowClosing(evt);
            }
        });

        jCheckBox1.setText("jCheckBox1");

        javax.swing.GroupLayout ClientViewLayout = new javax.swing.GroupLayout(ClientView.getContentPane());
        ClientView.getContentPane().setLayout(ClientViewLayout);
        ClientViewLayout.setHorizontalGroup(
            ClientViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ClientViewLayout.createSequentialGroup()
                .addGap(140, 140, 140)
                .addComponent(jCheckBox1)
                .addContainerGap(179, Short.MAX_VALUE))
        );
        ClientViewLayout.setVerticalGroup(
            ClientViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ClientViewLayout.createSequentialGroup()
                .addGap(99, 99, 99)
                .addComponent(jCheckBox1)
                .addContainerGap(178, Short.MAX_VALUE))
        );

        MasterView.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                MasterViewWindowClosing(evt);
            }
        });

        jButton1.setText("jButton1");

        javax.swing.GroupLayout MasterViewLayout = new javax.swing.GroupLayout(MasterView.getContentPane());
        MasterView.getContentPane().setLayout(MasterViewLayout);
        MasterViewLayout.setHorizontalGroup(
            MasterViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MasterViewLayout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(jButton1)
                .addContainerGap(240, Short.MAX_VALUE))
        );
        MasterViewLayout.setVerticalGroup(
            MasterViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MasterViewLayout.createSequentialGroup()
                .addContainerGap(257, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(20, 20, 20))
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
                                break;
                            case R.ModelType.MasterModel://Прораб
                                MasterView.setTitle(
                                        R.RoleType.Master + " : " + RoleInfo.getLogin() + " - " +
                                        "Connected to: "+ ConfigInfo.getHost() + " ["+ConfigInfo.getPath()+"]");
                                MasterView.setVisible(true);
                                break;
                            case R.ModelType.ClientModel://Клиент
                                ClientView.setTitle(
                                        R.RoleType.Client + " : " + RoleInfo.getLogin() + " - " +
                                        "Connected to: "+ ConfigInfo.getHost() + " ["+ConfigInfo.getPath()+"]");
                                ClientView.setVisible(true);
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
        RoleViewWindowClosing(evt,R.ModelType.ManagerModel);
    }//GEN-LAST:event_ManagerViewWindowClosing

    private void RoleComboBoxMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_RoleComboBoxMouseWheelMoved
        //Прокрутка вариантов мышью
        int i = RoleComboBox.getSelectedIndex() + evt.getWheelRotation();        
        if(i > -1 && i < RoleComboBox.getItemCount()){
            RoleComboBox.setSelectedIndex(i);
        }
    }//GEN-LAST:event_RoleComboBoxMouseWheelMoved

    private void ClientViewWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_ClientViewWindowClosing
        RoleViewWindowClosing(evt,R.ModelType.ClientModel);
    }//GEN-LAST:event_ClientViewWindowClosing

    private void MasterViewWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_MasterViewWindowClosing
        RoleViewWindowClosing(evt,R.ModelType.MasterModel);
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
        config.writetoFile(R.FileName.Config);
    }//GEN-LAST:event_formWindowClosing

    private void AskDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_AskDialogWindowClosing
        AskDialog.setVisible(false);
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));//закрыть приложение
    }//GEN-LAST:event_AskDialogWindowClosing

    private void RoleViewWindowClosing(java.awt.event.WindowEvent evt,int RoleType){
        //Закрытие текущего соединения
        try {
            DBManager.closeConnection();
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Can't close database connection", ex);
        }
        DBManager.close();
        //Закрытие окна роли (менеджера,прораба,клиента)
        switch(RoleType){
            case R.ModelType.ManagerModel:
                ManagerView.setVisible(false);
                break;
            case R.ModelType.ClientModel:
                ClientView.setVisible(false);
                break;
            case R.ModelType.MasterModel:
                MasterView.setVisible(false);
                break;
        }
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
                if ("Nimbus".equals(info.getName())) {
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
    private javax.swing.JFrame ClientView;
    private javax.swing.JButton ExitButton;
    private javax.swing.JTextField LoginTextField;
    private javax.swing.JFrame ManagerView;
    private javax.swing.JFrame MasterView;
    private javax.swing.JButton NotExitButton;
    private javax.swing.JButton OkButton;
    private javax.swing.JPasswordField PasswordFieldText;
    private javax.swing.JComboBox RoleComboBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    // End of variables declaration//GEN-END:variables
}
