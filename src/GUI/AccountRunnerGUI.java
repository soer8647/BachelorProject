package GUI;

import Crypto.Impl.RSAPublicKey;
import Impl.Communication.NotEnoughMoneyException;
import Impl.PublicKeyAddress;
import Impl.Transactions.ConfirmedTransaction;
import Interfaces.CoinBaseTransaction;
import Interfaces.Communication.AccountRunner;

import javax.swing.*;
import java.awt.*;

public class AccountRunnerGUI{
    private final JButton makeButton;
    private final Container historyContainer;
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
        transInfo.add(receiverField);

        makeTrans.add(transInfo);

        // Make trans button
        makeButton = new JButton("Make transaction!");

        makeTrans.add(makeButton);
        main.add(makeTrans);

        historyContainer = new Container();
        historyContainer.setLayout(new BoxLayout(historyContainer,BoxLayout.Y_AXIS));

        historyContainer.add(new JLabel("Transaction History:"));

        for (ConfirmedTransaction confirmedTransaction:accountRunner.getTransactionHistory().getConfirmedTransactions()){
            historyContainer.add(new JLabel("Sender "+confirmedTransaction.getSenderAddress().toString().substring(0,5)
                    +" ,Receiver "+confirmedTransaction.getReceiverAddress().toString().substring(0,5)
            +"value "+confirmedTransaction.getValue()));
        }

        for (CoinBaseTransaction coinBaseTransaction: accountRunner.getTransactionHistory().getCoinBaseTransactions()){
            historyContainer.add(new JLabel("Coin: "+coinBaseTransaction.getValue() + " ,blocknumber: "+coinBaseTransaction.getBlockNumber()));
        }
        main.add(historyContainer);

        // ActionListener for button
        makeButton.addActionListener(e->{
            try {
                accountRunner.makeTransaction(new PublicKeyAddress(new RSAPublicKey(receiverField.getText())),Integer.valueOf(valueField.getText()));
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
                moneyArea = new JLabel(String.valueOf(accountRunner.getBalance()));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void updateHistory(){
        historyContainer.removeAll();
        System.out.println(accountRunner.getTransactionHistory().size());
        for (ConfirmedTransaction confirmedTransaction:accountRunner.getTransactionHistory().getConfirmedTransactions()){
            historyContainer.add(new JLabel("Sender "+confirmedTransaction.getSenderAddress().toString().substring(0,5)
                    +" ,Receiver "+confirmedTransaction.getReceiverAddress().toString().substring(0,5)
                    +"value "+confirmedTransaction.getValue()));
        }

        for (CoinBaseTransaction coinBaseTransaction: accountRunner.getTransactionHistory().getCoinBaseTransactions()){
                historyContainer.add(new JLabel("Coin: "+coinBaseTransaction.getValue() + " ,blocknumber: "+coinBaseTransaction.getBlockNumber()));
            System.out.println("Adding coinbase");
        }
        frame.getContentPane().validate();
        frame.pack();
        frame.getContentPane().repaint();

    }
}
