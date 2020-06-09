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
public class PCBuilderItemCollection {
    private final String source;
    private final ArrayList<PCBuilderItem> rows = new ArrayList<>();
    private final ArrayList<String> divs = new ArrayList<>();
    private String name;

    public ArrayList<PCBuilderItem> getRows() {
        return rows;
    }
    
    public PCBuilderItemCollection(String source) {
        this.source = source;
        buildFromSource();
    }
    
    private void buildFromSource(){
        String [] ss = this.source.split("<table");
        ArrayList<String> trStrings = Tools.search(this.source, "<tr(.+?)<\\/tr>");
        trStrings.stream().forEach(x -> this.rows.add(new PCBuilderItem(x)));
        this.rows.stream().forEach(x->{
            x.getDivs().stream().forEach(y->{
               if(!this.divs.contains(y)) this.divs.add(y);
            });
        });
        this.name = ss[0].trim();
    }
    
    public int getNumRows(){
        return rows.size();
    }

    public ArrayList<String> getDivs() {
        return divs;
    }

    public String getName() {
        return name;
    }
    
    

}
