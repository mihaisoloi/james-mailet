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


package org.apache.james.transport.matchers;

import junit.framework.TestCase;
import org.apache.mailet.base.test.MockMail;
import org.apache.mailet.base.test.MockMailContext;
import org.apache.mailet.base.test.MockMatcherConfig;
import org.apache.mailet.base.test.MailUtil;
import org.apache.mailet.Matcher;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.ParseException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

public class FetchedFromTest extends TestCase {

    private MimeMessage mockedMimeMessage;

    private MockMail mockedMail;

    private Matcher matcher;

    private final String HEADER_NAME = "X-fetched-from";

    private final String HEADER_VALUE = "james-user";

    private String headerName = "defaultHeaderName";

    private String headerValue = "defaultHeaderValue";

    public FetchedFromTest(String arg0) throws UnsupportedEncodingException {
        super(arg0);
    }

    private void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    private void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    private void setupMockedMimeMessage() throws MessagingException {
        mockedMimeMessage = MailUtil.createMimeMessage(headerName, headerValue);
    }

    private void setupMockedMail(MimeMessage m) throws ParseException {
        mockedMail = MailUtil.createMockMail2Recipients(m);
    }

    private void setupMatcher() throws MessagingException {
        setupMockedMimeMessage();
        matcher = new FetchedFrom();
        MockMatcherConfig mci = new MockMatcherConfig("FetchedFrom="
                + HEADER_VALUE, new MockMailContext());
        matcher.init(mci);
    }

    // test if the Header was matched
    public void testHeaderIsMatched() throws MessagingException {
        setHeaderName(HEADER_NAME);
        setHeaderValue(HEADER_VALUE);

        setupMockedMimeMessage();
        setupMockedMail(mockedMimeMessage);
        setupMatcher();

        Collection matchedRecipients = matcher.match(mockedMail);
        assertNotNull(matchedRecipients);
        assertEquals(matchedRecipients.size(), mockedMail.getRecipients()
                .size());
    }

    // test if the Header was not matched
    public void testHeaderIsNotMatched() throws MessagingException {
        setHeaderName(HEADER_NAME);
        setHeaderValue(headerValue);

        setupMockedMimeMessage();
        setupMockedMail(mockedMimeMessage);
        setupMatcher();

        Collection matchedRecipients = matcher.match(mockedMail);
        assertNull(matchedRecipients);
    }

    // test if the Header was removed after matched
    public void testHeaderWasRemovedAfterMatched() throws MessagingException {
        setHeaderName(HEADER_NAME);
        setHeaderValue(HEADER_VALUE);

        setupMockedMimeMessage();
        setupMockedMail(mockedMimeMessage);
        setupMatcher();

        Collection matchedRecipients = matcher.match(mockedMail);
        Collection matchedRecipients2 = matcher.match(mockedMail);

        assertNotNull(matchedRecipients);
        assertNull(matchedRecipients2);
    }
}
