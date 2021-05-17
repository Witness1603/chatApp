package chatapp;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author basel
 */
public class Server extends Thread{
    private final int portNumber;
    private ArrayList<ServerWorker> workerList = new ArrayList<>();
public Server(int portNumber){
    this.portNumber = portNumber;
}
public List<ServerWorker> getWorkerList() {
    return workerList;
}
    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            while(true)
            {
                System.out.println("Listening for Connection... ");
            Socket clientSocket = serverSocket.accept();
                System.out.println("Connected to " + clientSocket);
                ServerWorker worker = new ServerWorker(this, clientSocket);
                workerList.add(worker);
                worker.start();   
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void removeWorker(ServerWorker serverWorker) {
            workerList.remove(serverWorker);
    }

}
