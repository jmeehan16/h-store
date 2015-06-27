/***************************************************************************
 *  Copyright (C) 2012 by H-Store Project                                  *
 *  Brown University                                                       *
 *  Massachusetts Institute of Technology                                  *
 *  Yale University                                                        *
 *                                                                         *
 *  Original By: VoltDB Inc.											   *
 *  Ported By:  Justin A. DeBrabant (http://www.cs.brown.edu/~debrabant/)  *								   
 *                                                                         *
 *                                                                         *
 *  Permission is hereby granted, free of charge, to any person obtaining  *
 *  a copy of this software and associated documentation files (the        *
 *  "Software"), to deal in the Software without restriction, including    *
 *  without limitation the rights to use, copy, modify, merge, publish,    *
 *  distribute, sublicense, and/or sell copies of the Software, and to     *
 *  permit persons to whom the Software is furnished to do so, subject to  *
 *  the following conditions:                                              *
 *                                                                         *
 *  The above copyright notice and this permission notice shall be         *
 *  included in all copies or substantial portions of the Software.        *
 *                                                                         *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,        *
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF     *
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. *
 *  IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR      *
 *  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,  *
 *  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR  *
 *  OTHER DEALINGS IN THE SOFTWARE.                                        *
 ***************************************************************************/

package edu.brown.benchmark.voterexperiments.demohstore.asynchfile;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.voltdb.VoltTable;
import org.voltdb.client.Client;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcCallException;
import org.voltdb.client.ProcedureCallback;

import weka.classifiers.meta.Vote;

import edu.brown.api.BenchmarkComponent;
import edu.brown.benchmark.voterexperiments.demohstore.asynchfile.procedures.GenerateLeaderboard;
import edu.brown.benchmark.voterexperiments.demohstore.asynchfile.procedures.DeleteContestant;
import edu.brown.hstore.Hstoreservice.Status;
import edu.brown.logging.LoggerUtil.LoggerBoolean;

public class VoterDemoHStoreClient extends BenchmarkComponent {
    private static final Logger LOG = Logger.getLogger(VoterDemoHStoreClient.class);
    private static final LoggerBoolean debug = new LoggerBoolean();
    private static long lastTime;
    private static int timestamp;

    // Phone number generator
    PhoneCallGenerator switchboard;

    // Flags to tell the worker threads to stop or go
    AtomicBoolean warmupComplete = new AtomicBoolean(false);
    AtomicBoolean benchmarkComplete = new AtomicBoolean(false);

    // voterdemohstore benchmark state
    AtomicLong acceptedVotes = new AtomicLong(0);
    AtomicLong badContestantVotes = new AtomicLong(0);
    AtomicLong badVoteCountVotes = new AtomicLong(0);
    AtomicLong failedVotes = new AtomicLong(0);
    
    boolean genLeaderboard;

    public static void main(String args[]) {
        BenchmarkComponent.main(VoterDemoHStoreClient.class, args, false);
    }

    public VoterDemoHStoreClient(String args[]) {
        super(args);
        int numContestants = VoterDemoHStoreUtil.getScaledNumContestants(this.getScaleFactor());
        this.switchboard = new PhoneCallGenerator(VoterDemoHStoreConstants.VOTE_DIR + VoterDemoHStoreConstants.VOTE_FILE);
        lastTime = System.nanoTime();
        timestamp = 0;
        genLeaderboard = false;
    }

    @Override
    public void runLoop() {
        try {
            while (true) {
                // synchronously call the "Vote" procedure
                try {
                    runOnce();
                } catch (Exception e) {
                    failedVotes.incrementAndGet();
                }

            } // WHILE
        } catch (Exception e) {
            // Client has no clean mechanism for terminating with the DB.
            e.printStackTrace();
        }
    }

    @Override
    protected boolean runOnce() throws IOException {
        // Get the next phone call
    	Client client = this.getClientHandle();
    	
        PhoneCallGenerator.PhoneCall call = switchboard.receive();
        
        if(call == null) {
        	return false;
        }
        
        Callback callback = new Callback(0, client);

		boolean response = client.callProcedure(       callback,
													"Vote",
			                                        call.voteId,
			                                        call.phoneNumber,
			                                        call.contestantNumber,
			                                        VoterDemoHStoreConstants.MAX_VOTES);
		
        return response;
    }

    @Override
    public String[] getTransactionDisplayNames() {
        // Return an array of transaction names
        String procNames[] = new String[]{
            Vote.class.getSimpleName(),
            GenerateLeaderboard.class.getSimpleName()
        };
        return (procNames);
    }

    private class Callback implements ProcedureCallback {
    	
    	private int idx;
    	private long prevStatus;
    	private Client client;
    	
    	public Callback(int idx, Client c)
    	{
    		super();
    		this.idx = idx;
    		client = c;
    	}
    	
    	public long getStatus()
    	{
    		return prevStatus;
    	}

        @Override
        public void clientCallback(ClientResponse clientResponse) {
            // Increment the BenchmarkComponent's internal counter on the
            // number of transactions that have been completed
            //incrementTransactionCounter(clientResponse, this.idx);
            try {
	            if(this.idx == 0)
	            {
	            	incrementTransactionCounter(clientResponse, 0);
	            	VoltTable[] v = clientResponse.getResults();
	            	if(v != null && v.length > 0 && v[0].getRowCount() > 0 && v[0].asScalarLong() >= 0)
	            	{
	            		long voteId = v[0].asScalarLong();
	            		Callback callback = new Callback(1, client);
	            		client.callProcedure(callback, "GenerateLeaderboard", voteId);
	            	}
	            }
	            else if(this.idx == 1)
	            {
	            	//incrementTransactionCounter(clientResponse, 0);
	            	VoltTable[] v = clientResponse.getResults();
	            	if(v != null && v.length > 0 && v[0].getRowCount() > 0 && v[0].asScalarLong() >= 0)
	            	{
	            		long voteId = v[0].asScalarLong();
	            		Callback callback = new Callback(2, client);
	            		client.callProcedure(callback, "DeleteContestant", voteId);
	            	}
	            }
            }
            catch (IOException e) {
            	System.err.println(e.getMessage());
            }
        }
    } // END CLASS
    
}
