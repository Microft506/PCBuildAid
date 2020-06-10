/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData.Hardware.Base;

/**
 *
 * @author Marc
 */
public enum EnumKeyStrings {
    BASIC_CPU_SCORE("Basic CPU Score", "0"),
    CAN_OVERCLOCK("Can Overclock", "No"),
    CASE_SIZE("Case Size", "Unknown"),
    CHIPSET("Chipset", "Unknown"),
    CONCAT_NAME("Concat name", "Unknown"),
    CORES("Cores", "1"),
    CPU_SOCKET("CPU Socket", "Unknown"), 
    FREQUENCY("Frequency", "0"),
    ID("ID", "Unknown"),
    IMPORTED_TO_GAME("Imported to Game", "Yes"),
    IN_GAME("In Game", "Yes"),
    IN_SHOP("In Shop", "Yes"),
    LEVEL("Level", "0"),
    MANUFACTURER("Manufacturer", "Unknown"),
    MAX_CPU_FAN_HEIGHT("Max CPU Fan Height", "999"),
    MAX_FREQ("Max Freq", "0"),
    MAX_GPU_LEN("Max GPU length", "999"),
    MAX_MEM_CHAN("Max Memory Channels", "1"),
    MAX_PSU_LEN("Max PSU length", "999"),
    MOTHERBOARD_SIZES("Motherboard Size", ""),
    PART_NAME("Part Name", "Unknown"),
    PRICE("Price", "0"),
    PSU_SIZE("PSU size", "Unknown"),
    RAM_TYPE("Ram Type", "Unknown"),
    WATTAGE("Wattage", "0"),
    WORKING_IN_GAME("Working in game", "Yes");

    private final String text, defText;

    EnumKeyStrings(String text, String defaultText) {
        this.text = text;
        this.defText = defaultText;
    }

    public String getDefValText() {
        return defText;
    }

    public String getKeyText() {
        return text;
    }

}
