package com.hello2morrow.sonargraph.jenkinsplugin.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;

import com.hello2morrow.sonargraph.jenkinsplugin.model.IMetricHistoryProvider;
import com.hello2morrow.sonargraph.jenkinsplugin.model.SonargraphMetrics;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileReader;
import de.schlichtherle.truezip.file.TFileWriter;

/**
 * Handles operations on a CSV file.
 * @author esteban
 *
 */
public class CSVFileHandler implements IMetricHistoryProvider
{
    /** Default separator for the CSV file. */
    private static final char SEPARATOR = ';';
    private TFile m_file;

    public CSVFileHandler(TFile csvFile)
    {
        m_file = csvFile;
        if (!m_file.exists())
        {
            try
            {
                m_file.createNewFile();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.jenkinsplugin.persistence.IMetricHistoryProvider#readMetrics()
     */
    @Deprecated
    public HashMap<Integer, Integer> readMetrics()
    {
        HashMap<Integer, Integer> sonargraphDataset = new HashMap<Integer, Integer>();
        if (!m_file.exists())
        {
            return sonargraphDataset;
        }
        try
        {
            CSVReader csvReader = new CSVReader(new TFileReader(m_file), SEPARATOR);
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null)
            {
                sonargraphDataset.put(Integer.parseInt(nextLine[0]), Integer.parseInt(nextLine[1]));
            }
            csvReader.close();
        }

        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        return sonargraphDataset;
    }

    public HashMap<Integer, Double> readMetrics(int csvColumn)
    {
        HashMap<Integer, Double> sonargraphDataset = new HashMap<Integer, Double>();
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);

        if (!m_file.exists())
        {
            return sonargraphDataset;
        }

        try
        {
            CSVReader csvReader = new CSVReader(new TFileReader(m_file), SEPARATOR);
            String[] nextLine;
            Number value;
            while ((nextLine = csvReader.readNext()) != null)
            {
                try
                {
                    value = format.parse(nextLine[csvColumn]);
                    sonargraphDataset.put(Integer.parseInt(nextLine[0]), value.doubleValue());
                }
                catch (ParseException ex)
                {
                    //TODO: At least log some error statement.
                }
            }
            csvReader.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        return sonargraphDataset;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.jenkinsplugin.persistence.IMetricHistoryProvider#writeMetric(java.lang.Integer, java.lang.Integer)
     */
    @Deprecated
    public void writeMetric(Integer buildNumber, Integer metricValue) throws IOException
    {
        TFileWriter fileWriter = new TFileWriter(m_file, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        String lineToAppend = String.valueOf(buildNumber) + SEPARATOR + String.valueOf(metricValue);
        bufferedWriter.write(lineToAppend);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public void writeMetrics(Integer buildNumber, LinkedHashMap<SonargraphMetrics, String> metricValues) throws IOException
    {
        TFileWriter fileWriter = new TFileWriter(m_file, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        String lineToAppend = String.valueOf(buildNumber) + SEPARATOR;
        for (SonargraphMetrics metric : metricValues.keySet())
        {
            lineToAppend += metricValues.get(metric) + SEPARATOR;
        }
        lineToAppend = lineToAppend.substring(0, lineToAppend.length() - 1);
        bufferedWriter.write(lineToAppend);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        bufferedWriter.close();
    }

}
