/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.UI;

import microft.software.pcbuildaid.resources.EnumHardwareType;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import microft.software.pcbuildaid.PCBuildData.GameData;
import microft.software.pcbuildaid.resources.EnumKeyStrings;
import microft.software.pcbuildaid.PCBuildData.Hardware;
import microft.software.pcvuildaid.calculators.CompatibilityChecker;
import microft.software.pcvuildaid.calculators.PCBuild;

/**
 *
 * @author marcc
 */
public class HardwarePicker extends javax.swing.JFrame {
    private final PCBuild pc;
    private final EnumHardwareType hwType;
    private final JFrame parent;
    private List<Hardware> tableList;
    
    /**
     * Creates new form CPUPicker
     * @param parent
     * @param pc
     * @param hwType
     */
    public HardwarePicker(JFrame parent, PCBuild pc, EnumHardwareType hwType) {
        this.parent = parent;
        parent.setEnabled(false);
        initComponents();
        this.pc = pc;
        this.hwType = hwType;
        
        this.setExtendedState( this.getExtendedState()|JFrame.MAXIMIZED_BOTH );
        
        this.btnOKx2.setVisible(hwType.getMaxNumberInBuild() >= 2);
        this.btnOKx4.setVisible(hwType.getMaxNumberInBuild() >= 4);
        
        this.tblMain.getSelectionModel().addListSelectionListener((e)->{
            this.btnOK.setEnabled(this.tblMain.getSelectedRowCount() > 0);
            this.btnOKx2.setEnabled((this.tblMain.getSelectedRowCount() * 2) <= hwType.getMaxNumberInBuild());
            this.btnOKx4.setEnabled((this.tblMain.getSelectedRowCount() * 4) <= hwType.getMaxNumberInBuild());
        });
        
        this.chkLevelFilter.setText("Filter to level " + GameData.getLevel());
        
        populateTable();
    }
    
