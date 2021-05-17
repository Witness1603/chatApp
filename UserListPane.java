/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.event.*;
/**
 *
 * @author basel
 */
public class UserListPane extends JPanel implements UserStatusListener {
    private final Client client;
    private JList<String> userListUI;
    private final DefaultListModel<String> userListModel;
    
    public UserListPane(Client client){
        this.client = client;
        this.client.addUserStatusListener(this);
    
    userListModel = new DefaultListModel<>();
    userListUI = new JList<>(userListModel);
    setLayout(new BorderLayout());
    add(new JScrollPane(userListUI), BorderLayout.CENTER);
    userListUI.addMouseListener(new MouseAdapter(){
        @Override
        public void mouseClicked(MouseEvent e){
            if( e.getClickCount() > 1){
                String login = userListUI.getSelectedValue();
                MessagePane messagePane = new MessagePane(client, login); 
                JFrame f = new JFrame("Message: " + login);
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setSize(500,500);
                f.getContentPane().add(messagePane, BorderLayout.CENTER);
                f.setVisible(true);
            }
        }
    });
    }
    public static void main(String args[]) {
    Client client = new Client("localhost", 2000);
    UserListPane userListPane = new UserListPane(client);
    JFrame frame = new JFrame("User List");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 600);
    frame.getContentPane().add(userListPane, BorderLayout.CENTER);
    frame.setVisible(true);   
    if(client.connect()){
        try {
            client.login("client2", "client2");
        } catch (IOException ex) {
            Logger.getLogger(UserListPane.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
   }
    @Override
    public void online(String login) {
       userListModel.addElement(login);
    }
    @Override
    public void offline(String login) {
        userListModel.removeElement(login);
    }
}
