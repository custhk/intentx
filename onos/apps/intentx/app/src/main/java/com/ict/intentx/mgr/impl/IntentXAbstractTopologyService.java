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
import com.ict.intentx.mgr.intf.LinkWeigherX;
import com.ict.intentx.mgr.intf.IntentXService;
import com.ict.intentx.mgr.intf.IntentXTopologyService;
import com.ict.intentx.utils.graph.WayPointGraphSearch;
import org.onlab.graph.DijkstraGraphSearch;
import org.onlab.graph.GraphPathSearch;
import org.onlab.graph.KShortestPathsSearch;
import org.onlab.graph.LazyKShortestPathsSearch;
import org.onlab.graph.SuurballeGraphSearch;
import org.onlab.graph.TarjanGraphSearch;
import org.onosproject.common.DefaultTopology;
import org.onosproject.net.DefaultPath;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.net.Path;
import org.onosproject.net.intent.Constraint;
import org.onosproject.net.intent.constraint.WaypointConstraint;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.topology.DefaultTopologyVertex;
import org.onosproject.net.topology.LinkWeigher;
import org.onosproject.net.topology.Topology;
import org.onosproject.net.topology.TopologyEdge;
import org.onosproject.net.topology.TopologyGraph;
import org.onosproject.net.topology.TopologyVertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.onlab.graph.GraphPathSearch.ALL_PATHS;

/**
 * description TODO.
 *
 * @author HuangKai, njnu_cs_hk@qq.com
 * @version 1.0
 * @since 19-3-13 下午3:03
 */
public class IntentXAbstractTopologyService implements IntentXTopologyService {

    private static final ProviderId PID = IntentXService.INTENT_EXTEND_PROVIDER_ID;

    private static final WayPointGraphSearch<TopologyVertex, TopologyEdge> WAY_POINT_GRAPH_SEARCH =
            new WayPointGraphSearch<>();
    private static final DijkstraGraphSearch<TopologyVertex, TopologyEdge> DIJKSTRA =
            new DijkstraGraphSearch<>();
    private static final TarjanGraphSearch<TopologyVertex, TopologyEdge> TARJAN =
            new TarjanGraphSearch<>();
    private static final SuurballeGraphSearch<TopologyVertex, TopologyEdge> SUURBALLE =
            new SuurballeGraphSearch<>();
    private static final KShortestPathsSearch<TopologyVertex, TopologyEdge> KSHORTEST =
            new KShortestPathsSearch<>();
    private static final LazyKShortestPathsSearch<TopologyVertex, TopologyEdge> LAZY_KSHORTEST =
            new LazyKShortestPathsSearch<>();

    @Override
    public Set<Path> getWayPointPaths(Topology topology, DeviceId src, DeviceId dst, LinkWeigher weigher) {
        return getWayPointPaths(defaultTopology(topology), src, dst, weigher, ALL_PATHS);

    }

    private Set<Path> getWayPointPaths(DefaultTopology defaultTopology, DeviceId src, DeviceId dst, LinkWeigher weigher,
                                       int maxPaths) {
        DefaultTopologyVertex srcV = new DefaultTopologyVertex(src);
        DefaultTopologyVertex dstV = new DefaultTopologyVertex(dst);
        TopologyGraph graph = defaultTopology.getGraph();
        Set<TopologyVertex> vertices = graph.getVertexes();
        if (!vertices.contains(srcV) || !vertices.contains(dstV)) {
            // src or dst not part of the current graph
            return ImmutableSet.of();
        }
        ImmutableSet.Builder<Path> builder = ImmutableSet.builder();
        GraphPathSearch.Result<TopologyVertex, TopologyEdge> result;
        Map<TopologyVertex, Integer> wayPoints = new HashMap<>();
        // start form 1
        int index = 1;
        wayPoints.put(srcV, index);
        index++;
        // if my extend or hopcount
        if (weigher instanceof LinkWeigherX) {
            LinkWeigherX linkWeigherX = (LinkWeigherX) weigher;
            //check if waypoint exits
            for (Constraint constraint : linkWeigherX.getConstraints()) {
                // find WaypointConstraint
                if (constraint instanceof WaypointConstraint) {
                    for (DeviceId deviceId:((WaypointConstraint) constraint).waypoints()) {
                        DefaultTopologyVertex wayPoint = new DefaultTopologyVertex(deviceId);
                        if (!vertices.contains(wayPoint)) {
                            return ImmutableSet.of();
                        }
                        wayPoints.put(wayPoint, index);
                        index++;
                    }
                }
            }
            wayPoints.put(dstV, index);
            result = WAY_POINT_GRAPH_SEARCH.search(graph, srcV, dstV, weigher, wayPoints, maxPaths);
        } else {
            result = DIJKSTRA.search(graph, srcV, dstV, weigher, maxPaths);
        }

        for (org.onlab.graph.Path<TopologyVertex, TopologyEdge> path : result.paths()) {
            builder.add(networkPath(path));
        }
        return builder.build();
    }
    // Validates the specified topology and returns it as a default
    private DefaultTopology defaultTopology(Topology topology) {
        checkArgument(topology instanceof DefaultTopology,
                "Topology class %s not supported", topology.getClass());
        return (DefaultTopology) topology;
    }

    // Converts graph path to a network path with the same cost.
    private Path networkPath(org.onlab.graph.Path<TopologyVertex, TopologyEdge> path) {
        List<Link> links = path.edges().stream().map(TopologyEdge::link)
                .collect(Collectors.toList());
        return new DefaultPath(PID, links, path.cost());
    }


}
