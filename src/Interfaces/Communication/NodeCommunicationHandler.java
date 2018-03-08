package Interfaces.Communication;

import Impl.Communication.Events.ReceivedBlockEvent;
import Interfaces.Block;
import Interfaces.Transaction;

/**
 *  This Should handle events and glue together the Node with network modules (see StandardNodeCommunicationHandler)
 */
public interface NodeCommunicationHandler {
    void handleReceivedBlock(ReceivedBlockEvent event);
    void handleNewTransaction(Transaction transaction);
    void handleMinedBlock(Block block);
//  void HandleNewUser(User user);
}
