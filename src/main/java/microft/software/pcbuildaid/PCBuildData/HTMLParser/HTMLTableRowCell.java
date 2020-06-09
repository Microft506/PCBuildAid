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
public class HTMLTableRowCell {
    private final String source;
    private String div;
    private String val;

    public HTMLTableRowCell(String source) {
        this.source = source;
        buildFromSource();
    }
    
    private void buildFromSource(){
        ArrayList<String> s = Tools.search(this.source, "div=\\\"(.+?)\\\"");
        if(! s.isEmpty()) this.div = s.get(0).replace("div=\"", "").replace("\"", "").trim();
        s = Tools.search(this.source, ">(.+?)<");
        if(! s.isEmpty()) this.val = s.get(0).replace(">", "").replace("<", "").trim();
    }

    public String getSource() {
        return source;
    }

    public String getDiv() {
        return div;
    }

    public String getVal() {
        return val;
    }
    
    public boolean hasDiv(){
        return !(this.div.length() == 0);
    }
    
        
}
