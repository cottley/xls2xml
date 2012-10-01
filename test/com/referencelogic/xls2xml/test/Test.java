/*
 * Copyright (c) 2004-2010, P. Simon Tuffs (simon@simontuffs.com)
 * All rights reserved.
 *
 * See the full license at http://one-jar.sourceforge.net/one-jar-license.html
 * This license is also included in the distributions of this software
 * under doc/one-jar-license.txt
 */
package com.referencelogic.xls2xml.test;

import com.simontuffs.onejar.test.Testable;

public class Test extends Testable {
    
    public static void main(String args[]) throws Exception {
        Test test = new Test();
        test.runTests();
    }
    
    // Test other aspects of the application at unit level (e.g. library
    // methods).
    public void testXls2xml1() {
        System.out.println("testXls2xml1: OK");
    }
    public void testXls2xml2() {
        System.out.println("testXls2xml2: OK");
    }
    public void testXls2xml3() {
        System.out.println("testXls2xml3: OK");
    }
    
}
