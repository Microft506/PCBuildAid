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
import microft.software.pcbuildaid.PCBuildData.Tools;

/**
 *
 * @author Marc Cabot
 */
public class PCBuilderItemCollection {
    private final String source;
    private final ArrayList<PCBuilderItem> rows = new ArrayList<>();
    private final ArrayList<String> divs = new ArrayList<>();
    private String name;

    public ArrayList<PCBuilderItem> getRows() {
        return rows;
    }
    
    public PCBuilderItemCollection(String source, String name){
        this(source);
        this.name = name;
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
    
    public String getSource(){
        return source;
    }

    public void setName(String newname){
        this.name = newname;
    }
}
