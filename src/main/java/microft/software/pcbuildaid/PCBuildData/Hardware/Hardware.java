/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData.Hardware;

import microft.software.pcbuildaid.PCBuildData.HTMLParser.HTMLTableRow;

/**
 *
 * @author marcc
 */
public abstract class Hardware {
    protected HTMLTableRow sourceData;

    public Hardware(HTMLTableRow sourceData) {
        this.sourceData = sourceData;
    }
    
    public int readIntVal(String key){
        String v = sourceData.getCellData(key);
        try{
            return Integer.parseInt(v);
        } catch(NumberFormatException e){
            return 0;
        }
    }
    
    public String readVal(String key, String def){
        return sourceData.getCellData(key, def);
    }
    
}
