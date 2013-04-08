package com.hello2morrow.sonargraph.jenkinsplugin.controller;

import hudson.Extension;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.Proc;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;
import hudson.tasks.Maven;
import hudson.tasks.Maven.MavenInstallation;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.hello2morrow.sonargraph.jenkinsplugin.foundation.StringUtility;
import com.hello2morrow.sonargraph.jenkinsplugin.model.ProductVersion;
import com.hello2morrow.sonargraph.jenkinsplugin.model.SonargraphProductType;

import de.schlichtherle.truezip.file.TFile;

/**
 * This class contains all the functionality of the build step.
 * 
 * @author esteban
 * 
 */
public class SonargraphReportBuilder extends AbstractSonargraphRecorder
{
    private static final String PROPERTY_PREFIX = " -Dsonargraph.";
    private static final String SONARGRAPH_REPORT_FILE_NAME = "sonargraph-report";

    private final String mavenInstallation;
    private final String systemFile;
    private final String reportDirectory;
    private final String useSonargraphWorkspace;
    private final String prepareForSonar;

    private static final String GROUP_ID = "com.hello2morrow.sonargraph";
    private static final String ARTIFACT_ID = "maven-sonargraph-plugin";
    private static final String ARCHITECT_GOAL = "architect-report";
    private static final String QUALITY_GOAL = "quality-report-direct-parsing-mode";

    private final String pathToExecutable = "/bin";
    private static final String M2_HOME = "M2_HOME";
    private PrintStream logger;

