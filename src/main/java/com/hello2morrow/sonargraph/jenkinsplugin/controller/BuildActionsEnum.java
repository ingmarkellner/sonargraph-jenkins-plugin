package com.hello2morrow.sonargraph.jenkinsplugin.controller;

enum BuildActionsEnum
{
    /** Build not affected. */
    NOTHING("Don't mark", "nothing"),

    /** Make the build unstable. */
    UNSTABLE("Build unstable", "unstable"),

    /** Make the build failed. */
    FAILED("Build failed", "failed");

    /** Action name. Used for the combobox in the UI. */
    private String m_actionName;

    /** Action code. Used for the logic of the build. */
    private String m_actionCode;

    private BuildActionsEnum(String actionName, String actionCode)
    {
        m_actionName = actionName;
        m_actionCode = actionCode;
    }

    public String getActionName()
    {
        return m_actionName;
    }

    public String getActionCode()
    {
        return m_actionCode;
    }
}
