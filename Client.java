/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author basel
 */
public class Client {
    private final String serverName;
    private final int portNumber;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedReader;
    
    private final ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private final ArrayList<MessageListener> messageListeners = new ArrayList<>();

    public Client(String serverName, int portNumber){
        this.serverName = serverName;
        this.portNumber = portNumber;
    }
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String args[]) throws IOException {
        Client client = new Client("localhost", 2000);
        client.addUserStatusListener(new UserStatusListener(){
            @Override
            public void online(String login) {
                System.out.println("Online users: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("Offline users: " + login);
            }
        });
        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String msgBody) {
                System.out.println("Message from: " + fromLogin + ": " + msgBody);
            }
        });
        if(!client.connect()){
            System.err.println("Connection failed.");
        } else{
            System.out.println("Connected.");
            if(client.login("client1", "client1")){
                System.out.println("Logged in."); 
                
                client.msg("client2", "Hello");
            }else{
                System.err.println("Login Failed.");
            }
            //client.logoff();
        }
    }
     private void readMessageLoop() {
        try {
            String line;
            while((line = bufferedReader.readLine()) !=null){
                String[] tokens = line.split("\\s+");
                if(tokens != null && tokens.length > 0){
                   String command = tokens[0];
                   if("online".equalsIgnoreCase(command)){
                     onlineHandler(tokens);
                   }else if("offline".equalsIgnoreCase(command)){
                       offlineHandler(tokens);
                   }else if("msg".equalsIgnoreCase(command)){
                       String[] tokensMsg = StringUtils.split(line, null, 3);
                       messageHandler(tokensMsg);
                   }
                }
            }  } catch (IOException ex) {
            try {
                socket.close();
            } catch (IOException ex1) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
            }
    public boolean connect(){
        try {
            this.socket = new Socket(serverName, portNumber);
            this.serverIn = socket.getInputStream();
            this.serverOut = socket.getOutputStream();
            this.bufferedReader = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean login(String login, String password) throws IOException {
        String command = "login " + login + " " + password + "\n";
        serverOut.write(command.getBytes());
        
        String response = bufferedReader.readLine();
        System.out.println(response + "\n");
        if("login ok".equalsIgnoreCase(response)){
            startMessageReader();
            return true;
        }else{
            return false;
        }
    }
    public void addUserStatusListener(UserStatusListener listener){
        userStatusListeners.add(listener);
    }
    public void removeUserStatusListener(UserStatusListener listener){
        userStatusListeners.remove(listener);
    }
    public void addMessageListener(MessageListener listener){
        messageListeners.add(listener);
    }
    public void removeMessageListener(MessageListener listener){
        messageListeners.remove(listener);
    }
    private void startMessageReader() {
        Thread t = new Thread(){
            @Override
            public void run(){
                readMessageLoop();
            } 
        };
        t.start();
    }

    private void onlineHandler(String[] tokens) {
       String login = tokens[1];
       for(UserStatusListener listener : userStatusListeners){
           listener.online(login);
       }
    }

     private void offlineHandler(String[] tokens) {
       String login = tokens[1];
       for(UserStatusListener listener : userStatusListeners){
           listener.offline(login);
       }
    }

    public void logoff() throws IOException {
        String command = "logoff\n";
        serverOut.write(command.getBytes());
    }

   public void msg(String sendTo, String msgBody) throws IOException {
        String command = "msg " + sendTo + " " + msgBody + "\n";
        serverOut.write(command.getBytes());
    }

    private void messageHandler(String[] tokensMsg) {
        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];
        
        for(MessageListener listener : messageListeners){
            listener.onMessage(login, msgBody);
        }
    }
    
}
