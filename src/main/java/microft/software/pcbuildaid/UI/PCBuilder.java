/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.UI;

import java.awt.Color;
import microft.software.pcbuildaid.resources.EnumHardwareType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static java.util.Objects.isNull;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import microft.software.pcbuildaid.PCBuildData.Hardware;
import microft.software.pcbuildaid.PCBuildData.HardwareSet;
import microft.software.pcbuildaid.resources.EnumKeyStrings;
import microft.software.pcbuildaid.calculators.BenchmarkCalc;
import microft.software.pcbuildaid.calculators.CompatibilityChecker;
import microft.software.pcbuildaid.PCBuildData.Note;
import microft.software.pcbuildaid.calculators.PCBuild;
import microft.software.pcbuildaid.calculators.PCCostTracker;
import microft.software.pcbuildaid.calculators.PCWattageTracker;

/**
 *
 * @author Marc
 */
public class PCBuilder extends javax.swing.JFrame {
    private final PCBuild pc = new PCBuild();
    private final PCWattageTracker pcWattageTracker = new PCWattageTracker(pc);
    private final PCCostTracker pcCostTracker = new PCCostTracker(pc);
    private final ArrayList<JLabel> cpuLabels = new ArrayList<>();
    private final ArrayList<JLabel> motherboardLabels = new ArrayList<>();
    private final ArrayList<JLabel> caseLabels = new ArrayList<>();
    private final ArrayList<JLabel> psuLabels = new ArrayList<>();
    private final ArrayList<JLabel> coolerLabels = new ArrayList<>();
    private final ArrayList<JLabel> gpuLabels = new ArrayList<>(); 
    
    /**
     * Creates new form BenchmarkCalculatorUI
     */
    public PCBuilder(int benchNumber) {
        initComponents();    
        
        this.getContentPane().setBackground(Color.GRAY);
        this.setTitle("PC Build Aid Bench " + benchNumber);
        
        // Add labels to CPU list.
        cpuLabels.add(this.lblCPUBasicScore);
        cpuLabels.add(this.lblCPUPrice);
        cpuLabels.add(this.lblCPUFreq);
        cpuLabels.add(this.lblCPUCores);
        cpuLabels.add(this.lblCPUOverclockable);
        cpuLabels.add(this.lblCPUWattage);
        cpuLabels.add(this.lblCPUMemChan);
        cpuLabels.add(this.lblCPUSocket);
        cpuLabels.add(this.lblCPUMaxVoltage);
        
        motherboardLabels.add(this.lblMotherboardPrice);
        motherboardLabels.add(this.lblMotherboardMGPUSupp);
        motherboardLabels.add(this.lblMotherboardSocket);
        motherboardLabels.add(this.lblMotherboardSize);
        motherboardLabels.add(this.lblMotherboardRamType);
        motherboardLabels.add(this.lblMotherboardOverclockable);
        
        caseLabels.add(this.lblCaseMoboSize);
        caseLabels.add(this.lblCasePSULen);
        caseLabels.add(this.lblCasePSUSize);
        caseLabels.add(this.lblCaseSize);
        caseLabels.add(this.lblCaseCPUFanClearance);
        caseLabels.add(this.lblCaseGPULen);
        caseLabels.add(this.lblCasePrice);
        
        psuLabels.add(this.lblPSULen);
        psuLabels.add(this.lblPSUModulatiry);
        psuLabels.add(this.lblPSUPrice);
        psuLabels.add(this.lblPSUSize);
        psuLabels.add(this.lblPSUWattage);
        
        gpuLabels.add(this.lblGPUVRam);
        gpuLabels.add(this.lblGPUMaxLen);
        gpuLabels.add(this.lblGPUPrice);
        gpuLabels.add(this.lblGPUWattage);
        
        coolerLabels.add(this.lblCoolerAirFlow);
        coolerLabels.add(this.lblCoolerHeight);
        coolerLabels.add(this.lblCoolerPrice);
        coolerLabels.add(this.lblCoolerSockets);
        coolerLabels.add(this.lblCoolerType);
        
        this.lstRam.getSelectionModel().addListSelectionListener((e)->ramListSelectionListener());
        this.ramListSelectionListener();
        
        this.lstStorage.getSelectionModel().addListSelectionListener((e)->this.storageListSelectionListener());
        this.storageListSelectionListener();
        
        pc.addONHardwareChange(()->reactToHardwareChange());
        pc.fireHardwareChange();
        pc.onCPUorGPUClockChange(()->this.reactToCPUorGPUOverclockChange());
        this.reactToCPUorGPUOverclockChange();
        
        pcCostTracker.addOnCostChange(()->updatePrices());
        updatePrices();
        
        pcWattageTracker.addOnWattageChange(()->updateWattages());
        updateWattages();
    }
    
    private void reactToHardwareChange(){
        // this is where we react to hardware changes.
        // Display the currently selected case.
        displayCurrentCPU();
        displayCurrentMotherboard();
        displayCurrentCase();
        displayCurrentPSU();
        displayCurrentRAM();
        displayCurrentCooler();
        displayCurrentStorage();
        displayCurrentGPUs();
        updatePrices();
        reactToCPUorGPUOverclockChange();
        
    }
    
    private void reactToCPUorGPUOverclockChange(){
        this.txtCPUBaseClock.setText(Integer.toString((int)pc.getCurrentBaseClocFreq()));
        this.txtClockMultiplier.setText(Double.toString(pc.getCurrentCPURatio()));
        this.lblCPUFinalFreq.setText((int)pc.getCurrentCPUClockFreq() + " MHz");
        this.txtGPUClock.setText(Integer.toString((int)pc.getCurrentGPUCoreClock()));
        this.txtGPUMemClock.setText(Integer.toString((int)pc.getCurrentGPUMemClock()));
        calcBenchmarks();
        updateNotes();
    }
    
    
    private void storageListSelectionListener(){
        this.btnStorageAdd1More.setEnabled(this.lstRam.getSelectedIndices().length == 1);
        this.btnStorageAdd2More.setEnabled(this.lstRam.getSelectedIndices().length == 1);
    }
    
    private void ramListSelectionListener(){
        this.btnRAMAdd1More.setEnabled(
                (this.lstRam.getSelectedIndices().length <= this.pc.getHardwareSet(EnumHardwareType.RAM).getNumEmptySlots()) &&
                this.lstRam.getSelectedIndices().length > 0
        );
        this.btnRAMAdd2More.setEnabled(this.lstRam.getSelectedIndices().length == 1);
    }
    
    private void updateWattages(){
        this.lblWattagePSU.setText(Integer.toString(this.pcWattageTracker.getWattageFor(EnumHardwareType.PSU)));
        this.lblWattageCPU.setText("- " + this.pcWattageTracker.getWattageFor(EnumHardwareType.CPU));
        this.lblWattageGPU.setText("- " + this.pcWattageTracker.getWattageFor(EnumHardwareType.GPU));
        this.lblWattageBalance.setText(Integer.toString(this.pcWattageTracker.getPowerBalance()));
    }
    
    private void updatePrices(){
        this.lblCostCPU.setText("$" + pcCostTracker.getPrice(EnumHardwareType.CPU));
        this.lblCostCase.setText("$" + pcCostTracker.getPrice(EnumHardwareType.CASES));
        this.lblCostCooler.setText("$" + pcCostTracker.getPrice(EnumHardwareType.COOLER));
        this.lblCostGPU.setText("$" + pcCostTracker.getPrice(EnumHardwareType.GPU));
        this.lblCostMobo.setText("$" + pcCostTracker.getPrice(EnumHardwareType.MOTHERBOARD));
        this.lblCostPSU.setText("$" + pcCostTracker.getPrice(EnumHardwareType.PSU));
        this.lblCostRAM.setText("$" + pcCostTracker.getPrice(EnumHardwareType.RAM));
        this.lblCostStorage.setText("$" + pcCostTracker.getPrice(EnumHardwareType.STORAGE));
        this.lblCostTotal.setText("$" + pcCostTracker.getTotalPrice());
    }
    
    
    
    private void updateNotes(){
        System.out.println("Updating notes");
        List<Note> Notes = CompatibilityChecker.checkInterCompatibilities(pc);
        Notes.addAll(pc.checkForNotes());

        this.lstNotes.setListData(Notes.stream().map(x->x.getNoteWithType()).toArray(String[]::new));
    }
    
    private void calcBenchmarks(){
        double basicCPU = BenchmarkCalc.calcBasicCPUScore(pc);
        double ocCPU = BenchmarkCalc.calcOverclockCPUScore(pc);
        double memChan = BenchmarkCalc.calcMemChanScore(pc);
        double memClk = BenchmarkCalc.calcMemClkScore(pc);
        
        this.txtBMBasicCPU.setText(formatDouble(basicCPU, 2));
        this.txtBMCPUOC.setText(formatDouble(ocCPU,2));
        this.txtBMMemChan.setText(formatDouble(memChan,2));
        this.txtBMMemClk.setText(formatDouble(memClk,2));
        this.lblBMCPUScore.setText(Integer.toString((int)Math.floor(basicCPU+ocCPU+memChan+memClk)));
        
        this.txtGPU1Score.setText(Integer.toString((int)Math.floor(BenchmarkCalc.getGPUScore(pc, 1))));
        this.txtGPU2Score.setText(Integer.toString((int)Math.floor(BenchmarkCalc.getGPUScore(pc, 2))));
        this.txtGPUCombinedScore.setText(Integer.toString((int)Math.floor(BenchmarkCalc.getGPUScore(pc, 0))));
        
        this.lblBMOverallScore.setText(Integer.toString(BenchmarkCalc.getSystemScore(pc)));
    }
    
    private String formatDouble(double val, int decimalPlaces){
        return Double.toString(Math.round(val*Math.pow(10, decimalPlaces))/Math.pow(10, decimalPlaces));
        
    }
       
