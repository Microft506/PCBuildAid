/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData.Hardware;

import microft.software.pcbuildaid.resources.EnumKeyStrings;
import microft.software.pcbuildaid.PCBuildData.Hardware.Base.Hardware;
import microft.software.pcbuildaid.PCBuildData.HTMLParser.PCBuilderItem;

/**
 *
 * @author Marc
 */
public class CPU extends Hardware{
    public CPU(PCBuilderItem sourceData) {
        super(sourceData);
    }
          
    public int getBaselineScore(){
        return this.readIntVal(EnumKeyStrings.BASIC_CPU_SCORE);
    }

    public String getSocketType() {
        return this.readVal(EnumKeyStrings.CPU_SOCKET);
    }
    
    public int getNumberOfCores(){
        return this.readIntVal(EnumKeyStrings.CORES);
    }
    
    public boolean isOverclockable(){
        return this.readBoolVal(EnumKeyStrings.CAN_OVERCLOCK);
    }
    
    public int getMaxFreqKhz(){
        return this.readIntVal(EnumKeyStrings.MAX_FREQ);
    }
    
    public int getWattage(){
        return this.readIntVal(EnumKeyStrings.WATTAGE);
    }
    
    public int getMaxMemChannels(){
        return this.readIntVal(EnumKeyStrings.MAX_MEM_CHAN);
    }
    
    public String getChipset(){
        return this.readVal(EnumKeyStrings.CHIPSET);
    }
    
    public int getFrequencyKhz(){
        return this.readIntVal(EnumKeyStrings.FREQUENCY);
    }
    
}