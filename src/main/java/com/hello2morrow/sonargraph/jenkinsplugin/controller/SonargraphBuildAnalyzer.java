package com.hello2morrow.sonargraph.jenkinsplugin.controller;

import hudson.model.Result;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.hello2morrow.sonargraph.jenkinsplugin.model.IMetricHistoryProvider;
import com.hello2morrow.sonargraph.jenkinsplugin.model.IReportReader;
import com.hello2morrow.sonargraph.jenkinsplugin.model.SonargraphMetrics;
import com.hello2morrow.sonargraph.jenkinsplugin.model.SonargraphReport;
import com.hello2morrow.sonargraph.jenkinsplugin.persistence.CSVFileHandler;
import com.hello2morrow.sonargraph.jenkinsplugin.persistence.ReportFileReader;

import de.schlichtherle.truezip.file.TFile;

/**
 * Class that analyzes the values found for the metrics and takes action
 * depending of what the user selected to do.
 * 
 * @author esteban
 */
class SonargraphBuildAnalyzer
{
    /**
     * HashMap containing a summary of results for the metrics. e.g. key:
     * NumberOfViolations, value: 3
     */
    private SonargraphReport m_report;

    /**
     * HashMap containing a code for the build result and a Result object for
     * each code.
     */
    private final HashMap<String, Result> m_buildResults = new HashMap<String, Result>();

    /** Final result of the build process after being affected by the metrics analysis.s */
    private Result overallBuildResult;

    /**
     * Constructor.
     * @param architectReportPath Absolute path to the Sonargraph architect report.
     */
    public SonargraphBuildAnalyzer(TFile architectReportPath)
    {
        assert architectReportPath != null : "The path for the Sonargraph architect report must not be null";
        IReportReader reportReader = new ReportFileReader();
        m_report = reportReader.readSonargraphReport(architectReportPath);

        m_buildResults.put(BuildActionsEnum.UNSTABLE.getActionCode(), Result.UNSTABLE);
        m_buildResults.put(BuildActionsEnum.FAILED.getActionCode(), Result.FAILURE);

        overallBuildResult = null;
    }

    /**
     * Analyzes architecture specific metrics.
     */
    public Result changeBuildResultIfViolationThresholdsExceeded(Integer numberOfViolationsUnstable, Integer numberOfViolationsFailed)
    {
        Result result = null;
        Integer numberOfViolations = Integer.parseInt(m_report.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_VIOLATIONS));
        if (numberOfViolations > 0)
        {
            if (numberOfViolations >= numberOfViolationsFailed)
            {
                result = m_buildResults.get(BuildActionsEnum.FAILED.getActionCode());
            }
            else if (numberOfViolations >= numberOfViolationsUnstable && numberOfViolations < numberOfViolationsFailed)
            {
                result = m_buildResults.get(BuildActionsEnum.UNSTABLE.getActionCode());
            }
        }
        return result;
    }

    public void changeBuildResultIfMetricValueNotZero(SonargraphMetrics metric, String userDefinedAction)
    {
        Integer metricValue = Integer.parseInt(m_report.getSystemMetricValue(metric));
        if (metricValue > 0)
        {
            if (userDefinedAction.equals(BuildActionsEnum.FAILED.getActionCode()))
            {
                overallBuildResult = m_buildResults.get(BuildActionsEnum.FAILED.getActionCode());
            }
            else if (userDefinedAction.equals(BuildActionsEnum.UNSTABLE.getActionCode()))
            {
                if (overallBuildResult == null || !overallBuildResult.equals(Result.FAILURE))
                {
                    overallBuildResult = m_buildResults.get(BuildActionsEnum.UNSTABLE.getActionCode());
                }
            }
        }
    }

    public void changeBuildResultIfMetricValueIsZero(SonargraphMetrics metric, String userDefinedAction)
    {
        Integer metricValue = Integer.parseInt(m_report.getSystemMetricValue(metric));
        if (metricValue.equals(0))
        {
            if (userDefinedAction.equals(BuildActionsEnum.FAILED.getActionCode()))
            {
                overallBuildResult = m_buildResults.get(BuildActionsEnum.FAILED.getActionCode());
            }
            else if (userDefinedAction.equals(BuildActionsEnum.UNSTABLE.getActionCode()))
            {
                if (overallBuildResult == null || !overallBuildResult.equals(Result.FAILURE))
                {
                    overallBuildResult = m_buildResults.get(BuildActionsEnum.UNSTABLE.getActionCode());
                }
            }
        }
    }

    /**
     * Appends all gathered metrics to the sonargraph CSV file.
     */
    public void saveMetricsToCSV(TFile metricHistoryFile, Integer buildNumber) throws IOException
    {
        IMetricHistoryProvider fileHandler = new CSVFileHandler(metricHistoryFile);
        LinkedHashMap<SonargraphMetrics, String> buildMetricValues = new LinkedHashMap<SonargraphMetrics, String>();

        for (SonargraphMetrics metric : SonargraphMetrics.values())
        {
            buildMetricValues.put(metric, m_report.getSystemMetricValue(metric));
        }

        //TODO [IK -> EA] make a pendant to Map<> readMetrics() and save all metrics in one go to the persistence
        fileHandler.writeMetrics(buildNumber, buildMetricValues);
    }

    public Result getOverallBuildResult()
    {
        return overallBuildResult;
    }
}
