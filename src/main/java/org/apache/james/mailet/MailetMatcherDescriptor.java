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

/**
 * Simple bean to describe a mailet or a matcher
 */
public class MailetMatcherDescriptor {
    
    public final static int TYPE_MAILET = 1;

    public final static int TYPE_MATCHER = 2;

    private String fqName;

    private String name;

    private String info;

    private String classDocs;

    private int type;

    public String getFullyQualifiedName() {
        return fqName;
    }

    public void setFullyQualifiedName(String fqName) {
        this.fqName = fqName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getClassDocs() {
        return classDocs;
    }

    public void setClassDocs(String classDocs) {
        this.classDocs = classDocs;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}