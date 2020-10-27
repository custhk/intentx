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

package com.ict.intentx.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.rest.AbstractWebResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * description TODO.
 *
 * @author HuangKai, njnu_cs_hk@qq.com
 * @version 1.0
 * @since 19-3-12 下午8:13
 */
@Path("intentx")
public class IntentXWebResource extends AbstractWebResource {
    /**
     * Hello world.
     * REST API:
     * http://localhost:8181/onos/v1/intentx/intentx/hello
     *
     * @return Hello world
     */
    @GET
    @Path("hello")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello() {
        ObjectNode root = mapper().createObjectNode();
        root.put("Hello", "HK");
        return ok(root.toString()).build();
    }

    /**
     * Submits a new intent.
     * Creates and submits intent from the JSON request.
     * @param type intent type
     * @param stream input JSON
     * @return status of the request - CREATED if the JSON is correct,
     * BAD_REQUEST if the JSON is invalid
     *
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("createIntent/{type}")
    public Response createPointToPointIntent(@PathParam("type") String type,
                                             InputStream stream) {
        ObjectNode root = mapper().createObjectNode();

        root.put("Hello", "HK");
        return ok(root.toString()).build();
    }
}
