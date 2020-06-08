/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.UI;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Marc
 */
public class UIToolBox {
    public static void popUp(JFrame f, String message){
        JOptionPane.showMessageDialog(f, message, "Notice", JOptionPane.OK_OPTION);
    }
    
    public static void popUp(JDialog f, String message){
        JOptionPane.showMessageDialog(f, message, "Notice", JOptionPane.OK_OPTION);
    }
}
