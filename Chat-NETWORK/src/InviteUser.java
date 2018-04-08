package src;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.Color;

public class InviteUser extends JFrame {

    private JList listUsers;
    private JButton btnAdd;
    private JButton btnCancel;
    private JScrollPane userScrollPane;
    private JLabel lblInviteUsers;

    public InviteUser(GroupChat grpChat, DefaultListModel listModel){
    	this.setTitle("Invite Users");
    	getContentPane().setBackground(new Color(255, 255, 255));
        setBounds(0, 0, 300, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        listUsers = new JList(listModel);
        listUsers.setFont(new Font("Calibri", Font.BOLD, 12));
        listUsers.setBounds(50, 20, 200, 250);
        listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        userScrollPane = new JScrollPane(listUsers);
        userScrollPane.setBounds(26, 57, 234, 237);

        btnAdd = new JButton("Add");
        btnAdd.setBackground(new Color(0, 0, 128));
        btnAdd.setForeground(new Color(255, 255, 255));
        btnAdd.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
        btnAdd.setBounds(26, 305, 110, 30);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = 0;
                boolean found = false;
                while (i < grpChat.getUserListModel().getSize() && !found){
                    if (!grpChat.getUserListModel().get(i).toString().equalsIgnoreCase(listUsers.getSelectedValue().toString()))
                        i++;
                    else
                        found = true;
                }

                if (found)
                    JOptionPane.showMessageDialog(InviteUser.this, "User already in this group chat!");
                else{
                    JOptionPane.showMessageDialog(InviteUser.this, "User successfully added!");
                    grpChat.getUserListModel().addElement(listUsers.getSelectedValue());
                    grpChat.addUserSuccess((String) listUsers.getSelectedValue());
                    InviteUser.this.dispose();
                }
            }
        });

        btnCancel = new JButton("Cancel");
        btnCancel.setBackground(new Color(0, 0, 128));
        btnCancel.setForeground(new Color(255, 255, 255));
        btnCancel.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
        btnCancel.setBounds(150, 305, 110, 30);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        getContentPane().add(userScrollPane);
        getContentPane().add(btnAdd);
        getContentPane().add(btnCancel);
        
        lblInviteUsers = new JLabel("Invite Users");
        lblInviteUsers.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
        lblInviteUsers.setForeground(new Color(0, 0, 128));
        lblInviteUsers.setHorizontalAlignment(SwingConstants.CENTER);
        lblInviteUsers.setBounds(26, 16, 234, 30);
        getContentPane().add(lblInviteUsers);

        setVisible(true);
    }
}
