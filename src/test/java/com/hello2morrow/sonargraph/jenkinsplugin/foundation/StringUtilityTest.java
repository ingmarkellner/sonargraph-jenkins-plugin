package com.hello2morrow.sonargraph.jenkinsplugin.foundation;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilityTest
{
    @Test
    public void testConvertConstantNameToMixedCaseString()
    {
        assertEquals("NumberOfViolations", StringUtility.convertConstantNameToMixedCaseString("NUMBER_OF_VIOLATIONS", true, false));
    }

    @Test
    public void testConvertMixedCaseStringToConstantName()
    {
        assertEquals("NUMBER_OF_VIOLATIONS", StringUtility.convertMixedCaseStringToConstantName("NumberOfViolations"));
    }
}
