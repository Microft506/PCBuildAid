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

import java.util.ArrayList;
import static java.util.Objects.isNull;
import microft.software.pcbuildaid.PCBuildData.Hardware;
import microft.software.pcbuildaid.resources.EnumHardwareType;

/**
 *
 * @author Marc Cabot
 */
public class PCBenchmarker {
    private final PCBuild pc;
    private final ArrayList<Runnable> onBenchmarkChange = new ArrayList<>();
    private int cpuCoreClockMultiplier, cpuMemChanMultiplier, cpuMemClockMultiplier, cpuFinalAdj;

    public PCBenchmarker(PCBuild pc) {
        this.pc = pc;
        this.pc.addONHardwareChange(()->reactToHardwareChange());
        cpuCoreClockMultiplier = 0;
        cpuMemChanMultiplier = 0;
        cpuMemClockMultiplier = 0;
        cpuFinalAdj = 0;
    }
    
    private void reactToHardwareChange(){
        cpuCoreClockMultiplier = 0;
        cpuMemChanMultiplier = 0;
        cpuMemClockMultiplier = 0;
        cpuFinalAdj = 0;
        Hardware cpu = pc.getHardware(EnumHardwareType.CPU);
        if(!isNull(cpu)){
            
        }
    }
    
    private void fireBenchmarkChange(){
        onBenchmarkChange.stream().forEach(x->x.run());
    }
    
    public void addOnBenchmarkChange(Runnable r){
        onBenchmarkChange.add(r);
    }
    
    
}
