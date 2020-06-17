/*
 * The MIT License
 *
 * Copyright 2020 Marc Cabot.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package microft.software.pcbuildaid.PCBuildData.HTMLParser;

import java.util.ArrayList;
import java.util.HashMap;
import static java.util.Objects.isNull;
import microft.software.pcbuildaid.PCBuildData.Tools;

/**
 *
 * @author Marc Cabot
 */
public class PCBuilderItem {
    private final String source;
    private final HashMap<String, PCBuilderItemData> cells = new HashMap<>();
    private final ArrayList<String>divs = new ArrayList<>();

    public PCBuilderItem(String source) {
        this.source = source;
        buildFromSource();
        
    }
    
    private void buildFromSource(){
        ArrayList<String> s = Tools.search(this.source, "<td(.+?)<\\/td>");
        s.stream().forEach(x->{
            PCBuilderItemData n = new PCBuilderItemData(x);
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
        String rValue;
        if(cells.containsKey(cellKey)) rValue = cells.get(cellKey).getVal();
        else return defaultValue;
        if(!isNull(rValue) && rValue.length() > 0) return rValue;
        else return defaultValue;
    }
    
    public String getCellData(String cellKey){
        if(cells.containsKey(cellKey)) return cells.get(cellKey).getVal();
        else return "";
    }
}
