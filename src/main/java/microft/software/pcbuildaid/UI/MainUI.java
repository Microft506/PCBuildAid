/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.UI;

import java.io.IOException;
import static java.util.Objects.isNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import microft.software.pcbuildaid.PCBuildData.GameData;
import microft.software.pcbuildaid.PCBuildData.PCBuildSourceData;

/**
 *
 * @author Marc
 */
public class MainUI extends javax.swing.JFrame {

    /**
     * Creates new form Main
     */
    public MainUI() {
        final Preferences userRoot = Preferences.userRoot();
        usr = Preferences.userNodeForPackage(userRoot.getClass());
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dirChooser = new javax.swing.JFileChooser();
        jButton3 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtGameFile = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnLaunchBenchmarkCalculator = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        dirChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        jButton3.setText("jButton3");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel1.setText("PC Builder Game Directory:");

        txtGameFile.setEditable(false);
        txtGameFile.setText("jTextField1");

        jButton1.setText("...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtGameFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtGameFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel2.setEnabled(false);
        jPanel2.setLayout(new java.awt.GridLayout(1, 3, 5, 20));

        btnLaunchBenchmarkCalculator.setText("Benchmark Calculator");
        btnLaunchBenchmarkCalculator.setEnabled(false);
        btnLaunchBenchmarkCalculator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLaunchBenchmarkCalculatorActionPerformed(evt);
            }
        });
        jPanel2.add(btnLaunchBenchmarkCalculator);

        jButton4.setText("jButton4");
        jPanel2.add(jButton4);

        jButton5.setText("jButton5");
        jPanel2.add(jButton5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 746, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.chooseNewGameDataFile();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnLaunchBenchmarkCalculatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaunchBenchmarkCalculatorActionPerformed
        (new PCBuilder()).setVisible(true);
    }//GEN-LAST:event_btnLaunchBenchmarkCalculatorActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            MainUI me = new MainUI();
            me.setVisible(true);
            me.loadGameData();
        });

    }

    private void loadGameData() {
        // Pull the game directory location from the registry or apply default.
        String filename = usr.get("DefaultDir", "C:\\Program Files (x86)\\Steam\\steamapps\\common\\PC Building Simulator");
        this.txtGameFile.setText(filename);
                
        try {
            // Load in the data
            sourceData = new PCBuildSourceData(filename+ "\\PCBS_Data\\sharedassets1.assets");
            GameData.populateHardware(sourceData);
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            UIToolBox.popUp(this, "There was a problem reading the game file.  Choose your game location and try again.");
        }
        setButtonsEnabled();
    }

    private void chooseNewGameDataFile() {
        if(this.dirChooser.showDialog(this, "Choose PC Builder Game Directory") != 0) return;
        String newLoc = this.dirChooser.getSelectedFile().getAbsolutePath();
        this.txtGameFile.setText(newLoc);
        
        // Try to load in the new game data. - Note to self: Repetative code...
        try {
            // Load in the data
            sourceData = new PCBuildSourceData(newLoc + "\\PCBS_Data\\sharedassets1.assets");
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
            UIToolBox.popUp(this, "There was a problem reading the game file.  Choose your game location and try again.");
            return;
        }
        
        // If you've made it this far, it must be ok.  Save the new file location.
        usr.put("DefaultDir", this.txtGameFile.getText());
        setButtonsEnabled();
    }
    
    private void setButtonsEnabled(){
        boolean en = !isNull(this.sourceData);
        this.btnLaunchBenchmarkCalculator.setEnabled(en);
    }

    private PCBuildSourceData sourceData;
    private Preferences usr;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLaunchBenchmarkCalculator;
    private javax.swing.JFileChooser dirChooser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtGameFile;
    // End of variables declaration//GEN-END:variables
}
