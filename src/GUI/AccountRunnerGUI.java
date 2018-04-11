package GUI;

import Crypto.Impl.RSAPublicKey;
import Impl.Communication.NotEnoughMoneyException;
import Impl.PublicKeyAddress;
import Impl.Transactions.ConfirmedTransaction;
import Interfaces.Address;
import Interfaces.CoinBaseTransaction;
import Interfaces.Communication.AccountRunner;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AccountRunnerGUI{
    private final JList<String> historyText;
    private final DefaultListModel<String> model;
    private ArrayList<PublicKeyAddress> addresses;
    private final JButton makeButton;
    private final Container historyContainer;
    private JComboBox<Address> comboAddresses;

    private JTextArea receiverField;
    private JTextArea valueField;
    private AccountRunner accountRunner;
    private JFrame frame;
    private JLabel moneyArea;



    public AccountRunnerGUI(AccountRunner accountRunner) throws HeadlessException {
        this.accountRunner = accountRunner;
        frame = new JFrame("ACCOUNT");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Put content
        Container main = frame.getContentPane();
        main.setLayout(new BoxLayout(main,BoxLayout.Y_AXIS));

        addresses = new ArrayList<>();

        //Money label and money field
        Container moneyCont = new Container();
        moneyCont.setLayout(new BoxLayout(moneyCont,BoxLayout.X_AXIS));

        // Label
        JLabel moneyLabel = new JLabel("Money: ");

        moneyCont.add(moneyLabel);

        //Area to show balance
        moneyArea = new JLabel(String.valueOf(accountRunner.getBalance()));
        moneyCont.add(moneyArea);

        //Add to main
        main.add(moneyCont);

        // Make transactions container
        Container makeTrans = new Container();
        makeTrans.setLayout(new BoxLayout(makeTrans,BoxLayout.Y_AXIS));

        // Make transaction text label
        JLabel text = new JLabel("Make transaction:");
        makeTrans.add(text);

        //Value and receiver container
        Container transInfo = new Container();
        transInfo.setLayout(new BoxLayout(transInfo,BoxLayout.X_AXIS));
        valueField = new JTextArea("0");
        receiverField = new JTextArea();
        transInfo.add(new JLabel("Value"));
        transInfo.add(valueField);
        transInfo.add(new JLabel("Receiver"));
        comboAddresses = new JComboBox<Address>();
        comboAddresses.setRenderer(new ListCellRenderer<Address>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Address> list, Address value, int index, boolean isSelected, boolean cellHasFocus) {
                return new JLabel(value.getPublicKey().toString().substring(14,25));
            }
        });
        transInfo.add(comboAddresses);
        //transInfo.add(receiverField);

        //Add receiver
        Container newReceiverCont = new Container();
        newReceiverCont.setLayout(new BoxLayout(newReceiverCont,BoxLayout.X_AXIS));
        JTextArea newReceiver = new JTextArea("Add a new Receiver");
        JButton newReceiverButton = new JButton("Add receiver");
        newReceiverButton.addActionListener(e -> comboAddresses.addItem(new PublicKeyAddress(new RSAPublicKey(newReceiver.getText()))));
        newReceiverCont.add(newReceiver);
        newReceiverCont.add(newReceiverButton);
        main.add(newReceiverCont);


        makeTrans.add(transInfo);

        // Make trans button
        makeButton = new JButton("Make transaction!");

        makeTrans.add(makeButton);
        main.add(makeTrans);

        historyContainer = new Container();
        historyContainer.setLayout(new BoxLayout(historyContainer,BoxLayout.Y_AXIS));

        historyContainer.add(new JLabel("Transaction History:"));
        model = new DefaultListModel<>();
        historyText = new JList<>(model);
        historyText.setLayoutOrientation(JList.VERTICAL);
        historyText.setVisibleRowCount(10);
        JScrollPane bar = new JScrollPane(historyText);

        //Set number of rows before scroll


        historyContainer.add(bar);

        for (ConfirmedTransaction confirmedTransaction:accountRunner.getTransactionHistory().getConfirmedTransactions()){

            model.addElement("Sender "+confirmedTransaction.getSenderAddress().toString().substring(0,5)
                    +" ,Receiver "+confirmedTransaction.getReceiverAddress().toString().substring(0,5)
            +"value "+confirmedTransaction.getValue());
        }

        for (CoinBaseTransaction coinBaseTransaction: accountRunner.getTransactionHistory().getCoinBaseTransactions()){
            model.addElement("Coin: "+coinBaseTransaction.getValue() + " ,blocknumber: "+coinBaseTransaction.getBlockNumber());
        }
        main.add(historyContainer);

        // ActionListener for button
        makeButton.addActionListener(e->{
            try {
                accountRunner.makeTransaction((PublicKeyAddress)comboAddresses.getSelectedItem(),Integer.valueOf(valueField.getText()));
            } catch (NotEnoughMoneyException e1) {
                e1.printStackTrace();
            }
        });


        frame.pack();
        frame.setVisible(true);

        Thread t = new Thread(()->{
            while (!Thread.interrupted()) {
                accountRunner.updateTransactionHistory();
                System.out.println("Updating");
                updateHistory();
                moneyArea.setText(String.valueOf(accountRunner.getBalance()));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void updateHistory(){
        model.removeAllElements();
        try {
            accountRunner.getTransactionHistory().getSemaphore().acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (ConfirmedTransaction confirmedTransaction:accountRunner.getTransactionHistory().getConfirmedTransactions()){
            model.addElement("Sender "+confirmedTransaction.getSenderAddress().getPublicKey().toString().substring(13,25)
                    +" ,Receiver "+confirmedTransaction.getReceiverAddress().getPublicKey().toString().substring(13,25)
                    +"value "+confirmedTransaction.getValue());
        }
        for (CoinBaseTransaction coinBaseTransaction: accountRunner.getTransactionHistory().getCoinBaseTransactions()){
                model.addElement("Coin: "+coinBaseTransaction.getValue() + " ,blocknumber: "+coinBaseTransaction.getBlockNumber());
        }
        accountRunner.getTransactionHistory().getSemaphore().release();
    }

    public void addAddress(Address address){
        comboAddresses.addItem(address);
    }
}
