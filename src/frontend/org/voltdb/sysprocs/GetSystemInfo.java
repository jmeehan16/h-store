package org.voltdb.sysprocs;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.voltdb.DependencySet;
import org.voltdb.ParameterSet;
import org.voltdb.ProcInfo;
import org.voltdb.VoltSystemProcedure;
import org.voltdb.VoltTable;
import org.voltdb.VoltTable.ColumnInfo;
import org.voltdb.VoltType;
import org.voltdb.catalog.Column;
import org.voltdb.catalog.Index;
import org.voltdb.catalog.ProcParameter;
import org.voltdb.catalog.Procedure;
import org.voltdb.catalog.Table;

import edu.brown.hstore.PartitionExecutor.SystemProcedureExecutionContext;

/**
 * Access the meta data of the database. Tables, views, columns, procedures, indexes etc.
 */
@ProcInfo(
    singlePartition = false
)

public class GetSystemInfo extends VoltSystemProcedure {
    private static final Logger HOST_LOG = Logger.getLogger(GetSystemInfo.class);

    @Override
    public void initImpl() {}

    @Override
    public DependencySet executePlanFragment(Long txn_id,
                                             Map<Integer, List<VoltTable>> dependencies,
                                             int fragmentId,
                                             ParameterSet params,
                                             SystemProcedureExecutionContext context) {
    	return null;
    }

    /**
     * Returns specific info about database depending on selector.
     * requested.
     * @param selector     Selector requested TABLES, COLUMNS, PROCEDURES, INDEXES etc
     * @return             The information about the specified selector.
     * @throws VoltAbortException
     */
    public VoltTable run(String selector) throws VoltAbortException {
    	VoltTable result = null;
    	
        int i = 0;
        
        /*
         * access the specified information type and return as a table.
         */
        if(selector.equals("TABLES")){
        	HOST_LOG.log(Priority.INFO, "accessing table info");
        	
        	ColumnInfo c1 = new ColumnInfo("TABLE_NAME", VoltType.STRING);
        	ColumnInfo c2 = new ColumnInfo("TABLE_TYPE", VoltType.STRING);
    		ColumnInfo c3 = new ColumnInfo("ROW_COUNT", VoltType.INTEGER);
    		ColumnInfo c4 = new ColumnInfo("COL_COUNT", VoltType.INTEGER);
    		
    		result = new VoltTable(c1, c2, c3, c4);
            Collection<Table> dataTables = catalogContext.getDataTables();
            Collection<Table> viewTables = catalogContext.getViewTables();
        	
        	for(Table t : dataTables){
        		result.addRow(t.fullName(), "TABLE", t.getEstimatedtuplecount(), t.getColumns().size());
        		i++;
        	}
        	
        	for(Table t : viewTables){
        		result.addRow(t.fullName(), "VIEW", t.getEstimatedtuplecount(), t.getColumns().size());
        		i++;
        	}
        } else if (selector.equals("COLUMNS")){
        	HOST_LOG.log(Priority.INFO, "accessing column info");
        	
        	ColumnInfo c1 = new ColumnInfo("TABLE_NAME", VoltType.STRING);
        	ColumnInfo c2 = new ColumnInfo("COLUMN_NAME", VoltType.STRING);
        	ColumnInfo c3 = new ColumnInfo("COLUMN_TYPE", VoltType.STRING);
        	
        	result = new VoltTable(c1, c2, c3);
            Collection<Table> dataTables = catalogContext.getDataTables();
            Collection<Table> viewTables = catalogContext.getViewTables();
        	
        	for(Table t : dataTables){
        		Collection<Column> c = t.getColumns();
        		for(Column val : c){
        			result.addRow(t.fullName(), val.getName(), val.getTypeName());
        		}
        	}
        	for(Table t : viewTables){
        		Collection<Column> c = t.getColumns();
        		for(Column val : c){
        			
        			result.addRow(t.fullName(), val.getName(), val.getTypeName());
        		}
        	}
        	
        } else if (selector.equals("INDEXINFO")){
        	HOST_LOG.log(Priority.INFO, "accessing index info");
        	
        	ColumnInfo c1 = new ColumnInfo("TABLE_NAME", VoltType.STRING);
        	ColumnInfo c2 = new ColumnInfo("INDEX_NAME", VoltType.STRING);
        	ColumnInfo c3 = new ColumnInfo("NON_UNIQUE", VoltType.BOOLEAN);
        	
        	result = new VoltTable(c1, c2, c3);
            Collection<Table> dataTables = catalogContext.getDataTables();
            Collection<Table> viewTables = catalogContext.getViewTables();
        	
        	for(Table t : dataTables){
        		Collection<Index> indexes = t.getIndexes();
        		for(Index val : indexes){
        			result.addRow(t.fullName(), val.getName(), !val.getUnique());
        		}
        	}
        	
        	for(Table t : viewTables){
        		Collection<Index> indexes = t.getIndexes();
        		for(Index val : indexes){
        			result.addRow(t.fullName(), val.getName(), !val.getUnique());
        		}
        	}
        } else if(selector.equals("PRIMARYKEYS")){
        	ColumnInfo c1 = new ColumnInfo("TABLE_NAME", VoltType.STRING);
        	ColumnInfo c2 = new ColumnInfo("PK_NAME", VoltType.STRING);
        	
        	result = new VoltTable(c1, c2);
            Collection<Table> dataTables = catalogContext.getDataTables();
            Collection<Table> viewTables = catalogContext.getViewTables();
        	
        	for(Table t : dataTables){
        		
        		Collection<Index> indexes = t.getIndexes();
        		for(Index val : indexes){
        			if(val.getName().contains("PK") && val.getUnique()){
        				result.addRow(t.fullName(), val.getName());
        			}	
        		}
        	}
        	
        	for(Table t : viewTables){
        		Collection<Index> indexes = t.getIndexes();
        		for(Index val : indexes){
        			if(val.getName().contains("PK") && val.getUnique()){
        				result.addRow(t.fullName(), val.getName());
        			}
        		}
        	}
        	
        } else if (selector.equals("PROCEDURES")) {
        	HOST_LOG.log(Priority.INFO, "accessing procedure info");
        	
        	ColumnInfo c1 = new ColumnInfo("PROCEDURE_NAME", VoltType.STRING);
        	
        	Collection<Procedure> procedures = catalogContext.getRegularProcedures();
        	
        	result = new VoltTable(c1);
        	
        	for(Procedure proc : procedures){
        		result.addRow(proc.getName());
        	}
        } else if(selector.equals("PROCEDURECOLUMNS")){
        	HOST_LOG.log(Priority.INFO, "accessing procedure column info");
        	
        	ColumnInfo c1 = new ColumnInfo("PROCEDURE_NAME", VoltType.STRING);
        	ColumnInfo c2 = new ColumnInfo("COLUMN_NAME", VoltType.STRING);
        	
        	Collection<Procedure> procedures = catalogContext.getRegularProcedures();
        	
        	result = new VoltTable(c1, c2);
           
        	for(Procedure proc : procedures){
        		for(ProcParameter v : proc.getParameters()){
        			result.addRow(proc.getName(), v.fullName());
        		}
        		
        	}
        }
        
        
        return result;
    }
}
