/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData;

import java.util.ArrayList;
import microft.software.pcbuildaid.resources.EnumKeyStrings;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import microft.software.pcbuildaid.PCBuildData.HTMLParser.PCBuilderItem;
import microft.software.pcbuildaid.resources.EnumHardwareType;

/**
 *
 * @author marcc
 */
public class Hardware {
    private final PCBuilderItem sourceData;
    private final EnumHardwareType hardwareType;

    public Hardware(PCBuilderItem sourceData, EnumHardwareType hardwareType) {
        this.sourceData = sourceData;
        this.hardwareType = hardwareType;
        
    }

    public EnumHardwareType getHardwareType() {
        return hardwareType;
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
    
    public double readDoubleVal(EnumKeyStrings key){
        String v = readVal(key);
        try{
            return Double.parseDouble(v);
        } catch (NumberFormatException e){
            return Double.parseDouble(key.getDefValText());
        }
    }
        
    public boolean readBoolVal(EnumKeyStrings key){
        String v = this.readVal(key);
        return (v.equalsIgnoreCase("yes") || v.equals("1"));
    }
    
    public String readVal(EnumKeyStrings ks){
        if(ks.equals(EnumKeyStrings._MULTI_GPU_SUPPORT))
            return this.getDualGPUCompatList().stream().collect(Collectors.joining(", "));
        return readVal(ks.getKeyText(), ks.getDefValText());
    }
    
    public List<Integer> readIntValList(EnumKeyStrings key){
        ArrayList<Integer> rValue = new ArrayList<>();
        List<String> values = this.readValList(key);
        values.stream().forEach(x->rValue.add(Integer.parseInt(x)));
        return rValue;
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
    
    public int getSellPrice(){
        return this.readIntVal(EnumKeyStrings.SELL_PRICE);
    }
    
    private List<String>getDualGPUCompatList(){
        ArrayList<String> rValue = new ArrayList<>();
        if(this.readBoolVal(EnumKeyStrings.DOUBLE_GPU_SLI) || this.readBoolVal(EnumKeyStrings.SUPPORT_SLI)) rValue.add("SLI");
        if(this.readBoolVal(EnumKeyStrings.DOUBLE_GPU_SUPPORTED) || this.readBoolVal(EnumKeyStrings.SUPPORT_CROSSFIRE)) rValue.add("Crossfire");
        return rValue;
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