    /**
     * Constructor. Fields in the config.jelly must match the parameters in this
     * constructor.
     */
    @DataBoundConstructor
    public SonargraphReportBuilder(String mavenInstallation, String systemFile, String reportDirectory, String useSonargraphWorkspace,
            String prepareForSonar, String architectureViolationsAction, String unassignedTypesAction, String cyclicElementsAction,
            String thresholdViolationsAction, String architectureWarningsAction, String workspaceWarningsAction, String workItemsAction,
            String emptyWorkspaceAction)
    {
        super(architectureViolationsAction, unassignedTypesAction, cyclicElementsAction, thresholdViolationsAction, architectureWarningsAction,
                workspaceWarningsAction, workItemsAction, emptyWorkspaceAction);

        this.mavenInstallation = mavenInstallation;
        this.systemFile = systemFile;
        this.reportDirectory = reportDirectory;
        this.useSonargraphWorkspace = useSonargraphWorkspace;
        this.prepareForSonar = prepareForSonar;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException
    {
        // Badge should be added only once
        if (build.getAction(SonargraphBadgeAction.class) == null)
        {
            build.addAction(new SonargraphBadgeAction());
        }

        this.logger = listener.getLogger();

        String absoluteReportDir = new TFile(build.getWorkspace().getRemote(), reportDirectory).getNormalizedAbsolutePath();
        String mvnCommand = createMvnCommand(build.getWorkspace().getRemote(), System.getProperty("os.name", "unknown").trim().toLowerCase(),
                getDescriptor());

        ProcStarter procStarter = launcher.new ProcStarter();
        HashMap<String, String> envVars = new HashMap<String, String>();
        procStarter.cmdAsSingleString(mvnCommand);
        procStarter.stdout(listener.getLogger());
        procStarter = procStarter.pwd(build.getWorkspace()).envs(build.getEnvironment(listener));
        envVars.put(M2_HOME, mavenInstallation);
        procStarter.envs(envVars);
        Proc proc = launcher.launch(procStarter);
        int processExitCode = proc.join();

        if (processExitCode != 0)
        {
            // TODO: how to log the error properly? Can it be displayed to the
            // user as the status for the Sonargraph plugin?
            return false;
        }

        TFile sonargraphReportFile = new TFile(absoluteReportDir, SONARGRAPH_REPORT_FILE_NAME + ".xml");
        processSonargraphReport(build, sonargraphReportFile);

        /*
         * Must return true for jenkins to mark the build as SUCCESS. Only then,
         * it can be downgraded to the result that was set but it can never be
         * upgraded.
         */
        return true;
    }

    private String createMvnCommand(String workspacePath, String operatingSystem, DescriptorImpl descriptor)
    {
        String absoluteReportDir = new TFile(workspacePath, reportDirectory).getNormalizedAbsolutePath();

        String pathToMvn = null;
        if (mavenInstallation != null)
        {
            pathToMvn = new TFile(mavenInstallation + pathToExecutable, "mvn").getAbsolutePath();
        }
        else
        {
            pathToMvn = "mvn";
        }

        StringBuilder mvnCommand = new StringBuilder(pathToMvn);
        if (operatingSystem.startsWith("windows"))
        {
            mvnCommand.append(".bat");
        }

        //FIXME: Why are some modules not found if goal is run on multi-module projects? "package" solves this at the cost of extra time needed.
        mvnCommand.append(" package");

        mvnCommand.append(" ").append(GROUP_ID).append(":").append(ARTIFACT_ID).append(":").append(descriptor.getVersion()).append(":");
        if (descriptor.getProductType().equals(SonargraphProductType.ARCHITECT.getId()))
        {
            mvnCommand.append(ARCHITECT_GOAL);
        }
        else
        {
            mvnCommand.append(QUALITY_GOAL);
        }

        if (systemFile != null && systemFile.length() > 0)
        {
            TFile sonargraphFile = new TFile(workspacePath, systemFile);
            if (!sonargraphFile.exists())
            {
                logger.println("Specified Sonargraph system file '" + sonargraphFile.getNormalizedAbsolutePath() + "' does not exist!");
            }
            mvnCommand.append(PROPERTY_PREFIX).append("file=").append(sonargraphFile.getNormalizedAbsolutePath());
        }

        if (descriptor.getLicense() != null && descriptor.getLicense().length() > 0)
        {
            mvnCommand.append(PROPERTY_PREFIX).append("license=").append(descriptor.getLicense());
        }
        else if (descriptor.getActivationCode() != null && descriptor.getActivationCode().length() > 0)
        {
            mvnCommand.append(PROPERTY_PREFIX).append("activationCode=").append(descriptor.getActivationCode());
        }
        else
        {
            logger.println("You have to either specify a license file or activation code!");
        }
        mvnCommand.append(PROPERTY_PREFIX).append("prepareForJenkins=true");
        mvnCommand.append(PROPERTY_PREFIX).append("reportDirectory=").append(absoluteReportDir);
        mvnCommand.append(PROPERTY_PREFIX).append("reportName=").append(SONARGRAPH_REPORT_FILE_NAME);
        mvnCommand.append(PROPERTY_PREFIX).append("reportType=HTML");
        mvnCommand.append(PROPERTY_PREFIX).append("useSonargraphWorkspace=").append(useSonargraphWorkspace);
        mvnCommand.append(PROPERTY_PREFIX).append("prepareForSonar=").append(prepareForSonar);
        return mvnCommand.toString();
    }

    public String getMavenInstallation()
    {
        return mavenInstallation;
    }

    public String getSystemFile()
    {
        return systemFile;
    }

    public String getReportDirectory()
    {
        return reportDirectory;
    }

    public String getUseSonargraphWorkspace()
    {
        return useSonargraphWorkspace;
    }

    public String getPrepareForSonar()
    {
        return prepareForSonar;
    }

    @Override
    public DescriptorImpl getDescriptor()
    {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends AbstractBuildStepDescriptor
    {
        /** Absolute path to the Sonargraph license file. */
        private String license;

        private String activationCode;

        /** Installed version of Sonargraph. */
        private String version;

        /** Either SonargraphArchitect or SonargraphQuality **/
        private String productType;

        public DescriptorImpl()
        {
            super();
            load();
        }

        @Override
        public String getDisplayName()
        {
            return ConfigParameters.REPORT_BUILDER_DISPLAY_NAME.getValue();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws hudson.model.Descriptor.FormException
        {
            productType = formData.getString("productType");
            version = formData.getString("version");
            license = formData.getString("license");
            activationCode = formData.getString("activationCode");

            save();
            return super.configure(req, formData);
        }

        public String getProductType()
        {
            return productType;
        }

        public String getVersion()
        {
            return version;
        }

        public String getLicense()
        {
            return license;
        }

        public String getActivationCode()
        {
            return activationCode;
        }

        public FormValidation doCheckVersion(@QueryParameter
        String value)
        {
            return StringUtility.validateNotNullAndRegexp(value, "^(\\d\\.)+\\d$") ? FormValidation.ok() : FormValidation
                    .error("Please enter a valid version");
        }

        public FormValidation doCheckLicense(@QueryParameter
        String value)
        {

            return StringUtility.validateNotNullAndRegexp(value, "([a-zA-Z]:\\\\)?[\\/\\\\a-zA-Z0-9_.-]+.license$") ? FormValidation.ok()
                    : FormValidation.error("Please enter a valid path for the license");

        }

        public FormValidation doCheckSystemFile(@QueryParameter
        String value)
        {

            return StringUtility.validateNotNullAndRegexp(value, "([a-zA-Z]:\\\\)?[\\/\\\\a-zA-Z0-9_.-]+.sonargraph$") ? FormValidation.ok()
                    : FormValidation.error("Please enter a valid system file");

        }

        public FormValidation doCheckReportDirectory(@QueryParameter
        String value)
        {

            return StringUtility.validateNotNullAndRegexp(value, "[\\/\\\\a-zA-Z0-9_.-]+") ? FormValidation.ok() : FormValidation
                    .error("Please enter a valid path for the report directory");

        }

        public ListBoxModel doFillMavenInstallationItems()
        {
            ListBoxModel items = new ListBoxModel();
            MavenInstallation[] installations = Hudson.getInstance().getDescriptorByType(Maven.DescriptorImpl.class).getInstallations();
            for (MavenInstallation installation : installations)
            {
                items.add(installation.getName(), installation.getHome());
            }
            return items;
        }

        public ListBoxModel doFillProductTypeItems()
        {
            ListBoxModel items = new ListBoxModel();
            for (SonargraphProductType productType : SonargraphProductType.values())
            {
                items.add(productType.getDisplayName(), productType.getId());
            }
            return items;
        }

        public ListBoxModel doFillVersionItems()
        {
            ListBoxModel items = new ListBoxModel();
            for (ProductVersion version : ProductVersion.values())
            {
                items.add(version.getId(), version.getId());
            }
            return items;
        }
    }
}
