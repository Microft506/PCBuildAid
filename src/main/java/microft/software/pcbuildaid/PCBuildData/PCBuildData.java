/*
 * The MIT License
 *
 * Copyright 2020 Marc Cabot.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package microft.software.pcbuildaid.PCBuildData;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static java.util.Objects.isNull;
import java.util.prefs.Preferences;
import microft.software.pcbuildaid.PCBuildData.HTMLParser.PCBuilderItemCollection;
import microft.software.pcbuildaid.resources.*;

/**
 * This class contains methods to access all data external to this app.
 * @author Marc Cabot
 */
public class PCBuildData {
    
    /**
     * The CPU Frequency Base Envelope describes the base frequency available on
     * (seemingly) all motherboards.  
     */
    public static final double[] CPU_FREQ_BASE_ENV = new double[]{50,300};

    /**
     * The CPU Frequency Ratio Envelope describes the available frequency multiplier 
     * range available on (seemingly) all motherboards.
     */
    public static final double[] CPU_FREQ_RATIO_ENF = new double[]{1,84};
    
    private static final ArrayList<PCBuilderItemCollection> tables = new ArrayList<>();
    private static int level;
    private static int monitor;
    private static final HashMap<EnumHardwareType, ArrayList<Hardware>> hardwareListMap = new HashMap<>();
    
    /**
     * Retrieve a setting from the registry
     * @param key The key that the setting is stored under.
     * @param defaultString A default string to return if the setting is not found.
     * @return
     */
    public static String getSetting(String key, String defaultString){
        final Preferences userRoot = Preferences.userRoot();
        final Preferences usr = Preferences.userNodeForPackage(userRoot.getClass());
        return usr.get(key, defaultString);
    }
    
    /**
     * Store a setting to the registry
     * @param key The key to store it under
     * @param value The value to store.
     */
    public static void setSetting(String key, String value){
        final Preferences userRoot = Preferences.userRoot();
        final Preferences usr = Preferences.userNodeForPackage(userRoot.getClass());
        usr.put(key, value);
    }
    
    /**
     * Populate the hardware hash tables.
     * @param pcbsd A PCBuildSourceData object containing the raw game information to be processed.
     */
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
    
    /**
     * Selectively clear data from the hash table.
     * @param hwType The hardware type to clear out.
     */
    public static void clearHardwareType(EnumHardwareType hwType){
        hardwareListMap.get(hwType).clear();
    }
    
    /**
     * Add some new hardware
     * @param hw The Hardware object to add
     */
    public static void addHardware(Hardware hw){
        hardwareListMap.get(hw.getHardwareType()).add(hw);
    }
    
    /**
     * Get all Hardware of a certain type
     * @param hwType the type of hardware to return.
     * @return
     */
    public static List<Hardware> getHardwareArray(EnumHardwareType hwType){
        return hardwareListMap.get(hwType);
    }

    /**
     * Get the current game level
     * @return
     */
    public static int getLevel() {
        return level;
    }

    /**
     * Set the current game level
     * @param level The new level
     */
    public static void setLevel(int level) {
        PCBuildData.level = level;
        System.out.println("Game level set to: " + level);
    }

    /**
     * Get the current monitor preference.
     * @return
     */
    public static int getMonitor() {
        return monitor;
    }

    /**
     * Sets the current monitor preference
     * Note that the monitor index referenced here starts at 0.
     * @param monitor
     */
    public static void setMonitor(int monitor) {
        PCBuildData.monitor = monitor;
        setSetting("Display", Integer.toString(monitor));
        System.out.println("Monitor set to : " + monitor);
    }
    
    /**
     * Increments or decrements the monitor preference.
     * Note that the monitor index referenced here starts at 0.
     * @param inc
     */
    public static void changeMonitor(int inc){
        int nm = monitor + inc;
        if(nm<0) return;
        if(nm>=getNumberOfMonitors()) return;
        setMonitor(nm);
    }
    
    /**
     * Returns the graphics configuration for a particular monitor index.
     * Note that the monitor index referenced here starts at 0.
     * @param num
     * @return
     */
    public static GraphicsConfiguration getGraphicsConfiguration(int num){
         GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();        
         if(num<0) num = 0; if(num>=gs.length) num = gs.length-1;
         return gs[num].getDefaultConfiguration();
    }
    
    /**
     * Returns the graphics configuration for whichever monitor the user has selected
     * as their preferred monitor.
     * @return
     */
    public static GraphicsConfiguration getGraphicsConfiguration(){
        return getGraphicsConfiguration(getMonitor());
    }
    
    /**
     * Returns the total number of monitors available.
     * @return
     */
    public static int getNumberOfMonitors(){
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
    }
}
