/*
 * Copyright 2019 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */

package com.linkedin.kafka.cruisecontrol.detector.notifier;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Alerta.io data to create alert: https://docs.alerta.io/en/latest/api/reference.html#create-an-alert
 */
public final class AlertaMessage implements Serializable {

    @NotNull
    private String _resource;
    @NotNull
    private String _event;

    private String _environment;
    private String _severity;
    private List<String> _correlate;
    private List<String> _service;
    private String _group;
    private String _value;
    private String _text;
    private List<String> _tags;
    private String _origin;
    private String _type;
    private String _createTime;
    private String _timeout;
    private String _rawData;

    public AlertaMessage(String _resource, String _event) {
      this._event = _event;
      this._resource = _resource;
    }

    public String get_resource() {
      return _resource;
    }
    public String get_event() {
      return _event;
    }
    public String get_environment() {
      return _environment;
    }
    public void set_environment(String _environment) {
      this._environment = _environment;
    }
    public String get_severity() {
      return _severity;
    }
    public void set_severity(String _severity) {
      this._severity = _severity;
    }
    public List<String> get_correlate() {
      return _correlate;
    }
    public void set_correlate(List<String> _correlate) {
      this._correlate = _correlate;
    }
    public List<String> get_service() {
      return _service;
    }
    public void set_service(List<String> _service) {
      this._service = _service;
    }
    public String get_group() {
      return _group;
    }
    public void set_group(String _group) {
      this._group = _group;
    }
    public String get_value() {
      return _value;
    }
    public void set_value(String _value) {
      this._value = _value;
    }
    public String get_text() {
      return _text;
    }
    public void set_text(String _text) {
      this._text = _text;
    }
    public List<String> get_tags() {
      return _tags;
    }
    public void set_tags(List<String> _tags) {
      this._tags = _tags;
    }
    public String get_origin() {
      return _origin;
    }
    public void set_origin(String _origin) {
      this._origin = _origin;
    }
    public String get_type() {
      return _type;
    }
    public void set_type(String _type) {
      this._type = _type;
    }
    public String get_createTime() {
      return _createTime;
    }
    public void set_createTime(String _createTime) {
      this._createTime = _createTime;
    }
    public String get_timeout() {
      return _timeout;
    }
    public void set_timeout(String _timeout) {
      this._timeout = _timeout;
    }
    public String get_rawData() {
      return _rawData;
    }
    public void set_rawData(String _rawData) {
      this._rawData = _rawData;
    }
    @Override
    public String toString() {
      try {
        return new ObjectMapper().writeValueAsString(this);
      } catch (JsonProcessingException e) {
        return "AlertaMassage Object parsing error : " + e.getMessage();
      }
    }
}
