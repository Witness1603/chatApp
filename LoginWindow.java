/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author basel
 */
public class LoginWindow extends JFrame {
    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login");
    private final Client client;
    
            
    public LoginWindow(){
        super("Login");
        this.client = new Client("localhost", 2000);
        client.connect();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(loginField);
        p.add(passwordField);
        p.add(loginButton);
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }

           
        });
        
        getContentPane().add(p, BorderLayout.CENTER);
        
        pack();
        setVisible(true);     
    } 
    private void doLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();
        try {
            if(client.login(login, password)){
                UserListPane userListPane = new UserListPane(client);
                 JFrame frame = new JFrame("User List");
                 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                 frame.setSize(400, 600);
    
                 frame.getContentPane().add(userListPane, BorderLayout.CENTER);
                 frame.setVisible(true);
                setVisible(false);
            }
            else{
              JOptionPane.showMessageDialog(this, "invalid username/password");
            }
        } catch (IOException ex) {
            Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String[] args) {
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setVisible(true);
    }
}
