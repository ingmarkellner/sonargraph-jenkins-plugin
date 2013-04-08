package com.hello2morrow.sonargraph.jenkinsplugin.persistence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.schlichtherle.truezip.file.TFile;

public class ReportHistoryFileManagerTest
{
    private static final String reportFileName = "src/test/resources/sonargraph-sonar-report.xml";
    private static final String archReportHistoryPath = "src/test/resources/temp";
    private TFile sonargraphReportFile;

    @Before
    public void before() throws IOException
    {
        removeFiles();
    }
    
    @After
    public void tearDown() throws IOException
    {
        removeFiles();
    }

    private void removeFiles() throws IOException
    {
        if (sonargraphReportFile != null && sonargraphReportFile.exists())
        {
            sonargraphReportFile.rm();
        }
        TFile historyDir = new TFile(archReportHistoryPath);
        if (historyDir.exists())
        {
            historyDir.rm_r();
        }
    }
    
    @Test
    public void testStoreGeneratedReport() throws IOException
    {
        ReportHistoryFileManager rhfm = new ReportHistoryFileManager(new TFile(archReportHistoryPath));
        Integer buildNumber = 1;
        sonargraphReportFile = new TFile(rhfm.getReportHistoryDirectory(), ReportHistoryFileManager.SONARGRAPH_JENKINS_REPORT_FILE_NAME_PREFIX + buildNumber + ".xml");
        assertFalse(sonargraphReportFile.exists());
        
        rhfm.storeGeneratedReport(new TFile(reportFileName), buildNumber);
        assertTrue(sonargraphReportFile.exists());
    }
}
