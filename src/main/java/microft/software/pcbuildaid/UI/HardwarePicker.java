/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.UI;

import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import microft.software.pcbuildaid.PCBuildData.GameData;
import microft.software.pcbuildaid.PCBuildData.Hardware.Base.EnumKeyStrings;
import microft.software.pcbuildaid.PCBuildData.Hardware.Base.Hardware;
import microft.software.pcbuildaid.PCBuildData.Hardware.*;
import microft.software.pcvuildaid.calculators.PCBuild;

/**
 *
 * @author marcc
 */
public class HardwarePicker extends javax.swing.JFrame {
    private PCBuild pc;
    private EnumHardwareType hwType;
    private JFrame parent;
    
    /**
     * Creates new form CPUPicker
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
    
    public void populateTable(){
        final Class[] columnClasses;
        final String[] headers;
        final Object[][] data; // [row][col]
        
        ArrayList<Hardware> hw = new ArrayList<>();
        
        int count;
        
        switch(hwType){
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
                for(Case x:GameData.cases){
                    hw.add(x);
                    data[count][4] = x.getMotherboardSize();
                    data[count][5] = x.getCaseSize();
                    data[count][6] = x.getPSUSize();
                    data[count][7] = x.getMaxGPULen();
                    data[count][8] = x.getMaxCPUFanHeight();
                    data[count++][9] = x.getMaxPSULen();
                }
                
                this.tblMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                
                break;
            case MOTHERBOARD:
                // Set the headers (First four columns are standard and set later)
                headers = new String[]{"","","","",
                    EnumKeyStrings.CHIPSET.getKeyText(),
                    EnumKeyStrings.CPU_SOCKET.getKeyText(),
                    EnumKeyStrings.MOTHERBOARD_SIZES.getKeyText(),
                    EnumKeyStrings.RAM_TYPE.getKeyText(),
                    EnumKeyStrings.CAN_OVERCLOCK.getKeyText()
                };   
                
                // Set the column classes
                columnClasses = new Class[]{String.class, String.class, Integer.class, Integer.class,
                    String.class, String.class, String.class, String.class, Boolean.class
                };
                
                // Populate the data.
                data = new Object[GameData.motherboards.size()][columnClasses.length];
                count = 0;
                for(Motherboard x:GameData.motherboards){
                    hw.add(x);
                    data[count][4] = x.getChipset();
                    data[count][5] = x.getSocketType();
                    data[count][6] = x.getMotherboardSize();
                    data[count][7] = x.getRamType();
                    data[count++][8] = x.canOverclock();
                }
                
                this.tblMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                
                break;
            case CPU:
                // Set the headers (First four columns are standard and set later)
                headers = new String[]{"","","","",
                    EnumKeyStrings.CORES.getKeyText(),
                    EnumKeyStrings.CAN_OVERCLOCK.getKeyText(),
                    EnumKeyStrings.WATTAGE.getKeyText(),
                    EnumKeyStrings.MAX_MEM_CHAN.getKeyText(),
                    EnumKeyStrings.CPU_SOCKET.getKeyText()
                };                
                // Set the column classes
                columnClasses = new Class[]{String.class, String.class, Integer.class, Integer.class,
                    Integer.class, Boolean.class, Integer.class, Integer.class, String.class
                };
                
                // Populate the data.
                data = new Object[GameData.cpus.size()][columnClasses.length];
                count = 0;
                for(CPU x:GameData.cpus){
                    hw.add(x);
                    data[count][4] = x.getNumberOfCores();
                    data[count][5] = x.isOverclockable();
                    data[count][6] = x.getWattage();
                    data[count][7] = x.getMaxMemChannels();
                    data[count++][8] = x.getSocketType();
                }
                
                this.tblMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                break;
            default:
                columnClasses = new Class[]{String.class, String.class, Integer.class, Integer.class};
                data = new Object[][]{};
                headers = new String[4];
        }
        headers[0] = EnumKeyStrings.MANUFACTURER.getKeyText();
        headers[1] = EnumKeyStrings.PART_NAME.getKeyText();
        headers[2] = EnumKeyStrings.LEVEL.getKeyText();
        headers[3] = EnumKeyStrings.PRICE.getKeyText();
        for(int i=0; i<hw.size(); ++i){
            data[i][0]=hw.get(i).getManufacturer();
            data[i][1]=hw.get(i).getPartName();
            data[i][2]=hw.get(i).getLevel();
            data[i][3]=hw.get(i).getPrice();
        }
        
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
        switch(hwType){
            case CPU:
                this.pc.setCpu(GameData.cpus.get(selRow));
                break;
            case MOTHERBOARD:
                this.pc.setMotherboard(GameData.motherboards.get(selRow));
                break;
            case CASES:
                this.pc.setTheCase(GameData.cases.get(selRow));
                break;
        }
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
