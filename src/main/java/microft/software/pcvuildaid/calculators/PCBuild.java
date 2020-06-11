/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcvuildaid.calculators;

import java.util.ArrayList;
import java.util.HashMap;
import microft.software.pcbuildaid.PCBuildData.Hardware;
import microft.software.pcbuildaid.resources.EnumHardwareType;

/**
 *
 * @author Marc
 */
public class PCBuild {

    // The hashmap is used for one off hardware pieces.  RAM and GPU are different.
    // Maybe some day CPUS will also be multiple hardware.
    // The entry for multi hardware components will always be null.
    private final HashMap<EnumHardwareType, Hardware> hardwareMap = new HashMap<>();
    
        private final ArrayList<Runnable> onHardwareChange = new ArrayList<>();
    
    public PCBuild() {
        for(EnumHardwareType hwType:EnumHardwareType.values()) hardwareMap.put(hwType, null);
    }
    
    public void clearOnHardwareChange(){
        onHardwareChange.clear();
    }
    
    public void addONHardwareChange(Runnable r){
        onHardwareChange.add(r);
    }
    
    public void activateHardwareChange(){
        this.onHardwareChange.stream().forEach(x->x.run());
    }
    
    // ********* Accessors and setters.
    public Hardware getHardware(EnumHardwareType hwType){
        return hardwareMap.get(hwType);
    }
    
    public void setHardware(Hardware hw){
        hardwareMap.put(hw.getHardwareType(), hw);
        activateHardwareChange();
    }
    
    public void clearHardwareType(EnumHardwareType hwType){
        hardwareMap.put(hwType, null);
        activateHardwareChange();
    }
}
