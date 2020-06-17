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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static java.util.Objects.isNull;
import microft.software.pcbuildaid.PCBuildData.Hardware;
import microft.software.pcbuildaid.PCBuildData.HardwareSet;
import microft.software.pcbuildaid.resources.EnumHardwareType;
import microft.software.pcbuildaid.resources.EnumKeyStrings;

/**
 *
 * @author Marc Cabot
 */
public class PCWattageTracker {
    private final ArrayList<Runnable> onWattageChange = new ArrayList<>();
    private final HashMap<EnumHardwareType, Integer> wattages = new HashMap<>();
    private final PCBuild pc;

    public PCWattageTracker(PCBuild pc) {
        // Initialize a wattage for each hardware type.
        Arrays.asList(EnumHardwareType.values()).stream().forEach(x->wattages.put(x, 0));
        this.pc = pc;
        pc.addONHardwareChange(()->reactToHardwareChange());
    }
    
    private void reactToHardwareChange(){
        Arrays.asList(EnumHardwareType.values()).stream().forEach(hwType->{
            Hardware hw = pc.getHardware(hwType);
            HardwareSet hws = pc.getHardwareSet(hwType);
            if(!isNull(hw)) this.wattages.put(hwType, hw.readIntVal(EnumKeyStrings.WATTAGE));
            if(!isNull(hws)) this.wattages.put(hwType, this.wattages.get(hwType) + hws.readSumIntVal(EnumKeyStrings.WATTAGE));
        });
        fireWattageChange();
    }
    
    private void fireWattageChange(){
        onWattageChange.stream().forEach(x->x.run());
    }
    
    public void addOnWattageChange(Runnable r){
        onWattageChange.add(r);
    }
    
    public int getPowerBalance(){
        int rValue = 0;
        for(EnumHardwareType hwType:EnumHardwareType.values())
            if(!hwType.equals(EnumHardwareType.PSU))
                rValue -= wattages.get(hwType);
        rValue += wattages.get(EnumHardwareType.PSU);
        return rValue;
    }
    
    public int getWattageFor(EnumHardwareType hwType){
        return wattages.get(hwType);
    }
    
    
}
