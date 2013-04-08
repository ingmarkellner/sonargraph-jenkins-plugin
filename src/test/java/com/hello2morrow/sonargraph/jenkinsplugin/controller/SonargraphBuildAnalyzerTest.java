package com.hello2morrow.sonargraph.jenkinsplugin.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import hudson.model.Result;

import org.junit.Test;

import com.hello2morrow.sonargraph.jenkinsplugin.model.SonargraphMetrics;

import de.schlichtherle.truezip.file.TFile;

public class SonargraphBuildAnalyzerTest
{

    private static final String reportFileName = "src/test/resources/sonargraph-sonar-report.xml";

    @Test
    public void testChangeBuildResultIfViolationTresholdsExceeded()
    {
        Result result = null;
        SonargraphBuildAnalyzer analyzer = new SonargraphBuildAnalyzer(new TFile(reportFileName));
        result = analyzer.changeBuildResultIfViolationThresholdsExceeded(4, 6);
        assertNull(result);

        result = analyzer.changeBuildResultIfViolationThresholdsExceeded(2, 4);
        assertEquals(Result.UNSTABLE, result);

        result = analyzer.changeBuildResultIfViolationThresholdsExceeded(1, 3);
        assertEquals(Result.FAILURE, result);
    }

    @Test
    public void testChangeBuildResultIfMetricValueNotZero()
    {
        SonargraphBuildAnalyzer analyzer = new SonargraphBuildAnalyzer(new TFile(reportFileName));
        analyzer.changeBuildResultIfMetricValueNotZero(SonargraphMetrics.NUMBER_OF_VIOLATIONS, BuildActionsEnum.NOTHING.getActionCode());
        assertNull(analyzer.getOverallBuildResult());

        analyzer.changeBuildResultIfMetricValueNotZero(SonargraphMetrics.NUMBER_OF_VIOLATIONS, BuildActionsEnum.UNSTABLE.getActionCode());
        assertEquals(Result.UNSTABLE, analyzer.getOverallBuildResult());

        analyzer.changeBuildResultIfMetricValueNotZero(SonargraphMetrics.NUMBER_OF_VIOLATIONS, BuildActionsEnum.FAILED.getActionCode());
        assertEquals(Result.FAILURE, analyzer.getOverallBuildResult());
        
        analyzer.changeBuildResultIfMetricValueNotZero(SonargraphMetrics.NUMBER_OF_VIOLATIONS, BuildActionsEnum.UNSTABLE.getActionCode());
        assertEquals(Result.FAILURE, analyzer.getOverallBuildResult());
    }
    
    @Test
    public void testChangeBuildResultIfMetricValueIsZero()
    {
        SonargraphBuildAnalyzer analyzer = new SonargraphBuildAnalyzer(new TFile(reportFileName));
        analyzer.changeBuildResultIfMetricValueIsZero(SonargraphMetrics.NUMBER_OF_TARGET_FILES, BuildActionsEnum.FAILED.getActionCode());
        assertNull(analyzer.getOverallBuildResult());
    }
}
