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
    RAM(new String[]{"RAM"}, "RAM", 4),
    WATER_COLLANTS(new String[]{"r Collants"}, "Water Coolant", 1),
    WATER_COOLING(new String[]{"er Cooling"}, "Water Cooler", 1),
    PSU(new String[]{"PSU"}, "Power Supply", 1),
    CPU(new String[]{"CPU"}, "CPU", 1),
    MOTHERBOARD(new String[]{"otherboard"}, "Motherboard", 1),
    CASES(new String[]{"Cases"}, "Case", 1),
    COOLER(new String[]{"Coolers"}, "Cooler", 1),
    STORAGE(new String[]{"Storage"}, "Storage", 8),
    GPU(new String[]{"GPU", "Cooled GPU"}, "GPU", 2);
    //GPU(new String[]{"GPU"}, "GPU", 2);
    
    private final String[] keys;
    private final String description;
    private final int maxNumberInBuild;
    
    EnumHardwareType(String[] keys, String description, int maxNumberInBuild){
        this.keys = keys;
        this.description = description;
        this.maxNumberInBuild = maxNumberInBuild;
    }
    
    public String[] getKeys(){
        return keys;
    }

    public String getKey() {
        return keys[0];
    }
    
    public String getDescription() {
        return description;
    }

    public int getMaxNumberInBuild() {
        return maxNumberInBuild;
    }
    
}
