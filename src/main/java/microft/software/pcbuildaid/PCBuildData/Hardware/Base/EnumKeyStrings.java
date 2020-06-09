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
    MANUFACTURER("Manufacturer", "Unknown"),
    ID("ID", "Unknown"),
    PART_NAME("Part Name", "Unknown"),
    CPU_SOCKET("CPU Socket", "Unknown"),
    IN_GAME("In Game", "Yes"),
    IMPORTED_TO_GAME("Imported to Game", "Yes"),
    WORKING_IN_GAME("Working in game", "Yes"),
    IN_SHOP("In Shop", "Yes"),
    BASIC_CPU_SCORE("Basic CPU Score", "0"),
    CONCAT_NAME("Concat name", "Unknown"),
    PRICE("Price", "0"),
    LEVEL("Level", "0"),
    MAX_GPU_LEN("Max GPU length", "999"),
    MAX_CPU_FAN_HEIGHT("Max CPU Fan Height", "999"),
    MAX_PSU_LEN("Max PSU length", "999"),
    PSU_SIZE("PSU size", "Unknown"),
    MOTHERBOARD_SIZES("Motherboard Size", ""),
    CORES("Cores", "1"),
    CAN_OVERCLOCK("Can Overclock", "No"),
    WATTAGE("Wattage", "0"),
    MAX_MEM_CHAN("Max Memory Channels", "1");

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
