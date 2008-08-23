/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.mailet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * @goal mailetdocs
 * 
 * @requiresDependencyResolution compile
 */
public class MailetdocsMojo extends AbstractMavenReport {

    /**
     * <i>Maven Internal</i>: The Doxia Site Renderer.
     * 
     * @component
     */
    private Renderer siteRenderer;

    /**
     * <i>Maven Internal</i>: The Project descriptor.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The output directory.
     * 
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     */
    private File outputDirectory;

    /**
     * A imple Predicate to extract only a given type from a list
     */
    private static final class TypePredicate implements Predicate {
        private int type;

        public TypePredicate(int typeMatcher) {
            this.type = typeMatcher;
        }

        public boolean evaluate(Object arg0) {
            return ((MailetMatcherDescriptor) arg0).getType() == type;
        }
    }

    protected void executeReport(Locale locale) throws MavenReportException {

        List descriptors = new DefaultDescriptorsExtractor().extractDescriptors(project, getLog());

        Collections.sort(descriptors, new Comparator() {

            public int compare(Object arg0, Object arg1) {
                return ((MailetMatcherDescriptor) arg0).getName().compareTo(
                        ((MailetMatcherDescriptor) arg1).getName());
            }

        });

        getLog().info("Executing Mailets/Matchers Report");

        getSink().head();
        getSink().title();
        getSink().text("Mailet and Matchers Reference");
        getSink().title_();
        getSink().head_();

        getSink().body();

        List matchers = (List) CollectionUtils.select(descriptors,
                new TypePredicate(MailetMatcherDescriptor.TYPE_MATCHER));
        List mailets = (List) CollectionUtils.select(descriptors,
                new TypePredicate(MailetMatcherDescriptor.TYPE_MAILET));
        
        getSink().section1();
        getSink().sectionTitle1();
        getSink().text("Mailets and Matchers Reference");
        getSink().sectionTitle1_();
        getSink().section1_();
        
        if (matchers.size() > 0 && mailets.size() > 0) {
            getSink().table();
            getSink().tableRow();
            getSink().tableCell();
        }
        if (matchers.size() > 0) {
            outputDescriptorIndex(matchers, "Matchers");
        }
        if (matchers.size() > 0 && mailets.size() > 0) {
            getSink().tableCell_();
            getSink().tableCell();
        }
        if (mailets.size() > 0) {
            outputDescriptorIndex(mailets, "Mailets");
        }
        if (matchers.size() > 0 && mailets.size() > 0) {
            getSink().tableCell_();
            getSink().tableRow_();
            getSink().table_();
        }

        if (matchers.size() > 0) {
            outputDescriptorList(matchers, "Matchers");
        }
        if (mailets.size() > 0) {
            outputDescriptorList(mailets, "Mailets");
        }

        getSink().body_();

        getSink().flush();
        getSink().close();

        /*
         * for (Iterator i = getProject().getCompileSourceRoots().iterator();
         * i.hasNext(); ) { String folder = (String) i.next(); DirectoryScanner
         * ds = new DirectoryScanner(); ds.setBasedir(folder);
         * ds.addDefaultExcludes(); ds.setIncludes(new
         * String[]{"**"+"/"+"*.java"}); ds.scan(); for (int k = 0; k <
         * ds.getIncludedFiles().length; k++) { getLog().info("include:
         * "+ds.getIncludedFiles()[k]); } }
         */

    }

    private void outputDescriptorIndex(List descriptors, String title) {
        getSink().section2();
        getSink().sectionTitle2();
        getSink().text(title);
        getSink().sectionTitle2_();

        getSink().list();
        for (int i = 0; i < descriptors.size(); i++) {
            getSink().listItem();
            getSink().link(((MailetMatcherDescriptor) descriptors.get(i)).getName());
            getSink().text(
                    ((MailetMatcherDescriptor) descriptors.get(i)).getName());
            getSink().link_();
            getSink().listItem_();
        }
        getSink().list_();

        getSink().section2_();
    }

    private void outputDescriptorList(List descriptors, String title) {
        getSink().section1();
        getSink().sectionTitle1();
        getSink().text(title);
        getSink().sectionTitle1_();

        for (int i = 0; i < descriptors.size(); i++) {
            getSink().section2();

            getSink().sectionTitle2();
            getSink().anchor(((MailetMatcherDescriptor) descriptors.get(i)).getName());
            getSink().text(
                    ((MailetMatcherDescriptor) descriptors.get(i)).getName());
            getSink().anchor_();
            getSink().sectionTitle2_();

            if (((MailetMatcherDescriptor) descriptors.get(i)).getInfo() != null) {
              getSink().paragraph();
	            if (((MailetMatcherDescriptor) descriptors.get(i)).getType() == MailetMatcherDescriptor.TYPE_MAILET) {
	                getSink().text("Mailet Info: ");
	            } else if (((MailetMatcherDescriptor) descriptors.get(i)).getType() == MailetMatcherDescriptor.TYPE_MATCHER) {
	                getSink().text("Matcher Info: ");
	            } else {
	                getSink().text("Info: ");
	            }
	            getSink().bold();
	            getSink().text(
	                    ((MailetMatcherDescriptor) descriptors.get(i)).getInfo());
	            getSink().bold_();
	            getSink().paragraph_();
            }

            getSink().paragraph();
            getSink().rawText(
                    ((MailetMatcherDescriptor) descriptors.get(i))
                            .getClassDocs());
            getSink().paragraph_();

            getSink().section2_();

        }

        getSink().section1_();
    }

    protected String getOutputDirectory() {
        return outputDirectory.getAbsolutePath();
    }

    /**
     * @return Returns the siteRenderer.
     */
    public Renderer getSiteRenderer() {
        return siteRenderer;
    }

    protected MavenProject getProject() {
        return project;
    }

    public String getDescription(Locale arg0) {
        return "Documentation about bundled mailets";
    }

    public String getName(Locale arg0) {
        return "Mailet Reference";
    }

    public String getOutputName() {
        return "mailet-report";
    }

    /**
     * @param siteRenderer
     *                The siteRenderer to set.
     */
    public void setSiteRenderer(Renderer siteRenderer) {
        this.siteRenderer = siteRenderer;
    }

    /**
     * For testing purpose only.
     * 
     * @param project
     *                The project to set.
     */
    public void setProject(MavenProject project) {
        this.project = project;
    }

}
