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

import microft.software.pcbuildaid.PCBuildData.HTMLParser.PCBuilderItemCollection;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Objects.isNull;
import microft.software.pcbuildaid.resources.EnumHardwareType;

/**
 *
 * @author Marc Cabot
 */
public class PCBuildSourceData {
    private final ArrayList<PCBuilderItemCollection> tables = new ArrayList<>();
    
    public PCBuildSourceData(String fileName) throws IOException {
        System.out.println("Reading from file: " + fileName);
        buildFromFile(fileName);
    }
    
    public void buildFromFile(String fileName) throws IOException{
               // Read the entirety of the contents into memory
        System.out.print("Reading file into memory...");
        String rawContents = Tools.readStream(new FileInputStream(fileName));
        //String rawContents = Tools.readTablesFromStream(fileName);
        System.out.println("Done.");
        System.out.println("Data size: " + rawContents.length());
        ArrayList<String> tableStrings = Tools.search(rawContents, ".{10}<table.+?<\\/table>");
        tableStrings.stream().forEach(x -> System.out.println(Tools.cleanString(x)));
        
        tableStrings.stream().forEach(x -> tables.add(new PCBuilderItemCollection(Tools.cleanString(x).trim())));
        
        // Match up all the tables
        System.out.println("Searching for tables...");      
        System.out.println("Found: " + tables.size() + " tables.");
        
        // List the tables:
        for(int i=0; i<tables.size(); ++i) System.out.println("Table " + i + " [" + tables.get(i).getName() + "]");
        
        // Populate components
        
    }
    
    public PCBuilderItemCollection getTable(EnumHardwareType name){
        ArrayList<PCBuilderItemCollection> newTables = new ArrayList<>();
        for(String key:name.getKeys()) 
            for(PCBuilderItemCollection table:tables) if(table.getName().equals(key)) newTables.add(table);
        if(newTables.isEmpty()) return null;
        StringBuilder newString = new StringBuilder();
        newTables.stream().forEach(x->newString.append(x.getSource()));
        return new PCBuilderItemCollection(newString.toString(), name.getKey());
        //for(PCBuilderItemCollection table:tables) if(table.getName().equals(name.getKey())) return table;
        //return null;
    }
    
}
