package com.hello2morrow.sonargraph.jenkinsplugin.controller;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.ListBoxModel;

abstract class AbstractBuildStepDescriptor extends BuildStepDescriptor<Publisher>
{
    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType)
    {
        return true;
    }

    public ListBoxModel doFillArchitectureViolationsActionItems()
    {
        return createListWithActions();
    }

    public ListBoxModel doFillUnassignedTypesActionItems()
    {
        return createListWithActions();
    }

    public ListBoxModel doFillCyclicElementsActionItems()
    {
        return createListWithActions();
    }

    public ListBoxModel doFillThresholdViolationsActionItems()
    {
        return createListWithActions();
    }

    public ListBoxModel doFillArchitectureWarningsActionItems()
    {
        return createListWithActions();
    }

    public ListBoxModel doFillWorkspaceWarningsActionItems()
    {
        return createListWithActions();
    }

    public ListBoxModel doFillWorkItemsActionItems()
    {
        return createListWithActions();
    }

    public ListBoxModel doFillEmptyWorkspaceActionItems()
    {
        return createListWithActions();
    }

    private ListBoxModel createListWithActions()
    {
        ListBoxModel items = new ListBoxModel();
        for (BuildActionsEnum action : BuildActionsEnum.values())
        {
            items.add(action.getActionName(), action.getActionCode());
        }
        return items;
    }
}
