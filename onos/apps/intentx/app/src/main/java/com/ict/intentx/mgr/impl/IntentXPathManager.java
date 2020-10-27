/*
 * Copyright 2019-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ict.intentx.mgr.impl;

import com.ict.intentx.mgr.intf.IntentXPathService;
import com.ict.intentx.mgr.intf.IntentXTopologyService;
import org.onosproject.net.ElementId;
import org.onosproject.net.Path;
import org.onosproject.net.host.HostService;
import org.onosproject.net.topology.LinkWeigher;
import org.onosproject.net.topology.PathService;
import org.onosproject.net.topology.TopologyService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;

import java.util.Set;

import static org.onosproject.security.AppGuard.checkPermission;
import static org.onosproject.security.AppPermission.Type.TOPOLOGY_READ;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * description TODO.
 *
 * @author HuangKai, njnu_cs_hk@qq.com
 * @version 1.0
 * @since 19-3-12 下午10:43
 */
@Component(immediate = true, service = IntentXPathService.class)
public class IntentXPathManager extends IntentXAbstractPathService
        implements IntentXPathService {
    private final Logger log = getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected IntentXTopologyService intentXTopologyService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected TopologyService topologyService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PathService pathService;


    @Activate
    public void activate() {
        // initialize AbstractPathService
        super.topologyService = this.topologyService;
        super.intentXTopologyService = this.intentXTopologyService;
        super.hostService = this.hostService;
        super.pathService = this.pathService;
        log.info("Started");
    }

    @Deactivate
    public void deactivate() {
        log.info("Stopped");
    }

    @Override
    public Set<Path> getWayPointPaths(ElementId src, ElementId dst, LinkWeigher weigher) {
        checkPermission(TOPOLOGY_READ);
        return super.getWayPointPaths(src, dst, weigher);
    }
}
