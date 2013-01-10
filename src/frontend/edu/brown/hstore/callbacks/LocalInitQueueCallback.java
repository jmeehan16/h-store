package edu.brown.hstore.callbacks;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.protobuf.RpcCallback;

import edu.brown.hstore.HStoreSite;
import edu.brown.hstore.TransactionQueueManager;
import edu.brown.hstore.Hstoreservice.Status;
import edu.brown.hstore.Hstoreservice.TransactionInitResponse;
import edu.brown.hstore.txns.LocalTransaction;
import edu.brown.logging.LoggerUtil;
import edu.brown.logging.LoggerUtil.LoggerBoolean;
import edu.brown.utils.PartitionSet;
import edu.brown.utils.StringUtil;

/**
 * 
 * @author pavlo
 */
public class LocalInitQueueCallback extends PartitionCountingCallback<LocalTransaction> implements RpcCallback<TransactionInitResponse> {
    private static final Logger LOG = Logger.getLogger(LocalInitQueueCallback.class);
    private static final LoggerBoolean debug = new LoggerBoolean(LOG.isDebugEnabled());
    private static final LoggerBoolean trace = new LoggerBoolean(LOG.isTraceEnabled());
    static {
        LoggerUtil.attachObserver(LOG, debug, trace);
    }

    private final TransactionQueueManager txnQueueManager;
    private final List<TransactionInitResponse> responses = new ArrayList<TransactionInitResponse>();
    
    // ----------------------------------------------------------------------------
    // INTIALIZATION
    // ----------------------------------------------------------------------------
    
    public LocalInitQueueCallback(HStoreSite hstore_site) {
        super(hstore_site);
        this.txnQueueManager = hstore_site.getTransactionQueueManager();
    }
    
    @Override
    public void init(LocalTransaction ts, PartitionSet partitions) {
        this.responses.clear();
        super.init(ts, partitions);
    }
    
    // ----------------------------------------------------------------------------
    // FINISH
    // ----------------------------------------------------------------------------

//    @Override
//    protected void finishImpl() {
//        // Nothing to do?
//    }
    
    // ----------------------------------------------------------------------------
    // RUN METHOD
    // ----------------------------------------------------------------------------
    
    @Override
    protected int runImpl(int partition) {
//        // If this is a single-partition txn, then we'll want to tell the PartitionExecutor
//        // to start executing this txn immediately
//        if (this.ts.isPredictSinglePartition()) {
//            assert(partition == this.ts.getBasePartition());
//            this.hstore_site.transactionStart((LocalTransaction)this.ts);
//        }
//        // Otherwise we'll want to send a SetDistributedTxnMessage to this partition
//        // if it's not this txn's base partition.
//        else if (this.ts.getBasePartition() != partition && this.hstore_site.isLocalPartition(partition)) {
//            this.hstore_site.transactionSetPartitionLock((LocalTransaction)this.ts, partition);
//        }
        
        return (1);
    }
    
    // ----------------------------------------------------------------------------
    // CALLBACK METHODS
    // ----------------------------------------------------------------------------

    @Override
    protected void unblockCallback() {
        assert(this.isAborted() == false);
        
        // HACK: If this is a single-partition txn, then we don't
        // need to submit it for execution because the PartitionExecutor
        // will fire it off right away
        if (this.ts.isPredictSinglePartition() == false) {
            if (debug.val) LOG.debug(this.ts + " is ready to execute. Passing to HStoreSite");
            this.hstore_site.transactionStart((LocalTransaction)this.ts);
        }
//        else { 
//            if (hstore_conf.site.txn_profiling && ts.profiler != null) ts.profiler.startQueue();
//            if (debug.val)
//                LOG.debug(this.ts + " is ready to execute but it is single-partitioned. " +
//                          "Not telling the HStoreSite to start");
//        }
    }

    @Override
    protected void abortCallback(int partition, Status status) {
        // If the transaction needs to be restarted, then we'll attempt to requeue it.
        switch (status) {
            case ABORT_SPECULATIVE:
            case ABORT_RESTART:
                // We don't care whether our transaction was rejected or not because we 
                // know that we still need to call TransactionFinish, which will delete
                // the final transaction state
                this.txnQueueManager.restartTransaction(this.ts, status);
                break;
            case ABORT_REJECT:
                this.hstore_site.transactionReject(this.ts, status);
                break;
            default:
                throw new RuntimeException(String.format("Unexpected status %s for %s", status, this.ts));
        } // SWITCH
    }
    
    // ----------------------------------------------------------------------------
    // RPC CALLBACK
    // ----------------------------------------------------------------------------

    @Override
    public void run(TransactionInitResponse response) {
        if (debug.val)
            LOG.debug(String.format("%s - Got %s with status %s from partitions %s",
                      this.ts, response.getClass().getSimpleName(),
                      response.getStatus(), response.getPartitionsList()));
        this.responses.add(response);
        if (response.getStatus() != Status.OK) {
            int reject_partition = response.getRejectPartition();
            for (int partition : response.getPartitionsList()) {
                if (partition != reject_partition) {
                    this.decrementCounter(partition, false);
                }
            } // FOR
            // The last one should call the actual abort
            this.abort(reject_partition, response.getStatus());        
        } else {
            for (Integer partition : response.getPartitionsList()) {
                this.run(partition.intValue());
            } // FOR
        }
    }
    
    @Override
    public String toString() {
        String ret = super.toString();
        ret += "\n-------------\n";
        ret += String.format("ReceivedPartitions=%s / AllPartitions=%s\n",
                             this.getReceivedPartitions(), this.getPartitions());
        ret += StringUtil.join("\n", this.responses);
        return (ret);
    }
}
