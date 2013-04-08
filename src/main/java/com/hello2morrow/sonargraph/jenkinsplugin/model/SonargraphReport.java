package com.hello2morrow.sonargraph.jenkinsplugin.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Plain Java object representing the Sonargraph Report. This class makes it easier to access the metrics as using the JAXB generated classes.
 * And additionally, we only have the dependency on JAXB in the persistence.
 * 
 * @author Ingmar
 *
 */
public class SonargraphReport
{
    private String m_name;
    private String m_description;
    private Map<SonargraphMetrics, String> m_systemMetrics = new HashMap<SonargraphMetrics, String>();
    
    //If we need build unit metrics
//    private Map<String, Map<SonargraphMetrics, String>> m_buildUnitMetrics = new HashMap<String, Map<SonargraphMetrics, String>>();

    public SonargraphReport(String name)
    {
        assert name != null && name.length() > 0 : "Parameter 'name' of method 'SonargraphReport' must not be empty";
        m_name = name;
    }

    public String getDescription()
    {
        return m_description;
    }

    public void setDescription(String description)
    {
        assert description != null && description.length() > 0 : "Parameter 'description' of method 'setDescription' must not be empty";
        m_description = description;
    }

    public void addSystemMetric(SonargraphMetrics metric, String value)
    {
        m_systemMetrics.put(metric, value);
    }

    public String getName()
    {
        return m_name;
    }

    public String getSystemMetricValue(SonargraphMetrics metric)
    {
        return m_systemMetrics.get(metric);
    }
}
