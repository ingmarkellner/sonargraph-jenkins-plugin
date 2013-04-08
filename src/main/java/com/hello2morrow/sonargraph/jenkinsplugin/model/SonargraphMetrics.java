package com.hello2morrow.sonargraph.jenkinsplugin.model;

import com.hello2morrow.sonargraph.jenkinsplugin.foundation.StringUtility;

/**
 * Enumerator which contains the different metric names.
 * 
 * @author esteban
 * 
 */
public enum SonargraphMetrics
{

    /*---------------- START: Metrics that are going to be in the graphics section ------------------*/

    STRUCTURAL_DEBT_INDEX("Structural debt (index)", false),

    NUMBER_OF_VIOLATIONS("Number of violating type dependencies", false),

    AVERAGE_COMPONENT_DEPENDENCY("Average component dependency (ACD)", true),

    NUMBER_OF_STATEMENTS("Number of statements", false),

    NUMBER_OF_METRIC_WARNINGS("Number of warnings (thresholds)", false),

    NUMBER_OF_CYCLIC_NAMESPACES("Number of cyclic packages", true),

    /*---------------- END: Metrics that are going to be in the graphics section -------------------- */

    NUMBER_OF_CYCLIC_ELEMENTS("Number of cyclic elements", false),

    NUMBER_OF_NOT_ASSIGNED_TYPES("Number of unassigned types", false),

    NUMBER_OF_CONSISTENCY_PROBLEMS("Number of consistency warnings", false),

    NUMBER_OF_WORKSPACE_WARNINGS("Number of warnings (workspace)", false),

    NUMBER_OF_TASKS("Number of tasks", false),

    NUMBER_OF_TARGET_FILES("Number of Java target files", false);

    private String m_description;

    private boolean m_readFromBuildUnit;

    private SonargraphMetrics(String description, boolean readFromBuildUnit)
    {
        m_description = description;
        m_readFromBuildUnit = readFromBuildUnit;
    }

    /**
     * @return Metric's standard name.
     */
    public String getStandardName()
    {
        return StringUtility.convertConstantNameToMixedCaseString(name(), true, false);
    }

    public String getDescription()
    {
        return m_description;
    }

    public boolean readFromBuildUnit()
    {
        return m_readFromBuildUnit;
    }

    public static SonargraphMetrics fromStandardName(String standardName) throws IllegalArgumentException
    {
        assert standardName != null : "'standardName' must not be null";
        assert standardName.length() > 0 : "'standardName' must not be empty";
        String name = StringUtility.convertMixedCaseStringToConstantName(standardName);
        return SonargraphMetrics.valueOf(name);
    }
}
