/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData.Hardware.Base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import microft.software.pcbuildaid.PCBuildData.HTMLParser.PCBuilderItem;

/**
 *
 * @author marcc
 */
public abstract class Hardware {
    protected PCBuilderItem sourceData;

    public Hardware(PCBuilderItem sourceData) {
        this.sourceData = sourceData;
    }
    
    public int readIntVal(EnumKeyStrings key){
        return readIntVal(key.getKeyText(), Integer.parseInt(key.getDefValText()));
    }
    
    private int readIntVal(String key, int def){
        String v = sourceData.getCellData(key);
        try{
            return Integer.parseInt(v);
        } catch(NumberFormatException e){
            return def;
        }
    }
    
    public boolean readBoolVal(EnumKeyStrings key){
        String v = this.readVal(key);
        return (v.equalsIgnoreCase("yes") || v.equals("1"));
    }
    
    public String readVal(EnumKeyStrings ks){
        return readVal(ks.getKeyText(), ks.getDefValText());
    }
    
    public List<String> readValList(EnumKeyStrings ks){
        return Arrays.asList(this.readVal(ks).split("\\s*,\\s*"));
    }
    
    private String readVal(String key, String def){
        return sourceData.getCellData(key, def);
    }
    
    public String getConcatName(){
        return this.readVal(EnumKeyStrings.CONCAT_NAME);
    }
    
    public String getManufacturer(){
        return this.readVal(EnumKeyStrings.MANUFACTURER);
    }
    
    public String getPartName(){
        return this.readVal(EnumKeyStrings.PART_NAME);
    }
    
    public int getLevel(){
        return this.readIntVal(EnumKeyStrings.LEVEL);
    }
    
    public int getPrice(){
        return this.readIntVal(EnumKeyStrings.PRICE);
    }
    
    public boolean isValid(){
        String a = this.readVal(EnumKeyStrings.IN_GAME);
        String b = this.readVal(EnumKeyStrings.IMPORTED_TO_GAME);
        String c = this.readVal(EnumKeyStrings.WORKING_IN_GAME);
        String d = this.readVal(EnumKeyStrings.IN_SHOP);
        String e = this.getConcatName();
        return (a.equals("Yes")) && (b.equals("Yes")) && (c.equals("Yes")) && (d.equals("Yes")) && !e.equalsIgnoreCase("unknown");
    }
    
    
}
