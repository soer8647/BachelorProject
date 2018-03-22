package Interfaces.Communication;

import Impl.Communication.Events.MinedBlockEvent;
import Impl.Communication.Events.ReceivedBlockEvent;
import Impl.Communication.Events.TransactionEvent;

/**
 *  This Should handle events and glue together the Node with network modules (see StandardNodeCommunicationHandler)
 */
public interface NodeCommunicationHandler {
    void handleReceivedBlock(ReceivedBlockEvent event);
    void handleNewTransaction(TransactionEvent transactionEvent);
    void handleMinedBlock(MinedBlockEvent minedBlockEvent);

    void stop();
}
