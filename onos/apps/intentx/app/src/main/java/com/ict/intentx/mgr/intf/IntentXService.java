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

import org.onosproject.net.provider.ProviderId;

/**
 * description TODO.
 *
 * @author HuangKai, njnu_cs_hk@qq.com
 * @version 1.0
 * @since 19-3-13 下午3:57
 */
public interface IntentXService {

    String INTENT_EXTEND_APP_NAME = "org.onosproject.core";
    ProviderId INTENT_EXTEND_PROVIDER_ID = new ProviderId("core", "org.onosproject.core");
    ProviderId INTENT_EXTEND_PROVIDER_IDA = new ProviderId("core", "org.onosproject.core", true);
    void extendIntent();
}
