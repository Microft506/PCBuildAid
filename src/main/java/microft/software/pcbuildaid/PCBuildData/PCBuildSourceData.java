/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData;

import microft.software.pcbuildaid.PCBuildData.HTMLParser.HTMLTable;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.*;

/**
 *
 * @author marcc
 */
public class PCBuildSourceData {
    private final ArrayList<HTMLTable> tables = new ArrayList<>();
    
    public PCBuildSourceData() {
        
    }
    
    public void buildFromFile(String fileName) throws IOException{
               // Read the entirety of the contents into memory
        System.out.print("Reading file into memory...");
        String rawContents = Tools.readStream(new FileInputStream(fileName));
        //String rawContents = Tools.readTablesFromStream(fileName);
        System.out.println("Done.");
        System.out.println("Data size: " + rawContents.length());
        ArrayList<String> tableStrings = Tools.search(rawContents, ".{10}<table.+?<\\/table>");
        System.out.println("Checkpoint.");
        tableStrings.stream().forEach(x -> System.out.println(Tools.cleanString(x)));
        
        tableStrings.stream().forEach(x -> tables.add(new HTMLTable(Tools.cleanString(x).trim())));
        
        // Match up all the tables
        System.out.println("Searching for tables...");      
        System.out.println("Found: " + tables.size() + " tables.");
        
        // List the tables:
        for(int i=0; i<tables.size(); ++i) System.out.println("Table " + i + " [" + tables.get(i).getName() + "]");
                
    }
    
    public HTMLTable getTable(String name){
        for(HTMLTable table:tables) if(table.getName().equals(name)) return table;
        return null;
    }
    
    
}
