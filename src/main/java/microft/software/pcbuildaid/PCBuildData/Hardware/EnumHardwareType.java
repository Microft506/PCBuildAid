/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData.Hardware;

/**
 *
 * @author Marc
 */
public enum EnumHardwareType {
    RAM("RAM"),
    WATER_COLLANTS("r Collants"),
    WATER_COOLING("er Cooling"),
    PSU("PUS"),
    CPU("CPU"),
    MOTHERBOARD("otherboard"),
    CASES("Cases");
    
    private final String key;
    
    EnumHardwareType(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }
    
    
    
}
