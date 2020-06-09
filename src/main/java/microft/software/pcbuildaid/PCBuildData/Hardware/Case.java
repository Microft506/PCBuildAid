/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData.Hardware;

import java.util.List;
import microft.software.pcbuildaid.PCBuildData.HTMLParser.PCBuilderItem;
import microft.software.pcbuildaid.PCBuildData.Hardware.Base.*;

/**
 *
 * @author Marc
 */
public class Case extends Hardware{
    
    public Case(PCBuilderItem sourceData) {
        super(sourceData);
    }

    public String getPSUSize() {
        return this.readVal(EnumKeyStrings.PSU_SIZE);
    }
    
    public int getMaxPSULen() {
        return this.readIntVal(EnumKeyStrings.MAX_PSU_LEN);
    }

    public List<String> getMotherboardSizes() {
        return this.readValList(EnumKeyStrings.MOTHERBOARD_SIZES);
    }
    
}
