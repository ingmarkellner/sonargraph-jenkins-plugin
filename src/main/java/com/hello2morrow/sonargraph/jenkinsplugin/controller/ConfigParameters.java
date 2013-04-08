package com.hello2morrow.sonargraph.jenkinsplugin.controller;

public enum ConfigParameters
{
    ACTION_URL_NAME("sonargraph"),
    SONARGRAPH_ICON("/plugin/sonargraph-plugin/icons/Sonargraph-Architect.png"),
    ACTION_DISPLAY_NAME("Sonargraph Reports"),
    REPORT_BUILDER_DISPLAY_NAME("Sonargraph Report Generation & Analysis"),
    REPORT_ANALYZER_DISPLAY_NAME("Sonargraph Report Analysis");

    private String m_value;

    private ConfigParameters(String value)
    {
        m_value = value;
    }

    public String getValue()
    {
        return m_value;
    }
}