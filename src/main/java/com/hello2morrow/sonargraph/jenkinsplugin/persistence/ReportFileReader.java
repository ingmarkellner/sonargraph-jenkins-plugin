/*
 * Sonar Sonargraph Plugin
 * Copyright (C) 2009, 2010, 2011 hello2morrow GmbH
 * mailto: info AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hello2morrow.sonargraph.jenkinsplugin.persistence;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.hello2morrow.sonargraph.jenkinsplugin.model.IReportReader;
import com.hello2morrow.sonargraph.jenkinsplugin.model.SonargraphMetrics;
import com.hello2morrow.sonargraph.jenkinsplugin.model.SonargraphReport;
import com.hello2morrow.sonargraph.jenkinsplugin.xsd.ReportContext;
import com.hello2morrow.sonargraph.jenkinsplugin.xsd.XsdAttribute;
import com.hello2morrow.sonargraph.jenkinsplugin.xsd.XsdAttributeCategory;
import com.hello2morrow.sonargraph.jenkinsplugin.xsd.XsdAttributeRoot;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;

/**
 * Utility class for reading in the Sonargraph Report.
 * 
 * @author Ingmar
 * 
 */
public class ReportFileReader implements IReportReader {
	private static final Logger LOG = Logger.getLogger(ReportFileReader.class);

	public ReportFileReader() {
	}

	public SonargraphReport readSonargraphReport(
			final TFile sonargraphReportFile) {
		BasicConfigurator.configure();
		if (sonargraphReportFile == null || !sonargraphReportFile.exists()) {
			LOG.error("No file path provided for reading sonargraph report");
			return null;
		}

		LOG.info("Reading Sonargraph metrics report from: "
				+ sonargraphReportFile.getNormalizedAbsolutePath());
		ReportContext report = null;
		SonargraphReport sonargraphReport = null;
		InputStream input = null;
		ClassLoader defaultClassLoader = Thread.currentThread()
				.getContextClassLoader();

		try {
			input = new TFileInputStream(sonargraphReportFile);
			Thread.currentThread().setContextClassLoader(
					ReportFileReader.class.getClassLoader());
			JAXBContext context = JAXBContext
					.newInstance("com.hello2morrow.sonargraph.jenkinsplugin.xsd");
			Unmarshaller u = context.createUnmarshaller();
			report = (ReportContext) u.unmarshal(input);
			sonargraphReport = createSonargraphReportFromXml(report);
		} catch (JAXBException e) {
			LOG.error(
					"JAXB Problem in "
							+ sonargraphReportFile.getNormalizedAbsolutePath(),
					e);
		} catch (IOException e) {
			LOG.error("Cannot open Sonargraph report: "
					+ sonargraphReportFile.getNormalizedAbsolutePath() + ".");
			// LOG.error("  Maven: Did you run the maven sonargraph goal before with the POM option <prepareForJenkins>true</prepareForJenkins> "
			// +
			// "or with the commandline option -Dsonargraph.prepareForJenkins=true?");
			// LOG.error("  Ant:   Did you create the Sonargraph XML report with the option prepareForJenkins set on true? ");
		} finally {
			Thread.currentThread().setContextClassLoader(defaultClassLoader);
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					LOG.error(
							"Cannot close "
									+ sonargraphReportFile
											.getNormalizedAbsolutePath(), e);
				}
			}
		}
		return sonargraphReport;
	}

	private SonargraphReport createSonargraphReportFromXml(
			ReportContext xmlReport) {
		SonargraphReport report = new SonargraphReport(xmlReport.getName());

		// Read the attribute categories of the software system
		for (XsdAttributeCategory category : xmlReport.getAttributes()
				.getAttributeCategory()) {
			for (XsdAttribute attribute : category.getAttribute()) {
				try {
					SonargraphMetrics metric = SonargraphMetrics
							.fromStandardName(attribute.getStandardName());
					if (metric != null && !metric.readFromBuildUnit()) {
						report.addSystemMetric(metric, attribute.getValue());
					}
				} catch (IllegalArgumentException ex) {
					LOG.debug("Unsupported metric '"
							+ attribute.getStandardName() + "'");
				}
			}
		}

		// FIXME [IK -> EA] Currently, build unit metrics are stored as system
		// metrics (in case they are greater than the system metric)
		// Better mirror the structure of the XML report in the Report model
		// object and have additional sections for build unit metrics.
		// For metrics like highest ACD, highest NCCD, there need to be
		// additional metrics in the Report model object.
		for (XsdAttributeRoot attrRoot : xmlReport.getBuildUnits()
				.getBuildUnit()) {
			for (XsdAttributeCategory category : attrRoot
					.getAttributeCategory()) {
				for (XsdAttribute attribute : category.getAttribute()) {
					try {
						SonargraphMetrics metric = SonargraphMetrics
								.fromStandardName(attribute.getStandardName());
						if (metric != null && metric.readFromBuildUnit()) {
							String presentMetricValue = report
									.getSystemMetricValue(metric);
							if (presentMetricValue != null) {
								Double presentMetricValueDouble = Double
										.parseDouble(presentMetricValue);
								Double newMetricValue = Double
										.parseDouble(attribute.getValue());
								if (newMetricValue > presentMetricValueDouble) {
									report.addSystemMetric(metric,
											attribute.getValue());
								}
							} else {
								report.addSystemMetric(metric,
										attribute.getValue());
							}
						}
					} catch (IllegalArgumentException ex) {
						LOG.debug("Unsupported metric '"
								+ attribute.getStandardName() + "'");
					}
				}
			}
		}
		return report;
	}
}
