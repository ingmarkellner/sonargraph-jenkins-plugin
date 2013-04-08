package com.hello2morrow.sonargraph.jenkinsplugin.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.util.ShapeUtilities;

/**
 * Object that allows to generate charts with discrete values in the X-axis.
 * @author esteban
 *
 */
public class DiscreteLinePlot
{

    /** The chart to generate. */
    private JFreeChart m_chart;

    private IMetricHistoryProvider m_datasetProvider;
    
    public DiscreteLinePlot(IMetricHistoryProvider datasetProvider)
    {
        assert datasetProvider != null : "Parameter 'datasetProvider' of method 'DiscreteLinePlot' must not be null";
        m_datasetProvider = datasetProvider;
    }
    
    /**
     * Creates a Dataset from a CSV file containing data.
     * @param lineName Name of the line that will be drawn in the chart.
     */
    private CategoryDataset createDataset(String lineName, int csvColumn)
    {
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        HashMap<Integer, Double> dataset = m_datasetProvider.readMetrics(csvColumn);
        ArrayList<Integer> buildNumbers = new ArrayList<Integer>();
        buildNumbers.addAll(dataset.keySet());
        Collections.sort(buildNumbers);
        for (Integer buildNumber : buildNumbers)
        {
            result.addValue(dataset.get(buildNumber), lineName, buildNumber);
        }

        return result;

    }

    /**
     * Creates a chart given a dataset and chart properties. 
     * @param chartTitle Title for the chart.
     * @param lineName Name of the line that is going to be plotted.
     * @param categoryName Name for the X-Axis, representing a category
     * @param yAxisName Name of the Y-Axis. 
     * @return Chart built with the given parameters.
     */
    public JFreeChart createChart(String chartTitle, String lineName, String categoryName, String yAxisName, int csvColumn)
    {
        CategoryDataset dataset = createDataset(lineName, csvColumn);
        m_chart = ChartFactory.createLineChart(chartTitle, categoryName, yAxisName, dataset, PlotOrientation.VERTICAL, true, false, false);
        CategoryPlot plot = (CategoryPlot) m_chart.getPlot();
        plot.setRangeGridlinePaint(Color.white);
        LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer)plot.getRenderer();   
        lineandshaperenderer.setSeriesShapesVisible(0, true);   
        lineandshaperenderer.setSeriesShapesVisible(1, false);   
        lineandshaperenderer.setSeriesShapesVisible(2, true);   
        lineandshaperenderer.setSeriesLinesVisible(2, false);   
        lineandshaperenderer.setSeriesShape(2, ShapeUtilities.createDiamond(4F));   
        lineandshaperenderer.setDrawOutlines(true);   
        lineandshaperenderer.setUseFillPaint(true);   
        lineandshaperenderer.setSeriesFillPaint(0, Color.WHITE);
        plot.setBackgroundPaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        return m_chart;
    }

    public JFreeChart getChart()
    {
        return m_chart;
    }
}
