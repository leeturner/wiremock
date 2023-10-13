/*
 * Copyright (C) 2014-2023 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tomakehurst.wiremock.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnrichedResponseBody implements ResponseBody {
    private final String encoding;
    private final String format;
    private final String compression;
    private final String dataStore;
    private final String dataRef;
    private final String data;

    public EnrichedResponseBody(
            @JsonProperty("encoding") String encoding,
            @JsonProperty("format") String format,
            @JsonProperty("compression") String compression,
            @JsonProperty("dataStore") String dataStore,
            @JsonProperty("dataRef") String dataRef,
            @JsonProperty("data") String data) {
        this.encoding = encoding;
        this.format = format;
        this.compression = compression;
        this.dataStore = dataStore;
        this.dataRef = dataRef;
        this.data = data;
    }

    public String getEncoding() {
        return encoding;
    }
    
    public String getFormat() {
        return format;
    }
    
    public String getCompression() {
        return compression;
    }
    
    public String getDataStore() {
        return dataStore;
    }
    
    public String getDataRef() {
        return dataRef;
    }
    
    public String getData() {
        return data;
    }
    
}
