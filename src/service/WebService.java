/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import Resources.R;
import businesslogic.Client;
import businesslogic.Order;
import database.ClientMapper;
import database.DatabaseManager;
import database.OrderMapper;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import service.config.Config;
import service.config.ConfigDatabase;
import service.config.ConfigRole;

/**
 *
 * @author Nik
 */
public class WebService implements Runnable {
    
    Socket socket;
    InputStream in;
    OutputStream out;
    Config config;
    
    public WebService(Socket s,Config config) throws Throwable {
        this.socket = s;
        this.in = s.getInputStream();
        this.out = s.getOutputStream();
        this.config = config;
    }

    @Override 
    public void run() { 
        try { 
            /* Получаем заголовок сообщения от клиента */ 
            String header = readHeader(); 
            /* Получаем из заголовка указатель на интересующий ресурс */ 
            String url = getURIFromHeader(header); 
            /* Отправляем содержимое ресурса клиенту */ 
            int code = send(url); 
        } catch (IOException ex) {
            Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, "WebService IOException", ex);
        } finally { 
            try { 
                socket.close(); 
            } catch (IOException e) { 
                Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, "WebService IOException", e);
            } 
        } 
    }
    
    /** 
     * Считывает заголовок сообщения от клиента. 
     * 
     * @return строка с заголовком сообщения от клиента. 
     * @throws IOException 
     */ 
    private String readHeader() throws IOException { 
        BufferedReader reader = new BufferedReader(new InputStreamReader(in)); 
        StringBuilder builder = new StringBuilder(); 
        String ln; 
        while (true) { 
            ln = reader.readLine(); 
            if (ln == null || ln.isEmpty()) { break; } 
            builder.append(ln).append(System.getProperty("line.separator")); 
        } 
        return builder.toString(); 
    } 
    /** 
     * Вытаскивает идентификатор запрашиваемого ресурса из заголовка сообщения от 
     * клиента. 
     * 
     * @param header 
     * заголовок сообщения от клиента. 
     * @return идентификатор ресурса. 
     */ 
    private String getURIFromHeader(String header) { 
        if(!header.trim().equals("")){
            int from = header.indexOf(" ") + 1; 
            int to = header.indexOf(" ", from); 
            String uri = header.substring(from, to); 
            int paramIndex = uri.indexOf("?"); 
            return (paramIndex != -1) ? uri.substring(0, paramIndex) : uri;
        }else{
            return header;
        }
    }
    /** 
     * Отправляет ответ клиенту. В качестве ответа отправляется http заголовок и 
     * содержимое указанного ресурса. Если ресурс не указан, отправляется 
     * перечень доступных ресурсов. 
     * 
     * @param url 
     * идентификатор запрашиваемого ресурса. 
     * @return код ответа. 200 - если ресурс был найден, 404 - если нет. 
     * @throws IOException 
     */ 
    private int send(String url) throws IOException {
        Client cl = null;
        ArrayList<Order> list = null;
        url = (!url.equals("")) ? url.substring(1) : url;
        int paramIndex = url.indexOf("&");
        String format = (paramIndex != -1)? url.substring(paramIndex + 1) : null;
        if(paramIndex != -1){url = url.substring(0,paramIndex);}
        String MimeType;
        String body;
        int code;
        if(config != null){
            boolean flag = false;
            int index = auth(url);
            if(index > 0){
                ConfigDatabase ConfigInfo = config.getConfigItem(index).getDatabase();
                //подключение к базе данных
                DatabaseManager DBManager = new DatabaseManager(
                        ConfigInfo.getUser(), 
                        ConfigInfo.getPassword(), 
                        ConfigInfo.getHost(), 
                        ConfigInfo.getPath(), 
                        ConfigInfo.getEncoding(), 
                        ConfigInfo.getType(), 
                        DatabaseManager.IsolationLevel.TRANSACTION_SERIALIZABLE.name());
                try{
                    DBManager.connect();
                    
                    int CurrentId = Authorization.DatabaseRoleAuth(config.getConfigItem(index).getRole(), DBManager);
                    if(CurrentId > 0){
                        cl = new ClientMapper().load(CurrentId, DBManager);
                        list = new OrderMapper().loadListbyClient(CurrentId, DBManager);
                        flag = true;
                    }else{
                        flag = false;
                    }
                    DBManager.closeConnection();
                }catch(SQLException|NullPointerException ex){
                    Logger.getLogger(BuisnessService.class.getName()).log(Level.SEVERE, "Can't connected to database", ex);
                    flag = false;
                }
                DBManager.close();
            }
            if(flag){
                if(format != null && format.equalsIgnoreCase("json")){
                    MimeType = "application/json";
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
                    try (JsonGenerator gen = Json.createGenerator(buffer)) {
                        if(cl != null && list != null){
                            gen.writeStartObject()
                                .write("ClienName", cl.getName())
                                .write("PhoneNumber",  cl.getPhoneNumber())
                                .write("Addres", cl.getAddres())
                                .write("Type", R.Client.ClientTypeName(cl.getType()));
                            gen.writeStartArray("Orders");
                            for (Order list1 : list) {
                                gen.writeStartObject()
                                    .write("Number", list1.getNumber())
                                    .write("Status", R.Order.StatusName(list1.getStatus()))
                                    .write("CurrentCoast", list1.getCurrentCoast())
                                    .write("TotalCoast", list1.getTotalCoast())
                                    .write("ManagerID", list1.getManagerID());                                
                                    gen.writeStartArray("Estimates");
                                        for(int j = 0; j < list1.getEstimateList().size(); j++){
                                            gen.writeStartObject()
                                                .write("Type", R.Estimate.TypeName(
                                                        list1.getEstimate(j).getType()))
                                                .write("Coast", R.Estimate.StatusName(
                                                        list1.getEstimate(j).isPaid(),
                                                        list1.getEstimate(j).isFinish()))
                                                .write("Coast", list1.getEstimate(j).getCoast())
                                            .writeEnd();
                                        }
                                    gen.writeEnd();
                                gen.writeEnd();
                            }
                            gen.writeEnd();
                            gen.writeEnd();
                        }
                    }
                    body = new String(buffer.toByteArray());
                    code = 200;
                }else{
                    MimeType = "text/html";
                    if(cl != null && list != null){
                        String TableHeader = "<tr>" +
                                "<th>Номер</th>" +
                                "<th>Статус</th>" +
                                "<th>Текущая стоимость</th>" +
                                "<th>Общая стоимость</th>" +
                                "<th>ManagerID</th>" +
                                "</tr>";
                        String block = "";
                        for (Order list1 : list) {
                            block = block + "<tr>" +
                                    "<td>" + list1.getNumber() + "</td>" +
                                    "<td>" + R.Order.StatusName(list1.getStatus()) + "</td>" +
                                    "<td>" + list1.getCurrentCoast() + "</td>" +
                                    "<td>" + list1.getTotalCoast() + "</td>" + 
                                    "<td>" + list1.getManagerID() + "</td>" +
                                    "</tr>";
                        }
                        String table = "<table border=\"1\">" + TableHeader + block + "</table>";
                        body = "<html>"
                                + "<body>"
                                + "<h2> Ф.И.О : " + cl.getName() + "</h2>"
                                + "<h2> Телефон : " + cl.getPhoneNumber() + "</h2>"
                                + "<h2> Адрес : " + cl.getAddres() + "</h2>"
                                + "<h2> Тип : " + R.Client.ClientTypeName(cl.getType()) + "</h2>"
                                + table
                                + "</body>"
                                + "</html>";
                            code = 200;
                    }else{
                        body = "<html><body><h1>404 ERROR\n</h1><h4>PAGE NOT FOUND</h4></body></html>";
                        code = 404;
                    }
                }
                
            }else{
                MimeType = "text/html";
                body = "<html><body><h1>404 ERROR\n</h1><h4>PAGE NOT FOUND</h4></body></html>";
                code = 404;
            }
        }else{
            MimeType = "text/html";
            body = "<html><body><h1>500 ERROR\n</h1><h4>Internal Server Error</h4></body></html>";
            code = 500;
        }
        String header = getHeader(code,MimeType);
        PrintStream answer = new PrintStream(out, true, "UTF-8");
        answer.print(header);
        answer.print(body);
        return code; 
    }
    /** 
     * Возвращает http заголовок ответа. 
     * 
     * @param code 
     * код результата отправки. 
     * @return http заголовок ответа. 
     */ 
    private String getHeader(int code,String MimeType) { 
        StringBuilder buffer = new StringBuilder();
        buffer.append("HTTP/1.1 ").append(code).append(" ").append(getAnswer(code)).append("\n"); 
        buffer.append("Date: ").append(new Date().toGMTString()).append("\n"); 
        buffer.append("Accept-Ranges: none\n"); 
        buffer.append("Content-Type: ").append(MimeType).append("; charset=utf-8").append("\n");
        buffer.append("\n"); 
        return buffer.toString(); 
    } 
    /** 
     * Возвращает комментарий к коду результата отправки. 
     * 
     * @param code 
     * код результата отправки. 
     * @return комментарий к коду результата отправки. 
     */ 
    private String getAnswer(int code) { 
        switch (code) {
            case 200: return "OK"; 
            case 404: return "Not Found";
            default: return "Internal Server Error"; 
        } 
    }
    
    private int auth(String login){
        int index = config.findbyLogin(login);
        if(index >= 0){
            ConfigRole RoleInfo = config.getConfigItem(index).getRole();
            if(RoleInfo.getType().compareTo(R.RoleType.ConfigClient) == 0){
                return index;
            }else{
                return -1;
            }
        }else{
            return -1;
        }
    }
    
}
