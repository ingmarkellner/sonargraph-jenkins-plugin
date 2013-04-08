package com.hello2morrow.sonargraph.jenkinsplugin.model;

public enum ReportType
{
    XML("XML"), HTML("HTML");
    
    private String value;

    private ReportType(String value)
    {
        this.value = value;
    }
    
    public String getValue()
    {
        return value;
    }
}

