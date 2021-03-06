/*
 * The MIT License
 *
 * Copyright 2020 Marc Cabot.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package microft.software.pcbuildaid.calculators;

import microft.software.pcbuildaid.PCBuildData.Note;
import microft.software.pcbuildaid.PCBuildData.Hardware;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static java.util.Objects.isNull;
import java.util.stream.Collectors;
import microft.software.pcbuildaid.resources.EnumNoteType;
import microft.software.pcbuildaid.PCBuildData.HardwareSet;
import microft.software.pcbuildaid.resources.EnumHardwareType;
import microft.software.pcbuildaid.resources.EnumKeyStrings;

/**
 *
 * @author Marc Cabot
 */
// Notes:
// - Note detecting socket incompatibility between socket and cpu.

public class CompatibilityChecker {
    
    public static List<Note> checkInterCompatibilities(PCBuild xpc, Hardware hw){
        ArrayList<Note> rValue = new ArrayList<>();
        Arrays.asList(EnumHardwareType.values()).stream().forEach(x->{
            rValue.addAll(isCompatible(hw, xpc.getHardware(x)));
            rValue.addAll(isCompatible(hw, xpc.getHardwareSet(x)));
        });
        return rValue;
    }
    
    public static List<Note> checkInterCompatibilities(PCBuild xpc){
        ArrayList<Note> rValue = new ArrayList<>();
        
        Hardware theCase = xpc.getHardware(EnumHardwareType.CASES);
        Hardware motherboard = xpc.getHardware(EnumHardwareType.MOTHERBOARD);
        rValue.addAll(isCompatible(theCase, motherboard));
        
        Hardware psu = xpc.getHardware(EnumHardwareType.PSU);
        rValue.addAll(isCompatible(theCase, psu));
        
        Hardware cooler = xpc.getHardware(EnumHardwareType.COOLER);
        rValue.addAll(isCompatible(theCase, cooler));
        rValue.addAll(isCompatible(cooler, motherboard));
        
        HardwareSet gpus = xpc.getHardwareSet(EnumHardwareType.GPU);
        rValue.addAll(isCompatible(theCase, gpus));
        rValue.addAll(isCompatible(motherboard, gpus));
        rValue.addAll(isCompatible(gpus));
        
        Hardware cpu = xpc.getHardware(EnumHardwareType.CPU);
        rValue.addAll(isCompatible(motherboard, cpu));
        
        HardwareSet rams = xpc.getHardwareSet(EnumHardwareType.RAM);
        rValue.addAll(isCompatible(motherboard, rams));
        rValue.addAll(isCompatible(rams));
        
        return rValue;
    }
    
    public static List<Note> isCompatible(HardwareSet units){
        ArrayList<Note> rValue = new ArrayList<>();
        if(isNull(units)) return rValue;
        HardwarePair hwp = new HardwarePair(units);
        if(hwp.isOnlyOfType(EnumHardwareType.GPU))
            rValue.addAll(checkGPUs(hwp));
        if(hwp.isOnlyOfType(EnumHardwareType.RAM))
            rValue.addAll(checkRAMs(hwp));
        return rValue;
    }
    
    public static List<Note> isCompatible(Hardware unitA, HardwareSet unitsB){
        ArrayList<Note> rValue = new ArrayList<>();
        if(isNull(unitA) || isNull(unitsB)) return rValue;
        
        // If unitA is the same as the set of unit B, combine and check the set.
        if(unitA.getHardwareType().equals(unitsB.getHardwareType()))
            return isCompatible(unitsB.combine(unitA));
        
        HardwarePair hwp = new HardwarePair(unitA, unitsB);
        if(hwp.isOfTypes(EnumHardwareType.CASES, EnumHardwareType.GPU))
            rValue.addAll(checkCaseGPU(hwp));
        if(hwp.isOfTypes(EnumHardwareType.MOTHERBOARD, EnumHardwareType.GPU))
            rValue.addAll(checkMotherboardGPU(hwp));           
        return rValue;
    }
    
    public static List<Note> isCompatible(Hardware unitA, Hardware unitB){
        ArrayList<Note> rValue = new ArrayList<>();
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
        if(hwp.isOfTypes(EnumHardwareType.MOTHERBOARD, EnumHardwareType.RAM))
            rValue.addAll(checkMotherboardRAM(hwp));
        if(hwp.isOfTypes(EnumHardwareType.MOTHERBOARD, EnumHardwareType.COOLER))
            rValue.addAll(checkMotherboardCooler(hwp));
        return  rValue;
    }
    
    // The following routines break down the various checks for individual pairings.
    
