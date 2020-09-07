/*
 * Cloud Resource & Information Management System (CRIMSy)
 * Copyright 2020 Leibniz-Institut f. Pflanzenbiochemie
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package de.ipb_halle.lbac.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This class will provide some test cases for the HexUtil class 
 */
public class UnitTest {


    /**
     * basic tests
     */
    @Test
    public void testUnit() {

        Unit cm = Unit.getUnit("cm");
        Unit mm = Unit.getUnit("mm");

        assertEquals("mm.transform(cm): conversion error ",
                0.1, mm.transform(cm), 0.0001);

        assertEquals("cm.transform(mm): conversion error ",
                10.0, cm.transform(mm), 0.0001);
    }

}
