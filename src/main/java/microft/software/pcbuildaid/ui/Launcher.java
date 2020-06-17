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
package microft.software.pcbuildaid.ui;

import microft.software.pcbuildaid.PCBuildData.PCBuildData;

/**
 *
 * @author Marc Cabot
 */
public final class Launcher {
    
    public static void main(String[] args){
        // We need to launch from out here so that we get the first window
        // on the proper display.  
        int monitor;
        try{
            monitor = Integer.parseInt(PCBuildData.getSetting("Display", "0"));
        } catch (Exception e){
            monitor = 0;
            System.out.println("Key not found, reverting to monitor 1");
        }
        System.out.println("monitor set to: " + monitor);
        PCBuildData.setMonitor(monitor);
        (new MainUI(PCBuildData.getGraphicsConfiguration())).setVisible(true);
    }
    
}
