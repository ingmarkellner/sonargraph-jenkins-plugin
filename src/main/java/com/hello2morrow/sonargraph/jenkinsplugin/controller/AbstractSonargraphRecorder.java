package com.hello2morrow.sonargraph.jenkinsplugin.controller;

import hudson.model.Action;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Project;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.hello2morrow.sonargraph.jenkinsplugin.model.SonargraphMetrics;
import com.hello2morrow.sonargraph.jenkinsplugin.persistence.ReportHistoryFileManager;

import de.schlichtherle.truezip.file.TFile;

abstract class AbstractSonargraphRecorder extends Recorder
{
    private final String architectureViolationsAction;
    private final String unassignedTypesAction;
    private final String cyclicElementsAction;
    private final String thresholdViolationsAction;
    private final String architectureWarningsAction;
    private final String workspaceWarningsAction;
    private final String workItemsAction;
    private final String emptyWorkspaceAction;

    /** CSV file path relative to the build workspace */
    private final String csvFilePath = "/sonargraph.csv";

    public AbstractSonargraphRecorder(String architectureViolationsAction, String unassignedTypesAction, String cyclicElementsAction,
            String thresholdViolationsAction, String architectureWarningsAction, String workspaceWarningsAction, String workItemsAction,
            String emptyWorkspaceAction)
    {
        this.architectureViolationsAction = architectureViolationsAction;
        this.unassignedTypesAction = unassignedTypesAction;
        this.cyclicElementsAction = cyclicElementsAction;
        this.thresholdViolationsAction = thresholdViolationsAction;
        this.architectureWarningsAction = architectureWarningsAction;
        this.workspaceWarningsAction = workspaceWarningsAction;
        this.workItemsAction = workItemsAction;
        this.emptyWorkspaceAction = emptyWorkspaceAction;
    }

    /**
     * We override the getProjectAction method to define our custom action
     * that will show the charts for sonargraph's metrics.
     */
    @Override
    public Collection<Action> getProjectActions(AbstractProject<?, ?> project)
    {
        Collection<Action> actions = null;
        if (project instanceof Project) {
            actions = new ArrayList<Action>();
            actions.add(new SonargraphChartAction((Project<?, ?>) project, this));
            actions.add(new SonargraphHTMLReportAction((Project<?, ?>) project, this));
        }
        return actions;
    }

    public BuildStepMonitor getRequiredMonitorService()
    {
        return BuildStepMonitor.NONE;
    }
    
    protected void processSonargraphReport(AbstractBuild<?, ?> build, TFile sonargraphReportFile) throws IOException
    {
        assert build != null : "Parameter 'build' of method 'processSonargraphReport' must not be null";
        assert sonargraphReportFile != null : "Parameter 'sonargraphReportFile' of method 'processSonargraphReport' must not be null";
        
        TFile projectRootDir = new TFile(build.getProject().getRootDir());
        ReportHistoryFileManager reportHistoryManager = new ReportHistoryFileManager(projectRootDir);
        reportHistoryManager.storeGeneratedReport(sonargraphReportFile, build.getNumber());
    
        SonargraphBuildAnalyzer sonargraphBuildAnalyzer = new SonargraphBuildAnalyzer(sonargraphReportFile);
        sonargraphBuildAnalyzer.changeBuildResultIfMetricValueNotZero(SonargraphMetrics.NUMBER_OF_VIOLATIONS, architectureViolationsAction);
        sonargraphBuildAnalyzer.changeBuildResultIfMetricValueNotZero(SonargraphMetrics.NUMBER_OF_NOT_ASSIGNED_TYPES, unassignedTypesAction);
        sonargraphBuildAnalyzer.changeBuildResultIfMetricValueNotZero(SonargraphMetrics.NUMBER_OF_CYCLIC_ELEMENTS, cyclicElementsAction);
        sonargraphBuildAnalyzer.changeBuildResultIfMetricValueNotZero(SonargraphMetrics.NUMBER_OF_METRIC_WARNINGS, thresholdViolationsAction);
        sonargraphBuildAnalyzer.changeBuildResultIfMetricValueNotZero(SonargraphMetrics.NUMBER_OF_CONSISTENCY_PROBLEMS, architectureWarningsAction);
        sonargraphBuildAnalyzer.changeBuildResultIfMetricValueNotZero(SonargraphMetrics.NUMBER_OF_WORKSPACE_WARNINGS, workspaceWarningsAction);
        sonargraphBuildAnalyzer.changeBuildResultIfMetricValueNotZero(SonargraphMetrics.NUMBER_OF_TASKS, workItemsAction);
        sonargraphBuildAnalyzer.changeBuildResultIfMetricValueIsZero(SonargraphMetrics.NUMBER_OF_TARGET_FILES, emptyWorkspaceAction);
        Result buildResult = sonargraphBuildAnalyzer.getOverallBuildResult();
    
        TFile metricHistoryFile = new TFile(build.getProject().getRootDir(), csvFilePath);
        sonargraphBuildAnalyzer.saveMetricsToCSV(metricHistoryFile, build.getNumber());
        if (buildResult != null)
        {
            build.setResult(buildResult);
        }
    }

    public String getArchitectureViolationsAction()
    {
        return architectureViolationsAction;
    }

    public String getUnassignedTypesAction()
    {
        return unassignedTypesAction;
    }

    public String getCyclicElementsAction()
    {
        return cyclicElementsAction;
    }

    public String getThresholdViolationsAction()
    {
        return thresholdViolationsAction;
    }

    public String getArchitectureWarningsAction()
    {
        return architectureWarningsAction;
    }

    public String getWorkspaceWarningsAction()
    {
        return workspaceWarningsAction;
    }

    public String getWorkItemsAction()
    {
        return workItemsAction;
    }

    public String getEmptyWorkspaceAction()
    {
        return emptyWorkspaceAction;
    }
    
    public AbstractBuildStepDescriptor getDescriptor()
    {
        return (AbstractBuildStepDescriptor) super.getDescriptor();
    }
}
