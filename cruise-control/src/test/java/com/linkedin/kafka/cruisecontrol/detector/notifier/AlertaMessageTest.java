/*
 * Copyright 2019 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See
 * License in the project root for license information.
 */

package com.linkedin.kafka.cruisecontrol.detector.notifier;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;

public class AlertaMessageTest {

  @Test
  public void testAlertaMessageJsonFormat() {
    AlertaMessage alertaMessage = new AlertaMessage("resource", "event");
    alertaMessage.set_correlate(Arrays.asList("cor1", "cor2"));
    alertaMessage.set_createTime("2021-03-23 10:00:00Z");
    alertaMessage.set_environment("env");
    alertaMessage.set_group("goup");
    alertaMessage.set_origin("origin");
    alertaMessage.set_rawData("raw");
    alertaMessage.set_service(Arrays.asList("service1", "service2"));
    alertaMessage.set_severity("severity");
    alertaMessage.set_tags(Arrays.asList("tag1", "tag2"));
    alertaMessage.set_text("text");
    alertaMessage.set_timeout("300");
    alertaMessage.set_type("type");
    alertaMessage.set_value("value");

    String expectedJson =
        "{\"_resource\":\"resource\","
        + "\"_event\":\"event\","
        + "\"_environment\":\"env\","
        + "\"_severity\":\"severity\","
        + "\"_correlate\":[\"cor1\",\"cor2\"],"
        + "\"_service\":[\"service1\",\"service2\"],"
        + "\"_group\":\"goup\","
        + "\"_value\":\"value\","
        + "\"_text\":\"text\","
        + "\"_tags\":[\"tag1\",\"tag2\"],"
        + "\"_origin\":\"origin\","
        + "\"_type\":\"type\","
        + "\"_createTime\":\"2021-03-23 10:00:00Z\","
        + "\"_timeout\":\"300\","
        + "\"_rawData\":\"raw\"}";

    assertEquals(expectedJson, alertaMessage.toString());
  }
}