    private static List<Note> checkCaseMotherboard(HardwarePair hwp){
        Hardware theCase = hwp.getHardware(EnumHardwareType.CASES);
        Hardware motherboard = hwp.getHardware(EnumHardwareType.MOTHERBOARD);
        ArrayList<Note> rValue = new ArrayList<>();
        if(isNull(theCase)||isNull(motherboard)) return rValue;
        // Check motherboard size
        if(!theCase.readValList(EnumKeyStrings.MOTHERBOARD_SIZES).contains(motherboard.readVal(EnumKeyStrings.MOTHERBOARD_SIZES)))
            rValue.add(new Note("Case does not support motherboard size " + motherboard.readVal(EnumKeyStrings.MOTHERBOARD_SIZES), EnumNoteType.INCOMPATIBILITY));
        return rValue;
    }
    
    private static List<Note> checkCasePSU(HardwarePair hwp){
        Hardware theCase = hwp.getHardware(EnumHardwareType.CASES);
        Hardware psu = hwp.getHardware(EnumHardwareType.PSU);
        ArrayList<Note> rValue = new ArrayList<>();
        if(isNull(theCase)||isNull(psu)) return rValue;
        if(!theCase.readValList(EnumKeyStrings.PSU_SIZE).contains(psu.readVal(EnumKeyStrings.SIZE)))
            rValue.add(new Note("Case does not support PSU size: " + psu.readVal(EnumKeyStrings.SIZE), EnumNoteType.INCOMPATIBILITY));
        if(theCase.readIntVal(EnumKeyStrings.MAX_PSU_LEN) < psu.readIntVal(EnumKeyStrings.LENGTH))
            rValue.add(new Note("PSU is too short (PSU: " 
                    + psu.readIntVal(EnumKeyStrings.LENGTH) + " mm, Max: " 
                    + theCase.readIntVal(EnumKeyStrings.MAX_PSU_LEN), EnumNoteType.INCOMPATIBILITY));
        return rValue;
    }
    
    private static List<Note> checkCaseCooler(HardwarePair hwp){
        Hardware theCase = hwp.getHardware(EnumHardwareType.CASES);
        Hardware cooler = hwp.getHardware(EnumHardwareType.COOLER);
        ArrayList<Note> rValue = new ArrayList<>();
        if(isNull(theCase)||isNull(cooler)) return rValue;
        if(theCase.readIntVal(EnumKeyStrings.MAX_CPU_FAN_HEIGHT) < cooler.readIntVal(EnumKeyStrings.HEIGHT))
            rValue.add(new Note("Cooler height ("+cooler.readVal(EnumKeyStrings.HEIGHT)+" mm) exceeds case ("+theCase.readVal(EnumKeyStrings.MAX_CPU_FAN_HEIGHT)+" mm)", EnumNoteType.INCOMPATIBILITY));
        return rValue;
    }
    
    private static List<Note> checkCaseGPU(HardwarePair hwp){
        Hardware theCase = hwp.getHardware(EnumHardwareType.CASES);
        HardwareSet gpus = hwp.getHardwareSet(EnumHardwareType.GPU);
        ArrayList<Note> rValue = new ArrayList<>();
        if(isNull(theCase)||isNull(gpus)||gpus.isEmpty()) return rValue;
        gpus.getHardwareList().stream().forEach(gpu->{
            if(gpu.readIntVal(EnumKeyStrings.LENGTH) > theCase.readIntVal(EnumKeyStrings.MAX_GPU_LEN))
                rValue.add(new Note("GPU: " + gpu.readVal(EnumKeyStrings.PART_NAME) 
                        + " ("+ gpu.readVal(EnumKeyStrings.LENGTH) 
                        + " mm) is too long for case ("
                        + theCase.readVal(EnumKeyStrings.MAX_GPU_LEN) +" mm)", EnumNoteType.INCOMPATIBILITY));
        });
        return rValue;
    }
    
    private static List<Note> checkMotherboardCPU(HardwarePair hwp){
        Hardware motherboard = hwp.getHardware(EnumHardwareType.MOTHERBOARD);
        Hardware cpu = hwp.getHardware(EnumHardwareType.CPU);
        ArrayList<Note> rValue = new ArrayList<>();
        if(isNull(motherboard)||isNull(cpu)) return rValue;
        if(!motherboard.readVal(EnumKeyStrings.CPU_SOCKET).equalsIgnoreCase(cpu.readVal(EnumKeyStrings.CPU_SOCKET)))
            rValue.add(new Note("CPU Socket ("
                    + cpu.readVal(EnumKeyStrings.CPU_SOCKET) +") does not match motherboard ("
                    + motherboard.readVal(EnumKeyStrings.CPU_SOCKET) +")", EnumNoteType.INCOMPATIBILITY));
        return rValue;
    }
    
