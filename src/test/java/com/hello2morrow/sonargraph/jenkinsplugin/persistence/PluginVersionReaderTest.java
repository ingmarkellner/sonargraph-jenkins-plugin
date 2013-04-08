package com.hello2morrow.sonargraph.jenkinsplugin.persistence;

import static org.junit.Assert.*;

import org.junit.Test;

public class PluginVersionReaderTest
{
    @Test
    public void testGetVersion()
    {
        assertFalse("unknown".equals(PluginVersionReader.INSTANCE.getVersion()));
    }
}
