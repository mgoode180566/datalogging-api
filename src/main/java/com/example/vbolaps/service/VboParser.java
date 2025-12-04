package com.example.vbolaps.service;

import com.example.vbolaps.utils.VBoxConverter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VboParser {
    
    String[] FixedColumns = {"satellites", "time", "latitude", "longitude", "velocity kmh", "heading", "height", "vertical velocity m/s", "sampleperiod", "solution type", "avifileindex", "avisynctime"};
    
    public static class Record {
        public Map<String, Double> baseValues = new LinkedHashMap<>();
        public Map<String, Double> channelValues = new LinkedHashMap<>();
    }
    public static class Parsed {
        public Map<String, String> header = new LinkedHashMap<>();
        public List<String> fixedColumns = new ArrayList<>();
        public List<String> channelColumns = new ArrayList<>();
        public List<String> headers = new ArrayList<>();
        public List<Record> rows = new ArrayList<>();
        public Optional<double[]> start1 = Optional.empty(); // lat, lon from [laptiming] if present
        public Optional<double[]> start2 = Optional.empty();
        public Map<String,String> sessionMeta = new LinkedHashMap<>();
    }

    public Parsed parse(InputStream in) throws IOException {
        Parsed p = new Parsed();
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line;
        boolean inCols=false, inData=false;
        Pattern startLine = Pattern.compile("^Start\\s+([+\\-]?[0-9.]+)\\s+([+\\-]?[0-9.]+)\\s+([+\\-]?[0-9.]+)\\s+([+\\-]?[0-9.]+).*$");
        
        
        while ((line = br.readLine()) != null) {
            String t = line.trim();
            
            if (t.equalsIgnoreCase("[header]")) {
                // we are in the list of headers
                while ( (t = br.readLine()) != null) {
                    if (t.isEmpty()) break;
                    p.headers.add(t.trim());
//                    p.fixedColumns.add(t);
//                    if (!Arrays.asList(FixedColumns).contains(t.trim())) {
//                        p.channelColumns.add(t.trim());
//                    }
                }
            }
            
            if (t.equalsIgnoreCase("[column names]")) {
                inCols=true;
                inData=false;
                continue; }
            
//            if (inCols) {
//                String[] parts = t.split("\s+");
//                p.columns.addAll(Arrays.asList(parts));
//                inCols=false; inData=true;
//                continue;
//            }
            if (t.equalsIgnoreCase("[data]")) { inData=true; continue; }
            if (t.equalsIgnoreCase("[laptiming]")) {
                // scan next lines for Start ... pattern
                while ((line = br.readLine()) != null) {
                    t = line.trim();
                    if (t.startsWith("[")) break;
                    Matcher m = startLine.matcher(t);
                    if (m.find()) {
                        try {
                            double alon = VBoxConverter.convertRawLongitude(Double.parseDouble(m.group(1)));
                            double alat = VBoxConverter.convertRawLatitude(Double.parseDouble(m.group(2)));
                            p.start1 = Optional.of(new double[]{alat, alon});
                            
                            
                            double blon = VBoxConverter.convertRawLongitude(Double.parseDouble(m.group(3)));
                            double blat = VBoxConverter.convertRawLatitude(Double.parseDouble(m.group(4)));
                            p.start2 = Optional.of(new double[]{blat, blon});
                            
                        } catch (Exception ignored) {}
                    }
                }
                if (line == null) break;
                // fallthrough to normal processing of the new section
                t = line.trim();
            }
            if (t.equalsIgnoreCase("[session data]")) {
                while ((line = br.readLine()) != null) {
                    t = line.trim();
                    if (t.startsWith("[")) { break; }
                    if (!t.isEmpty()) {
                        String[] kv = t.split("\s+", 2);
                        if (kv.length==2) p.sessionMeta.put(kv[0].toLowerCase(), kv[1]);
                    }
                }
                if (line == null) break;
                t = line.trim();
            }
            if (inData) {
                if (t.isEmpty() || t.startsWith("[")) { inData=false; continue; }
                String[] parts = t.split("\s+");
                Record r = new Record();
                for (int i=0; i<Math.min(parts.length, p.headers.size()); i++) {
                    try {
                        r.baseValues.put(p.headers.get(i), Double.parseDouble(parts[i]));
                    } catch (NumberFormatException ex) {
                        r.baseValues.put(p.headers.get(i), Double.NaN);
                    }
//                    if (Arrays.asList(FixedColumns).contains(p.fixedColumns.get(i))) {
//                        try {
//                            r.baseValues.put(p.fixedColumns.get(i), Double.parseDouble(parts[i]));
//                        } catch (NumberFormatException e) {
//                            r.baseValues.put(p.fixedColumns.get(i), Double.NaN);
//                        }
//                    } else {
//                        try {
//                            r.channelValues.put(p.fixedColumns.get(i), Double.parseDouble(parts[i]));
//                        } catch (NumberFormatException e) {
//                            r.channelValues.put(p.fixedColumns.get(i), Double.NaN);
//                        }
//                    }
//                    }
                }
                p.rows.add(r);
            }
        }
        return p;
    }
}
