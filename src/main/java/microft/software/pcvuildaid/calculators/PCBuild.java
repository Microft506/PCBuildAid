/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcvuildaid.calculators;

import java.util.ArrayList;
import java.util.HashMap;
import static java.util.Objects.isNull;
import microft.software.pcbuildaid.PCBuildData.GameData;
import microft.software.pcbuildaid.PCBuildData.Hardware;
import microft.software.pcbuildaid.PCBuildData.HardwareSet;
import microft.software.pcbuildaid.resources.EnumHardwareType;
import microft.software.pcbuildaid.resources.EnumKeyStrings;

/**
 *
 * @author Marc
 */
public class PCBuild {

    // The hashmap is used for one off hardware pieces.  RAM and GPU are different.
    // Maybe some day CPUS will also be multiple hardware.
    // The entry for multi hardware components will always be null.
    private final HashMap<EnumHardwareType, Hardware> hardwareMap = new HashMap<>();
    private final HashMap<EnumHardwareType, HardwareSet> hardwareSetMap = new HashMap<>();
    private EnumHardwareType lastChangeType;
    
    // CPU Overclocking values
    private double oc_currentCPUBaseClock;
    private double oc_currentCPURatio;

    // Multi hardware objects.
    private final ArrayList<Runnable> onHardwareChange = new ArrayList<>();
    private Runnable onCPUClockChange;

    public PCBuild() {
        // Initialize all hardware to null
        for (EnumHardwareType hwType : EnumHardwareType.values()) {
            hardwareMap.put(hwType, null);
            hardwareSetMap.put(hwType, new HardwareSet(hwType));
        }

        // Ensure all hardware sets notify when they are changed.
        hardwareSetMap.forEach((k, v) -> v.OnListChange(() -> this.activateHardwareChange()));
        
    }

    public void clearOnHardwareChange() {
        onHardwareChange.clear();
    }

    public void addONHardwareChange(Runnable r) {
        onHardwareChange.add(r);
    }

    public void activateHardwareChange() {
        this.onHardwareChange.stream().forEach(x -> x.run());
    }

    // ********* Accessors and setters.
    public Hardware getHardware(EnumHardwareType hwType) {
        return hardwareMap.get(hwType);
    }

    public HardwareSet getHardwareSet(EnumHardwareType hwType) {
        return hardwareSetMap.get(hwType);
    }

    public void addHardware(Hardware hw) {
        addHardware(hw, 1);
    }

    public void addHardware(Hardware hw, int num) {
        if (isSetType(hw.getHardwareType())) 
            for (int i = 0; i < num; ++i) hardwareSetMap.get(hw.getHardwareType()).addHardware(hw);
        else hardwareMap.put(hw.getHardwareType(), hw);
        lastChangeType = hw.getHardwareType();
        activateHardwareChange();
        if(hw.getHardwareType().equals(EnumHardwareType.CPU) || hw.getHardwareType().equals(EnumHardwareType.MOTHERBOARD)) resetCPUClockFreq();
    }

    public void clearHardwareType(EnumHardwareType hwType) {
        if (isSetType(hwType)) {
            hardwareSetMap.get(hwType).clear();
        } else {
            hardwareMap.put(hwType, null);
        }
        lastChangeType = hwType;
        activateHardwareChange();
        if(hwType.equals(EnumHardwareType.CPU) || hwType.equals(EnumHardwareType.MOTHERBOARD)) resetCPUClockFreq();
    }

    private boolean isSetType(EnumHardwareType hwType) {
        return (hwType.getMaxNumberInBuild() > 1);
    }
    
    public EnumHardwareType getLastChangeType(){
        return lastChangeType;
    }
    
    // *********** CPU Clocking
    
    private void fireCPUClockChange(){
        if(!isNull(this.onCPUClockChange)) this.onCPUClockChange.run();
    }
    
    public void onCPUClockChange(Runnable r){
        this.onCPUClockChange = r;
    }
    
    public double getCurrentCPUClockFreq(){
        return this.oc_currentCPUBaseClock*this.oc_currentCPURatio;
    }
    
    public double getCurrentBaseClocFreq(){
        return this.oc_currentCPUBaseClock;
    }
    
    public double getCurrentCPURatio(){
        return this.oc_currentCPURatio;
    }
    
    public void resetCPUClockFreq(){
        Hardware cpu = this.hardwareMap.get(EnumHardwareType.CPU);
        Hardware mobo = this.hardwareMap.get(EnumHardwareType.MOTHERBOARD);
        if(isNull(cpu)||isNull(mobo)){
            this.oc_currentCPUBaseClock = 0;
            this.oc_currentCPURatio = 0;
            return;
        }
        double freq = cpu.readDoubleVal(EnumKeyStrings.FREQUENCY);
        this.oc_currentCPUBaseClock = mobo.readDoubleVal(EnumKeyStrings.START_BASE_CLOCK);
        this.oc_currentCPURatio = freq / this.oc_currentCPUBaseClock;
        this.fireCPUClockChange();
    }
    
    public void changeCPURatio(double inc){
        if(this.oc_currentCPURatio == 0) return;
        
        Hardware cpu = this.hardwareMap.get(EnumHardwareType.CPU);
        if(isNull(cpu)) return;
        
        Hardware mobo = this.hardwareMap.get(EnumHardwareType.MOTHERBOARD);
        if(isNull(mobo)) return;
        
        if(!mobo.readBoolVal(EnumKeyStrings.CAN_OVERCLOCK)) return;
        
        // Prospect the new value.
        double newVal = oc_currentCPURatio+(inc * cpu.readDoubleVal(EnumKeyStrings.MULTIPLIER_STEP));
        if(newVal < GameData.CPU_FREQ_RATIO_ENF[0] || newVal > GameData.CPU_FREQ_RATIO_ENF[1]) return;
        if((newVal * this.oc_currentCPUBaseClock)>cpu.readDoubleVal(EnumKeyStrings.MAX_FREQ)) return;
        
        // Set the new value and fire clock change.
        this.oc_currentCPURatio = newVal;
        this.fireCPUClockChange();
    }
    
    public void changeCPUBaseClock(double inc){
        if(this.oc_currentCPUBaseClock == 0) return;
        
        Hardware cpu = this.hardwareMap.get(EnumHardwareType.CPU);
        if(isNull(cpu)) return;
        
        Hardware mobo = this.hardwareMap.get(EnumHardwareType.MOTHERBOARD);
        if(isNull(mobo)) return;
        
        if(!mobo.readBoolVal(EnumKeyStrings.CAN_OVERCLOCK)) return;
        
        double newVal = this.oc_currentCPUBaseClock + inc;
        if(newVal < GameData.CPU_FREQ_BASE_ENV[0] || newVal > GameData.CPU_FREQ_BASE_ENV[1]) return;
        
        if(newVal*this.oc_currentCPURatio > cpu.readDoubleVal(EnumKeyStrings.MAX_FREQ)) return;
        
        this.oc_currentCPUBaseClock = newVal;
        this.fireCPUClockChange();
    }
}