    private void addItems(int count){
        // Grab an array of selection indexes.
        int[] selRows = this.tblMain.getSelectedRows();

        // Compensate for any sorting that might have been done.
        for(int i=0; i < selRows.length; i++) selRows[i] = this.tblMain.getRowSorter().convertRowIndexToModel(selRows[i]);

        // Exit if there's nothing (which shouldn't ever happen)
        if(selRows.length == 0) return;
        
        // Iterate through each selection and add as many times as requested.
        for(int i:selRows)  for (int j=0; j<count; ++j)
            //this.pc.addHardware(GameData.getHardwareArray(hwType).get(i));
            this.pc.addHardware(this.tableList.get(i));
        
        // Close the window.
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
    
    private Class[] getKeyClasses(EnumKeyStrings[] colHeaders){
        Class[] rValue = new Class[colHeaders.length];
        for(int i=0; i<rValue.length; i++)
            rValue[i] = colHeaders[i].getClassType();
        return rValue;
    }
    
    private String[] getKeyStrings(EnumKeyStrings[] colHeaders){
        String[] rValue = new String[colHeaders.length];
        for(int i=0; i<rValue.length; i++)
            rValue[i] = colHeaders[i].getKeyText();
        return rValue;
    }
    
    private Object[][] getKeyData(EnumKeyStrings[] colHeaders, List<Hardware> hw){
        Object[][] rValue = new Object[hw.size()][colHeaders.length];
        for(int row=0; row<hw.size(); row++) for(int i=0; i<colHeaders.length; i++){
            if(colHeaders[i].getClassType().equals(Boolean.class)){
                rValue[row][i] = hw.get(row).readBoolVal(colHeaders[i]);
            } else if(colHeaders[i].getClassType().equals(Integer.class)){
                rValue[row][i] = hw.get(row).readIntVal(colHeaders[i]);
            } else {
                rValue[row][i] = hw.get(row).readVal(colHeaders[i]);
            }
        }
        return rValue;
    }
    
    public final void populateTable(){
        
        final EnumKeyStrings[] colKeys;
        
        ArrayList<Hardware> hwOG = GameData.getHardwareArray(hwType);

        // First filter also copies to a local list.
        List<Hardware> hw = hwOG.stream().filter(x->
            x.getLevel()<=GameData.getLevel() || !this.chkLevelFilter.isSelected()
        ).collect(Collectors.toList());
        
        // Now we can just remove from that list.
        if(this.chkCompatible.isSelected())
            hw.removeIf(x->!CompatibilityChecker.checkInterCompatibilities(pc, x).isEmpty());
        
        tableList = hw;
        
        this.tblMain.removeAll();
        
        this.tblMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        switch(hwType){
            case COOLER:
                colKeys = new EnumKeyStrings[]{
                    EnumKeyStrings.MANUFACTURER,
                    EnumKeyStrings.PART_NAME,
                    EnumKeyStrings.LEVEL,
                    EnumKeyStrings.PRICE,
                    EnumKeyStrings.PART_TYPE,
                    EnumKeyStrings.AIR_FLOW,
                    EnumKeyStrings.HEIGHT,
                    EnumKeyStrings.CPU_SOCKET_LIST
                };
                break;
            case PSU:
                colKeys = new EnumKeyStrings[]{
                    EnumKeyStrings.MANUFACTURER,
                    EnumKeyStrings.PART_NAME,
                    EnumKeyStrings.LEVEL,
                    EnumKeyStrings.PRICE,
                    EnumKeyStrings.SIZE,
                    EnumKeyStrings.WATTAGE,
                    EnumKeyStrings.LENGTH
                };
                
                break;
            case MOTHERBOARD:
                colKeys = new EnumKeyStrings[]{
                    EnumKeyStrings.MANUFACTURER,
                    EnumKeyStrings.PART_NAME,
                    EnumKeyStrings.LEVEL,
                    EnumKeyStrings.PRICE,
                    EnumKeyStrings.CHIPSET,
                    EnumKeyStrings.CPU_SOCKET,
                    EnumKeyStrings.MOTHERBOARD_SIZES,
                    EnumKeyStrings.RAM_TYPE,
                    EnumKeyStrings.CAN_OVERCLOCK,
                    EnumKeyStrings._MULTI_GPU_SUPPORT
                };
                break;
            case CPU:
                // Set the headers (First four columns are standard and set later)
                colKeys = new EnumKeyStrings[]{
                    EnumKeyStrings.MANUFACTURER,
                    EnumKeyStrings.PART_NAME,
                    EnumKeyStrings.LEVEL,
                    EnumKeyStrings.PRICE,
                    EnumKeyStrings.CORES,
                    EnumKeyStrings.CAN_OVERCLOCK,
                    EnumKeyStrings.WATTAGE,
                    EnumKeyStrings.MAX_MEM_CHAN,
                    EnumKeyStrings.CPU_SOCKET,
                };      
                break;
            case STORAGE:
                colKeys = new EnumKeyStrings[]{
                    EnumKeyStrings.MANUFACTURER,
                    EnumKeyStrings.PART_NAME,
                    EnumKeyStrings.LEVEL,
                    EnumKeyStrings.PRICE,
                    EnumKeyStrings.PART_TYPE,
                    EnumKeyStrings.AIR_FLOW,
                    EnumKeyStrings.HEIGHT,
                    EnumKeyStrings.CPU_SOCKET_LIST
                };
                this.tblMain.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                break;
            case RAM:
                // Set the headers (First four columns are standard and set later)
                colKeys = new EnumKeyStrings[]{
                    EnumKeyStrings.MANUFACTURER,
                    EnumKeyStrings.PART_NAME,
                    EnumKeyStrings.LEVEL,
                    EnumKeyStrings.PRICE,
                    EnumKeyStrings.LEVEL,
                    EnumKeyStrings.FREQUENCY,
                    EnumKeyStrings.SIZE_EACH_GB,
                    EnumKeyStrings.RAM_TYPE
                }; 
                this.tblMain.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                break;
            case GPU:
                colKeys = new EnumKeyStrings[]{
                    EnumKeyStrings.MANUFACTURER,
                    EnumKeyStrings.PART_NAME,
                    EnumKeyStrings.CHIPSET,
                    EnumKeyStrings.LEVEL,
                    EnumKeyStrings.PRICE,
                    EnumKeyStrings.VRAM_GB,
                    EnumKeyStrings.WATTAGE,
                    EnumKeyStrings.LENGTH,
                    EnumKeyStrings._MULTI_GPU_SUPPORT
                };
                this.tblMain.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                break;
            case CASES:
                colKeys = new EnumKeyStrings[]{
                    EnumKeyStrings.MANUFACTURER,
                    EnumKeyStrings.PART_NAME,
                    EnumKeyStrings.LEVEL,
                    EnumKeyStrings.PRICE,
                    EnumKeyStrings.MOTHERBOARD_SIZES,
                    EnumKeyStrings.MAX_PSU_LEN,
                    EnumKeyStrings.MAX_CPU_FAN_HEIGHT,
                    EnumKeyStrings.MAX_GPU_LEN
                };
                break;
     
            default:
                colKeys = new EnumKeyStrings[]{
                    EnumKeyStrings.MANUFACTURER,
                    EnumKeyStrings.PART_NAME,
                    EnumKeyStrings.LEVEL,
                    EnumKeyStrings.PRICE,
                };
        }
        // Set the column classes, headers, and data.
        final Class[]    columnClasses = this.getKeyClasses(colKeys);
        final String[]   headers       = this.getKeyStrings(colKeys);
        final Object[][] data          = this.getKeyData(colKeys, hw);
        
        //if(data.length == 0) return;
        
        // Build the default table model
        DefaultTableModel dtm = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnClasses[columnIndex];
            }

            @Override
            public Object getValueAt(int row, int column) {
                return data[row][column];
            }

            @Override
            public int getColumnCount() {
                return data[0].length;
            }

            @Override
            public int getRowCount() {
                return data.length;
            }

            @Override
            public String getColumnName(int column) {
                //return super.getColumnName(column); //To change body of generated methods, choose Tools | Templates.
                return headers[column];
            }
            
            
        };
        
