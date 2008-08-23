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

import org.apache.mailet.Mailet;
import org.apache.mailet.Matcher;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Looks for any class implementing Mailet or Matchers in the maven source trees.
 * Extract javadocs using QDox and extract MailetInfo informations instantiating
 * them.
 */
public class DefaultDescriptorsExtractor implements DescriptorsExtractor {

    /* (non-Javadoc)
     * @see org.apache.james.mailet.DescriptorsExtractor#extractDescriptors()
     */
    public List extractDescriptors(MavenProject project, Log log) {

        List res = new LinkedList();

        JavaDocBuilder builder = new JavaDocBuilder();
        for (Iterator i = project.getCompileSourceRoots().iterator(); i
                .hasNext();) {
            builder.addSourceTree(new File((String) i.next()));
        }
        JavaClass[] classes = builder.getClasses();

        URL[] urls = null;
        URLClassLoader classLoader = null;
        try {
            try {
                List cpes = project.getCompileClasspathElements();
                urls = new URL[cpes.size()];
                for (int k = 0; k < cpes.size(); k++) {
                    log.debug("CPE: " + cpes.get(k));
                    urls[k] = new File((String) cpes.get(k)).toURI().toURL();
                }
                classLoader = new URLClassLoader(urls);
            } catch (DependencyResolutionRequiredException e1) {
                log.error(e1);
            }
        } catch (MalformedURLException e) {
            log.error(e);
        }
        Set dependencies = project.getDependencyArtifacts();
        if (dependencies != null)
            for (Iterator i = dependencies.iterator(); i.hasNext();) {
                log.debug("DEP: " + i.next());
            }

        log
                .debug("OutDir: " + project.getBuild().getOutputDirectory());

        for (int i = 0; i < classes.length; i++) {
            log.debug("Class: " + classes[i].getFullyQualifiedName());
            try {
                Class klass = classLoader.loadClass(classes[i]
                        .getFullyQualifiedName());

                log.debug("Constr: " + klass.getConstructor(null));

                List zuper = getAllInterfaces(klass);
                Class mailetClass = classLoader.loadClass(Mailet.class
                        .getName());
                Class matcherClass = classLoader.loadClass(Matcher.class
                        .getName());
                if (zuper.contains(mailetClass)) {
                    Object m = klass.newInstance();
                    String mailetInfo = (String) klass.getMethod(
                            "getMailetInfo", null).invoke(m, null);
                    log.info("Found a Mailet: " + klass.getName());
                    MailetMatcherDescriptor mmdesc = new MailetMatcherDescriptor();
                    mmdesc.setName(classes[i].getName());
                    mmdesc.setFullyQualifiedName(classes[i]
                            .getFullyQualifiedName());
                    mmdesc.setType(MailetMatcherDescriptor.TYPE_MAILET);
                    if (mailetInfo != null && mailetInfo.length() > 0) {
                        mmdesc.setInfo(mailetInfo);
                    }
                    mmdesc.setClassDocs(classes[i].getComment());
                    res.add(mmdesc);

                } else if (zuper.contains(matcherClass)) {
                    Object m = klass.newInstance();
                    String matcherInfo = (String) klass.getMethod(
                            "getMatcherInfo", null).invoke(m, null);
                    log.info("Found a Matcher: " + klass.getName());
                    MailetMatcherDescriptor mmdesc = new MailetMatcherDescriptor();
                    mmdesc.setName(classes[i].getName());
                    mmdesc.setFullyQualifiedName(classes[i]
                            .getFullyQualifiedName());
                    mmdesc.setType(MailetMatcherDescriptor.TYPE_MATCHER);
                    if (matcherInfo != null && matcherInfo.length() > 0) {
                        mmdesc.setInfo(matcherInfo);
                    }
                    mmdesc.setClassDocs(classes[i].getComment());
                    res.add(mmdesc);
                } else if (zuper.size() > 0) {
                    for (int k = 0; k < zuper.size(); k++) {
                        log.debug("I: " + ((Class) zuper.get(k)).getName());
                    }
                } else {
                    log.debug("No interfaces for " + klass.getName());
                }

            } catch (ClassNotFoundException e) {
                log.error("NotFound: " + e.getMessage());
            } catch (InstantiationException e) {
                log.info("IE: " + e.getMessage()+" / Probably an abstract mailet/matcher: "+classes[i].getName());
            } catch (IllegalAccessException e) {
                log.error("IAE: " + e.getMessage());
            } catch (SecurityException e) {
                log.error("SE: " + e.getMessage());
            } catch (NoSuchMethodException e) {
                log.error("NSME: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                log.error("IAE2: " + e.getMessage());
            } catch (InvocationTargetException e) {
                log.error("ITE: " + e.getMessage());
            }

            List implementedInterfaces = getAllInterfacesQdox(classes[i]);
            for (int k = 0; k < implementedInterfaces.size(); k++) {
                log.info("I: " + implementedInterfaces.get(k));
            }

        }

        return res;
    }


    private List getAllInterfacesQdox(JavaClass javaClass) {
        List res = new LinkedList();
        if (javaClass.getImplementedInterfaces() != null) {
            JavaClass[] interfaces = javaClass.getImplementedInterfaces();
            for (int n = 0; n < interfaces.length; n++) {
                res.add(interfaces[n]);
            }
        }
        if (javaClass.getSuperJavaClass() != null) {
            res.addAll(getAllInterfacesQdox(javaClass.getSuperJavaClass()));
        }
        return res;
    }

    private List getAllInterfaces(Class klass) {
        List res = new LinkedList();
        if (klass.getInterfaces() != null) {
            Class[] interfaces = klass.getInterfaces();
            for (int n = 0; n < interfaces.length; n++) {
                res.add(interfaces[n]);
                // add also interfaces extensions
                res.addAll(getAllInterfaces(interfaces[n]));
            }
        }
        if (klass.getSuperclass() != null) {
            res.addAll(getAllInterfaces(klass.getSuperclass()));
        }
        return res;

    }

}