    private void displayCurrentCooler(){
        Hardware cooler = pc.getHardware(EnumHardwareType.COOLER);
        coolerLabels.stream().forEach(x->x.setText("-"));
        this.txtCooler.setText("No Cooler Selected");
        if(isNull(cooler)) return;
        this.txtCooler.setText(cooler.getConcatName());
        this.lblCoolerPrice.setText(this.formatPrice(cooler));
        this.lblCoolerAirFlow.setText(cooler.readVal(EnumKeyStrings.AIR_FLOW) + " CFM");
        this.lblCoolerHeight.setText(cooler.readVal(EnumKeyStrings.HEIGHT) + " mm");
        this.lblCoolerSockets.setText(cooler.readVal(EnumKeyStrings.CPU_SOCKET_LIST));
        this.lblCoolerType.setText(cooler.readVal(EnumKeyStrings.PART_TYPE));
    }
    
    private void displayCurrentPSU(){
        Hardware psu = pc.getHardware(EnumHardwareType.PSU);
        psuLabels.stream().forEach(x->x.setText("-"));
        this.txtPSU.setText("No PSU Selected.");
        if(isNull(psu)) return;
        this.txtPSU.setText(psu.getConcatName());
        this.lblPSULen.setText(psu.readVal(EnumKeyStrings.LENGTH) + " mm");
        this.lblPSUModulatiry.setText(psu.readVal(EnumKeyStrings.MODULARITY));
        this.lblPSUPrice.setText(this.formatPrice(psu));
        this.lblPSUSize.setText(psu.readVal(EnumKeyStrings.SIZE));
        this.lblPSUWattage.setText(this.formatWattage(psu));
    }
    
    private void displayCurrentMotherboard(){
        Hardware mobo = pc.getHardware(EnumHardwareType.MOTHERBOARD);
        motherboardLabels.stream().forEach(x->x.setText("-"));
        this.txtMotherboard.setText("No Motherboard Selected.");
        if(isNull(mobo)) return;
        this.txtMotherboard.setText(mobo.getConcatName());
        this.lblMotherboardPrice.setText("$"+mobo.getPrice()+".00");
        this.lblMotherboardMGPUSupp.setText(mobo.readVal(EnumKeyStrings._MULTI_GPU_SUPPORT));
        this.lblMotherboardSocket.setText(mobo.readVal(EnumKeyStrings.CPU_SOCKET));
        this.lblMotherboardSize.setText(mobo.readVal(EnumKeyStrings.MOTHERBOARD_SIZES));
        this.lblMotherboardRamType.setText(mobo.readVal(EnumKeyStrings.RAM_TYPE));
        this.lblMotherboardOverclockable.setText(mobo.readBoolVal(EnumKeyStrings.CAN_OVERCLOCK) ? "Yes":"No");
    }
    
    private void displayCurrentCPU(){
        Hardware cpu = pc.getHardware(EnumHardwareType.CPU);
        cpuLabels.stream().forEach(x->x.setText("-"));
        this.txtCPU.setText("No CPU Selected.");
        if(isNull(cpu)) return;
        this.txtCPU.setText(cpu.getConcatName());
        this.lblCPUPrice.setText(this.formatPrice(cpu));
        this.lblCPUFreq.setText(this.formatFreq(cpu));
        this.lblCPUBasicScore.setText(Integer.toString(cpu.readIntVal(EnumKeyStrings.BASIC_CPU_SCORE)));
        this.lblCPUCores.setText(Integer.toString(cpu.readIntVal(EnumKeyStrings.CORES)));
        this.lblCPUOverclockable.setText(this.formatIsOverClockable(cpu));
        this.lblCPUWattage.setText(this.formatWattage(cpu));
        this.lblCPUMemChan.setText(this.formatMaxMemChannels(cpu));
        this.lblCPUSocket.setText(cpu.readVal(EnumKeyStrings.CPU_SOCKET));
        this.lblCPUMaxVoltage.setText(cpu.readVal(EnumKeyStrings.MAX_VOLTAGE)+" Volts");
    }
    
    private void displayCurrentCase(){
        Hardware theCase = pc.getHardware(EnumHardwareType.CASES);
        caseLabels.stream().forEach(x->x.setText("-"));
        this.txtCase.setText("No Case Selected.");
        if(isNull(theCase)) return;
        this.txtCase.setText(theCase.getConcatName());
        this.lblCaseCPUFanClearance.setText(theCase.readVal(EnumKeyStrings.MAX_CPU_FAN_HEIGHT) + " mm");
        this.lblCaseCPUFanClearance.setText(this.formatMaxCPUFanHeight(theCase));
        this.lblCaseGPULen.setText(theCase.readVal(EnumKeyStrings.MAX_GPU_LEN) + " mm");
        this.lblCaseMoboSize.setText(theCase.readVal(EnumKeyStrings.MOTHERBOARD_SIZES));
        this.lblCasePSULen.setText(theCase.readVal(EnumKeyStrings.MAX_PSU_LEN) + " mm");
        this.lblCasePSUSize.setText(theCase.readVal(EnumKeyStrings.PSU_SIZE));
        this.lblCasePrice.setText(formatPrice(theCase.getPrice()));
        this.lblCaseSize.setText(theCase.readVal(EnumKeyStrings.CASE_SIZE));
    }
    
    private void displayCurrentRAM(){
        HardwareSet ram = pc.getHardwareSet(EnumHardwareType.RAM);
        this.lstRam.removeAll();
        String[] lines = new String[ram.getCount()];
        this.gpuLabels.stream().forEach(x->x.setText("-"));
        for(int i=0; i<lines.length; i++) lines[i] = ram.getHardwareList().get(i).getConcatName();
        this.lstRam.setListData(lines);
        this.lblRamType.setText(this.pc.getHardwareSet(EnumHardwareType.RAM).readUniqueStringVals(EnumKeyStrings.RAM_TYPE).stream().collect(Collectors.joining(", ")));
        this.lblRamCap.setText(this.pc.getHardwareSet(EnumHardwareType.RAM).readSumIntVal(EnumKeyStrings.SIZE_EACH_GB) + " GB");
        this.lblRamPrice.setText(this.formatPrice(this.pc.getHardwareSet(EnumHardwareType.RAM)));
    }
    
    private void displayCurrentStorage(){
        HardwareSet storage = pc.getHardwareSet(EnumHardwareType.STORAGE);
        this.lstStorage.removeAll();
        String[] lines = new String[storage.getCount()];
        for(int i=0; i<lines.length; i++) lines[i] = storage.getHardwareList().get(i).getConcatName();
        this.lstStorage.setListData(lines);
        this.lblStoragePrice.setText(this.formatPrice(storage));
        this.lblStorageType.setText(storage.readUniqueStringVals(EnumKeyStrings.PART_TYPE).stream().collect(Collectors.joining(", ")));
        this.lblStoragelTotalCapacity.setText(storage.readSumIntVal(EnumKeyStrings.SIZE_GB) + " GB");
    }
    
    private void displayCurrentGPUs(){
        HardwareSet gpus = pc.getHardwareSet(EnumHardwareType.GPU);
        this.lstGPU.removeAll();
        String[] lines = new String[gpus.getCount()];
        for(int i=0; i<lines.length; i++) lines[i] = gpus.getHardwareList().get(i).getConcatName();
        this.lstGPU.setListData(lines);
        this.lblGPUPrice.setText(this.formatPrice(pc.getHardwareSet(EnumHardwareType.GPU)));
        this.lblGPUDualType.setText(gpus.readCommonStringVals(EnumKeyStrings._MULTI_GPU_SUPPORT).stream().collect(Collectors.joining(", ")));
        this.lblGPUWattage.setText(gpus.readSumIntVal(EnumKeyStrings.WATTAGE) + " Watts");
        this.lblGPUVRam.setText(gpus.readSumIntVal(EnumKeyStrings.VRAM_GB) + " Gigs");
        this.lblGPUMaxLen.setText(gpus.readMaxIntVal(EnumKeyStrings.LENGTH) + " mm");
    }
    
    private String formatPrice(int Price){
        return "$" + Price + ".00";
    }
    
    private String formatPrice(HardwareSet hws){
        return "$" + hws.readSumIntVal(EnumKeyStrings.PRICE) + ((hws.readSumIntVal(EnumKeyStrings.SELL_PRICE) > 0) ? 
                " ($" + hws.readSumIntVal(EnumKeyStrings.SELL_PRICE) + " used)" : ".00");
    }
    
    private String formatPrice(Hardware hw){
        return "$" + hw.getPrice() + ((hw.readIntVal(EnumKeyStrings.SELL_PRICE) > 0) ? 
            " ($" + hw.readIntVal(EnumKeyStrings.SELL_PRICE) + " used)" : ".00");
    }
    
    private String formatIsOverClockable(Hardware hw){
        return hw.readBoolVal(EnumKeyStrings.CAN_OVERCLOCK) ?
                "to " + hw.readIntVal(EnumKeyStrings.MAX_FREQ) + " MHz" :
                "No";
    }
    
    private String formatFreq(Hardware hw){
        return ((double)hw.readIntVal(EnumKeyStrings.FREQUENCY)/1000) + " MHz";
    }
    
    private String formatWattage(Hardware hw){
        return hw.readIntVal(EnumKeyStrings.WATTAGE) + " Watts";
    }
    
    private String formatMaxMemChannels(Hardware hw){
        return hw.readIntVal(EnumKeyStrings.MAX_MEM_CHAN) + 
                ((hw.readIntVal(EnumKeyStrings.MAX_MEM_CHAN) == 1)? " channel": " channels");
    }
    
