package com.cybershield;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Small dependency-free CSV reader for the supplied UGRansome export. */
public class DatasetLoader {
    private static String[] columns = new String[0];
    private static int skippedRows;
    public static String[] getColumns() { return columns.clone(); }
    public static int getSkippedRows() { return skippedRows; }

    public static List<SecurityRecord> load(String filePath) {
        List<SecurityRecord> records = new ArrayList<>(); skippedRows = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headerLine = br.readLine();
            if (headerLine == null) { System.out.println("Dataset is empty."); columns = new String[0]; return records; }
            columns = parseCSVLine(headerLine);
            System.out.println("\nUGRansome columns (" + columns.length + "): " + Arrays.toString(columns));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = parseCSVLine(line);
                if (row.length > columns.length) { skippedRows++; continue; }
                // Missing trailing columns and blank values are retained as N/A.
                records.add(new SecurityRecord(value(row, field("time")), value(row, field("protocol")),
                    value(row, field("flag")), value(row, field("family")), value(row, field("clusters")),
                    value(row, field("seedaddress")), value(row, field("expaddress")), value(row, field("btc")),
                    value(row, field("usd")), value(row, field("netflowbytes")), value(row, field("ipaddress")),
                    value(row, field("threats")), value(row, field("port")), value(row, field("prediction"))));
            }
            System.out.println("Loaded records: " + records.size() + "; skipped malformed rows: " + skippedRows);
        } catch (Exception e) {
            System.out.println("Dataset loading error: " + e.getMessage());
        }
        return records;
    }
    private static int field(String wanted) {
        for (int i=0;i<columns.length;i++) if (normalize(columns[i]).equals(wanted)) return i;
        return -1;
    }
    private static String normalize(String s) {
        StringBuilder b=new StringBuilder(); for(char c:s.toLowerCase().toCharArray())if(Character.isLetterOrDigit(c))b.append(c);
        String x=b.toString(); if(x.equals("protcol"))return "protocol"; if(x.equals("seddaddress"))return "seedaddress"; return x;
    }
    private static String value(String[] row,int index) { return index<0||index>=row.length||row[index].trim().isEmpty()?"N/A":row[index].trim(); }
    /** Parses one CSV record, including quoted commas and doubled quote escapes. */
    private static String[] parseCSVLine(String line) {
        List<String> values=new ArrayList<>(); StringBuilder current=new StringBuilder(); boolean quoted=false;
        for(int i=0;i<line.length();i++) { char c=line.charAt(i); if(c=='"') { if(quoted&&i+1<line.length()&&line.charAt(i+1)=='"'){current.append('"');i++;}else quoted=!quoted; }
            else if(c==','&&!quoted){values.add(current.toString());current.setLength(0);}else current.append(c); }
        values.add(current.toString()); return values.toArray(new String[0]);
    }
}
