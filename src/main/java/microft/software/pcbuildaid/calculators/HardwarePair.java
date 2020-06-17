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
package microft.software.pcbuildaid.calculators;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import microft.software.pcbuildaid.PCBuildData.Hardware;
import microft.software.pcbuildaid.PCBuildData.HardwareSet;
import microft.software.pcbuildaid.resources.EnumHardwareType;

/**
 *
 * @author Marc Cabot
 */
class HardwarePair {
    private final Hardware[] hwa;
    private final HardwareSet[] hwsa;

    public HardwarePair(HardwareSet a){
        hwsa = new HardwareSet[]{a};
        hwa = new Hardware[0];
    }
    
    public HardwarePair(HardwareSet a, HardwareSet b){
        hwsa = new HardwareSet[]{a,b};
        hwa = new Hardware[0];
    }
    
    public HardwarePair(HardwareSet a, Hardware b){
        this(b,a);
    }
    
    public HardwarePair(Hardware a, HardwareSet b){
        hwsa = new HardwareSet[]{b};
        hwa = new Hardware[]{a};
    }
    
    public HardwarePair(Hardware a, Hardware b) {
        hwa = new Hardware[]{a,b};
        hwsa = new HardwareSet[0];
    }
    
    public Hardware getHardware(EnumHardwareType hwt){
        for(Hardware hw:hwa) if(hw.getHardwareType().equals(hwt)) return hw;
        return null;
    }
    
    public HardwareSet getHardwareSet(EnumHardwareType hwt){
        for(HardwareSet hws:hwsa) if(hws.getHardwareType().equals(hwt)) return hws;
        return null;
    }
    
    private List<EnumHardwareType> getTypes(){
        List<EnumHardwareType> hwts = Arrays.asList(hwa).stream().map(x->x.getHardwareType()).collect(Collectors.toList());
        hwts.addAll(Arrays.asList(hwsa).stream().map(x->x.getHardwareType()).collect(Collectors.toList()));
        return hwts;
    }
    
    public boolean isOfTypes(EnumHardwareType hwt1, EnumHardwareType hwt2){
        List<EnumHardwareType> hwts = getTypes();
        return hwts.contains(hwt1) && hwts.contains(hwt2);
    }
    
    public boolean isOnlyOfType(EnumHardwareType hwt){
        List<EnumHardwareType> hwts = getTypes();
        return hwts.contains(hwt) && (hwts.size()==1);
    }
    
    
}
