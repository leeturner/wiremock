/*
 * Copyright (C) 2012-2023 Thomas Akehurst
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
package com.github.tomakehurst.wiremock.stubbing;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;
import static com.github.tomakehurst.wiremock.http.HttpHeader.httpHeader;
import static com.github.tomakehurst.wiremock.http.ResponseDefinition.copyOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.CompressionType;
import com.github.tomakehurst.wiremock.http.EncodingType;
import com.github.tomakehurst.wiremock.http.EnrichedBody;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.http.FormatType;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;

public class ResponseDefinitionTest {

  @Test
  public void copyProducesEqualObject() {
    ResponseDefinition response =
        new ResponseDefinition(
            222,
            null,
            "blah",
            null,
            null,
            "name.json",
            new HttpHeaders(httpHeader("thing", "thingvalue")),
            null,
            1112,
            null,
            null,
            "http://base.com",
            null,
            Fault.EMPTY_RESPONSE,
            List.of("transformer-1"),
            Parameters.one("name", "Jeff"),
            true);

    ResponseDefinition copiedResponse = copyOf(response);

    assertTrue(response.equals(copiedResponse));
  }

  @Test
  public void copyPreservesConfiguredFlag() {
    ResponseDefinition response = ResponseDefinition.notConfigured();
    ResponseDefinition copiedResponse = copyOf(response);
    assertFalse(copiedResponse.wasConfigured(), "Should be not configured");
  }

  private static final String STRING_BODY =
      "{	        								\n"
          + "		\"status\": 200,    				\n"
          + "		\"body\": \"String content\" 		\n"
          + "}											";

  @Test
  public void correctlyUnmarshalsFromJsonWhenBodyIsAString() {
    ResponseDefinition responseDef = Json.read(STRING_BODY, ResponseDefinition.class);
    assertThat(responseDef.getBase64Body(), is(nullValue()));
    assertThat(responseDef.getJsonBody(), is(nullValue()));
    assertThat(responseDef.getBody(), is("String content"));
  }

  private static final String JSON_BODY =
      "{	        								\n"
          + "		\"status\": 200,    				\n"
          + "		\"jsonBody\": {\"name\":\"wirmock\",\"isCool\":true} \n"
          + "}											";

  @Test
  public void correctlyUnmarshalsFromJsonWhenBodyIsJson() {
    ResponseDefinition responseDef = Json.read(JSON_BODY, ResponseDefinition.class);
    assertThat(responseDef.getBase64Body(), is(nullValue()));
    assertThat(responseDef.getBody(), is(nullValue()));

    JsonNode jsonNode = Json.node("{\"name\":\"wirmock\",\"isCool\":true}");
    assertThat(responseDef.getJsonBody(), is(jsonNode));
  }

  private static final String ENRICHED_BODY_WITH_STRING_DATA =
      "{   							        	\n"
          + "		\"status\": 200,   			\n"
          + "		\"body\": {          		\n"
          + "			\"encoding\": \"text\",	\n"
          + "			\"format\": \"text\",	\n"
          + "			\"compression\": \"gzip\",	\n"
          + "			\"dataStore\": \"files\",	\n"
          + "			\"dataRef\": \"/path/to/my.data.json\",	\n"
          + "			\"data\": \"My Response Data\"	\n"
          + "       } 	\n"
          + "}	";

  @Test
  public void correctlyUnmarshallsFromEnrichedBodyWithStringData() {
    ResponseDefinition responseDef =
        Json.read(ENRICHED_BODY_WITH_STRING_DATA, ResponseDefinition.class);
    assertThat(responseDef.getBase64Body(), is(nullValue()));
    assertThat(responseDef.getJsonBody(), is(nullValue()));
    assertThat(responseDef.getBody(), is("My Response Data"));
  }

  private static final String ENRICHED_BODY_WITH_JSON_DATA =
      "{   							        	\n"
          + "		\"status\": 200,   			\n"
          + "		\"body\": {          		\n"
          + "			\"encoding\": \"text\",	\n"
          + "			\"format\": \"json\",	\n"
          + "			\"compression\": \"gzip\",	\n"
          + "			\"dataStore\": \"files\",	\n"
          + "			\"dataRef\": \"/path/to/my.data.json\",	\n"
          + "			\"data\": {\"name\":\"wiremock\",\"isCool\":true}	\n"
          + "       } 	\n"
          + "}	";

  @Test
  public void correctlyUnmarshallsFromEnrichedBodyWithJsonData() {
    ResponseDefinition responseDef =
        Json.read(ENRICHED_BODY_WITH_JSON_DATA, ResponseDefinition.class);
    assertThat(responseDef.getBase64Body(), is(nullValue()));
    JsonNode jsonNode = Json.node("{\"name\":\"wiremock\",\"isCool\":true}");
    assertThat(responseDef.getJsonBody(), is(jsonNode));
    assertThat(responseDef.getBody(), is(nullValue()));
  }

  private static final String ENRICHED_BODY_WITH_NULL_DATA =
      "{   							        	\n"
          + "		\"status\": 200,   			\n"
          + "		\"body\": {          		\n"
          + "			\"encoding\": \"text\",	\n"
          + "			\"format\": \"text\",	\n"
          + "			\"compression\": \"gzip\",	\n"
          + "			\"dataStore\": \"files\",	\n"
          + "			\"dataRef\": \"/path/to/my.data.json\"	\n"
          + "       } 	\n"
          + "}	";

  @Test
  public void correctlyUnmarshallsFromEnrichedBodyWithNullData() {
    ResponseDefinition responseDef =
        Json.read(ENRICHED_BODY_WITH_NULL_DATA, ResponseDefinition.class);
    assertThat(responseDef.getBase64Body(), is(nullValue()));
    assertThat(responseDef.getJsonBody(), is(nullValue()));
    assertThat(responseDef.getBody(), is(nullValue()));
  }

  private static final String ENRICHED_BODY_ENCODING_TEMPLATE =
      "{   							        	\n"
          + "		\"status\": 200,   			\n"
          + "		\"body\": {          		\n"
          + "			\"encoding\": \"{{encoding}}\",	\n"
          + "			\"format\": \"text\",	\n"
          + "			\"compression\": \"gzip\",	\n"
          + "			\"dataStore\": \"files\",	\n"
          + "			\"dataRef\": \"/path/to/my.data.json\"	\n"
          + "       } 	\n"
          + "}	";

  @ParameterizedTest
  @ValueSource(strings = {"text", "binary", "multipart"})
  public void correctlyUnmarshallsEncodingTypeFromEnrichedBody(String encoding) {
    String json = ENRICHED_BODY_ENCODING_TEMPLATE.replace("{{encoding}}", encoding);
    EncodingType expectedEncodingType = EncodingType.fromString(encoding);

    ResponseDefinition responseDef = Json.read(json, ResponseDefinition.class);

    assertThat(responseDef.getEnrichedBody(), isA(EnrichedBody.class));
    var enrichedBody = (EnrichedBody) responseDef.getEnrichedBody();
    assertThat(enrichedBody.getEncoding(), is(in(EncodingType.values())));
    assertThat(enrichedBody.getEncoding(), is(expectedEncodingType));
  }

  @ParameterizedTest
  @ValueSource(strings = {"unknown", "  "})
  public void correctlyUnmarshallsUnknownEncodingTypeFromEnrichedBody(String encoding) {
    String json = ENRICHED_BODY_ENCODING_TEMPLATE.replace("{{encoding}}", encoding);

    ResponseDefinition responseDef = Json.read(json, ResponseDefinition.class);

    assertThat(responseDef.getEnrichedBody(), isA(EnrichedBody.class));
    var enrichedBody = (EnrichedBody) responseDef.getEnrichedBody();
    assertThat(enrichedBody.getEncoding(), is(EncodingType.TEXT));
  }

  private static final String ENRICHED_BODY_WITH_NO_ENCODING =
      "{   							        	\n"
          + "		\"status\": 200,   			\n"
          + "		\"body\": {          		\n"
          + "			\"format\": \"text\",	\n"
          + "			\"compression\": \"gzip\",	\n"
          + "			\"dataStore\": \"files\",	\n"
          + "			\"dataRef\": \"/path/to/my.data.json\"	\n"
          + "       } 	\n"
          + "}	";

  @Test
  public void whenNoEncodingIsSuppliedTheDefaultOfTextIsAssigned() {
    ResponseDefinition responseDef =
        Json.read(ENRICHED_BODY_WITH_NO_ENCODING, ResponseDefinition.class);
    assertThat(responseDef.getEnrichedBody(), isA(EnrichedBody.class));
    assertThat(((EnrichedBody) responseDef.getEnrichedBody()).getEncoding(), is(EncodingType.TEXT));
  }

  private static final String ENRICHED_BODY_COMPRESSION_TEMPLATE =
      "{   							        	\n"
          + "		\"status\": 200,   			\n"
          + "		\"body\": {          		\n"
          + "           \"encoding\": \"text\",	\n"
          + "			\"format\": \"text\",	\n"
          + "			\"compression\": \"{{compression}}\",	\n"
          + "			\"dataStore\": \"files\",	\n"
          + "			\"dataRef\": \"/path/to/my.data.json\"	\n"
          + "       } 	\n"
          + "}	";

  @ParameterizedTest
  @ValueSource(strings = {"none", "brotli", "gzip", "deflate"})
  public void correctlyUnmarshallsCompressionTypeFromEnrichedBody(String compression) {
    String json = ENRICHED_BODY_COMPRESSION_TEMPLATE.replace("{{compression}}", compression);
    CompressionType expectedCompressionType = CompressionType.fromString(compression);

    ResponseDefinition responseDef = Json.read(json, ResponseDefinition.class);

    assertThat(responseDef.getEnrichedBody(), isA(EnrichedBody.class));
    var enrichedBody = (EnrichedBody) responseDef.getEnrichedBody();
    assertThat(enrichedBody.getCompression(), is(in(CompressionType.values())));
    assertThat(enrichedBody.getCompression(), is(expectedCompressionType));
  }

  @ParameterizedTest
  @ValueSource(strings = {"unknown", "  "})
  public void correctlyUnmarshallsUnknownCompressionTypeFromEnrichedBody(String compression) {
    String json = ENRICHED_BODY_COMPRESSION_TEMPLATE.replace("{{compression}}", compression);

    ResponseDefinition responseDef = Json.read(json, ResponseDefinition.class);

    assertThat(responseDef.getEnrichedBody(), isA(EnrichedBody.class));
    var enrichedBody = (EnrichedBody) responseDef.getEnrichedBody();
    assertThat(enrichedBody.getCompression(), is(CompressionType.NONE));
  }

  private static final String ENRICHED_BODY_WITH_NO_COMPRESSION =
      "{   							        	\n"
          + "		\"status\": 200,   			\n"
          + "		\"body\": {          		\n"
          + "           \"encoding\": \"text\",	\n"
          + "			\"format\": \"text\",	\n"
          + "			\"dataStore\": \"files\",	\n"
          + "			\"dataRef\": \"/path/to/my.data.json\"	\n"
          + "       } 	\n"
          + "}	";

  @Test
  public void whenNoCompressionIsSuppliedTheDefaultOfTextIsAssigned() {
    ResponseDefinition responseDef =
        Json.read(ENRICHED_BODY_WITH_NO_COMPRESSION, ResponseDefinition.class);
    assertThat(responseDef.getEnrichedBody(), isA(EnrichedBody.class));
    assertThat(
        ((EnrichedBody) responseDef.getEnrichedBody()).getCompression(), is(CompressionType.NONE));
  }

  private static final String ENRICHED_BODY_FORMAT_TEMPLATE =
      "{   							        	\n"
          + "		\"status\": 200,   			\n"
          + "		\"body\": {          		\n"
          + "           \"encoding\": \"text\",	\n"
          + "			\"format\": \"{{format}}\",	\n"
          + "			\"compression\": \"none\",	\n"
          + "			\"dataStore\": \"files\",	\n"
          + "			\"dataRef\": \"/path/to/my.data.json\"	\n"
          + "       } 	\n"
          + "}	";

  @ParameterizedTest
  @ValueSource(strings = {"text", "json", "xml", "html", "yaml", "csv"})
  public void correctlyUnmarshallsFormatTypeFromEnrichedBody(String format) {
    String json = ENRICHED_BODY_FORMAT_TEMPLATE.replace("{{format}}", format);
    FormatType expectedFormatType = FormatType.fromString(format);

    ResponseDefinition responseDef = Json.read(json, ResponseDefinition.class);

    assertThat(responseDef.getEnrichedBody(), isA(EnrichedBody.class));
    var enrichedBody = (EnrichedBody) responseDef.getEnrichedBody();
    assertThat(enrichedBody.getFormat(), is(in(FormatType.values())));
    assertThat(enrichedBody.getFormat(), is(expectedFormatType));
  }

  @ParameterizedTest
  @ValueSource(strings = {"unknown", "  "})
  public void correctlyUnmarshallsUnknownFormatTypeFromEnrichedBody(String format) {
    String json = ENRICHED_BODY_FORMAT_TEMPLATE.replace("{{format}}", format);

    ResponseDefinition responseDef = Json.read(json, ResponseDefinition.class);

    assertThat(responseDef.getEnrichedBody(), isA(EnrichedBody.class));
    var enrichedBody = (EnrichedBody) responseDef.getEnrichedBody();
    assertThat(enrichedBody.getFormat(), is(FormatType.TEXT));
  }

  private static final String ENRICHED_BODY_WITH_NO_FORMAT =
      "{   							        	\n"
          + "		\"status\": 200,   			\n"
          + "		\"body\": {          		\n"
          + "           \"encoding\": \"text\",	\n"
          + "			\"compression\": \"none\",	\n"
          + "			\"dataStore\": \"files\",	\n"
          + "			\"dataRef\": \"/path/to/my.data.json\"	\n"
          + "       } 	\n"
          + "}	";

  @Test
  public void whenNoFormatIsSuppliedTheDefaultOfTextIsAssigned() {
    ResponseDefinition responseDef =
        Json.read(ENRICHED_BODY_WITH_NO_FORMAT, ResponseDefinition.class);
    assertThat(responseDef.getEnrichedBody(), isA(EnrichedBody.class));
    assertThat(((EnrichedBody) responseDef.getEnrichedBody()).getFormat(), is(FormatType.TEXT));
  }

  @Test
  public void correctlyMarshalsToJsonWhenBodyIsAString() throws Exception {
    ResponseDefinition responseDef =
        responseDefinition().withStatus(200).withBody("String content").build();

    JSONAssert.assertEquals(STRING_BODY, Json.write(responseDef), false);
  }

  private static final byte[] BODY = new byte[] {1, 2, 3};
  private static final String BASE64_BODY = "AQID";
  private static final String BINARY_BODY =
      "{	        								        \n"
          + "		\"status\": 200,    				        \n"
          + "		\"base64Body\": \""
          + BASE64_BODY
          + "\"     \n"
          + "}											        ";

  @Test
  public void correctlyUnmarshalsFromJsonWhenBodyIsBinary() {
    ResponseDefinition responseDef = Json.read(BINARY_BODY, ResponseDefinition.class);
    assertThat(responseDef.getBody(), is(nullValue()));
    assertThat(responseDef.getByteBody(), is(BODY));
  }

  @Test
  public void correctlyMarshalsToJsonWhenBodyIsBinary() throws Exception {
    ResponseDefinition responseDef =
        responseDefinition().withStatus(200).withBase64Body(BASE64_BODY).build();

    String actualJson = Json.write(responseDef);
    JSONAssert.assertEquals(actualJson, BINARY_BODY, false);
  }

  @Test
  public void indicatesBodyFileIfBodyContentIsNotAlsoSpecified() {
    ResponseDefinition responseDefinition = responseDefinition().withBodyFile("my-file").build();

    assertTrue(responseDefinition.specifiesBodyFile());
    assertFalse(responseDefinition.specifiesBodyContent());
  }

  @Test
  public void doesNotIndicateBodyFileIfBodyContentIsAlsoSpecified() {
    ResponseDefinition responseDefinition =
        responseDefinition().withBodyFile("my-file").withBody("hello").build();

    assertFalse(responseDefinition.specifiesBodyFile());
    assertTrue(responseDefinition.specifiesBodyContent());
  }

  @Test
  public void omitsResponseTransformerAttributesFromJsonWhenEmpty() {
    String json = Json.write(new ResponseDefinition(200, ""));

    assertThat(json, not(containsString("transformers")));
    assertThat(json, not(containsString("transformerParameters")));
  }
}
