package microft.software.pcbuildaid.tester.PCBuildData;


import java.io.FileNotFoundException;
import java.io.IOException;
import microft.software.pcbuildaid.PCBuildData.PCBuildSourceData;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marcc
 */
public class PCBuildSourceDataTester {
    public static void main(String[] args) throws FileNotFoundException, IOException{
        final String fileName = "E:\\SteamLibraryHDD2\\steamapps\\common\\PC Building Simulator\\PCBS_Data\\sharedassets1.assets";
        PCBuildSourceData pcbsd = new PCBuildSourceData();
        pcbsd.buildFromFile(fileName);
        
        
    }
}
