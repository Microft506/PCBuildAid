/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData.HTMLParser;

import java.util.ArrayList;
import java.util.HashMap;
import static java.util.Objects.isNull;
import microft.software.pcbuildaid.PCBuildData.Tools;

/**
 *
 * @author marcc
 */
public class HTMLTableRow {
    private final String source;
    private final HashMap<String, HTMLTableRowCell> cells = new HashMap<>();
    private final ArrayList<String>divs = new ArrayList<>();

    public HTMLTableRow(String source) {
        this.source = source;
        buildFromSource();
        
    }
    
    private void buildFromSource(){
        ArrayList<String> s = Tools.search(this.source, "<td(.+?)<\\/td>");
        s.stream().forEach(x->{
            HTMLTableRowCell n = new HTMLTableRowCell(x);
            if(isNull(n)) return;
            this.cells.put(n.getDiv(), n);
            if(!this.divs.contains(n.getDiv())) this.divs.add(n.getDiv());
        });
    }
    
    public int getNumCells(){
        return this.cells.size();
    }

    public ArrayList<String> getDivs() {
        return divs;
    }
    
    public String getCellData(String cellKey, String defaultValue){
        if(cells.containsKey(cellKey)) return cells.get(cellKey).getVal();
        else return defaultValue;
    }
    
    public String getCellData(String cellKey){
        if(cells.containsKey(cellKey)) return cells.get(cellKey).getVal();
        else return "";
    }
}
