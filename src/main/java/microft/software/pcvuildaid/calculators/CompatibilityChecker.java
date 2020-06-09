/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcvuildaid.calculators;

import java.util.List;
import static java.util.Objects.isNull;
import microft.software.pcbuildaid.PCBuildData.Hardware.*;

/**
 *
 * @author Marc
 */
public class CompatibilityChecker {
    public static boolean isCompatible(CPU cpu, Motherboard motherboard){
        if(isNull(cpu) || isNull(motherboard)) return true;
        // For these two to be compatible, they must have the same socket, and that's it.
        return cpu.getSocketType().equalsIgnoreCase(motherboard.getSocketType());
    }
    
    public static boolean isCompatible(Case thecase, Motherboard motherboard){
        if(isNull(thecase) || isNull(motherboard)) return true;
        List<String> availSizes = thecase.getMotherboardSizes();
        availSizes.retainAll(motherboard.getMotherboardSizes());
        return availSizes.size()>0;
    }
    
}
