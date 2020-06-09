/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData.Hardware;

import microft.software.pcbuildaid.PCBuildData.Hardware.Base.Hardware;
import microft.software.pcbuildaid.PCBuildData.HTMLParser.PCBuilderItem;
import microft.software.pcbuildaid.PCBuildData.PCBuildSourceData;

/**
 *
 * @author marcc
 */
public class RAM extends Hardware{

    public RAM(PCBuilderItem sourceData) {
        super(sourceData);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String getConcatName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    

    
}
