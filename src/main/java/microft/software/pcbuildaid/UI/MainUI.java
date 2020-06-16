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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import microft.software.pcbuildaid.PCBuildData.GameData;
import microft.software.pcbuildaid.PCBuildData.PCBuildSourceData;


/**
 *
 * @author Marc
 */
public class MainUI extends javax.swing.JFrame {

    private PCBuildSourceData sourceData;
    private final PCBuilder[] pcBuilders = new PCBuilder[]{new PCBuilder(1), new PCBuilder(2), new PCBuilder(3)};
    
    /**
     * Creates new form Main
     */
    public MainUI() {
        
        initComponents();
    }
    
    public void moveLevel(int inc){
        this.txtLevel.setText(Integer.toString(Integer.parseInt(this.txtLevel.getText())+inc));
        GameData.setLevel(Integer.parseInt(this.txtLevel.getText()));
        GameData.setSetting("gameLevel", Integer.toString(GameData.getLevel()));
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
        btnBench1 = new javax.swing.JButton();
        btnBench2 = new javax.swing.JButton();
        btnBench3 = new javax.swing.JButton();
        btnLevelDown = new javax.swing.JButton();
        txtLevel = new javax.swing.JTextField();
        btnLevelUp = new javax.swing.JButton();

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

        btnBench1.setText("Bench 1");
        btnBench1.setEnabled(false);
        btnBench1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBench1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnBench1);

        btnBench2.setText("Bench 2");
        btnBench2.setEnabled(false);
        btnBench2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBench2ActionPerformed(evt);
            }
        });
        jPanel2.add(btnBench2);

        btnBench3.setText("Bench 3");
        btnBench3.setEnabled(false);
        btnBench3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBench3ActionPerformed(evt);
            }
        });
        jPanel2.add(btnBench3);

        btnLevelDown.setText("-");
        btnLevelDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLevelDownActionPerformed(evt);
            }
        });

        txtLevel.setEditable(false);
        txtLevel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtLevel.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLevel.setText("0");

        btnLevelUp.setText("+");
        btnLevelUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLevelUpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 746, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(btnLevelDown)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLevelUp)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLevelDown)
                    .addComponent(txtLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLevelUp))
                .addContainerGap(46, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.chooseNewGameDataFile();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnBench1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBench1ActionPerformed
        this.pcBuilders[0].setVisible(true);
    }//GEN-LAST:event_btnBench1ActionPerformed

    private void btnLevelDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLevelDownActionPerformed
        moveLevel(-1);
    }//GEN-LAST:event_btnLevelDownActionPerformed

    private void btnLevelUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLevelUpActionPerformed
        moveLevel(1);
    }//GEN-LAST:event_btnLevelUpActionPerformed

    private void btnBench2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBench2ActionPerformed
        this.pcBuilders[1].setVisible(true);
    }//GEN-LAST:event_btnBench2ActionPerformed

    private void btnBench3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBench3ActionPerformed
        this.pcBuilders[2].setVisible(true);
    }//GEN-LAST:event_btnBench3ActionPerformed

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
            me.txtLevel.setText(GameData.getSetting("gameLevel", "5"));
            GameData.setLevel(Integer.parseInt(GameData.getSetting("gameLevel", "5")));
            me.setVisible(true);
            me.loadGameData();
        });

    }

    private void loadGameData() {
        // Pull the game directory location from the registry or apply default.
        String filename = GameData.getSetting("DefaultDir", "C:\\Program Files (x86)\\Steam\\steamapps\\common\\PC Building Simulator");
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
        GameData.setSetting("DefaultDir", this.txtGameFile.getText());
        setButtonsEnabled();
    }
    
    private void setButtonsEnabled(){
        boolean en = !isNull(this.sourceData);
        this.btnBench1.setEnabled(en);
        this.btnBench2.setEnabled(en);
        this.btnBench3.setEnabled(en);
    }

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBench1;
    private javax.swing.JButton btnBench2;
    private javax.swing.JButton btnBench3;
    private javax.swing.JButton btnLevelDown;
    private javax.swing.JButton btnLevelUp;
    private javax.swing.JFileChooser dirChooser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtGameFile;
    private javax.swing.JTextField txtLevel;
    // End of variables declaration//GEN-END:variables
}
