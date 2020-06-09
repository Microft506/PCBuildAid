/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData.Hardware;

import java.util.List;
import microft.software.pcbuildaid.PCBuildData.Hardware.Base.EnumKeyStrings;
import microft.software.pcbuildaid.PCBuildData.Hardware.Base.*;
import microft.software.pcbuildaid.PCBuildData.HTMLParser.PCBuilderItem;

/**
 *
 * @author Marc
 */
public class Motherboard extends Hardware{
    
    public Motherboard(PCBuilderItem sourceData) {
        super(sourceData);
    }

    public String getSocketType() {
        return this.readVal(EnumKeyStrings.CPU_SOCKET);
    }

    public List<String> getMotherboardSizes() {
        return this.readValList(EnumKeyStrings.MOTHERBOARD_SIZES);
    }

    
    
}
