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
    RAM("RAM", "RAM", 4),
    WATER_COLLANTS("r Collants", "Water Coolant", 1),
    WATER_COOLING("er Cooling", "Water Cooler", 1),
    PSU("PSU", "Power Supply", 1),
    CPU("CPU", "CPU", 1),
    MOTHERBOARD("otherboard", "Motherboard", 1),
    CASES("Cases", "Case", 1),
    COOLER("Coolers", "Cooler", 1);
    
    private final String key;
    private final String description;
    private final int maxNumberInBuild;
    
    EnumHardwareType(String key, String description, int maxNumberInBuild){
        this.key = key;
        this.description = description;
        this.maxNumberInBuild = maxNumberInBuild;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxNumberInBuild() {
        return maxNumberInBuild;
    }
    
    
    
}
