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
package microft.software.pcvuildaid.calculators;

import java.util.List;
import static java.util.Objects.isNull;
import microft.software.pcbuildaid.PCBuildData.Hardware;
import microft.software.pcbuildaid.PCBuildData.HardwareSet;
import microft.software.pcbuildaid.resources.EnumHardwareType;
import microft.software.pcbuildaid.resources.EnumKeyStrings;

/**
 *
 * @author Marc Cabot
 */
public class BenchmarkCalc {
    public static double calcBasicCPUScore(PCBuild pc){
        Hardware cpu = pc.getHardware(EnumHardwareType.CPU);
        if(isNull(cpu)) return 0;
        double ccm = cpu.readDoubleVal(EnumKeyStrings.CORE_CLOCK_MULT);
        double fa = cpu.readDoubleVal(EnumKeyStrings.FINAL_ADJ);
        double cpuClk = cpu.readDoubleVal(EnumKeyStrings.FREQUENCY);
        return (298*ccm*cpuClk)+(298*fa);
    }
    
    public static double calcOverclockCPUScore(PCBuild pc){
        Hardware cpu = pc.getHardware(EnumHardwareType.CPU);
        if(isNull(cpu)) return 0;
        double ccm = cpu.readDoubleVal(EnumKeyStrings.CORE_CLOCK_MULT);
        double cpuOC = pc.getCurrentCPUClockFreq() - cpu.readDoubleVal(EnumKeyStrings.FREQUENCY);
        return 298*ccm*cpuOC;
        
    }
    
    public static double calcMemChanScore(PCBuild pc){
        Hardware cpu = pc.getHardware(EnumHardwareType.CPU);
        if(isNull(cpu)) return 0;
        
        HardwareSet ramSet = pc.getHardwareSet(EnumHardwareType.RAM);
        if(isNull(ramSet)||ramSet.isEmpty()) return 0;
        
        double ramChans = ramSet.getCount();
        if(ramChans == 3) ramChans = 2;
                
        double mcm = cpu.readDoubleVal(EnumKeyStrings.MEM_CHAN_MULT);
        System.out.println("mem chan mult: " + mcm);
        
        return 298*mcm*ramChans;
    }
    
    public static double calcMemClkScore(PCBuild pc){
        Hardware cpu = pc.getHardware(EnumHardwareType.CPU);
        if(isNull(cpu)) return 0;
        
        HardwareSet ramSet = pc.getHardwareSet(EnumHardwareType.RAM);
        if(isNull(ramSet)||ramSet.isEmpty()) return 0;
        
        List<String> ramFreqs = ramSet.readCommonStringVals(EnumKeyStrings.FREQUENCY);
        if(ramFreqs.size()!=1) return 0;
        double ramFreq;
        try { 
            ramFreq = Double.parseDouble(ramFreqs.get(0));
        } catch (NumberFormatException e){
            ramFreq = 0;
        }
        
        double mcm = cpu.readDoubleVal(EnumKeyStrings.MEM_CLOCK_MULT);
        System.out.println("mem clock mult: " + mcm);
        
        return 298*mcm*ramFreq;
    }
    
   
}
