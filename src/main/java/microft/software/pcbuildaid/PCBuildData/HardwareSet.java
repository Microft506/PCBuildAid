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
package microft.software.pcbuildaid.PCBuildData;

import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.isNull;
import java.util.stream.Collectors;
import microft.software.pcbuildaid.resources.EnumHardwareType;
import microft.software.pcbuildaid.resources.EnumKeyStrings;

/**
 *
 * @author Marc Cabot
 */
public class HardwareSet {

    private final ArrayList<Hardware> hardwareList = new ArrayList<>();
    private Runnable onListChange;

    private final EnumHardwareType hwType;

    
    
    public HardwareSet(EnumHardwareType hwType) {
        this.hwType = hwType;
    }
    
    public HardwareSet combine(HardwareSet hws){
        HardwareSet rValue = new HardwareSet(this.hwType);
        this.hardwareList.stream().forEach(x->rValue.addHardware(x));
        hws.hardwareList.stream().forEach(x->rValue.addHardware(x));
        return rValue;
    }
    
    public HardwareSet combine(Hardware hw){
        HardwareSet rValue = new HardwareSet(this.hwType);
        this.hardwareList.stream().forEach(x->rValue.addHardware(x));
        rValue.addHardware(hw);
        return rValue;
    }

    public boolean isEmpty(){
        return this.hardwareList.isEmpty();
    }
    
    public void clear() {
        hardwareList.clear();
        this.fireListChange();
    }

    public void fireListChange() {
        if (!isNull(onListChange)) {
            onListChange.run();
        }
    }

    public void clearOnListChange() {
        onListChange = null;
    }

    public void OnListChange(Runnable r) {
        onListChange = r;
    }
    
    public void addHardware(int index, int count){
        for(int i=0; i<count; ++i) 
            this.hardwareList.add(this.hardwareList.get(index));
        this.trimListToMax();
        this.fireListChange();
    }

    public void addHardware(Hardware hw) {
        hardwareList.add(hw);
        trimListToMax();
        this.fireListChange();
    }
    
    private void trimListToMax(){
        while(this.hardwareList.size() > this.hwType.getMaxNumberInBuild()) this.hardwareList.remove(0);
    }
    
    public void makeAllLikeIndex(int index){
        Hardware src = hardwareList.get(index);
        hardwareList.clear();
        for(int i=0; i<hwType.getMaxNumberInBuild(); ++i) hardwareList.add(src);
        this.fireListChange();
    }
    
    public int getNumEmptySlots(){
        return this.hwType.getMaxNumberInBuild() - this.hardwareList.size();
    }

    public int getCount() {
        return hardwareList.size();
    }

    public void removeHardwareAtIndex(int index) {
        if (index < hardwareList.size()) {
            hardwareList.remove(index);
        }
        this.fireListChange();
    }

    public EnumHardwareType getHardwareType() {
        return hwType;
    }

    public List<Hardware> getHardwareList() {
        return this.hardwareList;
    }

    public int readSumIntVal(EnumKeyStrings key) {
        return hardwareList.stream().collect(Collectors.summingInt(x -> x.readIntVal(key)));
    }
    
    public int readMaxIntVal(EnumKeyStrings key){
        if(hardwareList.isEmpty()) return 0;
        return hardwareList.stream().mapToInt(x->x.readIntVal(key)).max().getAsInt();
    }
    
    public int readMinIntVal(EnumKeyStrings key){
        if(hardwareList.isEmpty()) return 0;
        return hardwareList.stream().mapToInt(x->x.readIntVal(key)).min().getAsInt();
    }
    
    public List<String> readUniqueStringVals(EnumKeyStrings key){
        ArrayList<String> rValue = new ArrayList<>();
        this.hardwareList.stream().forEach(x->{
            if(!rValue.contains(x.readVal(key))) rValue.add(x.readVal(key));
        });
        return rValue;
    }
    
    public List<String> readCommonStringVals(EnumKeyStrings key){
        List<String> rValue;
        if(this.hardwareList.isEmpty()) return new ArrayList<>();
        rValue = this.hardwareList.get(0).readValList(key).stream().filter(rv->{
            if(rv.length()==0) return false;
            boolean rVal = true;
            for(Hardware hw:this.hardwareList)
                rVal = rVal && hw.readValList(key).contains(rv);
            return rVal;
        }).collect(Collectors.toList());
        
        return rValue;
    }
    
    public int getTotalPrice(){
        return this.readSumIntVal(EnumKeyStrings.PRICE);
    }
    
    public int getTotalSellPrice(){
        return this.readSumIntVal(EnumKeyStrings.SELL_PRICE);
    }

}
