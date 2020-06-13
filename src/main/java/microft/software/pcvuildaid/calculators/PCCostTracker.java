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
import java.util.Arrays;
import java.util.HashMap;
import static java.util.Objects.isNull;
import microft.software.pcbuildaid.PCBuildData.Hardware;
import microft.software.pcbuildaid.PCBuildData.HardwareSet;
import microft.software.pcbuildaid.resources.EnumHardwareType;

/**
 *
 * @author Marc Cabot
 */
public class PCCostTracker {
    private final HashMap<EnumHardwareType, Integer> newPrices = new HashMap<>();
    private final HashMap<EnumHardwareType, Integer> usedPrices = new HashMap<>();
    private final HashMap<EnumHardwareType, Boolean> used = new HashMap<>();
    private final HashMap<EnumHardwareType, Boolean> include = new HashMap<>();
    private final PCBuild pc;
    private final ArrayList<Runnable> onCostChange = new ArrayList<>();
    
    /**
     *
     * @param pc
     */
    public PCCostTracker(PCBuild pc) {
        this.pc = pc;
        pc.addONHardwareChange(()->this.reactToPCHardwareChange());
        
        for(EnumHardwareType hwt:EnumHardwareType.values()){
            newPrices.put(hwt, 0);
            usedPrices.put(hwt, 0);
            used.put(hwt, Boolean.FALSE); // <-- Make sure this lines up with the initial form state.
            include.put(hwt, Boolean.TRUE); // <-- Make sure this lines up with the initial form state.
        }
    }
    
    /**
     *
     * @param r
     */
    public void addOnCostChange(Runnable r){
        onCostChange.add(r);
    }
    
    /**
     *
     */
    public void clearOnCostChange(){
        onCostChange.clear();
    }
    
    private void fireOnCostChange(){
        onCostChange.stream().forEach(x->x.run());
    }
    
    /**
     *
     */
    public void reactToPCHardwareChange(){
        for(EnumHardwareType hwt: EnumHardwareType.values()){
            if(this.include.get(hwt)) {
                newPrices.put(hwt,
                    this.getPrice(pc.getHardware(hwt), false)
                    + this.getPrice(pc.getHardwareSet(hwt), false)
                );
                usedPrices.put(hwt, 
                    this.getPrice(pc.getHardware(hwt), true)
                    + this.getPrice(pc.getHardwareSet(hwt), true)
                );
            }
        }
        fireOnCostChange();
    }
    
    /**
     *
     * @param hwt
     * @param used
     */
    public void setUsed(EnumHardwareType hwt, boolean used){
        this.used.put(hwt, used);
        fireOnCostChange();
    }
    
    /**
     *
     * @param hwt
     * @param include
     */
    public void setInclude(EnumHardwareType hwt, boolean include){
        this.include.put(hwt, include);
        fireOnCostChange();
    }
    
    private int getPrice(Hardware hw, boolean used){
        // Grabs the price from the hardware itself.
        if(isNull(hw)) return 0;
        return used ? hw.getSellPrice() : hw.getPrice();
    }
    
    private int getPrice(HardwareSet hws, boolean used){
        // Grabs the price from the hardware set itself.
        if(isNull(hws)) return 0;
        return used ? hws.getTotalSellPrice() : hws.getTotalPrice();
    }
    
    /**
     *
     * @param hwType
     * @return
     */
    public int getPrice(EnumHardwareType hwType){
        if(!this.include.get(hwType)) return 0;
        if(this.used.get(hwType)) return this.usedPrices.get(hwType);
        else return this.newPrices.get(hwType);
    }
    
    /**
     *
     * @return
     */
    public int getTotalPrice(){
        return Arrays.asList(EnumHardwareType.values()).stream().mapToInt(x->getPrice(x)).sum();
    }
}
