/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.calculators;

import microft.software.pcbuildaid.PCBuildData.Note;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import static java.util.Objects.isNull;
import microft.software.pcbuildaid.PCBuildData.EnumNoteType;
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
    
    // CPU and GPU Overclocking values
    
    private double oc_currentCPUBaseClock;
    private double oc_currentCPURatio;
    private double oc_currentGPUCoreClock;
    private double oc_currentGPUMemClock;
    
    // Events
    // Note that a hardware change may include a cpu/gpu clock change.
    // Event pipe:
    // onHardwareChange                 onCPUGPUClockChange
    //       |                                  |
    //      \ /                                \ /
    //    Update Hardware display -> Update overclock dispay -> Update Benchmark display
    private final ArrayList<Runnable> onHardwareChange = new ArrayList<>();
    private Runnable onCPUGPUClockChange;
    
    public PCBuild() {
        // Initialize all hardware to null
        for (EnumHardwareType hwType : EnumHardwareType.values()) {
            hardwareMap.put(hwType, null);
            hardwareSetMap.put(hwType, new HardwareSet(hwType));
        }

        // Ensure all hardware sets notify when they are changed.
        // Note: Not doing this anymore.  Any hardware changes should go through pc interface.
        hardwareSetMap.forEach((k, v) -> v.OnListChange(() -> this.reactToHardwareSetMapChange(k)));
    }

    public void clearOnHardwareChange() {
        onHardwareChange.clear();
    }

    public void addONHardwareChange(Runnable r) {
        onHardwareChange.add(r);
    }
    
    private void reactToHardwareSetMapChange(EnumHardwareType hwType){
        System.out.println("Direct adjustment to hardware set detected: " + hwType.getDescription());
        this.fireHardwareChange();
        if(hwType.equals(EnumHardwareType.GPU)) this.resetGPUClockFreq();
        if(hwType.equals(EnumHardwareType.CPU) || hwType.equals(EnumHardwareType.MOTHERBOARD)) this.resetCPUClockFreq();
        
        
    }

    public void fireHardwareChange() {
        System.out.println("Firing hardware change..");
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
        System.out.println("Adding hardware: " + hw.getConcatName());
        if (isSetType(hw.getHardwareType())) 
            for (int i = 0; i < num; ++i) hardwareSetMap.get(hw.getHardwareType()).addHardware(hw);
        else hardwareMap.put(hw.getHardwareType(), hw);
        lastChangeType = hw.getHardwareType();
        if(hw.getHardwareType().equals(EnumHardwareType.CPU) || hw.getHardwareType().equals(EnumHardwareType.MOTHERBOARD)) resetCPUClockFreq();
        if(hw.getHardwareType().equals(EnumHardwareType.GPU)) this.resetGPUClockFreq();
        fireHardwareChange();
    }

    public void clearHardwareType(EnumHardwareType hwType) {
        if (isSetType(hwType)) {
            hardwareSetMap.get(hwType).clear();
        } else {
            hardwareMap.put(hwType, null);
        }
        lastChangeType = hwType;
        if(hwType.equals(EnumHardwareType.CPU) || hwType.equals(EnumHardwareType.MOTHERBOARD)) resetCPUClockFreq();
        if(hwType.equals(EnumHardwareType.GPU)) this.resetGPUClockFreq();
        fireHardwareChange();
    }

    private boolean isSetType(EnumHardwareType hwType) {
        return (hwType.getMaxNumberInBuild() > 1);
    }
    
    public EnumHardwareType getLastChangeType(){
        return lastChangeType;
    }
    
    // *********** CPU and GPU Clocking
       
    private void fireCPUorGPUClockChange(){
        System.out.println("Firing CPU/GPU Clock Change");
        if(!isNull(this.onCPUGPUClockChange)) this.onCPUGPUClockChange.run();
    }
    
    public void onCPUorGPUClockChange(Runnable r){
        this.onCPUGPUClockChange = r;
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
    
    public double getCurrentGPUCoreClock(){
        return this.oc_currentGPUCoreClock;
    }
    
    public double getCurrentGPUMemClock(){
        return this.oc_currentGPUMemClock;
    }
    
    public void resetCPUClockFreq(){
        Hardware cpu = this.hardwareMap.get(EnumHardwareType.CPU);
        Hardware mobo = this.hardwareMap.get(EnumHardwareType.MOTHERBOARD);
        if(isNull(cpu)||isNull(mobo)){
            this.oc_currentCPUBaseClock = 0;
            this.oc_currentCPURatio = 0;
            this.fireCPUorGPUClockChange();
               return;
        }
        double freq = cpu.readDoubleVal(EnumKeyStrings.FREQUENCY);
        this.oc_currentCPUBaseClock = mobo.readDoubleVal(EnumKeyStrings.START_BASE_CLOCK);
        this.oc_currentCPURatio = freq / this.oc_currentCPUBaseClock;
        this.fireCPUorGPUClockChange();
    }
    
    public void resetGPUClockFreq(){
        System.out.println("Resetting GPU Clock Freq..");
        this.oc_currentGPUCoreClock = (double)(this.getGPUCoreClockRange()[0]);
        this.oc_currentGPUMemClock =  (double)(this.getGPUMemClockRange()[0]);
        this.fireCPUorGPUClockChange();
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
        this.fireCPUorGPUClockChange();
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
        this.fireCPUorGPUClockChange();
    }
    
    public void changeGPUCoreClock(double inc){
        int[] range = this.getGPUCoreClockRange();
        this.oc_currentGPUCoreClock += inc;
        if(this.oc_currentGPUCoreClock < range[0]) this.oc_currentGPUCoreClock = range[0];
        if(this.oc_currentGPUCoreClock > range[1]) this.oc_currentGPUCoreClock = range[1];
        this.fireCPUorGPUClockChange();
    }
    
    public void changeGPUMemClock(double inc){
        int[] range = this.getGPUMemClockRange();
        this.oc_currentGPUMemClock += inc;
        if(this.oc_currentGPUMemClock < range[0]) this.oc_currentGPUMemClock = range[0];
        if(this.oc_currentGPUMemClock > range[1]) this.oc_currentGPUMemClock = range[1];
        this.fireCPUorGPUClockChange();
    }
    
    public int getMaxPossibleMemFreq(){
        Hardware mobo = this.hardwareMap.get(EnumHardwareType.MOTHERBOARD);
        if(isNull(mobo)) return 0;
        HardwareSet ramSet = this.hardwareSetMap.get(EnumHardwareType.RAM);
        if(ramSet.isEmpty()) return 0;
        
        int ramFreq = getInstalledMemFreq();
        
        List<Integer> moboFreqs = mobo.readIntValList(EnumKeyStrings.MEMORY_SPEED_STEPS);
        if(moboFreqs.contains(ramFreq)) return ramFreq;
        
        moboFreqs.removeIf(x->x>ramFreq);
        int maxMoboFreq = Collections.max(moboFreqs);
        return Math.min(maxMoboFreq, ramFreq);
    }
    
    public int getInstalledMemFreq(){
        // Note that if there isn't a common memory frequency, this will return 0.
        HardwareSet ramSet = this.hardwareSetMap.get(EnumHardwareType.RAM);
        if(ramSet.isEmpty()) return 0;
        
        List<String> ramFreqs = ramSet.readCommonStringVals(EnumKeyStrings.FREQUENCY);
        if(ramFreqs.size() != 1) return 0;
        try{
            return Integer.parseInt(ramFreqs.get(0));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public int[] getGPUCoreClockRange(){
        HardwareSet gpuSet = this.hardwareSetMap.get(EnumHardwareType.GPU);
        if(gpuSet.isEmpty()) return new int[]{0,0};
        if(gpuSet.getCount()==1) return new int[] {
            gpuSet.getHardwareList().get(0).readIntVal(EnumKeyStrings.BASE_CORE_CLOCK_FREQ),
            Math.max(gpuSet.getHardwareList().get(0).readIntVal(EnumKeyStrings.GPU_MAX_CLOCK),
                    gpuSet.getHardwareList().get(0).readIntVal(EnumKeyStrings.BASE_CORE_CLOCK_FREQ)
            )
        };
        int minCoreClock = gpuSet.getHardwareList().stream().mapToInt(x->x.readIntVal(EnumKeyStrings.BASE_CORE_CLOCK_FREQ)).min().getAsInt();
        // You're gonna love this next variable name....
        int minMaxClock = gpuSet.getHardwareList().stream().mapToInt(x->x.readIntVal(EnumKeyStrings.GPU_MAX_CLOCK)).min().getAsInt();
        
        if(minMaxClock < minCoreClock) minMaxClock = minCoreClock;
        
        return new int[]{minCoreClock, minMaxClock};
            
    }
    
    public int[] getGPUMemClockRange(){
        HardwareSet gpuSet = this.hardwareSetMap.get(EnumHardwareType.GPU);
        int[] rValue = new int[]{0,0};
        if(gpuSet.isEmpty()) return rValue;
        List<Integer> a = gpuSet.readUniqueIntVals(EnumKeyStrings.BASE_MEM_CLOCK_FREQ);
        List<Integer> b = gpuSet.readUniqueIntVals(EnumKeyStrings.GPU_MAX_MEM_CLOCK);
        return new int[] { a.stream().mapToInt(x->x).min().getAsInt(), 
                           b.stream().mapToInt(x->x).min().getAsInt()
        };
    }
    
    public List<Note> checkForNotes(){
        ArrayList<Note> rValue = new ArrayList<>();
        
        // Check to see if the the installed ram is faster than what the pc is capable of.
        int installedRAMFreq = this.getInstalledMemFreq();
        int maxRAMFreq = this.getMaxPossibleMemFreq();
        int defRAMFreq = 0;
        if(!isNull(this.hardwareMap.get(EnumHardwareType.MOTHERBOARD)))
            defRAMFreq = this.hardwareMap.get(EnumHardwareType.MOTHERBOARD).readIntVal(EnumKeyStrings.DEFAULT_RAM_FREQ);
        
        if(maxRAMFreq > 0 && installedRAMFreq > 0 && maxRAMFreq < installedRAMFreq)
            rValue.add(new Note("Installed ram runs at " + installedRAMFreq + " MHz, Motherboard can only provide up to " + maxRAMFreq + " MHz.",EnumNoteType.GENERAL));
        
        if(this.getHardwareSet(EnumHardwareType.GPU).readUniqueStringVals(EnumKeyStrings.BASE_CORE_CLOCK_FREQ).size() > 1)
            rValue.add(new Note("GPUs have different base core clocks.  Unless overclocked, one will be throttled.", EnumNoteType.GENERAL));
        
        if(this.getHardwareSet(EnumHardwareType.GPU).readUniqueStringVals(EnumKeyStrings.BASE_MEM_CLOCK_FREQ).size() > 1)
            rValue.add(new Note("GPUs have different base memory clocks.  Unless overclocked, one will be throttled.", EnumNoteType.GENERAL));
        
        if(defRAMFreq != 0 && installedRAMFreq > defRAMFreq)
            rValue.add(new Note("Installed RAM is faster than the default motherboard RAM clock.  Remember to turn on ???", EnumNoteType.GENERAL));
        
        return rValue;
    }
}
