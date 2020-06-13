/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcvuildaid.calculators;

import microft.software.pcbuildaid.PCBuildData.Hardware;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.util.Objects.isNull;
import microft.software.pcbuildaid.PCBuildData.HardwareSet;
import microft.software.pcbuildaid.resources.EnumHardwareType;
import microft.software.pcbuildaid.resources.EnumKeyStrings;

/**
 *
 * @author Marc
 */


public class CompatibilityChecker {
    
    public static List<CompatibilityNote> checkInterCompatibilities(PCBuild xpc, Hardware hw){
        ArrayList<CompatibilityNote> rValue = new ArrayList<>();
        Arrays.asList(EnumHardwareType.values()).stream().forEach(x->{
            rValue.addAll(isCompatible(hw, xpc.getHardware(x)));
            rValue.addAll(isCompatible(hw, xpc.getHardwareSet(x)));
        });
        return rValue;
    }
    
    public static List<CompatibilityNote> checkInterCompatibilities(PCBuild xpc){
        ArrayList<CompatibilityNote> rValue = new ArrayList<>();
        
        Hardware theCase = xpc.getHardware(EnumHardwareType.CASES);
        Hardware motherboard = xpc.getHardware(EnumHardwareType.MOTHERBOARD);
        rValue.addAll(isCompatible(theCase, motherboard));
        
        Hardware psu = xpc.getHardware(EnumHardwareType.PSU);
        rValue.addAll(isCompatible(theCase, psu));
        
        Hardware cooler = xpc.getHardware(EnumHardwareType.COOLER);
        rValue.addAll(isCompatible(theCase, cooler));
        
        HardwareSet gpus = xpc.getHardwareSet(EnumHardwareType.GPU);
        rValue.addAll(isCompatible(theCase, gpus));
        
        Hardware cpu = xpc.getHardware(EnumHardwareType.CPU);
        rValue.addAll(isCompatible(motherboard, cpu));
        
        return rValue;
    }
    
    
    
    public static List<CompatibilityNote> isCompatible(Hardware unitA, HardwareSet unitsB){
        ArrayList<CompatibilityNote> rValue = new ArrayList<>();
        if(isNull(unitA) || isNull(unitsB)) return rValue;
        HardwarePair hwp = new HardwarePair(unitA, unitsB);
        if(hwp.isOfTypes(EnumHardwareType.CASES, EnumHardwareType.GPU))
            rValue.addAll(checkCaseGPU(hwp));
        return rValue;
    }
    
    public static List<CompatibilityNote> isCompatible(Hardware unitA, Hardware unitB){
        ArrayList<CompatibilityNote> rValue = new ArrayList<>();
        if(isNull(unitA) || isNull(unitB)) return rValue;
        HardwarePair hwp = new HardwarePair(unitA, unitB);
        if(hwp.isOfTypes(EnumHardwareType.CASES, EnumHardwareType.MOTHERBOARD))
            rValue.addAll(checkCaseMotherboard(hwp));
        if(hwp.isOfTypes(EnumHardwareType.CASES, EnumHardwareType.PSU))
            rValue.addAll(checkCasePSU(hwp));
        if(hwp.isOfTypes(EnumHardwareType.CASES, EnumHardwareType.COOLER))
            rValue.addAll(checkCaseCooler(hwp));
        if(hwp.isOfTypes(EnumHardwareType.MOTHERBOARD, EnumHardwareType.CPU))
            rValue.addAll(checkMotherboardCPU(hwp));
        return  rValue;
    }
    
    // The following routines break down the various checks for individual pairings.
    
    private static List<CompatibilityNote> checkCaseMotherboard(HardwarePair hwp){
        Hardware theCase = hwp.getHardware(EnumHardwareType.CASES);
        Hardware motherboard = hwp.getHardware(EnumHardwareType.MOTHERBOARD);
        ArrayList<CompatibilityNote> rValue = new ArrayList<>();
        // Check motherboard size
        if(!theCase.readValList(EnumKeyStrings.MOTHERBOARD_SIZES).contains(motherboard.readVal(EnumKeyStrings.MOTHERBOARD_SIZES)))
            rValue.add(new CompatibilityNote("Case does not support motherboard size " + motherboard.readVal(EnumKeyStrings.MOTHERBOARD_SIZES), 3));
        return rValue;
    }
    
