package edu.brown.benchmark.airline.util;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.brown.benchmark.airline.AirlineConstants;
import edu.brown.logging.LoggerUtil;
import edu.brown.logging.LoggerUtil.LoggerBoolean;
import edu.brown.statistics.Histogram;

public abstract class HistogramUtil {
    private static final Logger LOG = Logger.getLogger(HistogramUtil.class);
    private static final LoggerBoolean debug = new LoggerBoolean(LOG.isDebugEnabled());
    private static final LoggerBoolean trace = new LoggerBoolean(LOG.isTraceEnabled());
    static {
        LoggerUtil.attachObserver(LOG, debug, trace);
    }

//    private static final Pattern p = Pattern.compile("\\|");
    
    private static final Map<File, Histogram<String>> cached_Histograms = new HashMap<File, Histogram<String>>();
    
    private static Map<String, Histogram<String>> cached_AirportFlights; 

    private static File getHistogramFile(File data_dir, String name) {
        File file = new File(data_dir.getAbsolutePath() + File.separator + "histogram." + name.toLowerCase());
        if (file.exists() == false) file = new File(file.getAbsolutePath() + ".gz");
        return (file);
    }
    
    public static Histogram<String> collapseAirportFlights(Map<String, Histogram<String>> m) {
        Histogram<String> h = new Histogram<String>();
        for (String depart : m.keySet()) {
            Histogram<String> depart_h = m.get(depart);
            for (String arrive : depart_h.values()) {
                String key = depart + "-" + arrive;
                h.put(key, depart_h.get(arrive));
            } // FOR (arrival airport)
        } // FOR (depart airport)
        return (h);
    }
    
    /**
     * Returns the Flights Per Airport Histogram
     * @param data_path
     * @return
     * @throws Exception
     */
    public static synchronized Map<String, Histogram<String>> loadAirportFlights(File data_path) throws Exception {
        if (cached_AirportFlights != null) return (cached_AirportFlights);
        
        File file = getHistogramFile(data_path, AirlineConstants.HISTOGRAM_FLIGHTS_PER_AIRPORT);
        Histogram<String> h = new Histogram<String>();
        h.load(file.getAbsolutePath(), null);
        
        Map<String, Histogram<String>> m = new TreeMap<String, Histogram<String>>();
        Pattern pattern = Pattern.compile("-");
        Collection<String> values = h.values();
        for (String value : values) {
            String split[] = pattern.split(value);
            Histogram<String> src_h = m.get(split[0]);
            if (src_h == null) {
                src_h = new Histogram<String>();
                m.put(split[0], src_h);
            }
            src_h.put(split[1], h.get(value));
        } // FOR
        
        cached_AirportFlights = m;
        return (m);
    }
    
    /**
     * Construct a histogram from an airline-benchmark data file
     * @param name
     * @param data_path
     * @param has_header
     * @return
     * @throws Exception
     */
    public static synchronized Histogram<String> loadHistogram(String name, File data_path, boolean has_header) throws Exception {
        File file = getHistogramFile(data_path, name);
        Histogram<String> histogram = cached_Histograms.get(file);
        if (histogram == null) {
            histogram = new Histogram<String>();
            histogram.load(file.getAbsolutePath(), null);
            cached_Histograms.put(file, histogram);
        }
        
//        BufferedReader reader = FileUtil.getReader(file);
//        boolean first = true;
//        int ctr = -1;
//        while (reader.ready()) {
//            ctr++;
//            String line = reader.readLine();
//            if (first && has_header) {
//                first = false;
//                continue;
//            }
//            if (line.isEmpty()) continue;
//            
//            String data[] = p.split(line);
//            if (data.length != 2) {
//                LOG.warn("Unexpected data on line " + ctr + " in '" + name + "'");
//            } else {
//                try {
//                    String key = data[0];
//                    Integer value = Integer.valueOf(data[1].trim());
//                    histogram.put(key, value);
//                } catch (Exception ex) {
//                    throw new Exception(String.format("Failed to parse data on line %d in '%s'", ctr, name), ex);
//                }
//            }
//        } // WHILE
        
        if (debug.get()) LOG.debug(String.format("Histogram %s\n%s", name, histogram.toString()));
        
        return (histogram);
    }
}
