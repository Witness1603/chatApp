package chatapp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.*;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
/**
 *
 * @author basel
 */
public class ServerWorker extends Thread {
    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> setTopic = new HashSet<>();
    public ServerWorker(Server server, Socket clientSocket){
        this.server = server;
        this.clientSocket = clientSocket;
    }
    @Override
    public void run(){
        try {
            clientSocketHandler();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void clientSocketHandler() throws IOException, InterruptedException {
            InputStream inputStream = clientSocket.getInputStream();
            this.outputStream = clientSocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            while( (str = reader.readLine()) != null) {
                String[] tokens = str.split("\\s+");
                if(tokens != null && tokens.length > 0){
                    String command = tokens[0];
                    if ("quit".equalsIgnoreCase(command) || "logoff".equalsIgnoreCase(command)) {
                        clientLogoff();
                        break;
                        
                    } else if("login".equalsIgnoreCase(command)){
                        clientLogin(outputStream, tokens);
                    } else if("msg".equalsIgnoreCase(command)){
                        String[] msgTokens = StringUtils.split(str, null, 3);
                        clientMessage(msgTokens);
                    } else if("join".equalsIgnoreCase(command)){
                        joinTopicHandler(tokens);
                    } else if("leave".equalsIgnoreCase(command)){
                        leaveTopicHandler(tokens);
                    }
                    else{
                       String msg =  command + " Command is Unknown\n";
                       outputStream.write(msg.getBytes());
                    }
                }
            }
            clientSocket.close();
        }
    public String getLogin(){
        return login;
    }
    private void clientLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if(tokens.length==3){
            String login = tokens[1];
            String password = tokens[2];
            if(login.equals("client1") && password.equals("client1") || login.equals("client2") && password.equals("client2")){
                String msg = "login ok\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User " + login + " Logged in.\n");
                
                List<ServerWorker> workerList = server.getWorkerList();
                // get others' status
               for(ServerWorker worker : workerList){
                   if(!login.equals(worker.getLogin())){
                       if(worker.getLogin() !=null){
                         String onlineMsg2 = worker.getLogin() + " Online.\n";
                         worker.send(onlineMsg2);
                       }
                   }
               }
                // send your status to others
                String onlineMsg = login + " is Online.\n";  
               for(ServerWorker worker : workerList){
                      if(!login.equals(worker.getLogin())){
                      worker.send(onlineMsg); 
                    }
               }
            }else{
                String msg = "login error\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for " + login);
            }
        }
        
    }
    private void send(String onlineMsg) throws IOException {
        if (login != null){
        outputStream.write(onlineMsg.getBytes());
        }
    }
    private void clientLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();
        String offlineMsg = login + " Logged Off\n";
        for(ServerWorker worker : workerList){
            if(!login.equals(worker.getLogin())){
                worker.send(offlineMsg);
            }
        }        
        clientSocket.close();
    }

    private void clientMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String msgBody = tokens[2];
        
        boolean isTopic = sendTo.charAt(0) == '#';
        List<ServerWorker> workerList = server.getWorkerList();
        for(ServerWorker worker : workerList){
            if(isTopic){
                if(worker.isMemberOfTopic(sendTo)){
                    String outMsg = "msg in " + sendTo + " from " + login + ": " + msgBody + "\n";
                    worker.send(outMsg);
                }
            }
            else if(sendTo.equalsIgnoreCase(worker.getLogin())){
                String outMsg = "msg from " + login + ": " + msgBody + " \n";
                worker.send(outMsg);
            }
        }
    }
    public boolean isMemberOfTopic(String topic){
        return setTopic.contains(topic);
    }
    private void joinTopicHandler(String[] tokens) {
    if(tokens.length>1){
        String topic = tokens[1];
        setTopic.add(topic);
        }
    }

    private void leaveTopicHandler(String[] tokens) {
         if(tokens.length>1){
        String topic = tokens[1];
        setTopic.remove(topic);
        }
    }
}
