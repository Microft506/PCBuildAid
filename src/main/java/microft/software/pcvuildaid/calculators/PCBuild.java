/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcvuildaid.calculators;

import java.util.ArrayList;
import java.util.HashMap;
import microft.software.pcbuildaid.PCBuildData.Hardware;
import microft.software.pcbuildaid.PCBuildData.HardwareSet;
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
    private final HashMap<EnumHardwareType, HardwareSet> hardwareSetMap = new HashMap<>();

    // Multi hardware objects.
    private final ArrayList<Runnable> onHardwareChange = new ArrayList<>();

    public PCBuild() {
        // Initialize all hardware to null
        for (EnumHardwareType hwType : EnumHardwareType.values()) {
            hardwareMap.put(hwType, null);
            hardwareSetMap.put(hwType, new HardwareSet(hwType));
        }

        // Ensure all hardware sets notify when they are changed.
        hardwareSetMap.forEach((k, v) -> v.OnListChange(() -> this.activateHardwareChange()));

    }

    public void clearOnHardwareChange() {
        onHardwareChange.clear();
    }

    public void addONHardwareChange(Runnable r) {
        onHardwareChange.add(r);
    }

    public void activateHardwareChange() {
        this.onHardwareChange.stream().forEach(x -> x.run());
    }

    // ********* Accessors and setters.
    public Hardware getHardware(EnumHardwareType hwType) {
        return hardwareMap.get(hwType);
    }

    public HardwareSet getHardwareSet(EnumHardwareType hwType) {
        return hardwareSetMap.get(hwType);
    }

    public void addHardware(Hardware hw) {
        addHardware(hw, 1);
    }

    public void addHardware(Hardware hw, int num) {
        if (isSetType(hw.getHardwareType())) 
            for (int i = 0; i < num; ++i) hardwareSetMap.get(hw.getHardwareType()).addHardware(hw);
        else hardwareMap.put(hw.getHardwareType(), hw);
        activateHardwareChange();
    }

    public void clearHardwareType(EnumHardwareType hwType) {
        if (isSetType(hwType)) {
            hardwareSetMap.get(hwType).clear();
        } else {
            hardwareMap.put(hwType, null);
        }
        activateHardwareChange();
    }

    private boolean isSetType(EnumHardwareType hwType) {
        return (hwType.getMaxNumberInBuild() > 1);
    }
}
