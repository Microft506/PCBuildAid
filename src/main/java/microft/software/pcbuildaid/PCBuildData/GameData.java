/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData;

import java.util.ArrayList;
import microft.software.pcbuildaid.PCBuildData.HTMLParser.PCBuilderItemCollection;
import microft.software.pcbuildaid.PCBuildData.Hardware.*;
import microft.software.pcbuildaid.PCBuildData.Hardware.Base.Hardware;
import microft.software.pcbuildaid.resources.*;

/**
 *
 * @author Marc
 */
public class GameData {
    public static final ArrayList<PCBuilderItemCollection> tables = new ArrayList<>();
    public static final ArrayList<Hardware> motherboards = new ArrayList<>();
    public static final ArrayList<Hardware> cpus = new ArrayList<>();
    public static final ArrayList<Hardware> cases = new ArrayList<>();
    public static final ArrayList<Hardware> ram = new ArrayList<>();
    
    public static ArrayList<Hardware> getHardwareArray(EnumHardwareType hwType){
        switch(hwType){
            case RAM:
                return ram;
            case MOTHERBOARD:
                return motherboards;
            case CPU:
                return cpus;
            case CASES:
                return cases;
            default:
                return null;
        }
    }
}
