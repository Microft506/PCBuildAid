/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData.Hardware;

import microft.software.pcbuildaid.PCBuildData.HTMLParser.HTMLTableRow;

/**
 *
 * @author Marc
 */
public class CPU extends Hardware{
    public CPU(HTMLTableRow sourceData) {
        super(sourceData);
    }
   
    public boolean isValid(){
        String c = this.sourceData.getCellData("In Game", "No");
        String a = this.sourceData.getCellData("Imported to Game", "No");
        String b = this.sourceData.getCellData("Working in game", "No");
        String d = this.sourceData.getCellData("In Shop", "No");
        return (a.equals("Yes")) && (b.equals("Yes")) && (c.equals("Yes")) && (d.equals("Yes"));
    }
    
    public String getSocketType(){
        return sourceData.getCellData("CPU Socket");
    }
    
    public String getManufacturer(){
        return sourceData.getCellData("Manufacturer");
    }
    
    public String getPartName(){
        return sourceData.getCellData("Part Name");
    }
    
    public int getLevel(){
        return this.readIntVal("Level");
    }
    
    public int getPrice(){
        return this.readIntVal("Price");
    }
    
    public int getBaselineScore(){
        return this.readIntVal("Basic CPU Score");
    }
    
    public String getConcatName(){
        return this.readVal("Concat name", "Unknown Name");
    }
    
}
