/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author marcc
 */
public class Tools {
    public static String readStream(InputStream is) {
        StringBuilder sb = new StringBuilder(512);
        try {
            Reader r = new InputStreamReader(is, "UTF-8");
            int c;
            while ((c = r.read()) != -1) {
                if((c <= 126 && c >=32)) sb.append((char) c);
                if((c == 0)) sb.append('`');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
    
    public static String readTablesFromStream(String fileName) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(fileName)); 
        StringBuilder sb = new StringBuilder(); 
        
        boolean rec = false;
        String line = br.readLine(); 
        while (line != null) { 
            if(line.contains("<table>")) rec = true;
            if(rec) sb.append(line.trim());
            line = br.readLine(); 
        } 
        
        return sb.toString();

    }
    
    public static ArrayList<String> search(String source, String regExPattern){
        ArrayList<String> rValue = new ArrayList<>();
        Pattern pattern = Pattern.compile(regExPattern);
        Matcher matcher = pattern.matcher(source);
        while(matcher.find()) rValue.add(matcher.group());
        return rValue;
    }
    
    public static String cleanString(String input){
        int startIndex = 0;
        for(int i=0; i<input.length(); ++i){
            char c = input.charAt(i);
            if(!(c <= 126 && c >=32)) startIndex = i+1;
            if(c == '`') startIndex = i+1;
        }
        if(startIndex < input.length()) return input.substring(startIndex);
        else return input;
    }
}
