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


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ict.intentx.mgr.intf.IntentXService;
import com.ict.intentx.mgr.intf.IntentXPathService;
import com.ict.intentx.mgr.intf.IntentXTopologyService;
import org.onlab.graph.Weight;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DefaultDisjointPath;
import org.onosproject.net.DefaultEdgeLink;
import org.onosproject.net.DefaultPath;
import org.onosproject.net.DeviceId;
import org.onosproject.net.DisjointPath;
import org.onosproject.net.EdgeLink;
import org.onosproject.net.ElementId;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.HostLocation;
import org.onosproject.net.Link;
import org.onosproject.net.Path;
import org.onosproject.net.PortNumber;
import org.onosproject.net.host.HostService;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.topology.DefaultTopologyEdge;
import org.onosproject.net.topology.HopCountLinkWeigher;
import org.onosproject.net.topology.LinkWeigher;
import org.onosproject.net.topology.PathService;
import org.onosproject.net.topology.Topology;
import org.onosproject.net.topology.TopologyService;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * description TODO.
 *
 * @author HuangKai, njnu_cs_hk@qq.com
 * @version 1.0
 * @since 19-3-12 下午10:37
 */
public class IntentXAbstractPathService implements IntentXPathService {
    private static final String ELEMENT_ID_NULL = "Element ID cannot be null";
    private static final String TOPOLOGY_NULL = "Topology cannot be null";
    private static final String DEVICE_ID_NULL = "Device ID cannot be null";
    private static final String LINK_WEIGHT_NULL = "Link weight cannot be null";

    private static final EdgeLink NOT_HOST = new NotHost();

    //private static final ProviderId PID = new ProviderId("intent-extend", "org.onosproject.intentextend");
    private static final ProviderId PID = IntentXService.INTENT_EXTEND_PROVIDER_ID;
    private static final PortNumber P0 = PortNumber.portNumber(0);

    protected static final LinkWeigher DEFAULT_WEIGHER =
            new HopCountLinkWeigher();

    protected IntentXTopologyService intentXTopologyService;
    protected TopologyService topologyService;
    protected HostService hostService;
    protected PathService pathService;

    @Override
    public Set<Path> getWayPointPaths(ElementId src, ElementId dst, LinkWeigher weigher) {
        checkNotNull(src, ELEMENT_ID_NULL);
        checkNotNull(dst, ELEMENT_ID_NULL);

        LinkWeigher internalWeigher = weigher != null ? weigher : DEFAULT_WEIGHER;

        // Get the source and destination edge locations
        EdgeLink srcEdge = getEdgeLink(src, true);
        EdgeLink dstEdge = getEdgeLink(dst, false);

        // If either edge is null, bail with no paths.
        if (srcEdge == null || dstEdge == null) {
            return ImmutableSet.of();
        }

        DeviceId srcDevice = srcEdge != NOT_HOST ? srcEdge.dst().deviceId() : (DeviceId) src;
        DeviceId dstDevice = dstEdge != NOT_HOST ? dstEdge.src().deviceId() : (DeviceId) dst;

        // If the source and destination are on the same edge device, there
        // is just one path, so build it and return it.
        if (srcDevice.equals(dstDevice)) {
            return edgeToEdgePaths(srcEdge, dstEdge, internalWeigher);
        }

        // Otherwise get all paths between the source and destination edge
        // devices.
        Topology topology = topologyService.currentTopology();
        Set<Path> paths = intentXTopologyService.getWayPointPaths(topology, srcDevice,
                dstDevice, internalWeigher);

        return edgeToEdgePaths(srcEdge, dstEdge, paths, internalWeigher);
        //return pathService.getPaths(src, dst, weigher);
    }



    // Finds the host edge link if the element ID is a host id of an existing
    // host. Otherwise, if the host does not exist, it returns null and if
    // the element ID is not a host ID, returns NOT_HOST edge link.
    private EdgeLink getEdgeLink(ElementId elementId, boolean isIngress) {
        if (elementId instanceof HostId) {
            // Resolve the host, return null.
            Host host = hostService.getHost((HostId) elementId);
            if (host == null) {
                return null;
            }
            return new DefaultEdgeLink(PID, new ConnectPoint(elementId, P0),
                    host.location(), isIngress);
        }
        return NOT_HOST;
    }

    // Produces a set of edge-to-edge paths using the set of infrastructure
    // paths and the given edge links.
    private Set<Path> edgeToEdgePaths(EdgeLink srcLink, EdgeLink dstLink, LinkWeigher weigher) {
        Set<Path> endToEndPaths = Sets.newHashSetWithExpectedSize(1);
        endToEndPaths.add(edgeToEdgePath(srcLink, dstLink, null, weigher));
        return endToEndPaths;
    }

    // Produces a set of edge-to-edge paths using the set of infrastructure
    // paths and the given edge links.
    private Set<Path> edgeToEdgePaths(EdgeLink srcLink, EdgeLink dstLink, Set<Path> paths,
                                      LinkWeigher weigher) {
        Set<Path> endToEndPaths = Sets.newHashSetWithExpectedSize(paths.size());
        for (Path path : paths) {
            endToEndPaths.add(edgeToEdgePath(srcLink, dstLink, path, weigher));
        }
        return endToEndPaths;
    }

    // Produces a direct edge-to-edge path.
    private Path edgeToEdgePath(EdgeLink srcLink, EdgeLink dstLink, Path path, LinkWeigher weigher) {
        List<Link> links = Lists.newArrayListWithCapacity(2);
        Weight cost = weigher.getInitialWeight();

        // Add source and destination edge links only if they are real and
        // add the infrastructure path only if it is not null.
        if (srcLink != NOT_HOST) {
            links.add(srcLink);
            cost = cost.merge(weigher.weight(new DefaultTopologyEdge(null, null, srcLink)));
        }
        if (path != null) {
            links.addAll(path.links());
            cost = cost.merge(path.weight());
        }
        if (dstLink != NOT_HOST) {
            links.add(dstLink);
            cost = cost.merge(weigher.weight(new DefaultTopologyEdge(null, null, dstLink)));
        }
        return new DefaultPath(PID, links, cost);
    }

    // Produces a direct edge-to-edge path.
    private DisjointPath edgeToEdgePathD(EdgeLink srcLink, EdgeLink dstLink, DisjointPath path,
                                         LinkWeigher weigher) {
        Path primary = null;
        Path backup = null;
        if (path != null) {
            primary = path.primary();
            backup = path.backup();
        }
        if (backup == null) {
            return new DefaultDisjointPath(PID,
                    (DefaultPath) edgeToEdgePath(srcLink, dstLink, primary, weigher));
        }
        return new DefaultDisjointPath(PID,
                (DefaultPath) edgeToEdgePath(srcLink, dstLink, primary, weigher),
                (DefaultPath) edgeToEdgePath(srcLink, dstLink, backup, weigher));
    }


    // Special value for edge link to represent that this is really not an
    // edge link since the src or dst are really an infrastructure device.
    private static class NotHost extends DefaultEdgeLink implements EdgeLink {
        NotHost() {
            super(PID, new ConnectPoint(HostId.NONE, P0),
                    new HostLocation(DeviceId.NONE, P0, 0L), false);
        }
    }
}