    private String formatMaxCPUFanHeight(Hardware hw){
        return hw.readVal(EnumKeyStrings.MAX_CPU_FAN_HEIGHT) + "mm";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel63 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnChooseCPU = new javax.swing.JButton();
        txtCPU = new javax.swing.JTextField();
        btnRemoveCPU = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        lblCPUPriceLabel = new javax.swing.JLabel();
        lblCPUPrice = new javax.swing.JLabel();
        lblCPUBasicScoreTitle = new javax.swing.JLabel();
        lblCPUBasicScore = new javax.swing.JLabel();
        lblCPUWattageLabel = new javax.swing.JLabel();
        lblCPUWattage = new javax.swing.JLabel();
        lblCPUFreqLabel = new javax.swing.JLabel();
        lblCPUFreq = new javax.swing.JLabel();
        lblCPUCoresLabel = new javax.swing.JLabel();
        lblCPUCores = new javax.swing.JLabel();
        lblCPUMemChanLabel = new javax.swing.JLabel();
        lblCPUMemChan = new javax.swing.JLabel();
        lblCPUOverclockableLabel = new javax.swing.JLabel();
        lblCPUOverclockable = new javax.swing.JLabel();
        lblCPUSocketLabel = new javax.swing.JLabel();
        lblCPUSocket = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        lblCPUMaxVoltage = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnChooseCPU1 = new javax.swing.JButton();
        btnRemoveCPU1 = new javax.swing.JButton();
        txtMotherboard = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblMotherboardPrice = new javax.swing.JLabel();
        lblMotherboardMGPUSupp = new javax.swing.JLabel();
        lblMotherboardSocket = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblMotherboardSize = new javax.swing.JLabel();
        lblMotherboardRamType = new javax.swing.JLabel();
        lblMotherboardOverclockable = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        btnRemoveCase = new javax.swing.JButton();
        btnChooseCase = new javax.swing.JButton();
        txtCase = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblCaseMoboSize = new javax.swing.JLabel();
        lblCaseSize = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblCasePSUSize = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblCaseGPULen = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lblCaseCPUFanClearance = new javax.swing.JLabel();
        lblCasePSULen = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblCasePrice = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        btnRemovePSU = new javax.swing.JButton();
        btnChoosePSU = new javax.swing.JButton();
        txtPSU = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        lblPSUWattage = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblPSUPrice = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        lblPSUSize = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        lblPSUModulatiry = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        lblPSULen = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        btnRamAdd = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstRam = new javax.swing.JList<>();
        btnRamClearRemove = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        lblRamCap = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        lblRamPrice = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        lblRamType = new javax.swing.JLabel();
        btnRAMAdd1More = new javax.swing.JButton();
        btnRAMAdd2More = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        btnCoolerRemove = new javax.swing.JButton();
        btnCoolerAdd = new javax.swing.JButton();
        txtCooler = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        lblCoolerPrice = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        lblCoolerAirFlow = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        lblCoolerHeight = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        lblCoolerType = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        lblCoolerSockets = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstStorage = new javax.swing.JList<>();
        btnStorageAdd = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        lblStoragelTotalCapacity = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        lblStoragePrice = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        lblStorageType = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        btnStorageClearRemove = new javax.swing.JButton();
        btnStorageAdd1More = new javax.swing.JButton();
        btnStorageAdd2More = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstGPU = new javax.swing.JList<>();
        btnGPUAdd = new javax.swing.JButton();
        btnGPUClearRemove = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        lblGPUWattage = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        lblGPUMaxLen = new javax.swing.JLabel();
        lblGPUPrice = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        lblGPUVRam = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        lblGPUDualType = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        lblCostAnalyzer = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        chkIncludeCase = new javax.swing.JCheckBox();
        chkUseCase = new javax.swing.JCheckBox();
        lblCostCase = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        chkIncludeMobo = new javax.swing.JCheckBox();
        chkUseMobo = new javax.swing.JCheckBox();
        chkIncludeCPU = new javax.swing.JCheckBox();
        chkUseCPU = new javax.swing.JCheckBox();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        chkIncludePSU = new javax.swing.JCheckBox();
        chkIncludeCooler = new javax.swing.JCheckBox();
        chkIncludeRAM = new javax.swing.JCheckBox();
        chkIncludeStorage = new javax.swing.JCheckBox();
        chkIncludeGPU = new javax.swing.JCheckBox();
        chkUsePSU = new javax.swing.JCheckBox();
        chkUseCooler = new javax.swing.JCheckBox();
        chkUseRAM = new javax.swing.JCheckBox();
        chkUseStorage = new javax.swing.JCheckBox();
        chkUseGPU = new javax.swing.JCheckBox();
        lblCostMobo = new javax.swing.JLabel();
        lblCostCPU = new javax.swing.JLabel();
        lblCostPSU = new javax.swing.JLabel();
        lblCostCooler = new javax.swing.JLabel();
        lblCostRAM = new javax.swing.JLabel();
        lblCostStorage = new javax.swing.JLabel();
        lblCostGPU = new javax.swing.JLabel();
        lblCostTotal = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        lblCostAnalyzer1 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstNotes = new javax.swing.JList<>();
        jPanel20 = new javax.swing.JPanel();
        lblCostAnalyzer2 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        lblWattagePSU = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        lblWattageCPU = new javax.swing.JLabel();
        lblWattageBalance = new javax.swing.JLabel();
        lblWattageGPU = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        btnDEBUG = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        txtBMBasicCPU = new javax.swing.JTextField();
        txtBMCPUOC = new javax.swing.JTextField();
        jLabel69 = new javax.swing.JLabel();
        txtBMMemChan = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        txtBMMemClk = new javax.swing.JTextField();
        lblBMCPUScore = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        txtGPU1Score = new javax.swing.JTextField();
        txtGPU2Score = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        txtGPUCombinedScore = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        lblBMOverallScore = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jLabel55 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        btnCPUBaseClockU1 = new javax.swing.JButton();
        btnCPUBaseClockU10 = new javax.swing.JButton();
        btnCPUBaseClockD10 = new javax.swing.JButton();
        btnCPUBaseClockD1 = new javax.swing.JButton();
        txtCPUBaseClock = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        txtClockMultiplier = new javax.swing.JTextField();
        btnCPUClockMultD10 = new javax.swing.JButton();
        btnCPUClockMultD1 = new javax.swing.JButton();
        btnCPUClockMultU1 = new javax.swing.JButton();
        btnCPUClockMultU10 = new javax.swing.JButton();
        lblCPUFinalFreq = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        btnOCCPUReset = new javax.swing.JButton();
        jLabel65 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        txtGPUClock = new javax.swing.JTextField();
        jLabel73 = new javax.swing.JLabel();
        btnGPUBaseClockD10 = new javax.swing.JButton();
        btnGPUBaseClockD1 = new javax.swing.JButton();
        btnGPUBaseClockU1 = new javax.swing.JButton();
        btnGPUBaseClockU10 = new javax.swing.JButton();
        jLabel74 = new javax.swing.JLabel();
        txtGPUMemClock = new javax.swing.JTextField();
        jLabel75 = new javax.swing.JLabel();
        btnGPUMemClockU10 = new javax.swing.JButton();
        btnGPUMemClockU1 = new javax.swing.JButton();
        btnGPUMemClockD1 = new javax.swing.JButton();
        btnGPUMemClockD10 = new javax.swing.JButton();
        btnGPUOCReset = new javax.swing.JButton();

        jLabel63.setText("jLabel63");

        setTitle("PC Build Aid - Benchmark Calculator");
        setBackground(new java.awt.Color(0, 0, 0));
        setForeground(new java.awt.Color(153, 153, 153));

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("CPU:");

        btnChooseCPU.setText("...");
        btnChooseCPU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseCPUActionPerformed(evt);
            }
        });

        txtCPU.setEditable(false);
        txtCPU.setBackground(new java.awt.Color(255, 255, 255));
        txtCPU.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtCPU.setText("None");

        btnRemoveCPU.setText("Remove");
        btnRemoveCPU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveCPUActionPerformed(evt);
            }
        });

        java.awt.GridBagLayout jPanel4Layout = new java.awt.GridBagLayout();
        jPanel4Layout.columnWidths = new int[] {100, 100, 100, 100, 100, 100};
        jPanel4.setLayout(jPanel4Layout);

        lblCPUPriceLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCPUPriceLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCPUPriceLabel.setText("Price:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(lblCPUPriceLabel, gridBagConstraints);

        lblCPUPrice.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel4.add(lblCPUPrice, gridBagConstraints);

        lblCPUBasicScoreTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCPUBasicScoreTitle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCPUBasicScoreTitle.setText("Basic Score:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(lblCPUBasicScoreTitle, gridBagConstraints);

        lblCPUBasicScore.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel4.add(lblCPUBasicScore, gridBagConstraints);

        lblCPUWattageLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCPUWattageLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCPUWattageLabel.setText("Wattage:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(lblCPUWattageLabel, gridBagConstraints);

        lblCPUWattage.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel4.add(lblCPUWattage, gridBagConstraints);

        lblCPUFreqLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCPUFreqLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCPUFreqLabel.setText("Frequency:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(lblCPUFreqLabel, gridBagConstraints);

        lblCPUFreq.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel4.add(lblCPUFreq, gridBagConstraints);

        lblCPUCoresLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCPUCoresLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCPUCoresLabel.setText("Cores:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(lblCPUCoresLabel, gridBagConstraints);

        lblCPUCores.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel4.add(lblCPUCores, gridBagConstraints);

        lblCPUMemChanLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCPUMemChanLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCPUMemChanLabel.setText("Mem. Channels:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(lblCPUMemChanLabel, gridBagConstraints);

        lblCPUMemChan.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel4.add(lblCPUMemChan, gridBagConstraints);

        lblCPUOverclockableLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCPUOverclockableLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCPUOverclockableLabel.setText("Overclockable:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(lblCPUOverclockableLabel, gridBagConstraints);

        lblCPUOverclockable.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel4.add(lblCPUOverclockable, gridBagConstraints);

        lblCPUSocketLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCPUSocketLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCPUSocketLabel.setText("Socket:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(lblCPUSocketLabel, gridBagConstraints);

        lblCPUSocket.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel4.add(lblCPUSocket, gridBagConstraints);

        jLabel61.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel61.setText("Max Voltage:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jLabel61, gridBagConstraints);

        lblCPUMaxVoltage.setText("jLabel63");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel4.add(lblCPUMaxVoltage, gridBagConstraints);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCPU)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnChooseCPU)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoveCPU)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnChooseCPU)
                    .addComponent(txtCPU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoveCPU))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Motherboard:");

        btnChooseCPU1.setText("...");
        btnChooseCPU1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseCPU1ActionPerformed(evt);
            }
        });

        btnRemoveCPU1.setText("Remove");
        btnRemoveCPU1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveCPU1ActionPerformed(evt);
            }
        });

        txtMotherboard.setEditable(false);
        txtMotherboard.setBackground(new java.awt.Color(255, 255, 255));
        txtMotherboard.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtMotherboard.setText("None");

        java.awt.GridBagLayout jPanel3Layout = new java.awt.GridBagLayout();
        jPanel3Layout.columnWidths = new int[] {100, 100, 100, 100, 100, 100};
        jPanel3.setLayout(jPanel3Layout);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Price:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel3.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Multi-GPU Support:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel3.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Socket:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel3.add(jLabel5, gridBagConstraints);

        lblMotherboardPrice.setText("jLabel6");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(lblMotherboardPrice, gridBagConstraints);

        lblMotherboardMGPUSupp.setText("jLabel7");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(lblMotherboardMGPUSupp, gridBagConstraints);

        lblMotherboardSocket.setText("jLabel8");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(lblMotherboardSocket, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel9, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Ram Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel10, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Overclockable:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel11, gridBagConstraints);

        lblMotherboardSize.setText("jLabel12");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(lblMotherboardSize, gridBagConstraints);

        lblMotherboardRamType.setText("jLabel13");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(lblMotherboardRamType, gridBagConstraints);

        lblMotherboardOverclockable.setText("jLabel14");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(lblMotherboardOverclockable, gridBagConstraints);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMotherboard)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnChooseCPU1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoveCPU1)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMotherboard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnChooseCPU1)
                    .addComponent(btnRemoveCPU1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("Case:");

        btnRemoveCase.setText("Remove");
        btnRemoveCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveCaseActionPerformed(evt);
            }
        });

        btnChooseCase.setText("...");
        btnChooseCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseCaseActionPerformed(evt);
            }
        });

        txtCase.setEditable(false);
        txtCase.setBackground(new java.awt.Color(255, 255, 255));
        txtCase.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtCase.setText("jTextField1");

        java.awt.GridBagLayout jPanel6Layout = new java.awt.GridBagLayout();
        jPanel6Layout.columnWidths = new int[] {100, 100, 100, 100, 100, 100};
        jPanel6.setLayout(jPanel6Layout);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Mobo Size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel6.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Case Size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel6.add(jLabel8, gridBagConstraints);

        lblCaseMoboSize.setText("jLabel12");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel6.add(lblCaseMoboSize, gridBagConstraints);

        lblCaseSize.setText("jLabel13");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel6.add(lblCaseSize, gridBagConstraints);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("PSU Size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel6.add(jLabel14, gridBagConstraints);

        lblCasePSUSize.setText("jLabel15");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel6.add(lblCasePSUSize, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("GPU Length:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel6.add(jLabel16, gridBagConstraints);

        lblCaseGPULen.setText("jLabel17");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel6.add(lblCaseGPULen, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("CPU Fan:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel6.add(jLabel18, gridBagConstraints);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("PSU Length:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel6.add(jLabel19, gridBagConstraints);

        lblCaseCPUFanClearance.setText("jLabel20");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel6.add(lblCaseCPUFanClearance, gridBagConstraints);

        lblCasePSULen.setText("jLabel21");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel6.add(lblCasePSULen, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Price:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel6.add(jLabel12, gridBagConstraints);

        lblCasePrice.setText("jLabel13");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel6.add(lblCasePrice, gridBagConstraints);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCase)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnChooseCase)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoveCase)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(btnRemoveCase)
                    .addComponent(btnChooseCase)
                    .addComponent(txtCase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setText("PSU:");

        btnRemovePSU.setText("Remove");
        btnRemovePSU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemovePSUActionPerformed(evt);
            }
        });

        btnChoosePSU.setText("...");
        btnChoosePSU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChoosePSUActionPerformed(evt);
            }
        });

        txtPSU.setEditable(false);
        txtPSU.setBackground(new java.awt.Color(255, 255, 255));
        txtPSU.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtPSU.setText("jTextField1");

        java.awt.GridBagLayout jPanel8Layout = new java.awt.GridBagLayout();
        jPanel8Layout.columnWidths = new int[] {100, 100, 100, 100, 100, 100};
        jPanel8.setLayout(jPanel8Layout);

        lblPSUWattage.setText("jLabel15");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel8.add(lblPSUWattage, gridBagConstraints);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("Price:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel8.add(jLabel17, gridBagConstraints);

        lblPSUPrice.setText("jLabel20");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel8.add(lblPSUPrice, gridBagConstraints);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("Size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel8.add(jLabel21, gridBagConstraints);

        lblPSUSize.setText("jLabel22");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel8.add(lblPSUSize, gridBagConstraints);

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("Wattage:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel8.add(jLabel23, gridBagConstraints);

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setText("Modularity:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel8.add(jLabel24, gridBagConstraints);

        lblPSUModulatiry.setText("jLabel25");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel8.add(lblPSUModulatiry, gridBagConstraints);

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel26.setText("Length:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel8.add(jLabel26, gridBagConstraints);

        lblPSULen.setText("jLabel27");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel8.add(lblPSULen, gridBagConstraints);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPSU)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnChoosePSU)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemovePSU))
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(btnRemovePSU)
                    .addComponent(btnChoosePSU)
                    .addComponent(txtPSU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        btnRamAdd.setText("Add...");
        btnRamAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRamAddActionPerformed(evt);
            }
        });

        lstRam.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lstRam.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(lstRam);

        btnRamClearRemove.setText("Clear/Remove");
        btnRamClearRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRamClearRemoveActionPerformed(evt);
            }
        });

        java.awt.GridBagLayout jPanel10Layout = new java.awt.GridBagLayout();
        jPanel10Layout.columnWidths = new int[] {75, 75, 75, 75, 75, 75};
        jPanel10.setLayout(jPanel10Layout);

        lblRamCap.setText("jLabel20");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel10.add(lblRamCap, gridBagConstraints);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("Price:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel10.add(jLabel22, gridBagConstraints);

        lblRamPrice.setText("jLabel25");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel10.add(lblRamPrice, gridBagConstraints);

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel27.setText("Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel10.add(jLabel27, gridBagConstraints);

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel28.setText("Capacity:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel10.add(jLabel28, gridBagConstraints);

        lblRamType.setText("jLabel29");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel10.add(lblRamType, gridBagConstraints);

        btnRAMAdd1More.setText("Add 1 More of Selected");
        btnRAMAdd1More.setEnabled(false);
        btnRAMAdd1More.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRAMAdd1MoreActionPerformed(evt);
            }
        });

        btnRAMAdd2More.setText("Fill with Selected");
        btnRAMAdd2More.setEnabled(false);
        btnRAMAdd2More.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRAMAdd2MoreActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel20.setText("RAM:");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(btnRamAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRamClearRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRAMAdd1More, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRAMAdd2More, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(10, 10, 10))))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRamAdd)
                    .addComponent(btnRamClearRemove)
                    .addComponent(btnRAMAdd2More)
                    .addComponent(btnRAMAdd1More))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setText("Cooler:");

        btnCoolerRemove.setText("Remove");
        btnCoolerRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCoolerRemoveActionPerformed(evt);
            }
        });

        btnCoolerAdd.setText("...");
        btnCoolerAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCoolerAddActionPerformed(evt);
            }
        });

        txtCooler.setEditable(false);
        txtCooler.setBackground(new java.awt.Color(255, 255, 255));
        txtCooler.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtCooler.setText("jTextField1");

        java.awt.GridBagLayout jPanel12Layout = new java.awt.GridBagLayout();
        jPanel12Layout.columnWidths = new int[] {100, 100, 100, 100};
        jPanel12.setLayout(jPanel12Layout);

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel29.setText("Price:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel12.add(jLabel29, gridBagConstraints);

        lblCoolerPrice.setText("jLabel30");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel12.add(lblCoolerPrice, gridBagConstraints);

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel31.setText("Height:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel12.add(jLabel31, gridBagConstraints);

        lblCoolerAirFlow.setText("jLabel32");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel12.add(lblCoolerAirFlow, gridBagConstraints);

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel33.setText("Air Flow:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel12.add(jLabel33, gridBagConstraints);

        lblCoolerHeight.setText("jLabel34");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel12.add(lblCoolerHeight, gridBagConstraints);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setText("Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel12.add(jLabel25, gridBagConstraints);

        lblCoolerType.setText("jLabel35");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel12.add(lblCoolerType, gridBagConstraints);

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel36.setText("Scoket(s):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel12.add(jLabel36, gridBagConstraints);

        lblCoolerSockets.setText("jLabel37");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel12.add(lblCoolerSockets, gridBagConstraints);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCooler)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCoolerAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCoolerRemove)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(btnCoolerRemove)
                    .addComponent(btnCoolerAdd)
                    .addComponent(txtCooler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel13.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel30.setText("Storage:");

        lstStorage.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lstStorage.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lstStorage);

        btnStorageAdd.setText("Add...");
        btnStorageAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStorageAddActionPerformed(evt);
            }
        });

        java.awt.GridBagLayout jPanel14Layout = new java.awt.GridBagLayout();
        jPanel14Layout.columnWidths = new int[] {75, 75, 75, 75, 75, 75};
        jPanel14.setLayout(jPanel14Layout);

        lblStoragelTotalCapacity.setText("jLabel32");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel14.add(lblStoragelTotalCapacity, gridBagConstraints);

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel34.setText("Price:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel14.add(jLabel34, gridBagConstraints);

        lblStoragePrice.setText("jLabel35");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel14.add(lblStoragePrice, gridBagConstraints);

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel37.setText("Type(s):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel14.add(jLabel37, gridBagConstraints);

        lblStorageType.setText("jLabel38");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel14.add(lblStorageType, gridBagConstraints);

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel39.setText("Total Capacity:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel14.add(jLabel39, gridBagConstraints);

        btnStorageClearRemove.setText("Clear/Remove");
        btnStorageClearRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStorageClearRemoveActionPerformed(evt);
            }
        });

        btnStorageAdd1More.setText("Add 1 More of Selected");
        btnStorageAdd1More.setEnabled(false);
        btnStorageAdd1More.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStorageAdd1MoreActionPerformed(evt);
            }
        });

        btnStorageAdd2More.setText("Add 2 more of Selected");
        btnStorageAdd2More.setEnabled(false);
        btnStorageAdd2More.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStorageAdd2MoreActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel13Layout.createSequentialGroup()
                                .addComponent(btnStorageAdd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnStorageClearRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnStorageAdd1More)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnStorageAdd2More)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStorageAdd)
                    .addComponent(btnStorageClearRemove)
                    .addComponent(btnStorageAdd1More)
                    .addComponent(btnStorageAdd2More))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel15.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel32.setText("GPU:");

        lstGPU.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lstGPU.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(lstGPU);

        btnGPUAdd.setText("Add...");
        btnGPUAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPUAddActionPerformed(evt);
            }
        });

        btnGPUClearRemove.setText("Clear/Remove");
        btnGPUClearRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPUClearRemoveActionPerformed(evt);
            }
        });

        java.awt.GridBagLayout jPanel16Layout = new java.awt.GridBagLayout();
        jPanel16Layout.columnWidths = new int[] {75, 75, 75, 75, 75, 75};
        jPanel16.setLayout(jPanel16Layout);

        lblGPUWattage.setText("jLabel35");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel16.add(lblGPUWattage, gridBagConstraints);

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel38.setText("Price:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel16.add(jLabel38, gridBagConstraints);

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel40.setText("Max Length:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel16.add(jLabel40, gridBagConstraints);

        lblGPUMaxLen.setText("jLabel41");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel16.add(lblGPUMaxLen, gridBagConstraints);

        lblGPUPrice.setText("jLabel43");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel16.add(lblGPUPrice, gridBagConstraints);

        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel44.setText("Wattage");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel16.add(jLabel44, gridBagConstraints);

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel46.setText("VRAM:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel16.add(jLabel46, gridBagConstraints);

        lblGPUVRam.setText("jLabel47");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel16.add(lblGPUVRam, gridBagConstraints);

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel35.setText("Dual GPU Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel16.add(jLabel35, gridBagConstraints);

        lblGPUDualType.setText("jLabel47");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel16.add(lblGPUDualType, gridBagConstraints);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(btnGPUAdd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGPUClearRemove)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGPUAdd)
                    .addComponent(btnGPUClearRemove))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel17.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblCostAnalyzer.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblCostAnalyzer.setText("Cost Analyzer:");

        jPanel18.setLayout(new java.awt.GridBagLayout());

        jLabel41.setText("Used?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel18.add(jLabel41, gridBagConstraints);

        jLabel42.setText("Cost");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        jPanel18.add(jLabel42, gridBagConstraints);

        jLabel43.setText("CPU:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel18.add(jLabel43, gridBagConstraints);

        jLabel45.setText("Include?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel18.add(jLabel45, gridBagConstraints);

        chkIncludeCase.setSelected(true);
        chkIncludeCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIncludeCaseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel18.add(chkIncludeCase, gridBagConstraints);

        chkUseCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseCaseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        jPanel18.add(chkUseCase, gridBagConstraints);

        lblCostCase.setText("jLabel47");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        jPanel18.add(lblCostCase, gridBagConstraints);

        jLabel48.setText("Case:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel18.add(jLabel48, gridBagConstraints);

        jLabel49.setText("Motherboard:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel18.add(jLabel49, gridBagConstraints);

        chkIncludeMobo.setSelected(true);
        chkIncludeMobo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIncludeMoboActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel18.add(chkIncludeMobo, gridBagConstraints);

        chkUseMobo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseMoboActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        jPanel18.add(chkUseMobo, gridBagConstraints);

        chkIncludeCPU.setSelected(true);
        chkIncludeCPU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIncludeCPUActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        jPanel18.add(chkIncludeCPU, gridBagConstraints);

        chkUseCPU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseCPUActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        jPanel18.add(chkUseCPU, gridBagConstraints);

        jLabel50.setText("PSU:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel18.add(jLabel50, gridBagConstraints);

        jLabel51.setText("Cooler:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel18.add(jLabel51, gridBagConstraints);

        jLabel52.setText("RAM:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel18.add(jLabel52, gridBagConstraints);

        jLabel53.setText("Storage:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel18.add(jLabel53, gridBagConstraints);

        jLabel54.setText("GPU:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel18.add(jLabel54, gridBagConstraints);

        chkIncludePSU.setSelected(true);
        chkIncludePSU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIncludePSUActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        jPanel18.add(chkIncludePSU, gridBagConstraints);

        chkIncludeCooler.setSelected(true);
        chkIncludeCooler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIncludeCoolerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        jPanel18.add(chkIncludeCooler, gridBagConstraints);

        chkIncludeRAM.setSelected(true);
        chkIncludeRAM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIncludeRAMActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        jPanel18.add(chkIncludeRAM, gridBagConstraints);

        chkIncludeStorage.setSelected(true);
        chkIncludeStorage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIncludeStorageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        jPanel18.add(chkIncludeStorage, gridBagConstraints);

        chkIncludeGPU.setSelected(true);
        chkIncludeGPU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIncludeGPUActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        jPanel18.add(chkIncludeGPU, gridBagConstraints);

        chkUsePSU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUsePSUActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        jPanel18.add(chkUsePSU, gridBagConstraints);

        chkUseCooler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseCoolerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        jPanel18.add(chkUseCooler, gridBagConstraints);

        chkUseRAM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseRAMActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        jPanel18.add(chkUseRAM, gridBagConstraints);

        chkUseStorage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseStorageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        jPanel18.add(chkUseStorage, gridBagConstraints);

        chkUseGPU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseGPUActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        jPanel18.add(chkUseGPU, gridBagConstraints);

        lblCostMobo.setText("jLabel55");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        jPanel18.add(lblCostMobo, gridBagConstraints);

        lblCostCPU.setText("jLabel56");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        jPanel18.add(lblCostCPU, gridBagConstraints);

        lblCostPSU.setText("jLabel57");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        jPanel18.add(lblCostPSU, gridBagConstraints);

        lblCostCooler.setText("jLabel58");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        jPanel18.add(lblCostCooler, gridBagConstraints);

        lblCostRAM.setText("jLabel59");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        jPanel18.add(lblCostRAM, gridBagConstraints);

        lblCostStorage.setText("jLabel60");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        jPanel18.add(lblCostStorage, gridBagConstraints);

        lblCostGPU.setText("jLabel61");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        jPanel18.add(lblCostGPU, gridBagConstraints);

        lblCostTotal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblCostTotal.setText("jLabel47");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        jPanel18.add(lblCostTotal, gridBagConstraints);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCostAnalyzer)))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCostAnalyzer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel19.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblCostAnalyzer1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblCostAnalyzer1.setText("Notes:");

        lstNotes.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lstNotes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(lstNotes);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(lblCostAnalyzer1)
                        .addGap(0, 478, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCostAnalyzer1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4)
                .addContainerGap())
        );

        jPanel20.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblCostAnalyzer2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblCostAnalyzer2.setText("Wattage Checker:");

        jPanel22.setLayout(new java.awt.GridBagLayout());

        lblWattagePSU.setText("jLabel55");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel22.add(lblWattagePSU, gridBagConstraints);

        jLabel56.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel56.setText("PSU:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel22.add(jLabel56, gridBagConstraints);

        jLabel57.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel57.setText("CPU:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel22.add(jLabel57, gridBagConstraints);

        jLabel58.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel58.setText("GPU:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel22.add(jLabel58, gridBagConstraints);

        lblWattageCPU.setText("jLabel59");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel22.add(lblWattageCPU, gridBagConstraints);

        lblWattageBalance.setText("jLabel60");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel22.add(lblWattageBalance, gridBagConstraints);

        lblWattageGPU.setText("jLabel61");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel22.add(lblWattageGPU, gridBagConstraints);

        jLabel62.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel62.setText("Balance:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel22.add(jLabel62, gridBagConstraints);

        btnDEBUG.setText("Debug");
        btnDEBUG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDEBUGActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCostAnalyzer2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDEBUG)
                .addGap(82, 82, 82))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCostAnalyzer2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDEBUG)
                .addGap(38, 38, 38))
        );

        jPanel21.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel47.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel47.setText("Benchmark:");

        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel67.setText("Basic CPU Score:");

        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel68.setText("Overclock Score");

        txtBMBasicCPU.setEditable(false);
        txtBMBasicCPU.setText("jTextField1");

        txtBMCPUOC.setEditable(false);
        txtBMCPUOC.setText("jTextField2");

        jLabel69.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel69.setText("Memory Channel Score:");

        txtBMMemChan.setEditable(false);
        txtBMMemChan.setText("jTextField3");

        jLabel70.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel70.setText("Memory Clock Score:");

        txtBMMemClk.setEditable(false);
        txtBMMemClk.setText("jTextField4");

        lblBMCPUScore.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblBMCPUScore.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBMCPUScore.setText("jLabel65");

        jLabel71.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        jLabel71.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel71.setText("CPU");

        jLabel76.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel76.setText("GPU");

        jLabel77.setText("GPU 1 Score:");

        txtGPU1Score.setEditable(false);
        txtGPU1Score.setText("jTextField1");

        txtGPU2Score.setEditable(false);
        txtGPU2Score.setText("jTextField2");

        jLabel78.setText("GPU 2 Score:");

        txtGPUCombinedScore.setEditable(false);
        txtGPUCombinedScore.setText("jTextField1");

        jLabel79.setText("Combined Score:");

        jLabel80.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        jLabel80.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel80.setText("GPU");

        lblBMOverallScore.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblBMOverallScore.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBMOverallScore.setText("jLabel81");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel71, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel70, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel69, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBMMemChan)
                            .addComponent(txtBMMemClk)))
                    .addComponent(jLabel76, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblBMCPUScore, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBMCPUOC))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                                .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBMBasicCPU, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel77)
                                    .addComponent(jLabel78)
                                    .addComponent(jLabel79))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtGPUCombinedScore, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                                    .addComponent(txtGPU2Score, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtGPU1Score, javax.swing.GroupLayout.Alignment.TRAILING)))))
                    .addComponent(jLabel80, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel47)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lblBMOverallScore, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel71)
                .addGap(12, 12, 12)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel67)
                    .addComponent(txtBMBasicCPU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel68)
                    .addComponent(txtBMCPUOC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69)
                    .addComponent(txtBMMemChan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel70)
                    .addComponent(txtBMMemClk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBMCPUScore)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel76)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77)
                    .addComponent(txtGPU1Score, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGPU2Score, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel78))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGPUCombinedScore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel80)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBMOverallScore)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel23.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel55.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel55.setText("CPU Overclocking:");

        jLabel59.setText("CPU Base Clock:");

        jLabel60.setText("MHz");

        btnCPUBaseClockU1.setText("+1");
        btnCPUBaseClockU1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCPUBaseClockU1ActionPerformed(evt);
            }
        });

        btnCPUBaseClockU10.setText("+10");
        btnCPUBaseClockU10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCPUBaseClockU10ActionPerformed(evt);
            }
        });

        btnCPUBaseClockD10.setText("-10");
        btnCPUBaseClockD10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCPUBaseClockD10ActionPerformed(evt);
            }
        });

        btnCPUBaseClockD1.setText("-1");
        btnCPUBaseClockD1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCPUBaseClockD1ActionPerformed(evt);
            }
        });

        txtCPUBaseClock.setEditable(false);
        txtCPUBaseClock.setBackground(new java.awt.Color(255, 255, 255));
        txtCPUBaseClock.setText("jTextField1");
        txtCPUBaseClock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCPUBaseClockActionPerformed(evt);
            }
        });

        jLabel64.setText("CPU Multiplier:");

        txtClockMultiplier.setEditable(false);
        txtClockMultiplier.setBackground(new java.awt.Color(255, 255, 255));
        txtClockMultiplier.setText("jTextField2");

        btnCPUClockMultD10.setText("-10");
        btnCPUClockMultD10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCPUClockMultD10ActionPerformed(evt);
            }
        });

        btnCPUClockMultD1.setText("-1");
        btnCPUClockMultD1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCPUClockMultD1ActionPerformed(evt);
            }
        });

        btnCPUClockMultU1.setText("+1");
        btnCPUClockMultU1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCPUClockMultU1ActionPerformed(evt);
            }
        });

        btnCPUClockMultU10.setText("+10");
        btnCPUClockMultU10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCPUClockMultU10ActionPerformed(evt);
            }
        });

        lblCPUFinalFreq.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblCPUFinalFreq.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCPUFinalFreq.setText("jLabel66");

        jLabel66.setText("X");

        btnOCCPUReset.setText("Reset");
        btnOCCPUReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOCCPUResetActionPerformed(evt);
            }
        });

        jLabel65.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel65.setText("GPU Overclocking:");

        jLabel72.setText("GPU Clock");

        txtGPUClock.setText("jTextField1");

        jLabel73.setText("MHz");

        btnGPUBaseClockD10.setText("-10");
        btnGPUBaseClockD10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPUBaseClockD10ActionPerformed(evt);
            }
        });

        btnGPUBaseClockD1.setText("-1");
        btnGPUBaseClockD1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPUBaseClockD1ActionPerformed(evt);
            }
        });

        btnGPUBaseClockU1.setText("+1");
        btnGPUBaseClockU1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPUBaseClockU1ActionPerformed(evt);
            }
        });

        btnGPUBaseClockU10.setText("+10");
        btnGPUBaseClockU10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPUBaseClockU10ActionPerformed(evt);
            }
        });

        jLabel74.setText("MHz");

        txtGPUMemClock.setText("jTextField1");

        jLabel75.setText("VRAM Clock");

        btnGPUMemClockU10.setText("+10");
        btnGPUMemClockU10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPUMemClockU10ActionPerformed(evt);
            }
        });

        btnGPUMemClockU1.setText("+1");
        btnGPUMemClockU1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPUMemClockU1ActionPerformed(evt);
            }
        });

        btnGPUMemClockD1.setText("-1");
        btnGPUMemClockD1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPUMemClockD1ActionPerformed(evt);
            }
        });

        btnGPUMemClockD10.setText("-10");
        btnGPUMemClockD10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPUMemClockD10ActionPerformed(evt);
            }
        });

        btnGPUOCReset.setText("Reset");
        btnGPUOCReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGPUOCResetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel65)
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addComponent(btnGPUBaseClockD10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGPUBaseClockD1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGPUBaseClockU1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGPUBaseClockU10))
                            .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel23Layout.createSequentialGroup()
                                    .addComponent(jLabel75)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtGPUMemClock)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel74))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel23Layout.createSequentialGroup()
                                    .addComponent(btnGPUMemClockD10)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel23Layout.createSequentialGroup()
                                            .addGap(10, 10, 10)
                                            .addComponent(btnGPUOCReset, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel23Layout.createSequentialGroup()
                                            .addComponent(btnGPUMemClockD1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnGPUMemClockU1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnGPUMemClockU10)))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                                .addComponent(jLabel72)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtGPUClock, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel73)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel55)
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addComponent(jLabel64)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtClockMultiplier, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel66))
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addComponent(lblCPUFinalFreq, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnOCCPUReset, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addComponent(btnCPUClockMultD10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCPUClockMultD1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCPUClockMultU1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCPUClockMultU10))
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel23Layout.createSequentialGroup()
                                        .addComponent(jLabel59)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtCPUBaseClock))
                                    .addGroup(jPanel23Layout.createSequentialGroup()
                                        .addComponent(btnCPUBaseClockD10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnCPUBaseClockD1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnCPUBaseClockU1)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnCPUBaseClockU10)
                                    .addComponent(jLabel60))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel55)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel59)
                    .addComponent(jLabel60)
                    .addComponent(txtCPUBaseClock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCPUBaseClockU1)
                    .addComponent(btnCPUBaseClockD10)
                    .addComponent(btnCPUBaseClockD1)
                    .addComponent(btnCPUBaseClockU10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel64)
                    .addComponent(txtClockMultiplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCPUClockMultU1)
                    .addComponent(btnCPUClockMultD10)
                    .addComponent(btnCPUClockMultD1)
                    .addComponent(btnCPUClockMultU10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCPUFinalFreq, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOCCPUReset))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel72)
                    .addComponent(txtGPUClock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel73))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGPUBaseClockU1)
                    .addComponent(btnGPUBaseClockD10)
                    .addComponent(btnGPUBaseClockD1)
                    .addComponent(btnGPUBaseClockU10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel75)
                    .addComponent(txtGPUMemClock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel74))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGPUMemClockU1)
                    .addComponent(btnGPUMemClockD10)
                    .addComponent(btnGPUMemClockD1)
                    .addComponent(btnGPUMemClockU10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGPUOCReset)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChooseCPUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseCPUActionPerformed
        (new HardwarePicker(this,pc, EnumHardwareType.CPU)).setVisible(true);
    }//GEN-LAST:event_btnChooseCPUActionPerformed

    private void btnRemoveCPUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveCPUActionPerformed
        this.pc.clearHardwareType(EnumHardwareType.CPU);
    }//GEN-LAST:event_btnRemoveCPUActionPerformed

    private void btnChooseCPU1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseCPU1ActionPerformed
        (new HardwarePicker(this,pc, EnumHardwareType.MOTHERBOARD)).setVisible(true);
    }//GEN-LAST:event_btnChooseCPU1ActionPerformed

    private void btnRemoveCPU1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveCPU1ActionPerformed
        this.pc.clearHardwareType(EnumHardwareType.MOTHERBOARD);
    }//GEN-LAST:event_btnRemoveCPU1ActionPerformed

    private void btnChooseCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseCaseActionPerformed
        (new HardwarePicker(this,pc, EnumHardwareType.CASES)).setVisible(true);
    }//GEN-LAST:event_btnChooseCaseActionPerformed

    private void btnRemoveCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveCaseActionPerformed
        this.pc.clearHardwareType(EnumHardwareType.CASES);
    }//GEN-LAST:event_btnRemoveCaseActionPerformed

    private void btnRemovePSUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemovePSUActionPerformed
        this.pc.clearHardwareType(EnumHardwareType.PSU);
    }//GEN-LAST:event_btnRemovePSUActionPerformed

    private void btnChoosePSUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoosePSUActionPerformed
        (new HardwarePicker(this,pc, EnumHardwareType.PSU)).setVisible(true);
    }//GEN-LAST:event_btnChoosePSUActionPerformed

    private void btnRamAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRamAddActionPerformed
        (new HardwarePicker(this,pc, EnumHardwareType.RAM)).setVisible(true);
    }//GEN-LAST:event_btnRamAddActionPerformed

    private void btnRamClearRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRamClearRemoveActionPerformed
        Integer[] selIndexes = Arrays.stream(this.lstRam.getSelectedIndices()).boxed().toArray(Integer[]::new);
        if(selIndexes.length == 0) this.pc.getHardwareSet(EnumHardwareType.RAM).clear();
        else {    
            Arrays.sort(selIndexes, Collections.reverseOrder());
            for(int selIndex:selIndexes) this.pc.getHardwareSet(EnumHardwareType.RAM).removeHardwareAtIndex(selIndex);
        }
    }//GEN-LAST:event_btnRamClearRemoveActionPerformed

    private void btnRAMAdd1MoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRAMAdd1MoreActionPerformed
        for(int i:this.lstRam.getSelectedIndices()) this.pc.getHardwareSet(EnumHardwareType.RAM).addHardware(i, 1);
    }//GEN-LAST:event_btnRAMAdd1MoreActionPerformed

    private void btnRAMAdd2MoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRAMAdd2MoreActionPerformed
        this.pc.getHardwareSet(EnumHardwareType.RAM).addHardware(this.lstRam.getSelectedIndex(), EnumHardwareType.RAM.getMaxNumberInBuild());
    }//GEN-LAST:event_btnRAMAdd2MoreActionPerformed

    private void btnCoolerAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCoolerAddActionPerformed
        (new HardwarePicker(this,pc,EnumHardwareType.COOLER)).setVisible(true);
    }//GEN-LAST:event_btnCoolerAddActionPerformed

    private void btnCoolerRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCoolerRemoveActionPerformed
        this.pc.clearHardwareType(EnumHardwareType.COOLER);
    }//GEN-LAST:event_btnCoolerRemoveActionPerformed

    private void btnStorageAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStorageAddActionPerformed
        (new HardwarePicker(this,pc,EnumHardwareType.STORAGE)).setVisible(true);
    }//GEN-LAST:event_btnStorageAddActionPerformed

    private void btnStorageClearRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStorageClearRemoveActionPerformed
        Integer[] selIndexes = Arrays.stream(this.lstStorage.getSelectedIndices()).boxed().toArray(Integer[]::new);
        if(selIndexes.length == 0) this.pc.getHardwareSet(EnumHardwareType.STORAGE).clear();
        else {
            Arrays.sort(selIndexes, Collections.reverseOrder());
            for(int selIndex:selIndexes) this.pc.getHardwareSet(EnumHardwareType.STORAGE).removeHardwareAtIndex(selIndex);
        }
    }//GEN-LAST:event_btnStorageClearRemoveActionPerformed

    private void btnStorageAdd2MoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStorageAdd2MoreActionPerformed
        for(int i:this.lstStorage.getSelectedIndices()) this.pc.getHardwareSet(EnumHardwareType.STORAGE).addHardware(i, 2);
    }//GEN-LAST:event_btnStorageAdd2MoreActionPerformed

    private void btnStorageAdd1MoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStorageAdd1MoreActionPerformed
        for(int i:this.lstStorage.getSelectedIndices()) this.pc.getHardwareSet(EnumHardwareType.STORAGE).addHardware(i, 1);
    }//GEN-LAST:event_btnStorageAdd1MoreActionPerformed

    private void btnGPUAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPUAddActionPerformed
        (new HardwarePicker(this,pc,EnumHardwareType.GPU)).setVisible(true);
    }//GEN-LAST:event_btnGPUAddActionPerformed

    private void btnGPUClearRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPUClearRemoveActionPerformed
        Integer[] selIndexes = Arrays.stream(this.lstGPU.getSelectedIndices()).boxed().toArray(Integer[]::new);
        if(selIndexes.length == 0) this.pc.getHardwareSet(EnumHardwareType.GPU).clear();
        else{
            Arrays.sort(selIndexes, Collections.reverseOrder());
            for(int selIndex:selIndexes) this.pc.getHardwareSet(EnumHardwareType.GPU).removeHardwareAtIndex(selIndex);
        }
    }//GEN-LAST:event_btnGPUClearRemoveActionPerformed

    private void chkIncludeMoboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIncludeMoboActionPerformed
        this.pcCostTracker.setInclude(EnumHardwareType.MOTHERBOARD, this.chkIncludeMobo.isSelected());
    }//GEN-LAST:event_chkIncludeMoboActionPerformed

    private void chkUseCPUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseCPUActionPerformed
        this.pcCostTracker.setUsed(EnumHardwareType.CPU, this.chkUseCPU.isSelected());
    }//GEN-LAST:event_chkUseCPUActionPerformed

    private void chkIncludeCPUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIncludeCPUActionPerformed
        this.pcCostTracker.setInclude(EnumHardwareType.CPU, this.chkIncludeCPU.isSelected());
    }//GEN-LAST:event_chkIncludeCPUActionPerformed

    private void chkIncludeCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIncludeCaseActionPerformed
        this.pcCostTracker.setInclude(EnumHardwareType.CASES, this.chkIncludeCase.isSelected());
    }//GEN-LAST:event_chkIncludeCaseActionPerformed

    private void chkIncludePSUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIncludePSUActionPerformed
        this.pcCostTracker.setInclude(EnumHardwareType.PSU, this.chkIncludePSU.isSelected());
    }//GEN-LAST:event_chkIncludePSUActionPerformed

    private void chkIncludeCoolerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIncludeCoolerActionPerformed
        this.pcCostTracker.setInclude(EnumHardwareType.COOLER, this.chkIncludeCooler.isSelected());
    }//GEN-LAST:event_chkIncludeCoolerActionPerformed

    private void chkIncludeRAMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIncludeRAMActionPerformed
        this.pcCostTracker.setInclude(EnumHardwareType.RAM, this.chkIncludeRAM.isSelected());
    }//GEN-LAST:event_chkIncludeRAMActionPerformed

    private void chkIncludeStorageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIncludeStorageActionPerformed
        this.pcCostTracker.setInclude(EnumHardwareType.STORAGE, this.chkIncludeStorage.isSelected());
    }//GEN-LAST:event_chkIncludeStorageActionPerformed

    private void chkIncludeGPUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIncludeGPUActionPerformed
        this.pcCostTracker.setInclude(EnumHardwareType.GPU, this.chkIncludeGPU.isSelected());
    }//GEN-LAST:event_chkIncludeGPUActionPerformed

    private void chkUseCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseCaseActionPerformed
        this.pcCostTracker.setUsed(EnumHardwareType.CASES, this.chkUseCase.isSelected());
    }//GEN-LAST:event_chkUseCaseActionPerformed

    private void chkUseMoboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseMoboActionPerformed
        this.pcCostTracker.setUsed(EnumHardwareType.MOTHERBOARD, this.chkUseMobo.isSelected());
    }//GEN-LAST:event_chkUseMoboActionPerformed

    private void chkUsePSUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUsePSUActionPerformed
        this.pcCostTracker.setUsed(EnumHardwareType.PSU, this.chkUsePSU.isSelected());
    }//GEN-LAST:event_chkUsePSUActionPerformed

    private void chkUseCoolerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseCoolerActionPerformed
        this.pcCostTracker.setUsed(EnumHardwareType.COOLER, this.chkUseCooler.isSelected());
    }//GEN-LAST:event_chkUseCoolerActionPerformed

    private void chkUseRAMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseRAMActionPerformed
        this.pcCostTracker.setUsed(EnumHardwareType.RAM, this.chkUseRAM.isSelected());
    }//GEN-LAST:event_chkUseRAMActionPerformed

    private void chkUseStorageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseStorageActionPerformed
        this.pcCostTracker.setUsed(EnumHardwareType.STORAGE, this.chkUseStorage.isSelected());
    }//GEN-LAST:event_chkUseStorageActionPerformed

    private void chkUseGPUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseGPUActionPerformed
        this.pcCostTracker.setUsed(EnumHardwareType.GPU, this.chkUseGPU.isSelected());
    }//GEN-LAST:event_chkUseGPUActionPerformed

    private void btnCPUBaseClockU1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCPUBaseClockU1ActionPerformed
        pc.changeCPUBaseClock(1);
    }//GEN-LAST:event_btnCPUBaseClockU1ActionPerformed

    private void btnCPUClockMultU1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCPUClockMultU1ActionPerformed
        pc.changeCPURatio(1);
    }//GEN-LAST:event_btnCPUClockMultU1ActionPerformed

    private void txtCPUBaseClockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCPUBaseClockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCPUBaseClockActionPerformed

    private void btnCPUBaseClockD10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCPUBaseClockD10ActionPerformed
        pc.changeCPUBaseClock(-10);
    }//GEN-LAST:event_btnCPUBaseClockD10ActionPerformed

    private void btnCPUBaseClockD1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCPUBaseClockD1ActionPerformed
        pc.changeCPUBaseClock(-1);
    }//GEN-LAST:event_btnCPUBaseClockD1ActionPerformed

    private void btnCPUBaseClockU10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCPUBaseClockU10ActionPerformed
        pc.changeCPUBaseClock(10);
    }//GEN-LAST:event_btnCPUBaseClockU10ActionPerformed

    private void btnCPUClockMultD10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCPUClockMultD10ActionPerformed
        pc.changeCPURatio(-10);
    }//GEN-LAST:event_btnCPUClockMultD10ActionPerformed

    private void btnCPUClockMultD1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCPUClockMultD1ActionPerformed
        pc.changeCPURatio(-1);
    }//GEN-LAST:event_btnCPUClockMultD1ActionPerformed

    private void btnCPUClockMultU10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCPUClockMultU10ActionPerformed
        pc.changeCPURatio(10);
    }//GEN-LAST:event_btnCPUClockMultU10ActionPerformed

    private void btnOCCPUResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOCCPUResetActionPerformed
        pc.resetCPUClockFreq();
    }//GEN-LAST:event_btnOCCPUResetActionPerformed

    private void btnGPUBaseClockD10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPUBaseClockD10ActionPerformed
        pc.changeGPUCoreClock(-10);
    }//GEN-LAST:event_btnGPUBaseClockD10ActionPerformed

    private void btnGPUBaseClockD1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPUBaseClockD1ActionPerformed
        pc.changeGPUCoreClock(-1);
    }//GEN-LAST:event_btnGPUBaseClockD1ActionPerformed

    private void btnGPUBaseClockU1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPUBaseClockU1ActionPerformed
        pc.changeGPUCoreClock(1);
    }//GEN-LAST:event_btnGPUBaseClockU1ActionPerformed

    private void btnGPUBaseClockU10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPUBaseClockU10ActionPerformed
        pc.changeGPUCoreClock(10);
    }//GEN-LAST:event_btnGPUBaseClockU10ActionPerformed

    private void btnGPUMemClockU10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPUMemClockU10ActionPerformed
        pc.changeGPUMemClock(10);
    }//GEN-LAST:event_btnGPUMemClockU10ActionPerformed

    private void btnGPUMemClockU1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPUMemClockU1ActionPerformed
        pc.changeGPUMemClock(1);
    }//GEN-LAST:event_btnGPUMemClockU1ActionPerformed

    private void btnGPUMemClockD1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPUMemClockD1ActionPerformed
        pc.changeGPUMemClock(-1);
    }//GEN-LAST:event_btnGPUMemClockD1ActionPerformed

    private void btnGPUMemClockD10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPUMemClockD10ActionPerformed
        pc.changeGPUMemClock(-10);
    }//GEN-LAST:event_btnGPUMemClockD10ActionPerformed

    private void btnGPUOCResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGPUOCResetActionPerformed
        pc.resetGPUClockFreq();
    }//GEN-LAST:event_btnGPUOCResetActionPerformed

    private void btnDEBUGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDEBUGActionPerformed
        System.out.println("GPU Core Clock: " + this.pc.getCurrentGPUCoreClock());
        System.out.println("GPU Mem Clock: " + this.pc.getCurrentGPUMemClock());
    }//GEN-LAST:event_btnDEBUGActionPerformed
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCPUBaseClockD1;
    private javax.swing.JButton btnCPUBaseClockD10;
    private javax.swing.JButton btnCPUBaseClockU1;
    private javax.swing.JButton btnCPUBaseClockU10;
    private javax.swing.JButton btnCPUClockMultD1;
    private javax.swing.JButton btnCPUClockMultD10;
    private javax.swing.JButton btnCPUClockMultU1;
    private javax.swing.JButton btnCPUClockMultU10;
    private javax.swing.JButton btnChooseCPU;
    private javax.swing.JButton btnChooseCPU1;
    private javax.swing.JButton btnChooseCase;
    private javax.swing.JButton btnChoosePSU;
    private javax.swing.JButton btnCoolerAdd;
    private javax.swing.JButton btnCoolerRemove;
    private javax.swing.JButton btnDEBUG;
    private javax.swing.JButton btnGPUAdd;
    private javax.swing.JButton btnGPUBaseClockD1;
    private javax.swing.JButton btnGPUBaseClockD10;
    private javax.swing.JButton btnGPUBaseClockU1;
    private javax.swing.JButton btnGPUBaseClockU10;
    private javax.swing.JButton btnGPUClearRemove;
    private javax.swing.JButton btnGPUMemClockD1;
    private javax.swing.JButton btnGPUMemClockD10;
    private javax.swing.JButton btnGPUMemClockU1;
    private javax.swing.JButton btnGPUMemClockU10;
    private javax.swing.JButton btnGPUOCReset;
    private javax.swing.JButton btnOCCPUReset;
    private javax.swing.JButton btnRAMAdd1More;
    private javax.swing.JButton btnRAMAdd2More;
    private javax.swing.JButton btnRamAdd;
    private javax.swing.JButton btnRamClearRemove;
    private javax.swing.JButton btnRemoveCPU;
    private javax.swing.JButton btnRemoveCPU1;
    private javax.swing.JButton btnRemoveCase;
    private javax.swing.JButton btnRemovePSU;
    private javax.swing.JButton btnStorageAdd;
    private javax.swing.JButton btnStorageAdd1More;
    private javax.swing.JButton btnStorageAdd2More;
    private javax.swing.JButton btnStorageClearRemove;
    private javax.swing.JCheckBox chkIncludeCPU;
    private javax.swing.JCheckBox chkIncludeCase;
    private javax.swing.JCheckBox chkIncludeCooler;
    private javax.swing.JCheckBox chkIncludeGPU;
    private javax.swing.JCheckBox chkIncludeMobo;
    private javax.swing.JCheckBox chkIncludePSU;
    private javax.swing.JCheckBox chkIncludeRAM;
    private javax.swing.JCheckBox chkIncludeStorage;
    private javax.swing.JCheckBox chkUseCPU;
    private javax.swing.JCheckBox chkUseCase;
    private javax.swing.JCheckBox chkUseCooler;
    private javax.swing.JCheckBox chkUseGPU;
    private javax.swing.JCheckBox chkUseMobo;
    private javax.swing.JCheckBox chkUsePSU;
    private javax.swing.JCheckBox chkUseRAM;
    private javax.swing.JCheckBox chkUseStorage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblBMCPUScore;
    private javax.swing.JLabel lblBMOverallScore;
    private javax.swing.JLabel lblCPUBasicScore;
    private javax.swing.JLabel lblCPUBasicScoreTitle;
    private javax.swing.JLabel lblCPUCores;
    private javax.swing.JLabel lblCPUCoresLabel;
    private javax.swing.JLabel lblCPUFinalFreq;
    private javax.swing.JLabel lblCPUFreq;
    private javax.swing.JLabel lblCPUFreqLabel;
    private javax.swing.JLabel lblCPUMaxVoltage;
    private javax.swing.JLabel lblCPUMemChan;
    private javax.swing.JLabel lblCPUMemChanLabel;
    private javax.swing.JLabel lblCPUOverclockable;
    private javax.swing.JLabel lblCPUOverclockableLabel;
    private javax.swing.JLabel lblCPUPrice;
    private javax.swing.JLabel lblCPUPriceLabel;
    private javax.swing.JLabel lblCPUSocket;
    private javax.swing.JLabel lblCPUSocketLabel;
    private javax.swing.JLabel lblCPUWattage;
    private javax.swing.JLabel lblCPUWattageLabel;
    private javax.swing.JLabel lblCaseCPUFanClearance;
    private javax.swing.JLabel lblCaseGPULen;
    private javax.swing.JLabel lblCaseMoboSize;
    private javax.swing.JLabel lblCasePSULen;
    private javax.swing.JLabel lblCasePSUSize;
    private javax.swing.JLabel lblCasePrice;
    private javax.swing.JLabel lblCaseSize;
    private javax.swing.JLabel lblCoolerAirFlow;
    private javax.swing.JLabel lblCoolerHeight;
    private javax.swing.JLabel lblCoolerPrice;
    private javax.swing.JLabel lblCoolerSockets;
    private javax.swing.JLabel lblCoolerType;
    private javax.swing.JLabel lblCostAnalyzer;
    private javax.swing.JLabel lblCostAnalyzer1;
    private javax.swing.JLabel lblCostAnalyzer2;
    private javax.swing.JLabel lblCostCPU;
    private javax.swing.JLabel lblCostCase;
    private javax.swing.JLabel lblCostCooler;
    private javax.swing.JLabel lblCostGPU;
    private javax.swing.JLabel lblCostMobo;
    private javax.swing.JLabel lblCostPSU;
    private javax.swing.JLabel lblCostRAM;
    private javax.swing.JLabel lblCostStorage;
    private javax.swing.JLabel lblCostTotal;
    private javax.swing.JLabel lblGPUDualType;
    private javax.swing.JLabel lblGPUMaxLen;
    private javax.swing.JLabel lblGPUPrice;
    private javax.swing.JLabel lblGPUVRam;
    private javax.swing.JLabel lblGPUWattage;
    private javax.swing.JLabel lblMotherboardMGPUSupp;
    private javax.swing.JLabel lblMotherboardOverclockable;
    private javax.swing.JLabel lblMotherboardPrice;
    private javax.swing.JLabel lblMotherboardRamType;
    private javax.swing.JLabel lblMotherboardSize;
    private javax.swing.JLabel lblMotherboardSocket;
    private javax.swing.JLabel lblPSULen;
    private javax.swing.JLabel lblPSUModulatiry;
    private javax.swing.JLabel lblPSUPrice;
    private javax.swing.JLabel lblPSUSize;
    private javax.swing.JLabel lblPSUWattage;
    private javax.swing.JLabel lblRamCap;
    private javax.swing.JLabel lblRamPrice;
    private javax.swing.JLabel lblRamType;
    private javax.swing.JLabel lblStoragePrice;
    private javax.swing.JLabel lblStorageType;
    private javax.swing.JLabel lblStoragelTotalCapacity;
    private javax.swing.JLabel lblWattageBalance;
    private javax.swing.JLabel lblWattageCPU;
    private javax.swing.JLabel lblWattageGPU;
    private javax.swing.JLabel lblWattagePSU;
    private javax.swing.JList<String> lstGPU;
    private javax.swing.JList<String> lstNotes;
    private javax.swing.JList<String> lstRam;
    private javax.swing.JList<String> lstStorage;
    private javax.swing.JTextField txtBMBasicCPU;
    private javax.swing.JTextField txtBMCPUOC;
    private javax.swing.JTextField txtBMMemChan;
    private javax.swing.JTextField txtBMMemClk;
    private javax.swing.JTextField txtCPU;
    private javax.swing.JTextField txtCPUBaseClock;
    private javax.swing.JTextField txtCase;
    private javax.swing.JTextField txtClockMultiplier;
    private javax.swing.JTextField txtCooler;
    private javax.swing.JTextField txtGPU1Score;
    private javax.swing.JTextField txtGPU2Score;
    private javax.swing.JTextField txtGPUClock;
    private javax.swing.JTextField txtGPUCombinedScore;
    private javax.swing.JTextField txtGPUMemClock;
    private javax.swing.JTextField txtMotherboard;
    private javax.swing.JTextField txtPSU;
    // End of variables declaration//GEN-END:variables
}
