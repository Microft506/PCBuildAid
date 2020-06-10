/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcvuildaid.calculators;

import java.util.ArrayList;
import static java.util.Objects.isNull;
import microft.software.pcbuildaid.PCBuildData.Hardware.*;

/**
 *
 * @author Marc
 */
public class PCBuild {

    private CPU cpu;
    private Motherboard motherboard;
    private Case theCase;
    
    private final ArrayList<Runnable> onHardwareChange = new ArrayList<>();
    
    public PCBuild() {
        
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
    
    public boolean isCompatibleWith(CPU cpuToCheck){
        return CompatibilityChecker.isCompatible(cpuToCheck, motherboard);
    }

    // ********* CPU
    
    public CPU getCpu() {
        return cpu;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
        activateHardwareChange();
    }
    
    public void clearCPU(){
        this.cpu = null;
        activateHardwareChange();
    }

    // ********** MOTHERBOARD
    
    public Motherboard getMotherboard() {
        return motherboard;
    }

    public void setMotherboard(Motherboard motherboard) {
        this.motherboard = motherboard;
        activateHardwareChange();
    }
    
    public void clearMotherboard(){
        this.motherboard = null;
        activateHardwareChange();
    }

    // **************** CASE
    
    public void setTheCase(Case theCase) {
        this.theCase = theCase;
        activateHardwareChange();
    }
    
    public Case getTheCase() {
        return theCase;
    }

    public void clearThecase(){
        this.theCase = null;
        activateHardwareChange();
    }
}
