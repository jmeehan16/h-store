/* This file is part of VoltDB.
 * Copyright (C) 2008-2012 VoltDB Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

//
// Accepts a vote, enforcing business logic: make sure the vote is for a valid
// contestant and that the voter (phone number of the caller) is not above the
// number of allowed votes.
//

package edu.brown.benchmark.microexperiments.noftriggersordered.trig1.procedures;

import org.voltdb.ProcInfo;
import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.types.TimestampType;

import edu.brown.benchmark.microexperiments.noftriggersordered.trig1.NoFTriggersConstants;

@ProcInfo (
	//partitionInfo = "a_tbl.a_id:1",
    singlePartition = true
)
public class ProcTen extends VoltProcedure {
	
	public final SQLStmt selectProcOutStmt = new SQLStmt(
			"SELECT * FROM proc_nine_out LIMIT 1"
	);
	
	public final SQLStmt insertATableStmt = new SQLStmt(
			"INSERT INTO a_tbl (a_id, a_val, proc_id, created) VALUES (?, ?, ?, ?);"
	);
    
    public final SQLStmt deleteProcOutStmt = new SQLStmt(
    		"DELETE FROM proc_nine_out WHERE a_id = ?;"
    );

    
    public VoltTable[] run() {
		voltQueueSQL(selectProcOutStmt);
		VoltTable[] v = voltExecuteSQL();
		assert(v[0].getRowCount() > 0);
		
		long rowId = v[0].fetchRow(0).getLong(0);
		long rowVal = v[0].fetchRow(0).getLong(1);
		TimestampType ts = new TimestampType();
    	// Queue up validation statements
   		voltQueueSQL(insertATableStmt,rowId,rowVal,10,ts);
    	
    	voltQueueSQL(deleteProcOutStmt,rowId);
    	voltExecuteSQL();
		
        return null;
    }
}