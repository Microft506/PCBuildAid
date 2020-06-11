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
public enum EnumKeyStrings {
    BASIC_CPU_SCORE("Basic CPU Score", "0", Integer.class),
    CAN_OVERCLOCK("Can Overclock", "No", Boolean.class),
    CASE_SIZE("Case Size", "Unknown", String.class),
    CHIPSET("Chipset", "Unknown", String.class),
    CONCAT_NAME("Concat name", "Unknown", String.class),
    CORES("Cores", "1", Integer.class),
    CPU_SOCKET("CPU Socket", "Unknown", String.class), 
    FREQUENCY("Frequency", "0", Integer.class),
    ID("ID", "Unknown", String.class),
    IMPORTED_TO_GAME("Imported to Game", "Yes", Boolean.class),
    IN_GAME("In Game", "Yes", Boolean.class),
    IN_SHOP("In Shop", "Yes", Boolean.class),
    LEVEL("Level", "0", Integer.class),
    MANUFACTURER("Manufacturer", "Unknown", String.class),
    MAX_CPU_FAN_HEIGHT("Max CPU Fan Height", "999", Integer.class),
    MAX_FREQ("Max Freq", "0", Integer.class),
    MAX_GPU_LEN("Max GPU length", "999", Integer.class),
    MAX_MEM_CHAN("Max Memory Channels", "1", Integer.class),
    MAX_PSU_LEN("Max PSU length", "999", Integer.class),
    MOTHERBOARD_SIZES("Motherboard Size", "", String.class),
    PART_NAME("Part Name", "Unknown", String.class),
    PRICE("Price", "0", Integer.class),
    PSU_SIZE("PSU size", "Unknown", String.class),
    RAM_TYPE("Ram Type", "Unknown", String.class),
    SIZE_EACH_GB("Size each (GB)", "0", Integer.class),
    WATTAGE("Wattage", "0", Integer.class),
    WORKING_IN_GAME("Working in game", "Yes", Boolean.class);

    private final String text, defText;
    private final Class c;

    EnumKeyStrings(String text, String defaultText, Class c) {
        this.text = text;
        this.defText = defaultText;
        this.c = c;
    }

    public String getDefValText() {
        return defText;
    }

    public String getKeyText() {
        return text;
    }
    
    public Class getClassType(){
        return c;
    }

}
