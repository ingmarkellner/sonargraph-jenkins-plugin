package com.hello2morrow.sonargraph.jenkinsplugin.persistence;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hello2morrow.sonargraph.jenkinsplugin.model.IMetricHistoryProvider;
import com.hello2morrow.sonargraph.jenkinsplugin.model.SonargraphMetrics;

import au.com.bytecode.opencsv.CSVReader;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileReader;

public class CSVFileHandlerTest
{
    private final String csvTestFilePath = "src/test/resources/sonargraph.csv";
    private final String nonExistingFilePath = "src/test/resources/non-existing.csv";
    private final HashMap<Integer, Integer> loadedHashMapInt = new HashMap<Integer, Integer>();
    private final HashMap<Integer, Double> loadedHashMapDouble = new HashMap<Integer, Double>();
    private TFile nowExistentFile;
    private static final char SEPARATOR = ';';

    @Before
    public void setUp() throws IOException
    {
        loadedHashMapInt.put(31, 3);
        loadedHashMapInt.put(32, 3);
        loadedHashMapInt.put(33, 3);
        loadedHashMapInt.put(34, 3);
        loadedHashMapInt.put(35, 3);
        
        for (Integer build :loadedHashMapInt.keySet())
        {
            loadedHashMapDouble.put(build, (double)loadedHashMapInt.get(build));
        }

        removeFiles();
    }

    @After
    public void tearDown() throws IOException
    {
        removeFiles();
    }

    private void removeFiles() throws IOException
    {

        if (nowExistentFile != null && nowExistentFile.exists())
        {
            nowExistentFile.rm();
        }
    }

    @Test
    public void testReadSonargraphCSVFile() throws IOException
    {
        HashMap<Integer, Integer> testDataset = new HashMap<Integer, Integer>();
        nowExistentFile = new TFile(nonExistingFilePath);
        IMetricHistoryProvider csvFileHandler = new CSVFileHandler(nowExistentFile);
        testDataset = csvFileHandler.readMetrics();
        assertEquals(0, testDataset.size());

        csvFileHandler = new CSVFileHandler(new TFile(csvTestFilePath));
        testDataset = csvFileHandler.readMetrics();
        assertEquals(5, testDataset.size());
        assertEquals(loadedHashMapInt, testDataset);
    }
    
    @Test
    public void testReadMetrics()
    {
        HashMap<Integer, Double> testDataset = new HashMap<Integer, Double>();
        TFile nonExistingFile = new TFile(nonExistingFilePath);
        IMetricHistoryProvider csvFileHandler = new CSVFileHandler(nonExistingFile);
        testDataset = csvFileHandler.readMetrics(1);
        assertEquals(0, testDataset.size());
        
        csvFileHandler = new CSVFileHandler(new TFile(csvTestFilePath));
        testDataset = csvFileHandler.readMetrics(1);
        assertEquals(5, testDataset.size());
        assertEquals(loadedHashMapDouble, testDataset);
    }

    //TODO: Try to eliminate coupling with the filesystem for this test case.
    @Test
    public void testWriteMetricToFile() throws IOException
    {
        nowExistentFile = new TFile(nonExistingFilePath);
     
        IMetricHistoryProvider csvFileHandler = new CSVFileHandler(nowExistentFile);
        csvFileHandler.writeMetric(36, 3);
        assertTrue(nowExistentFile.exists());

        CSVReader csvReader = new CSVReader(new TFileReader(nowExistentFile), SEPARATOR);
        ArrayList<String[]> lines = new ArrayList<String[]>();
        String[] line;
        while ((line = csvReader.readNext()) != null)
        {
            lines.add(line);
        }
        csvReader.close();
        assertEquals(1, lines.size());
        String[] expectedLine = { "36", "3" };
        assertArrayEquals(expectedLine, lines.get(0));
    }

    //TODO: Try to eliminate coupling with the filesystem for this test case.
    @Test
    public void testWriteMetricsToFile() throws IOException
    {

        nowExistentFile = new TFile(nonExistingFilePath);
        assertFalse(nowExistentFile.exists());

        IMetricHistoryProvider csvFileHandler = new CSVFileHandler(nowExistentFile);
        assertTrue(nowExistentFile.exists());

        LinkedHashMap<SonargraphMetrics, String> buildMetrics = new LinkedHashMap<SonargraphMetrics, String>();
        buildMetrics.put(SonargraphMetrics.NUMBER_OF_CONSISTENCY_PROBLEMS, "3");
        buildMetrics.put(SonargraphMetrics.NUMBER_OF_CYCLIC_ELEMENTS, "7");
        buildMetrics.put(SonargraphMetrics.AVERAGE_COMPONENT_DEPENDENCY, "2.6");
        buildMetrics.put(SonargraphMetrics.NUMBER_OF_STATEMENTS, "200");
        csvFileHandler.writeMetrics(1, buildMetrics);
        CSVReader csvReader = new CSVReader(new TFileReader(nowExistentFile), SEPARATOR);
        String[] line = csvReader.readNext();
        csvReader.close();
        String[] expectedLine = { "1", "3", "7", "2.6", "200" };
        assertArrayEquals(expectedLine, line);
    }
}
