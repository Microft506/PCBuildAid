/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcvuildaid.calculators;

import microft.software.pcbuildaid.PCBuildData.Hardware.CPU;

/**
 *
 * @author marcc
 */
public class BenchmarkCalc {
    private Runnable onCPUChange;

    // The hardware
    private CPU cpu;
    
    public BenchmarkCalc() {
    }

    public CPU getCpu() {
        return cpu;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
        onCPUChange.run();
    }

    public void setOnCPUChange(Runnable onCPUChange) {
        this.onCPUChange = onCPUChange;
    }
        
    
}
