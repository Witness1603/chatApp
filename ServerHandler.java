/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

/**
 *
 * @author basel
 */
public class ServerHandler {
    
    public static void main(String args[]) {
        int port = 2000;
        Server server = new Server(port);
        server.start();
        
    }
              
}

