package com.hello2morrow.sonargraph.jenkinsplugin.controller;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.DirectoryBrowserSupport;
import hudson.model.Project;

import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.hello2morrow.sonargraph.jenkinsplugin.persistence.TextFileReader;

import de.schlichtherle.truezip.file.TFile;

public class SonargraphHTMLReportAction implements Action
{
    /** Project or build that is calling this action. */
    private final Project<?, ?> project;

    /** Object that defines the post-buld step asociated with this action. */
    //TODO: Is that member needed?
    //    private final AbstractSonargraphRecorder builder;

    /**
     * FIXME: That folder is always fixed but variable.
     */
    private static final String HTML_REPORT_FOLDER = "/target/sonargraph-report";

    private static final String QUALITY_REPORT = "/sonargraph-quality-report.html";

    private static final String ARCHITECT_REPORT = "/sonargraph-architect-report.html";

    public SonargraphHTMLReportAction(Project<?, ?> project, AbstractSonargraphRecorder builder)
    {
        this.project = project;
    }

    public Project<?, ?> getProject()
    {
        return project;
    }

    /**
     * Returns null to hide the icon of the action.
     */
    public String getIconFileName()
    {
        return null;
    }

    /**
     * Return null to actually hide the action.
     */
    public String getDisplayName()
    {
        return null;
    }

    public String getUrlName()
    {
        // TODO Auto-generated method stub
        return "sonargraph-html-report";
    }

    public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException
    {
        DirectoryBrowserSupport directoryBrowser = new DirectoryBrowserSupport(this, new FilePath(new TFile(project.getWorkspace().getRemote(),
                HTML_REPORT_FOLDER)), this.getDisplayName() + "html2", "graph.gif", false);
        directoryBrowser.generateResponse(req, rsp, this);
    }

    public String getHTMLReport() throws IOException
    {
        TextFileReader fileReader = new TextFileReader();
        String reportFolderAbsouletPath = project.getWorkspace().getRemote() + HTML_REPORT_FOLDER;
        String htmlReport = "";
        TFile htmlFile = new TFile(reportFolderAbsouletPath, ARCHITECT_REPORT);
        if (htmlFile.exists())
        {
            htmlReport = fileReader.readLargeTextFile(htmlFile.getAbsolutePath());
        }
        else
        {
            htmlFile = new TFile(reportFolderAbsouletPath, QUALITY_REPORT);
            if (htmlFile.exists())
            {
                htmlReport = fileReader.readLargeTextFile(htmlFile.getAbsolutePath());
            }
            else
            {
                //TODO: Log this event. Associated with SG-307
                htmlReport = "No Sonargraph HTML report was found";
            }
        }

        return htmlReport;
    }
}
