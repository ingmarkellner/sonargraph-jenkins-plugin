package com.hello2morrow.sonargraph.jenkinsplugin.controller;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.util.FormValidation;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.hello2morrow.sonargraph.jenkinsplugin.foundation.StringUtility;

import de.schlichtherle.truezip.file.TFile;

/**
 * Processes a previously generated Sonargraph report.
 * 
 * @author Ingmar
 */
public class SonargraphReportAnalyzer extends AbstractSonargraphRecorder
{
    private String sonargraphReportPath;

    @DataBoundConstructor
    public SonargraphReportAnalyzer(String sonargraphReportPath, String architectureViolationsAction, String unassignedTypesAction,
            String cyclicElementsAction, String thresholdViolationsAction, String architectureWarningsAction, String workspaceWarningsAction,
            String workItemsAction, String emptyWorkspaceAction)
    {
        super(architectureViolationsAction, unassignedTypesAction, cyclicElementsAction, thresholdViolationsAction, architectureWarningsAction,
                workspaceWarningsAction, workItemsAction, emptyWorkspaceAction);

        assert sonargraphReportPath != null && sonargraphReportPath.length() > 0 : "Parameter 'sonargraphReportPath' of method 'SonargraphReportAnalyzer' must not be empty";
        this.sonargraphReportPath = sonargraphReportPath;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException
    {
        TFile sonargraphReportFile = new TFile(build.getWorkspace().getRemote(), sonargraphReportPath).getNormalizedAbsoluteFile();
        super.processSonargraphReport(build, sonargraphReportFile);
        return true;
    }

    public String getSonargraphReportPath()
    {
        return this.sonargraphReportPath;
    }

    @Extension
    public static final class DescriptorImpl extends AbstractBuildStepDescriptor
    {
        public DescriptorImpl()
        {
            super();
            load();
        }

        @Override
        public String getDisplayName()
        {
            return ConfigParameters.REPORT_ANALYZER_DISPLAY_NAME.getValue();
        }

        public FormValidation doCheckReportDirectory(@QueryParameter
        String value)
        {
            return StringUtility.validateNotNullAndRegexp(value, "[\\/\\\\a-zA-Z0-9_.-]+") ? FormValidation.ok() : FormValidation
                    .error("Please enter a valid path for the report directory");
        }
    }
}
