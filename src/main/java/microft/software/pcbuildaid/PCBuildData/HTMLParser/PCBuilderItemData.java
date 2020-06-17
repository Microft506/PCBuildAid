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
public class PCBuilderItemData {
    private final String source;
    private String div;
    private String val;

    public PCBuilderItemData(String source) {
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
