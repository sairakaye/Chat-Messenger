import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InviteUser extends JFrame{

    private JList listUsers;
    private JButton btnAdd;
    private JButton btnCancel;

    public InviteUser(GroupChat grpChat, DefaultListModel listModel){
        setBounds(0, 0, 300, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        listUsers = new JList(listModel);
        listUsers.setBounds(50, 20, 200, 250);
        listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnAdd = new JButton("Add");
        btnAdd.setBounds(50, 305, 100, 30);
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
        btnCancel.setBounds(160, 305, 100, 30);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getContentPane().add(listUsers);
        getContentPane().add(btnAdd);
        getContentPane().add(btnCancel);

        setVisible(true);
    }
}