    private static List<Note> checkMotherboardRAM(HardwarePair hwp){
        // This is an incredible waste of time...
        Hardware motherboard = hwp.getHardware(EnumHardwareType.MOTHERBOARD);
        HardwareSet rams = hwp.getHardwareSet(EnumHardwareType.RAM);
        ArrayList<Note> rValue = new ArrayList<>();
        if(isNull(motherboard)||isNull(rams)||rams.isEmpty()) return rValue;
        rams.getHardwareList().stream().forEach(ram->{
            if(!motherboard.readVal(EnumKeyStrings.RAM_TYPE).equals(ram.readVal(EnumKeyStrings.RAM_TYPE)))
                rValue.add(new Note("RAM: " + ram.readVal(EnumKeyStrings.PART_NAME)
                        + " type (" + ram.readVal(EnumKeyStrings.RAM_TYPE)
                        + ") does not match motherboard (" + motherboard.readVal(EnumKeyStrings.RAM_TYPE)
                        + ").", EnumNoteType.INCOMPATIBILITY));
        });
        return rValue;
    }
    
    private static List<Note> checkMotherboardGPU(HardwarePair hwp){
        Hardware motherboard = hwp.getHardware(EnumHardwareType.MOTHERBOARD);
        HardwareSet gpus = hwp.getHardwareSet(EnumHardwareType.GPU);
        ArrayList<Note> rValue = new ArrayList<>();
        if(isNull(motherboard)||isNull(gpus)||gpus.isEmpty()) return rValue;
        if(gpus.getCount() < 2) return rValue; // if there's only one video board, it's all good.
        if(Collections.disjoint(motherboard.readValList(EnumKeyStrings._MULTI_GPU_SUPPORT), gpus.readCommonStringVals(EnumKeyStrings._MULTI_GPU_SUPPORT)))
            rValue.add(new Note("Motherboard does not support selected dual GPUs", EnumNoteType.INCOMPATIBILITY));
        
        return rValue;
    }
    
    private static List<Note> checkGPUs(HardwarePair hwp){
        HardwareSet gpus = hwp.getHardwareSet(EnumHardwareType.GPU);
        ArrayList<Note> rValue = new ArrayList<>();
        if(isNull(gpus))return rValue;
        if(gpus.getCount() < 2) return rValue;
        if(!gpus.getHardwareList().get(0).readVal(EnumKeyStrings.CHIPSET).equals(gpus.getHardwareList().get(1).readVal(EnumKeyStrings.CHIPSET)))
            rValue.add(new Note("Dual GPUs have different chipsets.", EnumNoteType.INCOMPATIBILITY));
        if(gpus.readCommonStringVals(EnumKeyStrings._MULTI_GPU_SUPPORT).isEmpty())
            rValue.add(new Note("Dual GPUs do not have a common bridge support (such as SLI or Crossfire)", EnumNoteType.INCOMPATIBILITY));
        
        return rValue;
    }
    
    private static List<Note> checkRAMs(HardwarePair hwp){
        HardwareSet rams = hwp.getHardwareSet(EnumHardwareType.RAM);
        ArrayList<Note> rValue = new ArrayList<>();
        if(isNull(rams)) return rValue;
        if(rams.getCount() < 2) return rValue;
        // RAM sticks must be of all the same frequency.
        if(rams.readCommonStringVals(EnumKeyStrings.FREQUENCY).isEmpty())
            rValue.add(new Note("RAM contains sticks of varying frequencies: " + rams.readUniqueStringVals(EnumKeyStrings.FREQUENCY).stream().collect(Collectors.joining(", ")) + " MHz", EnumNoteType.INCOMPATIBILITY));
        if(rams.readCommonStringVals(EnumKeyStrings.SIZE_EACH_GB).isEmpty())
            rValue.add(new Note("RAM contains multiple capacities: " + rams.readUniqueStringVals(EnumKeyStrings.SIZE_EACH_GB).stream().collect(Collectors.joining(", ")) + " GB" , EnumNoteType.INCOMPATIBILITY));
        return rValue;
    }
    
    private static List<Note> checkMotherboardCooler(HardwarePair hwp){       
        Hardware mobo = hwp.getHardware(EnumHardwareType.MOTHERBOARD);
        Hardware cool = hwp.getHardware(EnumHardwareType.COOLER);
        System.out.println("Checking motherboard against cooler");
        ArrayList<Note> rValue = new ArrayList<>();
        if(!cool.readValList(EnumKeyStrings.CPU_SOCKET_LIST).contains(mobo.readVal(EnumKeyStrings.CPU_SOCKET)))
            rValue.add(new Note("Motherboard has a socket type: " + mobo.readVal(EnumKeyStrings.CPU_SOCKET) + ", but the cooler will only fit " + cool.readValList(EnumKeyStrings.CPU_SOCKET_LIST).stream().collect(Collectors.joining(", ")), EnumNoteType.INCOMPATIBILITY));
        return rValue;
    }
}
