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

package edu.brown.benchmark.voterexperiments.demohstore.checkcorrect;

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
import edu.brown.benchmark.voterexperiments.demohstore.checkcorrect.procedures.GenerateLeaderboard;
import edu.brown.benchmark.voterwintimesstore.VoterWinTimeSStoreConstants;
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
    
    private final Callback procOneCallback = new Callback(0);
    private final Callback otherProcCallback = new Callback(1);
    
    boolean genLeaderboard;

    public static void main(String args[]) {
        BenchmarkComponent.main(VoterDemoHStoreClient.class, args, false);
    }

    public VoterDemoHStoreClient(String args[]) {
        super(args);
        int numContestants = VoterDemoHStoreUtil.getScaledNumContestants(this.getScaleFactor());
        this.switchboard = new PhoneCallGenerator(this.getClientId(), numContestants);
        lastTime = System.nanoTime();
        timestamp = 0;
        genLeaderboard = false;
        System.out.println("CLIENT STARTED");
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
        if(call == null)
        {
        	try {
        		if(System.nanoTime() - lastTime <= VoterDemoHStoreConstants.DURATION)
                {
                	return false;
                }
        		
        		ClientResponse result;
			
				result = client.callProcedure("GetResults");
			
				VoltTable[] v = result.getResults();
				assert(v[0].getRowCount() > 0);
				int[] databaseResults = new int[v[0].getRowCount()];				
				for(int i = 0; i < databaseResults.length; i++)
				{
					databaseResults[i] = (int)v[0].fetchRow(i).getLong(1);
				}
				int[] actualResults = this.switchboard.getRemovedCandidates();
				assert(databaseResults.length == actualResults.length);
				boolean success = true;
				for(int i = 0; i < databaseResults.length; i++)
				{
					if(databaseResults[i] != actualResults[i])
					{
						System.out.println("FAILURE XX");
						success = false;
						System.exit(0);
					}
				}
				if(success)
				{
					System.out.println("SUCCESS!!!");
					System.exit(0);
				}
				
        	}
        	catch (ProcCallException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        lastTime = System.nanoTime();
        
        
        Callback callback = new Callback(0);

		boolean response = client.callProcedure(       callback,
													"Vote",
			                                        call.voteId,
			                                        call.phoneNumber,
			                                        call.contestantNumber,
			                                        VoterDemoHStoreConstants.MAX_VOTES);
		
		if(response)
		{
			Callback callback1 = new Callback(1);
			response = client.callProcedure(callback1, "GenerateLeaderboard");
		}
       	
		if(response)
		{
			Callback callback2 = new Callback(2);
			response = client.callProcedure(callback2, "DeleteContestant");
		}
        
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
    	
    	public Callback(int idx)
    	{
    		super();
    		this.idx = idx;
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
            
            if(this.idx == 0)
            {
            	incrementTransactionCounter(clientResponse, 0);
	            // Keep track of state (optional)
	            if (clientResponse.getStatus() == Status.OK) {
	                VoltTable results[] = clientResponse.getResults();
	                assert(results.length == 1);
	                long status = results[0].asScalarLong();
	                prevStatus = status;
	                if (status == VoterDemoHStoreConstants.VOTE_SUCCESSFUL) {
	                    acceptedVotes.incrementAndGet();
	                }
	                else if (status == VoterDemoHStoreConstants.ERR_INVALID_CONTESTANT) {
	                    badContestantVotes.incrementAndGet();
	                }
	                else if (status == VoterDemoHStoreConstants.ERR_VOTER_OVER_VOTE_LIMIT) {
	                    badVoteCountVotes.incrementAndGet();
	                }
	            }
	            else if (clientResponse.getStatus() == Status.ABORT_UNEXPECTED) {
	                if (clientResponse.getException() != null) {
	                    clientResponse.getException().printStackTrace();
	                }
	                if (debug.val && clientResponse.getStatusString() != null) {
	                    LOG.warn(clientResponse.getStatusString());
	                }
	            }
            }
        }
    } // END CLASS
    
}