    private static List<CompatibilityNote> checkCasePSU(HardwarePair hwp){
        Hardware theCase = hwp.getHardware(EnumHardwareType.CASES);
        Hardware psu = hwp.getHardware(EnumHardwareType.PSU);
        ArrayList<CompatibilityNote> rValue = new ArrayList<>();
        if(!theCase.readValList(EnumKeyStrings.PSU_SIZE).contains(psu.readVal(EnumKeyStrings.SIZE)))
            rValue.add(new CompatibilityNote("Case does not support PSU size: " + psu.readVal(EnumKeyStrings.SIZE), 3));
        if(theCase.readIntVal(EnumKeyStrings.MAX_PSU_LEN) < psu.readIntVal(EnumKeyStrings.LENGTH))
            rValue.add(new CompatibilityNote("PSU is too short (PSU: " 
                    + psu.readIntVal(EnumKeyStrings.LENGTH) + " mm, Max: " 
                    + theCase.readIntVal(EnumKeyStrings.MAX_PSU_LEN), 3));
        return rValue;
    }
    
    private static List<CompatibilityNote> checkCaseCooler(HardwarePair hwp){
        Hardware theCase = hwp.getHardware(EnumHardwareType.CASES);
        Hardware cooler = hwp.getHardware(EnumHardwareType.COOLER);
        ArrayList<CompatibilityNote> rValue = new ArrayList<>();
        if(theCase.readIntVal(EnumKeyStrings.MAX_CPU_FAN_HEIGHT) < cooler.readIntVal(EnumKeyStrings.HEIGHT))
            rValue.add(new CompatibilityNote("Cooler height ("+cooler.readVal(EnumKeyStrings.HEIGHT)+" mm) exceeds case ("+theCase.readVal(EnumKeyStrings.MAX_CPU_FAN_HEIGHT)+" mm)",3));
        return rValue;
    }
    
    private static List<CompatibilityNote> checkCaseGPU(HardwarePair hwp){
        Hardware theCase = hwp.getHardware(EnumHardwareType.CASES);
        HardwareSet gpus = hwp.getHardwareSet(EnumHardwareType.GPU);
        ArrayList<CompatibilityNote> rValue = new ArrayList<>();
        gpus.getHardwareList().stream().forEach(gpu->{
            if(gpu.readIntVal(EnumKeyStrings.LENGTH) > theCase.readIntVal(EnumKeyStrings.MAX_GPU_LEN))
                rValue.add(new CompatibilityNote("GPU: " + gpu.readVal(EnumKeyStrings.PART_NAME) 
                        + " ("+ gpu.readVal(EnumKeyStrings.LENGTH) 
                        + " mm) is too long for case ("
                        + theCase.readVal(EnumKeyStrings.MAX_GPU_LEN) +" mm)", 3));
        });
        return rValue;
    }
    
    private static List<CompatibilityNote> checkMotherboardCPU(HardwarePair hwp){
        Hardware motherboard = hwp.getHardware(EnumHardwareType.MOTHERBOARD);
        Hardware cpu = hwp.getHardware(EnumHardwareType.CPU);
        ArrayList<CompatibilityNote> rValue = new ArrayList<>();
        if(!motherboard.readVal(EnumKeyStrings.CPU_SOCKET).equalsIgnoreCase(cpu.readVal(EnumKeyStrings.CPU_SOCKET)))
            rValue.add(new CompatibilityNote("CPU Socket ("
                    + cpu.readVal(EnumKeyStrings.CPU_SOCKET) +") does not match motherboard ("
                    + motherboard.readVal(EnumKeyStrings.CPU_SOCKET) +")", 3));
        return rValue;
    }
}
