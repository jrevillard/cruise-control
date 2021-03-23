/*
 * Copyright 2019 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */

package com.linkedin.kafka.cruisecontrol.detector.notifier;

import com.linkedin.cruisecontrol.detector.Anomaly;
import com.linkedin.cruisecontrol.detector.AnomalyType;
import com.linkedin.kafka.cruisecontrol.detector.BrokerFailures;
import com.linkedin.kafka.cruisecontrol.detector.DiskFailures;
import com.linkedin.kafka.cruisecontrol.detector.GoalViolations;
import com.linkedin.kafka.cruisecontrol.detector.KafkaAnomaly;
import com.linkedin.kafka.cruisecontrol.detector.KafkaMetricAnomaly;
import com.linkedin.kafka.cruisecontrol.detector.MaintenanceEvent;
import com.linkedin.kafka.cruisecontrol.detector.TopicAnomaly;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.kafka.common.utils.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;

import static com.linkedin.cruisecontrol.CruiseControlUtils.utcDateFor;

public class AlertaSelfHealingNotifier extends SelfHealingNotifier {

    private static final Logger LOG = LoggerFactory.getLogger(AlertaSelfHealingNotifier.class);
    public static final String ALERTA_SELF_HEALING_NOTIFIER_WEBHOOK = "alerta.self.healing.notifier.webhook";
    public static final String ALERTA_SELF_HEALING_NOTIFIER_API_KEY = "alerta.self.healing.notifier.api_key";
    public static final String ALERTA_SELF_HEALING_NOTIFIER_ENVIRONMENT = "alerta.self.healing.notifier.management";
    
    protected String _alertaWebhook;
    protected String _alertaApiKey;
    protected String _alertaEnvironment;

    public AlertaSelfHealingNotifier() {
    }

    public AlertaSelfHealingNotifier(Time time) {
        super(time);
    }

    @Override
    public void configure(Map<String, ?> config) {
        super.configure(config);
        _alertaWebhook = (String) config.get(ALERTA_SELF_HEALING_NOTIFIER_WEBHOOK);
        _alertaApiKey = (String) config.get(ALERTA_SELF_HEALING_NOTIFIER_API_KEY);
        _alertaEnvironment = (String) config.get(ALERTA_SELF_HEALING_NOTIFIER_ENVIRONMENT);
    }

    @Override
    public void alert(Anomaly anomaly, boolean autoFixTriggered, long selfHealingStartTime, AnomalyType anomalyType) {
        super.alert(anomaly, autoFixTriggered, selfHealingStartTime, anomalyType);

        if (_alertaWebhook == null) {
            LOG.warn("Alerta.io webhook is null, can't send Alerta.io self healing notification");
            return;
        }

        if (_alertaApiKey == null) {
            LOG.warn("Alerta.io channel name is null, can't send Alerta.io self healing notification");
            return;
        }

        String text = String.format("%s detected %s. Self healing %s.%s", anomalyType, anomaly,
                _selfHealingEnabled.get(anomalyType) ? String.format("start time %s", utcDateFor(selfHealingStartTime))
                        : "is disabled",
                autoFixTriggered ? "%nSelf-healing has been triggered." : "");

        String localHostname = "Cruise Control";
        try {
          localHostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e1) {
          LOG.warn("Unable to get the hostname of the Cruise Control server");
        }

        String resource = localHostname + ":" + anomalyType;
        String event = anomalyType.toString();

        String severity = "warning";
        if (anomalyType.priority() == KafkaAnomalyType.BROKER_FAILURE.priority() ||
            anomalyType.priority() == KafkaAnomalyType.DISK_FAILURE.priority() ||
            anomalyType.priority() == KafkaAnomalyType.TOPIC_ANOMALY.priority()) {
          severity = "critical";
        }

        AlertaMessage alertaMessage =  new AlertaMessage( resource, event);
        
        alertaMessage.set_environment(_alertaEnvironment);
        alertaMessage.set_severity(event);
        alertaMessage.set_service(Arrays.asList("cruise-control"));
        alertaMessage.set_group(severity);
        alertaMessage.set_text(text);

        // tags
        // attributes
        
        alertaMessage.set_origin("cruise-control/" + localHostname);
        alertaMessage.set_type("cruiseControlAlarm");
        alertaMessage.set_rawData(anomaly.toString());
        alertaMessage.set_createTime(utcDateFor(selfHealingStartTime));

        try {
            sendAlertaMessage(alertaMessage);
        } catch (IOException e) {
            LOG.warn("ERROR sending alert to Alerta.io", e);
        }
    }

    protected void sendAlertaMessage(AlertaMessage alertaMessage) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(_alertaWebhook);
        StringEntity entity = new StringEntity(alertaMessage.toString());
        httpPost.setEntity(entity);
        httpPost.setHeader("Authorization", _alertaApiKey);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        try {
            client.execute(httpPost);
        } finally {
            client.close();
        }
    }
}
