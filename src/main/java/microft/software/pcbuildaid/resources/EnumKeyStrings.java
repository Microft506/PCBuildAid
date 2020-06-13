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
    AIR_FLOW("Air Flow", "0", Integer.class),
    BASIC_CPU_SCORE("Basic CPU Score", "0", Integer.class),
    CAN_OVERCLOCK("Can Overclock", "No", Boolean.class),
    CASE_SIZE("Case Size", "Unknown", String.class),
    CHIPSET("Chipset", "Unknown", String.class),
    CONCAT_NAME("Concat name", "Unknown", String.class),
    CORES("Cores", "1", Integer.class),
    CPU_SOCKET("CPU Socket", "Unknown", String.class), 
    CPU_SOCKET_LIST("CPU Socket List", "Unknown", String.class),
    DOUBLE_GPU_SLI("Double GPU SLI", "No", Boolean.class),
    DOUBLE_GPU_SUPPORTED("Double GPU supported", "No", Boolean.class),
    FREQUENCY("Frequency", "0", Integer.class),
    HEIGHT("Height", "0", Integer.class),
    ID("ID", "Unknown", String.class),
    IMPORTED_TO_GAME("Imported to Game", "Yes", Boolean.class),
    IN_GAME("In Game", "Yes", Boolean.class),
    IN_SHOP("In Shop", "Yes", Boolean.class),
    LENGTH("Length", "0", Integer.class),
    LEVEL("Level", "0", Integer.class),
    MANUFACTURER("Manufacturer", "Unknown", String.class),
    MAX_CPU_FAN_HEIGHT("Max CPU Fan Height", "999", Integer.class),
    MAX_FREQ("Max Freq", "0", Integer.class),
    MAX_GPU_LEN("Max GPU length", "999", Integer.class),
    MAX_MEM_CHAN("Max Memory Channels", "1", Integer.class),
    MAX_PSU_LEN("Max PSU length", "999", Integer.class),
    MEMORY_SPEED_STEPS("Memory Speed Steps", "", String.class),
    MODULARITY("Modularity", "None", String.class),
    MOTHERBOARD_SIZES("Motherboard Size", "", String.class),
    PART_NAME("Part Name", "Unknown", String.class),
    PART_TYPE("Part Type", "Unknown", String.class),
    PRICE("Price", "0", Integer.class),
    PSU_SIZE("PSU size", "Unknown", String.class),
    RAM_TYPE("Ram Type", "Unknown", String.class),
    SELL_PRICE("SellPrice", "0", Integer.class),
    SIZE("Size", "Unknown", String.class),
    SIZE_EACH_GB("Size each (GB)", "0", Integer.class),
    SIZE_GB("Size (GB)", "0", Integer.class),
    SUPPORT_SLI("Support SLI", "No", Boolean.class),
    SUPPORT_CROSSFIRE("Support Crossfire", "No", Boolean.class),
    VRAM_GB("VRAM (GB)", "0", Integer.class),
    WATTAGE("Wattage", "0", Integer.class),
    WORKING_IN_GAME("Working in game", "Yes", Boolean.class), 
    _MULTI_GPU_SUPPORT("Multi-GPU Support", "", String.class);

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
