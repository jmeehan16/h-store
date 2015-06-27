/***************************************************************************
 *  Copyright (C) 2012 by H-Store Project                                  *
 *  Brown University                                                       *
 *  Massachusetts Institute of Technology                                  *
 *  Yale University                                                        *
 *                                                                         *
 *  http://hstore.cs.brown.edu/                                            *
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
package edu.brown.utils;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ProjectType {

    TPCC("TPC-C", "org.voltdb.benchmark.tpcc"),
    TPCE("TPC-E", "edu.brown.benchmark.tpce"),
    TM1("TM1", "edu.brown.benchmark.tm1"),
    SIMPLE("Simple", "edu.brown.benchmark.simple"),
    SEATS("SEATS", "edu.brown.benchmark.seats"),
    MARKOV("Markov", "edu.brown.benchmark.markov"),
    BINGO("Bingo", "org.voltdb.benchmark.bingo"),
    AUCTIONMARK("AuctionMark", "edu.brown.benchmark.auctionmark"),
    LOCALITY("Locality", "edu.brown.benchmark.locality"),
    MAPREDUCE("MapReduce", "edu.brown.benchmark.mapreduce"),
    WIKIPEDIA("Wikipedia", "edu.brown.benchmark.wikipedia"),
    YCSB("YCSB", "edu.brown.benchmark.ycsb"), 
    VOTER("Voter", "edu.brown.benchmark.voter"),
    SMALLBANK("SmallBank", "edu.brown.benchmark.smallbank"),
    EXAMPLE("Example", "edu.brown.benchmark.example"),
    ARTICLES ("Articles", "edu.brown.benchmark.articles"),
    USERS ("Users", "edu.brown.benchmark.users"),
    TEST("Test", null),

     MICROEXPNOFTRIGGERS("MicroExpNoFTriggers", "edu.brown.benchmark.microexperiments.noftriggers.orig"),
    MICROEXPNOFTRIGGERSTRIG1("MicroExpNoFTriggersTrig1", "edu.brown.benchmark.microexperiments.noftriggers.trig1"),
    MICROEXPNOFTRIGGERSTRIG2("MicroExpNoFTriggersTrig2", "edu.brown.benchmark.microexperiments.noftriggers.trig2"),
    MICROEXPNOFTRIGGERSTRIG3("MicroExpNoFTriggersTrig3", "edu.brown.benchmark.microexperiments.noftriggers.trig3"),
    MICROEXPNOFTRIGGERSTRIG4("MicroExpNoFTriggersTrig4", "edu.brown.benchmark.microexperiments.noftriggers.trig4"),
    MICROEXPNOFTRIGGERSTRIG5("MicroExpNoFTriggersTrig5", "edu.brown.benchmark.microexperiments.noftriggers.trig5"),
    MICROEXPNOFTRIGGERSTRIG6("MicroExpNoFTriggersTrig6", "edu.brown.benchmark.microexperiments.noftriggers.trig6"),
    MICROEXPNOFTRIGGERSTRIG7("MicroExpNoFTriggersTrig7", "edu.brown.benchmark.microexperiments.noftriggers.trig7"),
    MICROEXPNOFTRIGGERSTRIG8("MicroExpNoFTriggersTrig8", "edu.brown.benchmark.microexperiments.noftriggers.trig8"),
    MICROEXPNOFTRIGGERSTRIG9("MicroExpNoFTriggersTrig9", "edu.brown.benchmark.microexperiments.noftriggers.trig9"),
    MICROEXPNOFTRIGGERSTRIG10("MicroExpNoFTriggersTrig10", "edu.brown.benchmark.microexperiments.noftriggers.trig10"),
    
    MICROEXPNOFTRIGGERSORDERED("MicroExpNoFTriggersOrdered", "edu.brown.benchmark.microexperiments.noftriggersordered.orig"),
    MICROEXPNOFTRIGGERSORDEREDTRIG1("MicroExpNoFTriggersOrderedTrig1", "edu.brown.benchmark.microexperiments.noftriggersordered.trig1"),
    MICROEXPNOFTRIGGERSORDEREDTRIG2("MicroExpNoFTriggersOrderedTrig2", "edu.brown.benchmark.microexperiments.noftriggersordered.trig2"),
    MICROEXPNOFTRIGGERSORDEREDTRIG3("MicroExpNoFTriggersOrderedTrig3", "edu.brown.benchmark.microexperiments.noftriggersordered.trig3"),
    MICROEXPNOFTRIGGERSORDEREDTRIG4("MicroExpNoFTriggersOrderedTrig4", "edu.brown.benchmark.microexperiments.noftriggersordered.trig4"),
    MICROEXPNOFTRIGGERSORDEREDTRIG5("MicroExpNoFTriggersOrderedTrig5", "edu.brown.benchmark.microexperiments.noftriggersordered.trig5"),
    MICROEXPNOFTRIGGERSORDEREDTRIG6("MicroExpNoFTriggersOrderedTrig6", "edu.brown.benchmark.microexperiments.noftriggersordered.trig6"),
    MICROEXPNOFTRIGGERSORDEREDTRIG7("MicroExpNoFTriggersOrderedTrig7", "edu.brown.benchmark.microexperiments.noftriggersordered.trig7"),
    MICROEXPNOFTRIGGERSORDEREDTRIG8("MicroExpNoFTriggersOrderedTrig8", "edu.brown.benchmark.microexperiments.noftriggersordered.trig8"),
    MICROEXPNOFTRIGGERSORDEREDTRIG9("MicroExpNoFTriggersOrderedTrig9", "edu.brown.benchmark.microexperiments.noftriggersordered.trig9"),
    MICROEXPNOFTRIGGERSORDEREDTRIG10("MicroExpNoFTriggersOrderedTrig10", "edu.brown.benchmark.microexperiments.noftriggersordered.trig10"),
    
    MICROEXPNOROUTETRIGTRIG("MicroExpNoRouteTrigTrig", "edu.brown.benchmark.microexperiments.noroutetrig.orig"),
    MICROEXPNOROUTETRIGTRIG1("MicroExpNoRouteTrigTrig1", "edu.brown.benchmark.microexperiments.noroutetrig.trig1"),
    MICROEXPNOROUTETRIGTRIG2("MicroExpNoRouteTrigTrig2", "edu.brown.benchmark.microexperiments.noroutetrig.trig2"),
    MICROEXPNOROUTETRIGTRIG3("MicroExpNoRouteTrigTrig3", "edu.brown.benchmark.microexperiments.noroutetrig.trig3"),
    MICROEXPNOROUTETRIGTRIG4("MicroExpNoRouteTrigTrig4", "edu.brown.benchmark.microexperiments.noroutetrig.trig4"),
    MICROEXPNOROUTETRIGTRIG5("MicroExpNoRouteTrigTrig5", "edu.brown.benchmark.microexperiments.noroutetrig.trig5"),
    MICROEXPNOROUTETRIGTRIG6("MicroExpNoRouteTrigTrig6", "edu.brown.benchmark.microexperiments.noroutetrig.trig6"),
    MICROEXPNOROUTETRIGTRIG7("MicroExpNoRouteTrigTrig7", "edu.brown.benchmark.microexperiments.noroutetrig.trig7"),
    MICROEXPNOROUTETRIGTRIG8("MicroExpNoRouteTrigTrig8", "edu.brown.benchmark.microexperiments.noroutetrig.trig8"),
    MICROEXPNOROUTETRIGTRIG9("MicroExpNoRouteTrigTrig9", "edu.brown.benchmark.microexperiments.noroutetrig.trig9"),
    MICROEXPNOROUTETRIGTRIG10("MicroExpNoRouteTrigTrig10", "edu.brown.benchmark.microexperiments.noroutetrig.trig10"),

    VOTERDEMOHSTOREWXSYY("VoterDemoHStoreWXSYY", "edu.brown.benchmark.voterexperiments.demohstore.wXsYY"),
    VOTERDEMOHSTOREFILE("VoterDemoHStoreFile", "edu.brown.benchmark.voterexperiments.demohstore.file"),
    VOTERDEMOHSTORESTAGEFLAG("VoterDemoHStoreStageFlag", "edu.brown.benchmark.voterexperiments.demohstore.stageflag"),
    VOTERDEMOHSTOREASYNCHFILE("VoterDemoHStoreAsynchFile", "edu.brown.benchmark.voterexperiments.demohstore.asynchfile"),
    //VOTERDEMOHSTOREPARAMPROC("VoterDemoHStoreParamProc", "edu.brown.benchmark.voterexperiments.demohstore.paramproc"),
    ;


    private final String package_name;
    private final String benchmark_name;

    private ProjectType(String benchmark_name, String package_name) {
        this.benchmark_name = benchmark_name;
        this.package_name = package_name;
    }

    public String getBenchmarkName() {
        return (this.benchmark_name);
    }

    public String getBenchmarkPrefix() {
        return (this.benchmark_name.replace("-", ""));
    }

    /**
     * Returns the package name for where this We need this because we need to
     * be able to dynamically reference various things from the 'src/frontend'
     * directory before we compile the 'tests/frontend' directory
     * 
     * @return
     */
    public String getPackageName() {
        return (this.package_name);
    }

    protected static final Map<Integer, ProjectType> idx_lookup = new HashMap<Integer, ProjectType>();
    protected static final Map<String, ProjectType> name_lookup = new HashMap<String, ProjectType>();
    static {
        for (ProjectType vt : EnumSet.allOf(ProjectType.class)) {
            ProjectType.idx_lookup.put(vt.ordinal(), vt);
            ProjectType.name_lookup.put(vt.name().toLowerCase().intern(), vt);
        } // FOR
    }

    public static ProjectType get(Integer idx) {
        return (ProjectType.idx_lookup.get(idx));
    }

    public static ProjectType get(String name) {
        return (ProjectType.name_lookup.get(name.toLowerCase().intern()));
    }
    
    /**
     * Attempt to find a specific file from the supplemental files directory.
     * @param current
     * @param target_dir
     * @param target_ext
     * @return
     * @throws IOException
     */
    public File getProjectFile(File current, String target_dir, String target_ext) throws IOException {
        boolean has_svn = false;
        for (File file : current.listFiles()) {
            if (file.getCanonicalPath().endsWith("files") && file.isDirectory()) {
                // Look for either a .<target_ext> or a .<target_ext>.gz file
                String file_name = this.name().toLowerCase() + target_ext;
                for (int i = 0; i < 2; i++) {
                    if (i > 0) file_name += ".gz";
                    File target_file = new File(file + File.separator + target_dir + File.separator + file_name);
                    if (target_file.exists() && target_file.isFile()) {
                        return (target_file);
                    }
                } // FOR
                assert(false) : "Unable to find '" + file_name + "' for '" + this + "' in directory '" + file + "'";
            // Make sure that we don't go to far down...
            } else if (file.getCanonicalPath().endsWith("/.svn")) {
                has_svn = true;
            }
        } // FOR
        assert(has_svn) : "Unable to find files directory [last_dir=" + current.getAbsolutePath() + "]";  
        File next = new File(current.getCanonicalPath() + File.separator + "..");
        return (this.getProjectFile(next, target_dir, target_ext));
    }
}