        if(data.length==0) tblMain.setModel(new DefaultTableModel());
        else tblMain.setModel(dtm);
        tblMain.setAutoCreateRowSorter(true);      
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblMain = new javax.swing.JTable();
        btnOKx2 = new javax.swing.JButton();
        btnOKx4 = new javax.swing.JButton();
        chkLevelFilter = new javax.swing.JCheckBox();
        chkCompatible = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        btnOK.setText("OK");
        btnOK.setEnabled(false);
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        tblMain.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblMain.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMainMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblMain);

        btnOKx2.setText("OK (x2)");
        btnOKx2.setEnabled(false);
        btnOKx2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKx2ActionPerformed(evt);
            }
        });

        btnOKx4.setText("OK (x4)");
        btnOKx4.setEnabled(false);
        btnOKx4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKx4ActionPerformed(evt);
            }
        });

        chkLevelFilter.setText("Available at level");
        chkLevelFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLevelFilterActionPerformed(evt);
            }
        });

        chkCompatible.setText("Compatible with current build");
        chkCompatible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCompatibleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkLevelFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCompatible)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnOKx4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOKx2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOK)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel)
                    .addComponent(btnOKx2)
                    .addComponent(btnOKx4)
                    .addComponent(chkLevelFilter)
                    .addComponent(chkCompatible))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        addItems(1);
    }//GEN-LAST:event_btnOKActionPerformed

    private void tblMainMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMainMouseClicked
        if(evt.getClickCount() == 2) addItems(1);
    }//GEN-LAST:event_tblMainMouseClicked

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_btnCancelActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        parent.setEnabled(true);
        parent.toFront();
        parent.requestFocus();
    }//GEN-LAST:event_formWindowClosed

    private void btnOKx2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKx2ActionPerformed
        addItems(2);
    }//GEN-LAST:event_btnOKx2ActionPerformed

    private void btnOKx4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKx4ActionPerformed
        addItems(4);
    }//GEN-LAST:event_btnOKx4ActionPerformed

    private void chkLevelFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLevelFilterActionPerformed
        populateTable();
    }//GEN-LAST:event_chkLevelFilterActionPerformed

    private void chkCompatibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCompatibleActionPerformed
        populateTable();
    }//GEN-LAST:event_chkCompatibleActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnOKx2;
    private javax.swing.JButton btnOKx4;
    private javax.swing.JCheckBox chkCompatible;
    private javax.swing.JCheckBox chkLevelFilter;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblMain;
    // End of variables declaration//GEN-END:variables
}
