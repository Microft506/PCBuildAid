/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.UI;

import microft.software.pcbuildaid.resources.EnumHardwareType;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import microft.software.pcbuildaid.PCBuildData.GameData;
import microft.software.pcbuildaid.resources.EnumKeyStrings;
import microft.software.pcbuildaid.PCBuildData.Hardware;
import microft.software.pcvuildaid.calculators.PCBuild;

/**
 *
 * @author marcc
 */
public class HardwarePicker extends javax.swing.JFrame {
    private final PCBuild pc;
    private final EnumHardwareType hwType;
    private final JFrame parent;
    
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
        
        populateTable();
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
    
    private Object[][] getKeyData(EnumKeyStrings[] colHeaders, ArrayList<Hardware> hw){
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
        
        ArrayList<Hardware> hw = GameData.getHardwareArray(hwType);

        
        switch(hwType){
            /*
            
                
                
                break;
            case CASES:
                // Set the headers (First four columns are standard and set later)
                headers = new String[]{"","","","",
                    EnumKeyStrings.MOTHERBOARD_SIZES.getKeyText(),
                    EnumKeyStrings.CASE_SIZE.getKeyText(),
                    EnumKeyStrings.PSU_SIZE.getKeyText(),
                    EnumKeyStrings.MAX_GPU_LEN.getKeyText(),
                    EnumKeyStrings.MAX_CPU_FAN_HEIGHT.getKeyText(),
                    EnumKeyStrings.MAX_PSU_LEN.getKeyText()
                }; 
                
                // Set the column classes
                columnClasses = new Class[]{String.class, String.class, Integer.class, Integer.class,
                    String.class, String.class, String.class, Integer.class, Integer.class, Integer.class
                };
                
                // Populate the data.
                data = new Object[GameData.cases.size()][columnClasses.length];
                count = 0;
              
                
                this.tblMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                
                break;
            
            */
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
                this.tblMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
                };
                this.tblMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
                this.tblMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
                this.tblMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                break;
            default:
                colKeys = new EnumKeyStrings[]{
                    EnumKeyStrings.MANUFACTURER,
                    EnumKeyStrings.PART_NAME,
                    EnumKeyStrings.LEVEL,
                    EnumKeyStrings.PRICE,
                    EnumKeyStrings.MANUFACTURER,
                    EnumKeyStrings.PART_NAME,
                    EnumKeyStrings.LEVEL,
                    EnumKeyStrings.PRICE
                };
        }
        // Set the column classes, headers, and data.
        final Class[]    columnClasses = this.getKeyClasses(colKeys);
        final String[]   headers       = this.getKeyStrings(colKeys);
        final Object[][] data          = this.getKeyData(colKeys, hw);
        
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
        
        tblMain.setModel(dtm);
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
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblMain.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMainMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblMain);

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        int selRow = this.tblMain.getRowSorter().convertRowIndexToModel(this.tblMain.getSelectedRow());
        if(selRow < 0) return;
        this.pc.setHardware(GameData.getHardwareArray(hwType).get(selRow));
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_btnOKActionPerformed

    private void tblMainMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMainMouseClicked
        this.btnOK.setEnabled(this.tblMain.getSelectedRowCount() > 0);
    }//GEN-LAST:event_tblMainMouseClicked

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_btnCancelActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        parent.setEnabled(true);
        parent.toFront();
        parent.requestFocus();
    }//GEN-LAST:event_formWindowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblMain;
    // End of variables declaration//GEN-END:variables
}
