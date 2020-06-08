/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microft.software.pcbuildaid.PCBuildData.HTMLParser;

import java.util.ArrayList;
import microft.software.pcbuildaid.PCBuildData.Tools;

/**
 *
 * @author marcc
 */
public class HTMLTableRow {
    private final String source;
    private final ArrayList<HTMLTableRowCell> cells = new ArrayList<>();
    private final ArrayList<String>divs = new ArrayList<>();

    public HTMLTableRow(String source) {
        this.source = source;
        buildFromSource();
        
    }
    
    private void buildFromSource(){
        ArrayList<String> s = Tools.search(this.source, "<td(.+?)<\\/td>");
        s.stream().forEach(x->this.cells.add(new HTMLTableRowCell(x)));
        cells.stream().forEach(x-> {
            if(!this.divs.contains(x.getDiv())) this.divs.add(x.getDiv());
        });
    }
    
    public int getNumCells(){
        return this.cells.size();
    }

    public ArrayList<String> getDivs() {
        return divs;
    }
}
