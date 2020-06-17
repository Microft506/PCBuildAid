/*
 * The MIT License
 *
 * Copyright 2020 Marc Cabot.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
 * @author Marc Cabot
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
