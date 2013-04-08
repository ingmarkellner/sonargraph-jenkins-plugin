package com.hello2morrow.sonargraph.jenkinsplugin.model;

public enum ProductVersion
{
    SEVEN_ONE_NINE("7.1.9");
    
    private String m_id;

    private ProductVersion(String id)
    {
        m_id = id;
    }
    
    public String getId()
    {
        return m_id;
    }
    
}
