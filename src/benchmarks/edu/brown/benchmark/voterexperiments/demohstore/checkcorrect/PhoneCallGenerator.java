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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import org.voltdb.VoltTable;
import org.apache.commons.lang.ArrayUtils;

public class PhoneCallGenerator {
	
    private long nextVoteId;
	private final int contestantCount;
    private final Random rand = new Random();
    private final int[] votingMap = new int[AREA_CODES.length];
    
    private LinkedList<Long> usedVotes;
    private LinkedList<Long> releasedVotes;
    private HashSet<Integer> remainingCandidates;
    private ArrayList<Integer> releasedCandidates;
    private HashMap<Integer,ArrayList<Long>> allVotes;
    private int validVotes = 0;
    private boolean finished = false;
	
	// Initialize some common constants and variables
    private static final String[] AREA_CODE_STRS = ("907,205,256,334,251,870,501,479" +
													",480,602,623,928,520,341,764,628,831,925,909,562,661,510,650,949,760" +
													",415,951,209,669,408,559,626,442,530,916,627,714,707,310,323,213,424" +
													",747,818,858,935,619,805,369,720,303,970,719,860,203,959,475,202,302" +
													",689,407,239,850,727,321,754,954,927,352,863,386,904,561,772,786,305" +
													",941,813,478,770,470,404,762,706,678,912,229,808,515,319,563,641,712" +
													",208,217,872,312,773,464,708,224,847,779,815,618,309,331,630,317,765" +
													",574,260,219,812,913,785,316,620,606,859,502,270,504,985,225,318,337" +
													",774,508,339,781,857,617,978,351,413,443,410,301,240,207,517,810,278" +
													",679,313,586,947,248,734,269,989,906,616,231,612,320,651,763,952,218" +
													",507,636,660,975,816,573,314,557,417,769,601,662,228,406,336,252,984" +
													",919,980,910,828,704,701,402,308,603,908,848,732,551,201,862,973,609" +
													",856,575,957,505,775,702,315,518,646,347,212,718,516,917,845,631,716" +
													",585,607,914,216,330,234,567,419,440,380,740,614,283,513,937,918,580" +
													",405,503,541,971,814,717,570,878,835,484,610,267,215,724,412,401,843" +
													",864,803,605,423,865,931,615,901,731,254,325,713,940,817,430,903,806" +
													",737,512,361,210,979,936,409,972,469,214,682,832,281,830,956,432,915" +
													",435,801,385,434,804,757,703,571,276,236,540,802,509,360,564,206,425" +
													",253,715,920,262,414,608,304,307").split(",");
	
	// convert the area code array to a list of digits
    private static final long[] AREA_CODES = new long[AREA_CODE_STRS.length];
    static {
        for (int i = 0; i < AREA_CODES.length; i++)
            AREA_CODES[i] = Long.parseLong(AREA_CODE_STRS[i]);
	}
	
	public static class PhoneCall {
	    public final long voteId;
        public final int contestantNumber;
        public final long phoneNumber;
		
        protected PhoneCall(long voteId, int contestantNumber, long phoneNumber) {
            this.voteId = voteId;
            this.contestantNumber = contestantNumber;
            this.phoneNumber = phoneNumber;
        }
    }
	
	public PhoneCallGenerator(int clientId, int contestantCount) {
	    this.nextVoteId = clientId * 10000000l + 1;
        this.contestantCount = contestantCount;
        
        usedVotes = new LinkedList<Long>();
        releasedVotes = new LinkedList<Long>();
        remainingCandidates = new HashSet<Integer>();
        releasedCandidates = new ArrayList<Integer>();
        allVotes = new HashMap<Integer, ArrayList<Long>>();
        
        for(int i = 1; i <= contestantCount; i++)
        {
        	remainingCandidates.add(i);
        	allVotes.put(i, new ArrayList<Long>());
        }
		
        // This is a just a small fudge to make the geographical voting map more interesting for the benchmark!
        for(int i = 0; i < votingMap.length; i++) {
            votingMap[i] = 1;
            if (rand.nextInt(100) >= 30) {
                votingMap[i] = (int) (Math.abs(Math.sin(i)* contestantCount) % contestantCount) + 1;
            }
        }
    }
	
	public int[] getRemovedCandidates()
	{
		return ArrayUtils.toPrimitive(releasedCandidates.toArray(new Integer[releasedCandidates.size()]));
	}
	
	public void findCandidateToRemove()
	{
		int leastVotes = 100000000;
		int lowest = 0;
		if(remainingCandidates.size() == 1)
		{
			finished = true;
			return;
		}
		for(Integer candidate : remainingCandidates)
		{
			if(allVotes.get(candidate).size() < leastVotes)
			{
				leastVotes = allVotes.get(candidate).size();
				lowest = candidate;
			}
		}
		releaseVotes(lowest);
	}
	
	public void releaseVotes(int candidate)
	{
		System.out.println("REMOVING CANDIDATE " + candidate);
		assert(allVotes.get(candidate) != null);
		for(int i = 0; i < allVotes.get(candidate).size(); i++)
		{
			releasedVotes.add(allVotes.get(candidate).get(i));
			usedVotes.remove(allVotes.get(candidate).get(i));
		}
		allVotes.put(candidate, null);
		releasedCandidates.add(candidate);

		remainingCandidates.remove(candidate);
		Collections.shuffle(releasedVotes);

	}
	
	public int randomRemainingContestant()
	{
		int size = remainingCandidates.size();
    	int item = rand.nextInt(size);
    	int i = 0;
    	for(Integer val : remainingCandidates)
    	{
    		if(i == item){
    			return val;
    		}
    		i++;
    	}
    	return -1;
	}
	
	/**
     * Receives/generates a simulated voting call
     * @return Call details (calling number and contestant to whom the vote is given)
     */
    public PhoneCall receive()
    {
        // For the purpose of a benchmark, issue random voting activity
        // (including invalid votes to demonstrate transaction validationg in the database)
		
        // Pick a random area code for the originating phone call
        int areaCodeIndex = rand.nextInt(AREA_CODES.length);
        	
        long phoneNumber = 0;
        
        int contestantNumber = 1;
//        if(finished)
//        {
//        	return null;
//        }
        	
        //contestantNumber = randomRemainingContestant();
        contestantNumber = rand.nextInt(VoterDemoHStoreConstants.NUM_CONTESTANTS) + 1;
        
//        if (rand.nextInt(100) == 0) {
//        	int size = releasedCandidates.size();
//        	int item = rand.nextInt(size);
//        	int i = 0;
//        	for(Integer val : releasedCandidates)
//        	{
//        		if(i == item){
//        			contestantNumber = val;
//        			break;
//        		}
//        		i++;
//        	}
//        }
        if(!releasedVotes.isEmpty())
        {
        	phoneNumber = releasedVotes.pop();
        }
        else {
        	while(phoneNumber == 0 || usedVotes.contains(phoneNumber))
        	{
	        	// Build the phone number
	        	phoneNumber = AREA_CODES[areaCodeIndex] * 10000000L + rand.nextInt(10000000);
        	}
        	usedVotes.add(phoneNumber);
        }
        PhoneCall toSend = new PhoneCall(this.nextVoteId++, contestantNumber, phoneNumber);
        
        if(!releasedCandidates.contains(contestantNumber))
        {
        	allVotes.get(contestantNumber).add(phoneNumber);
        	validVotes++;
        }
        
        if(validVotes % VoterDemoHStoreConstants.VOTE_THRESHOLD == 0)
        {
        	findCandidateToRemove();
        }
        return toSend;
    }

}
