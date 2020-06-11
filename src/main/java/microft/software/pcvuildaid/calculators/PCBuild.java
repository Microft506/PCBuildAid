/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcvuildaid.calculators;

import java.util.ArrayList;
import static java.util.Objects.isNull;
import microft.software.pcbuildaid.PCBuildData.Hardware.*;
import microft.software.pcbuildaid.PCBuildData.Hardware.Base.Hardware;

/**
 *
 * @author Marc
 */
public class PCBuild {

    private Hardware cpu;
    private Hardware motherboard;
    private Hardware theCase;
    
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

    // ********* CPU
    
    public Hardware getCpu() {
        return cpu;
    }

    public void setCpu(Hardware cpu) {
        this.cpu = cpu;
        activateHardwareChange();
    }
    
    public void clearCPU(){
        this.cpu = null;
        activateHardwareChange();
    }

    // ********** MOTHERBOARD
    
    public Hardware getMotherboard() {
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
    
    public Hardware getTheCase() {
        return theCase;
    }

    public void clearThecase(){
        this.theCase = null;
        activateHardwareChange();
    }
}
