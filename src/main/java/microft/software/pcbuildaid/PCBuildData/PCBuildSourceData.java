/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData;

import microft.software.pcbuildaid.PCBuildData.Hardware.CPU;
import microft.software.pcbuildaid.PCBuildData.HTMLParser.PCBuilderItemCollection;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Objects.isNull;
import java.util.regex.*;
import microft.software.pcbuildaid.PCBuildData.Hardware.Motherboard;
import microft.software.pcbuildaid.PCBuildData.Hardware.EnumHardwareType;

/**
 *
 * @author marcc
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
        this.populateCPUs();
        this.populateMotherboards();
    }
    
    private void populateCPUs(){
        System.out.print("Populating CPUs...");
        PCBuilderItemCollection cpuTable = this.getTable(EnumHardwareType.CPU);
        System.out.print("Found " + cpuTable.getNumRows() + " CPUs...");
        cpuTable.getRows().stream().forEach(x->{
            CPU y = new CPU(x);
            if(!isNull(y) && y.isValid()) GameData.cpus.add(y);
        });
        System.out.println(GameData.cpus.size() + " are valid.");
    }
    
    private void populateMotherboards(){
        System.out.print("Populating Motherboards...");
        PCBuilderItemCollection moboTable = this.getTable(EnumHardwareType.MOTHERBOARD);
        System.out.print("Found " + moboTable.getNumRows() + " motherboards...");
        moboTable.getRows().stream().forEach(x->{
            Motherboard m = new Motherboard(x);
            if(!isNull(m) && m.isValid()) GameData.motherboards.add(m);
        });
        System.out.println(GameData.motherboards.size() + " are valid.");
        
    }
    
    public PCBuilderItemCollection getTable(EnumHardwareType name){
        for(PCBuilderItemCollection table:tables) if(table.getName().equals(name.getKey())) return table;
        return null;
    }
    
}
