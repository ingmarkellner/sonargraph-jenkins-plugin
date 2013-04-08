package com.hello2morrow.sonargraph.jenkinsplugin.model;

public enum SonargraphProductType
{
    ARCHITECT("SonargraphArchitect", "Sonargraph Architect"), QUALITY("SonargraphQuality", "Sonargraph Quality");

    private String m_id;
    private String m_displayName;

    private SonargraphProductType(String id, String displayName)
    {
        m_id = id;
        m_displayName = displayName;
    }

    public String getId()
    {
        return m_id;
    }

    public String getDisplayName()
    {
        return m_displayName;
    }

}