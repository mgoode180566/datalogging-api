package com.example.vbolaps.service;

import com.example.vbolaps.utils.VBoxConverter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VboParser {
    public static class Record {
        public Map<String, Double> values = new LinkedHashMap<>();
    }
    public static class Parsed {
        public Map<String, String> header = new LinkedHashMap<>();
        public List<String> columns = new ArrayList<>();
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
                    p.columns.add(t.trim());
                }
            }
            
            if (t.equalsIgnoreCase("[column names]")) {
                inCols=true;
                inData=false;
                continue; }
            
            if (inCols) {
                String[] parts = t.split("\s+");
                p.columns.addAll(Arrays.asList(parts));
                inCols=false; inData=true;
                continue;
            }
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
                for (int i=0; i<Math.min(parts.length, p.columns.size()); i++) {
                    try {
                        r.values.put(p.columns.get(i), Double.parseDouble(parts[i]));
                    } catch (NumberFormatException e) {
                        r.values.put(p.columns.get(i), Double.NaN);
                    }
                }
                p.rows.add(r);
            }
        }
        return p;
    }
}
