package GUI;
//Imports are listed in full to show what's being used
//could just import javax.swing.* and java.awt.* etc..
import Interfaces.Block;

import javax.swing.*;
import java.awt.*;

public class JFrameListDisplay implements SimpleListDisplay {

    private final DefaultListModel model;
    private JFrame guiFrame;

    public JFrameListDisplay(String title)
    {
        guiFrame = new JFrame();

        //make sure the program exits when the frame closes
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle(title);
        guiFrame.setSize(3000,250);

        //This will center the JFrame in the middle of the screen
        guiFrame.setLocationRelativeTo(null);

        //The first JPanel contains a JLabel and JCombobox
        final JPanel comboPanel = new JPanel();

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
        if (o instanceof Block) {
            Block b = (Block) o;
            String s = "nr: " +b.getBlockNumber() + " hash: " + b.hash() + " prev: " + b.getPreviousHash() + "Pwner: " + b.getCoinBase().getMinerAddress().getPublicKey();
            model.add(0,s);
        } else {
            model.add(0,o);
        }
    }

    @Override
    public void removeLatestFromDisplay() {
        model.removeElementAt(0);
    }

    @Override
    public void stop() {
        guiFrame.dispose();
        //guiFrame.dispatchEvent(new WindowEvent(guiFrame, WindowEvent.WINDOW_CLOSING));
    }
}