/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.UI;

import microft.software.pcbuildaid.resources.EnumHardwareType;
import java.util.ArrayList;
import static java.util.Objects.isNull;
import javax.swing.JLabel;
import microft.software.pcbuildaid.PCBuildData.Hardware;
import microft.software.pcbuildaid.resources.EnumKeyStrings;
import microft.software.pcvuildaid.calculators.PCBuild;

/**
 *
 * @author Marc
 */
public class PCBuilder extends javax.swing.JFrame {
    private final PCBuild pc = new PCBuild();
    private final ArrayList<JLabel> cpuLabels = new ArrayList<>();
    private final ArrayList<JLabel> motherboardLabels = new ArrayList<>();
    private final ArrayList<JLabel> caseLabels = new ArrayList<>();
    private final ArrayList<JLabel> psuLabels = new ArrayList<>();
    
    /**
     * Creates new form BenchmarkCalculatorUI
     */
    public PCBuilder() {
        initComponents();    
        
        // Add labels to CPU list.
        cpuLabels.add(this.lblCPUBasicScore);
        cpuLabels.add(this.lblCPUPrice);
        cpuLabels.add(this.lblCPUFreq);
        cpuLabels.add(this.lblCPUCores);
        cpuLabels.add(this.lblCPUOverclockable);
        cpuLabels.add(this.lblCPUWattage);
        cpuLabels.add(this.lblCPUMemChan);
        cpuLabels.add(this.lblCPUSocket);
        
        motherboardLabels.add(this.lblMotherboardPrice);
        motherboardLabels.add(this.lblMotherboardChipset);
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
        
        pc.addONHardwareChange(()->reactToHardwareChange());
        pc.activateHardwareChange();
    }
    
    private void reactToHardwareChange(){
        // this is where we react to hardware changes.
        // Display the currently selected case.
        displayCurrentCPU();
        displayCurrentMotherboard();
        displayCurrentCase();
        displayCurrentPSU();
    }
    
    private void displayCurrentPSU(){
        Hardware psu = pc.getHardware(EnumHardwareType.PSU);
        psuLabels.stream().forEach(x->x.setText("-"));
        this.txtPSU.setText("No PSU Selected.");
        if(isNull(psu)) return;
        this.txtPSU.setText(psu.getConcatName());
        this.lblPSULen.setText(psu.readVal(EnumKeyStrings.LENGTH));
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
        this.lblMotherboardChipset.setText(mobo.readVal(EnumKeyStrings.CHIPSET));
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
    }
    
    private void displayCurrentCase(){
        Hardware theCase = pc.getHardware(EnumHardwareType.CASES);
        caseLabels.stream().forEach(x->x.setText("-"));
        this.txtCase.setText("No Case Selected.");
        if(isNull(theCase)) return;
        this.txtCase.setText(theCase.getConcatName());
        this.lblCaseCPUFanClearance.setText(theCase.readIntVal(EnumKeyStrings.MAX_CPU_FAN_HEIGHT) + " mm");
        this.lblCaseCPUFanClearance.setText(this.formatMaxCPUFanHeight(theCase));
        this.lblCaseGPULen.setText(theCase.readVal(EnumKeyStrings.MAX_GPU_LEN) + " mm");
        this.lblCaseMoboSize.setText(theCase.readVal(EnumKeyStrings.MOTHERBOARD_SIZES));
        this.lblCasePSULen.setText(theCase.readVal(EnumKeyStrings.MAX_PSU_LEN) + " mm");
        this.lblCasePSUSize.setText(theCase.readVal(EnumKeyStrings.PSU_SIZE));
        this.lblCasePrice.setText(formatPrice(theCase.getPrice()));
        this.lblCaseSize.setText(theCase.readVal(EnumKeyStrings.CASE_SIZE));
    }
    
    private String formatPrice(int Price){
        return "$" + Price + ".00";
    }
    
    private String formatPrice(Hardware hw){
        return "$" + hw.getPrice() + ".00";
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
        lblMotherboardChipset = new javax.swing.JLabel();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("PC Build Aid - Benchmark Calculator");
        setBackground(new java.awt.Color(102, 102, 102));

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("CPU:");

        btnChooseCPU.setText("...");
        btnChooseCPU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseCPUActionPerformed(evt);
            }
        });

        txtCPU.setText("None");
        txtCPU.setEnabled(false);

        btnRemoveCPU.setText("Remove");
        btnRemoveCPU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveCPUActionPerformed(evt);
            }
        });

        java.awt.GridBagLayout jPanel4Layout = new java.awt.GridBagLayout();
        jPanel4Layout.columnWidths = new int[] {50, 100, 50, 100, 50, 100};
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
        lblCPUMemChanLabel.setText("Mem. Channels");
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
        txtMotherboard.setText("None");

        java.awt.GridBagLayout jPanel3Layout = new java.awt.GridBagLayout();
        jPanel3Layout.columnWidths = new int[] {50, 100, 50, 100, 50, 100};
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
        jLabel4.setText("Chipset:");
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

        lblMotherboardChipset.setText("jLabel7");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel3.add(lblMotherboardChipset, gridBagConstraints);

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
        txtCase.setText("jTextField1");

        java.awt.GridBagLayout jPanel6Layout = new java.awt.GridBagLayout();
        jPanel6Layout.columnWidths = new int[] {100, 100, 100, 100};
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(728, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(115, Short.MAX_VALUE))
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
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseCPU;
    private javax.swing.JButton btnChooseCPU1;
    private javax.swing.JButton btnChooseCase;
    private javax.swing.JButton btnChoosePSU;
    private javax.swing.JButton btnRemoveCPU;
    private javax.swing.JButton btnRemoveCPU1;
    private javax.swing.JButton btnRemoveCase;
    private javax.swing.JButton btnRemovePSU;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel lblCPUBasicScore;
    private javax.swing.JLabel lblCPUBasicScoreTitle;
    private javax.swing.JLabel lblCPUCores;
    private javax.swing.JLabel lblCPUCoresLabel;
    private javax.swing.JLabel lblCPUFreq;
    private javax.swing.JLabel lblCPUFreqLabel;
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
    private javax.swing.JLabel lblMotherboardChipset;
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
    private javax.swing.JTextField txtCPU;
    private javax.swing.JTextField txtCase;
    private javax.swing.JTextField txtMotherboard;
    private javax.swing.JTextField txtPSU;
    // End of variables declaration//GEN-END:variables
}
