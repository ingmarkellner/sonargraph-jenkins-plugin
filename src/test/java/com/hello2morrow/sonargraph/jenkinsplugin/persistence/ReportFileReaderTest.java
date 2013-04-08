package com.hello2morrow.sonargraph.jenkinsplugin.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.hello2morrow.sonargraph.jenkinsplugin.model.IReportReader;
import com.hello2morrow.sonargraph.jenkinsplugin.model.SonargraphMetrics;
import com.hello2morrow.sonargraph.jenkinsplugin.model.SonargraphReport;

import de.schlichtherle.truezip.file.TFile;

public class ReportFileReaderTest
{
    private static final String reportFileName = "src/test/resources/sonargraph-sonar-report_multiple-buildunits.xml";
    private static final String fakeDir = "fakeDir/ReporFileName.xml";
    private static final String nonExistingFile = "src/test/resources/report_error.xml";

    @Test
    public void testReadSonargraphReport()
    {
        IReportReader reader = new ReportFileReader();
        assertNull(reader.readSonargraphReport(new TFile(fakeDir)));
        assertNull(reader.readSonargraphReport(new TFile(nonExistingFile)));

		SonargraphReport sonargraphReport = reader.readSonargraphReport(new TFile(reportFileName));
		
		assertNotNull(sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_VIOLATIONS));
		assertNotNull(sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_NOT_ASSIGNED_TYPES));
		assertNotNull(sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_CYCLIC_ELEMENTS));
		assertNotNull(sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_METRIC_WARNINGS));
		assertNotNull(sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_WORKSPACE_WARNINGS));
		assertNotNull(sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_CONSISTENCY_PROBLEMS));
		assertNotNull(sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_TASKS));
		assertNotNull(sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_TARGET_FILES));
		assertNotNull(sonargraphReport.getSystemMetricValue(SonargraphMetrics.STRUCTURAL_DEBT_INDEX));
		assertNotNull(sonargraphReport.getSystemMetricValue(SonargraphMetrics.AVERAGE_COMPONENT_DEPENDENCY));
		assertNotNull(sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_STATEMENTS));
		assertNotNull(sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_CYCLIC_NAMESPACES));
		
		assertEquals("AlarmClock-3-levels", sonargraphReport.getName());
		assertEquals("2", sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_VIOLATIONS));
		assertEquals("0", sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_NOT_ASSIGNED_TYPES));
		assertEquals("6", sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_CYCLIC_ELEMENTS));
		assertEquals("0", sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_METRIC_WARNINGS));
		assertEquals("0", sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_WORKSPACE_WARNINGS));
		assertEquals("0", sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_CONSISTENCY_PROBLEMS));
		assertEquals("0", sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_TASKS));
		assertEquals("7", sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_TARGET_FILES));
		assertEquals("46", sonargraphReport.getSystemMetricValue(SonargraphMetrics.STRUCTURAL_DEBT_INDEX));
		assertEquals("2.33", sonargraphReport.getSystemMetricValue(SonargraphMetrics.AVERAGE_COMPONENT_DEPENDENCY));
		assertEquals("45", sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_STATEMENTS));
		assertEquals("2", sonargraphReport.getSystemMetricValue(SonargraphMetrics.NUMBER_OF_CYCLIC_NAMESPACES));
    }
}
