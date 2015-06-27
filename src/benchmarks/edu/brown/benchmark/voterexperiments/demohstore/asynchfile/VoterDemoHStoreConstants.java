/***************************************************************************
 *  Copyright (C) 2012 by H-Store Project                                  *
 *  Brown University                                                       *
 *  Massachusetts Institute of Technology                                  *
 *  Yale University                                                        *
 *                                                                         *
 *  Original By: VoltDB Inc.											   *
 *  Ported By:  Justin A. DeBrabant (http://www.cs.brown.edu/~debrabant/)  *                                                                      *
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

public abstract class VoterDemoHStoreConstants {

    public static final String TABLENAME_CONTESTANTS     = "contestants";
    public static final String TABLENAME_AREA_CODE_STATE = "area_code_state";
    public static final String TABLENAME_VOTES           = "votes";
    
    public static final int VOTE_THRESHOLD = 20000;
	public static final int MAX_VOTES = 1; 
	public static final int NUM_CONTESTANTS = 50; 

	// Initialize some common constants and variables
	public static String ROOT_DIR = "/home/jlmeehan/git/esper-voter/";
	public static String VOTE_DIR_SUFFIX = "data/";
	public static String BENCHMARK_DIR_SUFFIX = "voter-benchmark/";
	public static String DDL_DIR_SUFFIX = BENCHMARK_DIR_SUFFIX + "src/main/java/edu/brown/benchmark/voteresper/voltsp/";
	public static String VOTE_DIR = ROOT_DIR + VOTE_DIR_SUFFIX;
	public static String BENCHMARK_DIR = ROOT_DIR + BENCHMARK_DIR_SUFFIX;
	public static String DDL_DIR = ROOT_DIR + DDL_DIR_SUFFIX;
	public static String DDL_FILE = "voter-voltdb.sql";
	public static String LOAD_DB_FILE = "reloadDB.sh"; 
	public static String VOTE_FILE = "votes-50-20000_1.txt";
	//public static int QUEUE_SIZE = 10000;
	public static long SLEEP_TIME = 2;

	public static final String LOCAL_HOST = "localhost";

	// Initialize some common constants and variables
    public static final String CONTESTANT_NAMES_CSV = "Jann Arden,Micah Barnes,Justin Bieber,Jim Bryson,Michael Buble," +
    													"Leonard Cohen,Celine Dion,Nelly Furtado,Adam Gontier,Emily Haines," +
    													"Avril Lavigne,Ashley Leggat,Eileen McGann,Sarah McLachlan,Joni Mitchell," +
    													"Mae Moore,Alanis Morissette,Emilie Mover,Anne Murray,Sam Roberts," +
    													"Serena Ryder,Tamara Sandor,Nicholas Scribner,Shania Twain,Neil Young," + 
    													"Aann Jrden,Bicah Marnes,Bustin Jieber,Bim Jryson,Bichael Muble," +
    													"Ceonard Lohen,Deline Cion,Felly Nurtado,Gdam Aontier,Hmily Eaines," +
    													"Lvril Aavigne,Lshley Aeggat,Mileen EcGann,Marah ScLachlan,Moni Jitchell," +
    													"Nae Noore,Mlanis Aorissette,Mmilie Eover,Mnne Aurray,Ram Soberts," +
    													"Rerena Syder,Samara Tandor,Sicholas Ncribner,Thania Swain,Yeil Noung";
    // potential return codes
    public static final long VOTE_SUCCESSFUL = -9;
    public static final long ERR_INVALID_CONTESTANT = -1;
    public static final long ERR_VOTER_OVER_VOTE_LIMIT = -2;
    public static final long ERR_NO_VOTE_FOUND = -3;
    public static final long DELETE_CONTESTANT = -4;
    public static final long WINDOW_SUCCESSFUL = -5;
    public static final long ERR_NOT_ENOUGH_CONTESTANTS = -6;
    public static final long DELETE_SUCCESSFUL = -7;
    public static final long NOTHING_TO_DELETE = -8;
    
    public static final long WINDOW_SIZE = 100;
    public static final long SLIDE_SIZE = 10;
}
