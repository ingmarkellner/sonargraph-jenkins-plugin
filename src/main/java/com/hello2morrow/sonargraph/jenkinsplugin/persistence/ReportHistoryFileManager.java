package com.hello2morrow.sonargraph.jenkinsplugin.persistence;

import java.io.IOException;

import de.schlichtherle.truezip.file.TFile;

/**
 * Class that handles copies of each generated architect report to calculate trends or
 * generate graphics.
 * @author esteban
 *
 */
public class ReportHistoryFileManager
{
    public static final String SONARGRAPH_JENKINS_REPORT_FILE_NAME_PREFIX = "sonargraph-jenkins-report-build-";

    /** Path to the folder containing sonargraph report files generated for every build */
    private TFile m_sonargraphReportHistoryDir;

    public ReportHistoryFileManager(TFile projectRootDir)
    {
        assert projectRootDir != null : "The path to the folder where architect reports are stored must not be null";
        m_sonargraphReportHistoryDir = new TFile(projectRootDir, "sonargraphReportHistory");
        if (!m_sonargraphReportHistoryDir.exists())
        {
            try
            {
                m_sonargraphReportHistoryDir.mkdir(true);
            }
            catch (IOException ex)
            {
                System.err.println("Failed to create directory '" + m_sonargraphReportHistoryDir.getNormalizedAbsolutePath() + "'");
                ex.printStackTrace();
            }
        }
    }

    public TFile getReportHistoryDirectory()
    {
        return m_sonargraphReportHistoryDir;
    }

    /**
     * Stores a generated architect report in the location defined for this purpose. 
     * @param architectReport the architect report file.
     */
    public void storeGeneratedReport(TFile architectReport, Integer buildNumber) throws IOException
    {
        assert architectReport != null : "Parameter 'architectReport' of method 'storeGeneratedReport' must not be null";
        assert architectReport.exists() : "Parameter 'architectReport' must be an existing file. '" + architectReport.getNormalizedAbsolutePath()
                + "' does not exist.";

        if (architectReport == null || buildNumber == null)
        {
            return;
        }

        if (!m_sonargraphReportHistoryDir.exists())
        {
            String msg = "Unable to create directory " + m_sonargraphReportHistoryDir.getAbsolutePath();
            //TODO: log to jenkins console.
            throw new IOException(msg);
        }

        TFile to = new TFile(m_sonargraphReportHistoryDir, SONARGRAPH_JENKINS_REPORT_FILE_NAME_PREFIX + buildNumber + ".xml");
        TFile.cp(architectReport, to);
    }
}
