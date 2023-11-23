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

import static java.util.Arrays.asList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnrichedBody extends GenericBody {
  private final EncodingType encoding;
  private final FormatType format;
  private final CompressionType compression;
  private final String dataStore;
  private final String dataRef;
  private final Object data;

  public EnrichedBody(
      @JsonProperty("encoding") EncodingType encoding, // text, binary
      @JsonProperty("format") FormatType format,
      @JsonProperty("compression") CompressionType compression, // none, brotli, gzip, deflate
      @JsonProperty("dataStore")
          String dataStore, // none, file, db - validate and needs to be templatable
      @JsonProperty("dataRef") String dataRef, // and needs to be templatable
      @JsonProperty("data") Object data) {
    // default the encoding to text if not specified or not valid
    this.encoding = asList(EncodingType.values()).contains(encoding) ? encoding : EncodingType.TEXT;
    // default the format to text if not specified or not valid
    this.format = asList(FormatType.values()).contains(format) ? format : FormatType.TEXT;
    // default the compression to none if not specified or not valid
    this.compression =
        asList(CompressionType.values()).contains(compression) ? compression : CompressionType.NONE;
    this.dataStore = dataStore;
    this.dataRef = dataRef;
    this.data = data;
  }

  public EncodingType getEncoding() {
    return encoding;
  }

  public FormatType getFormat() {
    return format;
  }

  public CompressionType getCompression() {
    return compression;
  }

  public String getDataStore() {
    return dataStore;
  }

  public String getDataRef() {
    return dataRef;
  }

  public Object getData() {
    return data;
  }
}
