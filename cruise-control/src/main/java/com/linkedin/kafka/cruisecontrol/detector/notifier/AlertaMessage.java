/*
 * Copyright 2019 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */

package com.linkedin.kafka.cruisecontrol.detector.notifier;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Alerta.io data to create alert: https://docs.alerta.io/en/latest/api/reference.html#create-an-alert
 */
public final class AlertaMessage implements Serializable {

    @NotNull
    private String resource;
    @NotNull
    private String event;

    private String environment;
    private String severity;
    private List<String> correlate;
    private List<String> service;
    private String group;
    private String value;
    private String text;
    private List<String> tags;
    private String origin;
    private String type;
    private String createTime;
    private String timeout;
    private String rawData;

    public AlertaMessage(String _resource, String _event) {
      this.event = _event;
      this.resource = _resource;
    }

    public String getResource() {
      return resource;
    }

    public void setResource(String resource) {
      this.resource = resource;
    }

    public String getEvent() {
      return event;
    }

    public void setEvent(String event) {
      this.event = event;
    }

    public String getEnvironment() {
      return environment;
    }

    public void setEnvironment(String environment) {
      this.environment = environment;
    }

    public String getSeverity() {
      return severity;
    }

    public void setSeverity(String severity) {
      this.severity = severity;
    }

    public List<String> getCorrelate() {
      return correlate;
    }

    public void setCorrelate(List<String> correlate) {
      this.correlate = correlate;
    }

    public List<String> getService() {
      return service;
    }

    public void setService(List<String> service) {
      this.service = service;
    }

    public String getGroup() {
      return group;
    }

    public void setGroup(String group) {
      this.group = group;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }

    public List<String> getTags() {
      return tags;
    }

    public void setTags(List<String> tags) {
      this.tags = tags;
    }

    public String getOrigin() {
      return origin;
    }

    public void setOrigin(String origin) {
      this.origin = origin;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getCreateTime() {
      return createTime;
    }

    public void setCreateTime(String createTime) {
      this.createTime = createTime;
    }

    public String getTimeout() {
      return timeout;
    }

    public void setTimeout(String timeout) {
      this.timeout = timeout;
    }

    public String getRawData() {
      return rawData;
    }

    public void setRawData(String rawData) {
      this.rawData = rawData;
    }

    @Override
    public String toString() {
      try {
        return new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(this);
      } catch (JsonProcessingException e) {
        return "AlertaMassage Object parsing error : " + e.getMessage();
      }
    }
}
