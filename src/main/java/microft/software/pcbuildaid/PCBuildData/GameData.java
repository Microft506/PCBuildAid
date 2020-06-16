/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import static java.util.Objects.isNull;
import java.util.prefs.Preferences;
import microft.software.pcbuildaid.PCBuildData.HTMLParser.PCBuilderItemCollection;
import microft.software.pcbuildaid.resources.*;

/**
 *
 * @author Marc
 */
public class GameData {
    
    public static final double[] CPU_FREQ_BASE_ENV = new double[]{50,300};
    public static final double[] CPU_FREQ_RATIO_ENF = new double[]{1,84};
    
    private static final ArrayList<PCBuilderItemCollection> tables = new ArrayList<>();
    private static int level;
    private static int monitor;
    private static final HashMap<EnumHardwareType, ArrayList<Hardware>> hardwareListMap = new HashMap<>();
    

    public static String getSetting(String key, String defaultString){
        final Preferences userRoot = Preferences.userRoot();
        final Preferences usr = Preferences.userNodeForPackage(userRoot.getClass());
        return usr.get(key, defaultString);
    }
    
    public static void setSetting(String key, String value){
        final Preferences userRoot = Preferences.userRoot();
        final Preferences usr = Preferences.userNodeForPackage(userRoot.getClass());
        usr.put(key, value);
    }
    
    public static void populateHardware(PCBuildSourceData pcbsd){
        for(EnumHardwareType hwType: EnumHardwareType.values()){
            hardwareListMap.put(hwType, new ArrayList<>());
            System.out.print("Popualting " + hwType.getDescription() + "s... ");
            PCBuilderItemCollection hwTable = pcbsd.getTable(hwType);
            if(isNull(hwTable)){
                System.out.println("Failed.");
                continue;
            }
            System.out.print("Found " + hwTable.getNumRows() + " " + hwType.getDescription() + "s... ");
            hwTable.getRows().stream().forEach(x->{
                Hardware hw = new Hardware(x, hwType);
                if(!isNull(hw) && hw.isValid()) addHardware(hw);
            });
            System.out.println(getHardwareArray(hwType).size() + " are valid.");
        }
    }
    
    public static void clearHardwareType(EnumHardwareType hwType){
        hardwareListMap.get(hwType).clear();
    }
    
    public static void addHardware(Hardware hw){
        hardwareListMap.get(hw.getHardwareType()).add(hw);
    }
    
    public static ArrayList<Hardware> getHardwareArray(EnumHardwareType hwType){
        return hardwareListMap.get(hwType);
    }

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int level) {
        GameData.level = level;
        System.out.println("Game level set to: " + level);
    }

    public static int getMonitor() {
        return monitor;
    }

    public static void setMonitor(int monitor) {
        GameData.monitor = monitor;
        setSetting("Display", Integer.toString(monitor));
        System.out.println("Monitor set to : " + monitor);
    }
    
    public static void changeMonitor(int inc){
        int nm = monitor + inc;
        if(nm<0) return;
        if(nm>=getNumberOfMonitors()) return;
        setMonitor(nm);
    }
    
    public static GraphicsConfiguration getGraphicsConfiguration(int num){
         GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();        
         if(num<0) num = 0; if(num>=gs.length) num = gs.length-1;
         return gs[num].getDefaultConfiguration();
    }
    
    public static GraphicsConfiguration getGraphicsConfiguration(){
        return getGraphicsConfiguration(getMonitor());
    }
    
    public static int getNumberOfMonitors(){
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
    }
}
