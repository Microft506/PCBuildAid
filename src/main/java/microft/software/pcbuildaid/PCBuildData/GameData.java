/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData;

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
    private static final ArrayList<PCBuilderItemCollection> tables = new ArrayList<>();
    private static int level;
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
    }
    
    
}
