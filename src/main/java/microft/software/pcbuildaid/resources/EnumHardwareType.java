/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.resources;

/**
 *
 * @author Marc
 */
public enum EnumHardwareType {
    RAM("RAM", "RAM"),
    WATER_COLLANTS("r Collants", "Water Coolant"),
    WATER_COOLING("er Cooling", "Water Cooler"),
    PSU("PSU", "Power Supply"),
    CPU("CPU", "CPU"),
    MOTHERBOARD("otherboard", "Motherboard"),
    CASES("Cases", "Case");
    
    private final String key;
    private final String description;
    
    EnumHardwareType(String key, String description){
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
    
}
