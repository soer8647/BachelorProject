package GUI;
//Imports are listed in full to show what's being used
//could just import javax.swing.* and java.awt.* etc..
import Crypto.Interfaces.PublicKey;
import Impl.Communication.Display;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GuiApp implements Display{

    private final DefaultListModel model;

    public GuiApp(PublicKey publicKey)
    {
        JFrame guiFrame = new JFrame();

        //make sure the program exits when the frame closes
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle(publicKey+"");
        guiFrame.setSize(3000,250);

        //This will center the JFrame in the middle of the screen
        guiFrame.setLocationRelativeTo(null);

        //The first JPanel contains a JLabel and JCombobox
        final JPanel comboPanel = new JPanel();
        JLabel comboLbl = new JLabel("Fruits:");
        comboPanel.add(comboLbl);

        model = new DefaultListModel();
        JList listdata = new JList(model);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(listdata);

        guiFrame.add(scrollPane, BorderLayout.CENTER);

        guiFrame.add(comboPanel, BorderLayout.NORTH);

        //make sure the JFrame is visible
        guiFrame.setVisible(true);
    }

    @Override
    public void addToDisplay(Object o) {
        model.addElement(o);
    }
}