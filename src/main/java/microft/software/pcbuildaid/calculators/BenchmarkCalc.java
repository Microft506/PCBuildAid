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
        
        double maxMemChan = cpu.readDoubleVal(EnumKeyStrings.MAX_MEM_CHAN);
        ramChans = Math.min(ramChans, maxMemChan);
        double mcm = cpu.readDoubleVal(EnumKeyStrings.MEM_CHAN_MULT);
        System.out.println("mem chan mult: " + mcm);
        
        return 298*mcm*ramChans;
    }
    
    public static double calcMemClkScore(PCBuild pc){
        Hardware cpu = pc.getHardware(EnumHardwareType.CPU);
        if(isNull(cpu)) return 0;
        
        HardwareSet ramSet = pc.getHardwareSet(EnumHardwareType.RAM);
        if(isNull(ramSet)||ramSet.isEmpty()) return 0;
        
        double ramFreq = pc.getMaxPossibleMemFreq();
        
        double mcm = cpu.readDoubleVal(EnumKeyStrings.MEM_CLOCK_MULT);
        System.out.println("mem clock mult: " + mcm);
        
        return 298*mcm*ramFreq;
    }
    
    public static double getGPUScore(PCBuild pc, int boardNum){
        HardwareSet gpuSet = pc.getHardwareSet(EnumHardwareType.GPU);
        if(gpuSet.isEmpty()) return 0;
        if(boardNum == 2 && gpuSet.getCount() == 1) return 0;
        if(boardNum == 1 || boardNum == 2){
            Hardware gpu = gpuSet.getHardwareList().get(boardNum-1);
            double cc = pc.getCurrentGPUCoreClock();
            double mc = pc.getCurrentGPUMemClock();
            double ccm1 = gpu.readDoubleVal(EnumKeyStrings.GT1_SINGLE_CORE_CLOCK_MULT);
            double mcm1 = gpu.readDoubleVal(EnumKeyStrings.GT1_SINGLE_MEM_CLOCK_MULT);
            double bma1 = gpu.readDoubleVal(EnumKeyStrings.GT1_SINGLE_BENCHMARK_ADJ);
            double ccm2 = gpu.readDoubleVal(EnumKeyStrings.GT2_SINGLE_CORE_CLOCK_MULT);
            double mcm2 = gpu.readDoubleVal(EnumKeyStrings.GT2_SINGLE_MEM_CLOCK_MULT);
            double bma2 = gpu.readDoubleVal(EnumKeyStrings.GT2_SINGLE_BENCHMARK_ADJ);
            double x1 = 0.5/((ccm1*cc)+(mcm1*mc)+bma1);
            double x2 = 0.5/((ccm2*cc)+(mcm2*mc)+bma2);
            return 164/(x1+x2);
        } else {
            if(gpuSet.getCount() != 2) return 0;
            return Math.min(getGPUDualScore(pc, gpuSet.getHardwareList().get(0)), getGPUDualScore(pc, gpuSet.getHardwareList().get(1)));
        }
    }
    
    private static double getGPUDualScore(PCBuild pc, Hardware gpu){
        if(isNull(gpu)) return 0;
        double cc = pc.getCurrentGPUCoreClock();
        double mc = pc.getCurrentGPUMemClock();
        double ccm1 = gpu.readDoubleVal(EnumKeyStrings.GT1_DUAL_CORE_CLOCK_MULT);
        double mcm1 = gpu.readDoubleVal(EnumKeyStrings.GT1_DUAL_MEM_CLOCK_MULT);
        double bma1 = gpu.readDoubleVal(EnumKeyStrings.GT1_DUAL_BENCHMARK_ADJ);
        double ccm2 = gpu.readDoubleVal(EnumKeyStrings.GT2_DUAL_CORE_CLOCK_MULT);
        double mcm2 = gpu.readDoubleVal(EnumKeyStrings.GT2_DUAL_MEM_CLOCK_MULT);
        double bma2 = gpu.readDoubleVal(EnumKeyStrings.GT2_DUAL_BENCHMARK_ADJ);
        double x1 = (ccm1*cc)+(mcm1*mc)+bma1;
        double x2 = (ccm2*cc)+(mcm2*mc)+bma2;
        System.out.println(gpu.getConcatName() + " X1=" + x1);
        System.out.println(gpu.getConcatName() + " X2=" + x2);
        return 164/((0.5/x1)+(0.5/x2));
    }
    
    public static int getSystemScore(PCBuild pc){
        double cpuScore = Math.floor(calcBasicCPUScore(pc) + calcOverclockCPUScore(pc) + calcMemChanScore(pc) + calcMemClkScore(pc));
        if(cpuScore == 0) return 0;
        double gpuScore;
        double gpu1Score = getGPUScore(pc, 1);
        double gpu2Score = getGPUScore(pc, 2);
        if(gpu2Score == 0) gpuScore = gpu1Score;
        else gpuScore = getGPUScore(pc,0);
        if(gpuScore == 0) return 0;
        gpuScore = Math.floor(gpuScore);
        //System.out.println("CPU Score: " + cpuScore);
        //System.out.println("GPU Score: " + gpuScore);
        return (int)Math.floor(1/((0.15/cpuScore)+(0.85/gpuScore)));
    }
}
