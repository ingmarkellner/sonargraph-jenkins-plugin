package com.hello2morrow.sonargraph.jenkinsplugin.controller;

import hudson.model.BuildBadgeAction;

//TODO: Is it really save to return null for the getters? 
public class SonargraphBadgeAction implements BuildBadgeAction
{
    public String getIconFileName()
    {
        return null;
    }

    public String getDisplayName()
    {
        return null;
    }

    public String getUrlName()
    {
        return null;
    }
    
    public String getIcon()
    {
        return ConfigParameters.SONARGRAPH_ICON.getValue();
    }
}
