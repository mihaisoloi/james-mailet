package org.apache.mailet;

import java.util.Iterator;
import org.apache.mailet.Matcher;

/**
 * A CompositeMatcher contains child matchers that are invoked in turn and their
 * recipient results are composed from the composite operation. See And, Or, Xor
 * and Not. One or more children may be supplied to a composite via declaration
 * inside a processor in the james-config.xml file. When the composite is the
 * outter-most declaration it must be named, as in the example below. The
 * composite matcher may be referenced by name and used in a subsequent mailet.
 * Any matcher may be included as a child of a composite matcher, including
 * another composite matcher or the Not matcher. As a consequence, the class
 * names: And, Or, Not and Xor are permanently reserved.
 * 
 * <pre>
 *   &lt;matcher name=&quot;a-composite&quot; match=&quot;Or&quot;&gt;
 *      &lt;matcher match=&quot;And&quot;&gt;
 *          &lt;matcher match=&quot;Not&quot;&gt;
 *              &lt;matcher match=&quot;HostIs=65.55.116.84&quot;/&gt;
 *          &lt;/matcher&gt;
 *          &lt;matcher match=&quot;HasHeaderWithRegex=X-Verify-SMTP,Host(.*)sending to us was not listening&quot;/&gt;
 *      &lt;/matcher&gt;
 *      &lt;matchwe match=&quot;HasHeaderWithRegex=X-DNS-Paranoid,(.*)&quot;/&gt;
 *   &lt;/matcher&gt;
 *   
 *   &lt;mailet match=&quot;a-composite&quot; class=&quot;ToProcessor&quot;&gt;
 *       &lt;processor&gt;spam&lt;/processor&gt;
 *   &lt;/mailet&gt;
 * *
 * </pre>
 * 
 */
public interface CompositeMatcher extends Matcher {

    /**
     * @return Iterator if child Matchers
     */
    public Iterator iterator();

    /**
     * Add a child matcher to this composite matcher. This is called by
     * SpoolManager.setupMatcher()
     * 
     * @param matcher
     *            Matcher is the child that this composite treats.
     */
    public void add(Matcher matcher);

}
