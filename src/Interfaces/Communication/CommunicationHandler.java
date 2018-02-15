package Interfaces.Communication;

import Interfaces.Block;
        import Interfaces.Transaction;

/**
 *  This Should handle events and glue together the Node with network modules (see StandardCommunicationHandler)
 */
public interface CommunicationHandler {
    void HandleReceivedBlock(Block block);
    void HandleNewTransaction(Transaction transaction);
    void HandleMinedBlock(Block block);
//  void HandleNewUser(User user);
}
