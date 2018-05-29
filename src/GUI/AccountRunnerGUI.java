package GUI;

import Crypto.Impl.RSAPublicKey;
import Impl.Communication.NotEnoughMoneyException;
import Impl.PublicKeyAddress;
import Impl.TransactionHistory;
import Impl.Transactions.ConfirmedTransaction;
import Impl.Transactions.PendingTransaction;
import Interfaces.Address;
import Interfaces.CoinBaseTransaction;
import Interfaces.Communication.AccountRunner;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigInteger;
import java.util.Map;

public class AccountRunnerGUI{
    private final JList<String> historyText;
    private final DefaultListModel<String> historyModel;
    private final JList<String> pendingText;
    private final DefaultListModel<String> pendingTransactionsModel;
    private final JScrollPane historyBar;
    private final JButton makeButton;
    private final Container historyContainer;
    private final JLabel errorField;
    private final JScrollPane pendingBar;
    private final JPanel pendingContainer;
    private JComboBox<Address> comboAddresses;

    private JTextArea valueField;
    private AccountRunner accountRunner;
    private JFrame frame;
    private JLabel moneyArea;



    public AccountRunnerGUI(AccountRunner accountRunner) throws HeadlessException {
        this.accountRunner = accountRunner;
        frame = new JFrame("ACCOUNT");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setPreferredSize(new Dimension(530,300));

        // Put content
        Container main = frame.getContentPane();

        main.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        //Money label and money field
        Container moneyCont = new Container();
        moneyCont.setLayout(new BoxLayout(moneyCont,BoxLayout.X_AXIS));

        // Label
        JLabel moneyLabel = new JLabel("Money: ");

        moneyCont.add(moneyLabel);

        //Area to show balance
        moneyArea = new JLabel(String.valueOf(accountRunner.getBalance()));
        moneyCont.add(moneyArea);

        // Make transactions container
        Container makeTrans = new Container();
        makeTrans.setLayout(new BoxLayout(makeTrans,BoxLayout.X_AXIS));

        // Make transaction text label
        JLabel makeTransText = new JLabel("Make transaction:");


        //Value and receiver container
        Container valueInfo = new Container();
        valueInfo.setLayout(new BoxLayout(valueInfo,BoxLayout.X_AXIS));
        valueField = new JTextArea("0");
        valueField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkTextAreaAndEnableTransButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkTextAreaAndEnableTransButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkTextAreaAndEnableTransButton();
            }
        });
        valueInfo.add(new JLabel("Value"));
        valueInfo.add(valueField);

        comboAddresses = new JComboBox<Address>();
        comboAddresses.setRenderer(new ListCellRenderer<Address>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Address> list, Address value, int index, boolean isSelected, boolean cellHasFocus) {
                return new JLabel(value.getPublicKey().toString().substring(14,25));
            }
        });

        Container receiverInfo = new Container();
        receiverInfo.setLayout(new BoxLayout(receiverInfo,BoxLayout.X_AXIS));
        receiverInfo.add(new JLabel("Receiver"));
        receiverInfo.add(comboAddresses);

        //Add receive
        JTextArea newReceiver = new JTextArea("Add a new Receiver");


        JButton newReceiverButton = new JButton("Add receiver");
        newReceiverButton.addActionListener(e -> comboAddresses.addItem(new PublicKeyAddress(new RSAPublicKey(newReceiver.getText()))));

        makeTrans.add(valueInfo);

        // Make trans button
        makeButton = new JButton("Make transaction!");

        // Pending transactions
        pendingContainer = new JPanel();
        pendingContainer.setLayout(new BoxLayout(pendingContainer,BoxLayout.Y_AXIS));
        JLabel pendingTextLabel = new JLabel("Pending transactions:");
        pendingTextLabel.setHorizontalAlignment(SwingConstants.LEFT);
        pendingTextLabel.setHorizontalTextPosition(SwingConstants.LEFT);

        pendingTransactionsModel = new DefaultListModel<>();
        pendingText = new JList<>(pendingTransactionsModel);
        pendingText.setLayoutOrientation(JList.VERTICAL);
        pendingText.setVisibleRowCount(10);
        pendingBar = new JScrollPane(pendingText);
        pendingBar.setPreferredSize(new Dimension(220,173));
        pendingContainer.add(pendingBar);

        historyContainer = new JPanel();
        historyContainer.setLayout(new BoxLayout(historyContainer,BoxLayout.Y_AXIS));

        JLabel transHistoryLabel = new JLabel("Transaction History:");
        transHistoryLabel.setHorizontalAlignment(SwingConstants.LEFT);
        transHistoryLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        // The historyModel that holds the data
        historyModel = new DefaultListModel<>();
        historyText = new JList<>(historyModel);
        historyText.setLayoutOrientation(JList.VERTICAL);
        //Set number of rows before scroll
        historyText.setVisibleRowCount(10);
        historyBar = new JScrollPane(historyText);
        historyBar.setPreferredSize(new Dimension(220,173));
        historyContainer.add(historyBar);

        errorField = new JLabel();
        errorField.setVisible(false);

        updateGUI();
        //Add to main

        //Add money text area
        constraints.gridx = 2;
        constraints.gridy = 6;
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        main.add(moneyCont,constraints);

        //Add receiver text area
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.BOTH;
        main.add(newReceiver,constraints);

        //Add transaction text
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        main.add(makeTransText,constraints);


        //add "add receiver button"
        constraints.gridx = 1;
        constraints.gridy = 1;
        main.add(newReceiverButton,constraints);

        //add "make transaction" button
        constraints.gridx = 1;
        constraints.gridy = 4;
        main.add(makeButton,constraints);


        constraints.gridx = 0;
        constraints.gridy = 3;
        main.add(makeTrans,constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        main.add(receiverInfo,constraints);




        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        main.add(transHistoryLabel,constraints);

        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        main.add(pendingTextLabel,constraints);


        constraints.gridx = 1;
        constraints.gridy = 3   ;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        main.add(errorField,constraints);

        constraints.gridx = 1;
        constraints.gridy = 6;
        main.add(pendingContainer,constraints);

        constraints.gridx = 0;
        constraints.gridy = 6;
        main.add(historyContainer,constraints);

        // ActionListener for button
        makeButton.addActionListener(e->{
            try {
                accountRunner.makeTransaction((PublicKeyAddress)comboAddresses.getSelectedItem(),Integer.valueOf(valueField.getText()));
            } catch (NotEnoughMoneyException e1) {
                errorField.setText("Not enough money to make transaction");
                errorField.setVisible(true);
                e1.printStackTrace();
            }
        });


        frame.pack();
        frame.setVisible(true);

        Thread t = new Thread(()->{
            while (!Thread.interrupted()) {
                accountRunner.updateTransactionHistory();
                accountRunner.updatePendingTransactions();
                System.out.println("Updating");
                SwingUtilities.invokeLater(this::updateGUI);
                moneyArea.setText(String.valueOf(accountRunner.getBalance()));
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void updateGUI(){
        try {
            inputPendingTransactionsElements(accountRunner.getPendingTransactionMap());
            inputTransactionHistoryElements(accountRunner.getTransactionHistory());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void inputPendingTransactionsElements(Map<BigInteger,PendingTransaction> pendingTransactions){
        pendingTransactionsModel.removeAllElements();
        for (PendingTransaction t:pendingTransactions.values()){
            pendingTransactionsModel.addElement("S: "+t.getSenderAddress().getPublicKey().toString().substring(16,22)
                    +", R: "+t.getReceiverAddress().getPublicKey().toString().substring(16,22)
                    +", V: "+t.getValue());
        }
    }

    private void inputTransactionHistoryElements(TransactionHistory transactionHistory) throws InterruptedException {
        transactionHistory.getSemaphore().acquire();
        historyModel.removeAllElements();
        for (ConfirmedTransaction confirmedTransaction:accountRunner.getTransactionHistory().getConfirmedTransactions()){
            historyModel.addElement("S: "+confirmedTransaction.getSenderAddress().getPublicKey().toString().substring(16,22)
                    +", R: "+confirmedTransaction.getReceiverAddress().getPublicKey().toString().substring(16,22)
                    +", V: "+confirmedTransaction.getValue());
        }
        for (CoinBaseTransaction coinBaseTransaction: accountRunner.getTransactionHistory().getCoinBaseTransactions()){
            historyModel.addElement("Coin: "+coinBaseTransaction.getValue() + " ,blocknumber: "+coinBaseTransaction.getBlockNumber());
        }
        transactionHistory.getSemaphore().release();
    }

    public void addAddress(Address address){
        comboAddresses.addItem(address);
    }


    private void checkTextAreaAndEnableTransButton(){
        String value = valueField.getText();
        errorField.setVisible(false);
        try{
            int transactionValue = Integer.valueOf(value);
            // Maybe not this TODO
            if (transactionValue>accountRunner.getBalance()){
                makeButton.setEnabled(false);
                errorField.setVisible(true);
                errorField.setText("<html>Not enough funds.</html>");
                errorField.setForeground(Color.RED);
            }else {
                makeButton.setEnabled(true);
            }
        }catch (NumberFormatException e){
            makeButton.setEnabled(false);
            errorField.setText("<html>Value must be integer.</html>");
            errorField.setForeground(Color.RED);
            errorField.setVisible(true);
        }
    }

}
