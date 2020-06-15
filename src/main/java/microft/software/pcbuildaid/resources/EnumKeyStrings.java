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
    AIR_FLOW(                   "Air Flow", "0", Integer.class),
    BASE_CORE_CLOCK_FREQ(       "Base Core Clock Freq", "0", Integer.class),
    BASE_MEM_CLOCK_FREQ(        "Base Mem Clock Freq", "0", Integer.class),
    BASIC_CPU_SCORE(            "Basic CPU Score", "0", Integer.class),
    CAN_OVERCLOCK(              "Can Overclock", "No", Boolean.class),
    CASE_SIZE(                  "Case Size", "Unknown", String.class),
    CHIPSET(                    "Chipset", "Unknown", String.class),
    CONCAT_NAME(                "Concat name", "Unknown", String.class),
    CORE_CLOCK_MULT(            "CoreClockMultiplier", "0", Double.class),
    CORES(                      "Cores", "1", Integer.class),
    CPU_SOCKET(                 "CPU Socket", "Unknown", String.class), 
    CPU_SOCKET_LIST(            "CPU Socket List", "Unknown", String.class),
    DEFAULT_RAM_FREQ(           "Default Memory Speed", "0", String.class),
    DOUBLE_GPU_SLI(             "Double GPU SLI", "No", Boolean.class),
    DOUBLE_GPU_SUPPORTED(       "Double GPU supported", "No", Boolean.class),
    FINAL_ADJ(                  "FinalAdjustment", "0", Double.class),
    FREQUENCY(                  "Frequency", "0", Integer.class),
    GPU_MAX_CLOCK(              "GPU max clock", "0", Integer.class),
    GPU_MAX_MEM_CLOCK(          "GPU max mem clock", "0", Integer.class),
    GT1_SINGLE_CORE_CLOCK_MULT( "GT1 Single Core Clock Multiplier",     "0",        Double.class),
    GT1_SINGLE_MEM_CLOCK_MULT(  "GT1 Single Mem Clock Multiplier",      "0",        Double.class),
    GT1_SINGLE_BENCHMARK_ADJ(   "GT1 Single Benchmark Adjustment",      "0",        Double.class),
    GT2_SINGLE_CORE_CLOCK_MULT( "GT2 Single Core Clock Multiplier",     "0",        Double.class),
    GT2_SINGLE_MEM_CLOCK_MULT(  "GT2 Single Mem Clock Multiplier",      "0",        Double.class),
    GT2_SINGLE_BENCHMARK_ADJ(   "GT2 Single Benchmark Adjustment",      "0",        Double.class),
    GT1_DUAL_CORE_CLOCK_MULT(   "GT1 Dual Core Clock Multiplier",       "0",        Double.class),
    GT1_DUAL_MEM_CLOCK_MULT(    "GT1 Dual Mem Clock Multiplier",        "0",        Double.class),
    GT1_DUAL_BENCHMARK_ADJ(     "GT1 Dual Benchmark Adjustment",        "0",        Double.class),
    GT2_DUAL_CORE_CLOCK_MULT(   "GT2 Dual Core Clock Multiplier",       "0",        Double.class),
    GT2_DUAL_MEM_CLOCK_MULT(    "GT2 Dual Mem Clock Multiplier",        "0",        Double.class),
    GT2_DUAL_BENCHMARK_ADJ(     "GT2 Dual Benchmark Adjustment",        "0",        Double.class),
    HEIGHT(                     "Height",                               "0",        Integer.class),
    ID(                         "ID",                                   "Unknown",  String.class),
    IMPORTED_TO_GAME(           "Imported to Game",                     "Yes",      Boolean.class),
    IN_GAME(                    "In Game",                              "Yes",      Boolean.class),
    IN_SHOP(                    "In Shop",                              "Yes",      Boolean.class),
    LENGTH(                     "Length",                               "0",        Integer.class),
    LEVEL(                      "Level",                                "0",        Integer.class),
    MANUFACTURER(               "Manufacturer", "Unknown", String.class),
    MAX_CPU_FAN_HEIGHT(         "Max CPU Fan Height", "999", Integer.class),
    MAX_FREQ(                   "Max Freq", "0", Integer.class),
    MAX_GPU_LEN(                "Max GPU length", "999", Integer.class),
    MAX_MEM_CHAN(               "Max Memory Channels", "1", Integer.class),
    MAX_MEM_SPEED(              "Max Memory Speed", "0", Integer.class),
    MAX_PSU_LEN(                "Max PSU length", "999", Integer.class),
    MAX_VOLTAGE(                "Max Voltage", "0", Double.class),
    MEM_CHAN_MULT(              "MemChannelsMultiplier", "0", Double.class),
    MEM_CLOCK_MULT(             "MemClockMultiplier", "0", Double.class),
    MEMORY_SPEED_STEPS(         "Memory Speed Steps", "", String.class),
    MODULARITY(                 "Modularity", "None", String.class),
    MOTHERBOARD_SIZES(          "Motherboard Size", "", String.class),
    MULTIPLIER_STEP(            "Multiplier Step", "0", Double.class),
    PART_NAME(                  "Part Name", "Unknown", String.class),
    PART_TYPE(                  "Part Type", "Unknown", String.class),
    PRICE(                      "Price", "0", Integer.class),
    PSU_SIZE(                   "PSU size", "Unknown", String.class),
    RAM_TYPE(                   "Ram Type", "Unknown", String.class),
    SELL_PRICE(                 "SellPrice", "0", Integer.class),
    SIZE(                       "Size", "Unknown", String.class),
    SIZE_EACH_GB(               "Size each (GB)", "0", Integer.class),
    SIZE_GB(                    "Size (GB)", "0", Integer.class),
    START_BASE_CLOCK(           "Start Base Clock", "0", Integer.class),
    SUPPORT_SLI(                "Support SLI", "No", Boolean.class),
    SUPPORT_CROSSFIRE(          "Support Crossfire", "No", Boolean.class),
    VRAM_GB(                    "VRAM (GB)", "0", Integer.class),
    WATTAGE(                    "Wattage", "0", Integer.class),
    WORKING_IN_GAME(            "Working in game", "Yes", Boolean.class), 
    _MULTI_GPU_SUPPORT(         "Multi-GPU Support", "", String.class);

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
