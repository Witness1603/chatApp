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
import javax.swing.*;

/**
 *
 * @author basel
 */
public class MessagePane extends JPanel implements MessageListener{

    private final Client client;
    private final String login;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> messageList = new JList<>();
    private JTextField inputField = new JTextField();
    MessagePane(Client client, String login) {
        this.client = client;
        this.login = login;
        
        client.addMessageListener(this);
        
        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
        
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = inputField.getText();
                    client.msg(login, text);
                    listModel.addElement("You: " + text);
                    inputField.setText(""); 
                } catch (IOException e1) {
                    System.out.println(e1);
                }
               
            }
        });
    }
    
    @Override
    public void onMessage(String fromLogin, String msgBody){
        if(login.equalsIgnoreCase(fromLogin)){
             String line = fromLogin + ":" + msgBody;
             listModel.addElement(line);
        }
       
    }
}
