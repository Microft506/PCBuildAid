/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.tester.etc;

import java.util.prefs.Preferences;

/**
 *
 * @author Marc
 */
public class RegistryTester {
    public static void main(String[] args){
        final Preferences userRoot = Preferences.userRoot();
        final Preferences usr = Preferences.userNodeForPackage(userRoot.getClass());
        usr.put("DefaultDir", "C:\\Program Files (x86)\\Steam\\steamapps\\common\\PC Building Simulator");
        String dir = usr.get("DefaultDir", "NONE");
        if(dir.compareTo("NONE") == 0) System.out.println("No default directory found.");
        else System.out.println("Default directory: " + dir);
    }
}
