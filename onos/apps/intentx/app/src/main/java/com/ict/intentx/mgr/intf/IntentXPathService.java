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

package com.ict.intentx.mgr.intf;

import org.onosproject.net.ElementId;
import org.onosproject.net.Path;
import org.onosproject.net.topology.LinkWeigher;


import java.util.Set;

/**
 * description TODO.
 *
 * @author HuangKai, njnu_cs_hk@qq.com
 * @version 1.0
 * @since 19-3-13 下午2:39
 */
public interface IntentXPathService {

    /**
     * Returns the set of all shortest paths between the specified source and
     * destination network elements.  The path is computed using the supplied
     * edge-weight function.
     *
     * @param src     source element
     * @param dst     destination element
     * @param weigher edge-weight entity
     * @return set of all shortest paths between the two element
     */
    Set<Path> getWayPointPaths(ElementId src, ElementId dst, LinkWeigher weigher);
}
