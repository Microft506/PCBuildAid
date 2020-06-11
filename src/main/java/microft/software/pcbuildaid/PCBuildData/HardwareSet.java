/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData;

import java.util.ArrayList;
import java.util.List;
import microft.software.pcbuildaid.resources.EnumHardwareType;

/**
 *
 * @author Marc
 */
public class HardwareSet{
    private final ArrayList<Hardware> hardwareList = new ArrayList<>();
    private final ArrayList<Runnable> onListChange = new ArrayList<>();
    
    private final EnumHardwareType hwType;

    public HardwareSet(EnumHardwareType hwType) {
        this.hwType = hwType;
    }
    
    public void clear(){
        hardwareList.clear();
    }
    
    public void fireListChange(){
        onListChange.stream().forEach(x->x.run());
    }
    
    public void clearOnListChange(){
        onListChange.clear();
    }
    
    public void OnListChange(Runnable r){
        onListChange.add(r);
    }
    
    public void addHardware(Hardware hw){
        if(this.getCount() > hwType.getMaxNumberInBuild()) hardwareList.remove(0);
        if(hw.getHardwareType().equals(EnumHardwareType.RAM)) hardwareList.add(hw);
    }
    
    public int getCount(){
        return hardwareList.size();
    }
    
    public void removeHardwareAtIndex(int index){
        if(index < hardwareList.size()) hardwareList.remove(index);
    }

    public EnumHardwareType getHardwareType() {
        return hwType;
    }
    
    public List<Hardware> getHardwareList(){
        return this.hardwareList;
    }
    
}
