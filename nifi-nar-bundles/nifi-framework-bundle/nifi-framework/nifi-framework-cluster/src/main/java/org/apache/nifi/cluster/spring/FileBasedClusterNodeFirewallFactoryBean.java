/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.cluster.spring;

import java.io.File;

import org.apache.nifi.cluster.firewall.ClusterNodeFirewall;
import org.apache.nifi.cluster.firewall.impl.FileBasedClusterNodeFirewall;
import org.apache.nifi.util.NiFiProperties;
import org.springframework.beans.factory.FactoryBean;

/**
 * Factory bean for creating a singleton FileBasedClusterNodeFirewall instance.
 */
public class FileBasedClusterNodeFirewallFactoryBean implements FactoryBean<ClusterNodeFirewall> {

    private ClusterNodeFirewall firewall;

    private NiFiProperties properties;

    @Override
    public ClusterNodeFirewall getObject() throws Exception {
        if (firewall == null) {
            final File config = properties.getClusterNodeFirewallFile();
            final File restoreDirectory = properties.getRestoreDirectory();
            if (config == null) {
                firewall = new PermitAllClusterNodeFirewall();
            } else {
                firewall = new FileBasedClusterNodeFirewall(config, restoreDirectory);
            }
        }
        return firewall;
    }

    @Override
    public Class<ClusterNodeFirewall> getObjectType() {
        return ClusterNodeFirewall.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setProperties(NiFiProperties properties) {
        this.properties = properties;
    }

    private static class PermitAllClusterNodeFirewall implements ClusterNodeFirewall {

        @Override
        public boolean isPermissible(final String hostOrIp) {
            return true;
        }
    }
}
