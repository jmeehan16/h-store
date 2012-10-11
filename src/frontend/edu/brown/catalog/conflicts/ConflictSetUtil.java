package edu.brown.catalog.conflicts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.voltdb.catalog.ConflictPair;
import org.voltdb.catalog.ConflictSet;
import org.voltdb.catalog.Database;
import org.voltdb.catalog.Procedure;
import org.voltdb.catalog.Table;

import edu.brown.catalog.CatalogUtil;

public abstract class ConflictSetUtil {

    /**
     * Return the set of all Tables referenced in the collection of ConflictPairs
     * @param conflicts
     * @return
     */
    public static Collection<Table> getAllTables(Collection<ConflictPair> conflicts) {
        Set<Table> tables = new HashSet<Table>();
        for (ConflictPair cp : conflicts) {
            tables.addAll(CatalogUtil.getTablesFromRefs(cp.getTables()));
        } // FOR
        return (tables);
    }

    /**
     * Get the Procedure handles that are marked as Write-Write conflicting for the
     * given Procedure
     * @param catalog_proc
     * @return
     */
    public static Collection<Procedure> getWriteWriteConflicts(Procedure catalog_proc) {
        List<Procedure> conflicts = new ArrayList<Procedure>();
        Database catalog_db = CatalogUtil.getDatabase(catalog_proc);
        for (String procName : catalog_proc.getConflicts().keySet()) {
            ConflictSet cs = catalog_proc.getConflicts().get(procName);
            if (cs.getWritewriteconflicts().isEmpty() == false) {
                conflicts.add(catalog_db.getProcedures().get(procName));
            }
        } // FOR
        return (conflicts);
    }

    /**
     * Get the Procedure handles that have any conflict with the given Procedure
     * @param catalog_proc
     * @return
     */
    public static Collection<Procedure> getAllConflicts(Procedure catalog_proc) {
        List<Procedure> conflicts = new ArrayList<Procedure>();
        Database catalog_db = CatalogUtil.getDatabase(catalog_proc);
        for (ConflictSet cs : catalog_proc.getConflicts().values()) {
            if (cs.getReadwriteconflicts().isEmpty() == false) {
                conflicts.add(catalog_db.getProcedures().get(cs.getName()));
            }
            if (cs.getWritewriteconflicts().isEmpty() == false) {
                conflicts.add(catalog_db.getProcedures().get(cs.getName()));
            }
        } // FOR
        return (conflicts);
    }

    /**
     * Get the Procedure handles that are marked as Read-Write conflicting for the
     * given Procedure
     * @param catalog_proc
     * @return
     */
    public static Collection<Procedure> getReadWriteConflicts(Procedure catalog_proc) {
        List<Procedure> conflicts = new ArrayList<Procedure>();
        Database catalog_db = CatalogUtil.getDatabase(catalog_proc);
        for (ConflictSet cs : catalog_proc.getConflicts().values()) {
            if (cs.getReadwriteconflicts().isEmpty() == false) {
                conflicts.add(catalog_db.getProcedures().get(cs.getName()));
            }
        } // FOR
        return (conflicts);
    }
    
}
