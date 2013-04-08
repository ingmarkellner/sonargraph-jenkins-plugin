package com.hello2morrow.sonargraph.jenkinsplugin.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

//TODO: Decide if the IOException should be thrown or handled internally. Provide consistent methods.
public interface IMetricHistoryProvider
{
    /**
     * @return HashMap with the CSV file values.
     */
    public HashMap<Integer, Integer> readMetrics();
    
    /**
     * @return HashMap with the CSV file values of a specific column.
     */
    public HashMap<Integer, Double> readMetrics(int csvColumn);

    /**
     * Appends a sonargraph metric for a specific build.
     * @param buildNumber Number of the build where the metric was gathered
     * @param metricValue Value of the metric
     */
    public void writeMetric(Integer buildNumber, Integer metricValue) throws IOException;
    
    /**
     * Appends all supported metrics for a specific build.
     * @param buildNumber Number of the build where the metric was gathered
     * @param metricValues Ordered map containing the supported metrics and their values for the current build.
     */
    public void writeMetrics(Integer buildNumber, LinkedHashMap<SonargraphMetrics, String> metricValues) throws IOException;
}