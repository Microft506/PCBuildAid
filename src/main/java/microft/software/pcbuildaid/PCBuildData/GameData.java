/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData;

import java.util.ArrayList;
import microft.software.pcbuildaid.PCBuildData.HTMLParser.PCBuilderItemCollection;
import microft.software.pcbuildaid.PCBuildData.Hardware.*;

/**
 *
 * @author Marc
 */
public class GameData {
    public static final ArrayList<PCBuilderItemCollection> tables = new ArrayList<>();
    public static final ArrayList<Motherboard> motherboards = new ArrayList<>();
    public static final ArrayList<CPU> cpus = new ArrayList<>();
    public static final ArrayList<Case> cases = new ArrayList<>();
}
