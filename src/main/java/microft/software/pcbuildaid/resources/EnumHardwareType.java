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
package microft.software.pcbuildaid.resources;

/**
 *
 * @author Marc Cabot
 */
public enum EnumHardwareType {
    RAM(new String[]{"RAM"}, "RAM", 4),
    WATER_COLLANTS(new String[]{"r Collants"}, "Water Coolant", 1),
    WATER_COOLING(new String[]{"er Cooling"}, "Water Cooler", 1),
    PSU(new String[]{"PSU"}, "Power Supply", 1),
    CPU(new String[]{"CPU"}, "CPU", 1),
    MOTHERBOARD(new String[]{"otherboard"}, "Motherboard", 1),
    CASES(new String[]{"Cases"}, "Case", 1),
    COOLER(new String[]{"Coolers"}, "Cooler", 1),
    STORAGE(new String[]{"Storage"}, "Storage", 8),
    GPU(new String[]{"GPU", "Cooled GPU"}, "GPU", 2);
    //GPU(new String[]{"GPU"}, "GPU", 2);
    
    private final String[] keys;
    private final String description;
    private final int maxNumberInBuild;
    
    EnumHardwareType(String[] keys, String description, int maxNumberInBuild){
        this.keys = keys;
        this.description = description;
        this.maxNumberInBuild = maxNumberInBuild;
    }
    
    public String[] getKeys(){
        return keys;
    }

    public String getKey() {
        return keys[0];
    }
    
    public String getDescription() {
        return description;
    }

    public int getMaxNumberInBuild() {
        return maxNumberInBuild;
    }
    
}
