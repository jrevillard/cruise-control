/*
 * Copyright 2019 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */

package com.linkedin.kafka.cruisecontrol.detector.notifier;

import com.linkedin.cruisecontrol.detector.AnomalyType;
import com.linkedin.kafka.cruisecontrol.KafkaCruiseControl;
import com.linkedin.kafka.cruisecontrol.KafkaCruiseControlUnitTestUtils;
import com.linkedin.kafka.cruisecontrol.config.KafkaCruiseControlConfig;
import com.linkedin.kafka.cruisecontrol.config.constants.AnomalyDetectorConfig;
import com.linkedin.kafka.cruisecontrol.detector.BrokerFailures;
import java.util.Properties;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.linkedin.kafka.cruisecontrol.detector.AnomalyDetectorUtils.KAFKA_CRUISE_CONTROL_OBJECT_CONFIG;
import static com.linkedin.kafka.cruisecontrol.detector.AnomalyDetectorUtils.ANOMALY_DETECTION_TIME_MS_OBJECT_CONFIG;
import static com.linkedin.kafka.cruisecontrol.detector.BrokerFailureDetector.BROKER_FAILURES_FIXABLE_CONFIG;
import static com.linkedin.kafka.cruisecontrol.detector.BrokerFailureDetector.FAILED_BROKERS_OBJECT_CONFIG;
import static org.junit.Assert.assertEquals;

public class AlertaSelfHealingNotifierTest {
    private static BrokerFailures FAILURES;
    private static Time MOCK_TIME;
    private MockAlertaSelfHealingNotifier _notifier;

    /**
     * Setup the test.
     */
    @BeforeClass
    public static void setup() {
        final long startTime = 500L;
        MOCK_TIME = new MockTime(0, startTime, TimeUnit.NANOSECONDS.convert(startTime, TimeUnit.MILLISECONDS));
        KafkaCruiseControl mockKafkaCruiseControl = EasyMock.mock(KafkaCruiseControl.class);
        Properties props = KafkaCruiseControlUnitTestUtils.getKafkaCruiseControlProperties();
        KafkaCruiseControlConfig kafkaCruiseControlConfig = new KafkaCruiseControlConfig(props);
        EasyMock.expect(mockKafkaCruiseControl.config()).andReturn(kafkaCruiseControlConfig).anyTimes();
        EasyMock.replay(mockKafkaCruiseControl);
        Map<Integer, Long> failedBrokers = new HashMap<>();
        failedBrokers.put(1, 200L);
        failedBrokers.put(2, 400L);
        Map<String, Object> parameterConfigOverrides = new HashMap<>(4);
        parameterConfigOverrides.put(KAFKA_CRUISE_CONTROL_OBJECT_CONFIG, mockKafkaCruiseControl);
        parameterConfigOverrides.put(ANOMALY_DETECTION_TIME_MS_OBJECT_CONFIG, 200L);
        parameterConfigOverrides.put(FAILED_BROKERS_OBJECT_CONFIG, failedBrokers);
        parameterConfigOverrides.put(BROKER_FAILURES_FIXABLE_CONFIG, true);
        FAILURES = kafkaCruiseControlConfig.getConfiguredInstance(AnomalyDetectorConfig.BROKER_FAILURES_CLASS_CONFIG,
                                                                  BrokerFailures.class,
                                                                  parameterConfigOverrides);
    }

    @Test
    public void testSlackAlertWithNoWebhook() {
        _notifier = new MockAlertaSelfHealingNotifier(MOCK_TIME);
        _notifier.alert(FAILURES, false, 1L, KafkaAnomalyType.BROKER_FAILURE);
        assertEquals(0, _notifier.getAlertaMessageList().size());
    }

    @Test
    public void testSlackAlertWithNoApiKey() {
        _notifier = new MockAlertaSelfHealingNotifier(MOCK_TIME);
        _notifier._alertaWebhook = "http://dummy.alerta.webhook";
        _notifier.alert(FAILURES, false, 1L, KafkaAnomalyType.BROKER_FAILURE);
        assertEquals(0, _notifier.getAlertaMessageList().size());
    }

    @Test
    public void testSlackAlertWithDefaultOptions() {
        _notifier = new MockAlertaSelfHealingNotifier(MOCK_TIME);
        _notifier._alertaWebhook = "http://dummy.alerta.webhook";
        _notifier._alertaApiKey = "dummy-alerta-api-key";
        _notifier.alert(FAILURES, false, 1L, KafkaAnomalyType.BROKER_FAILURE);
        assertEquals(1, _notifier.getAlertaMessageList().size());
    }

    @Test
    public void testSlackAlertWithEnvironment() {
        _notifier = new MockAlertaSelfHealingNotifier(MOCK_TIME);
        _notifier._alertaWebhook = "http://dummy.alerta.webhook";
        _notifier._alertaApiKey = "dummy-alerta-api-key";
        _notifier._alertaEnvironment = "MyEnv";
        _notifier.alert(FAILURES, false, 1L, KafkaAnomalyType.BROKER_FAILURE);
        assertEquals(1, _notifier.getAlertaMessageList().size());
        assertEquals(_notifier._alertaEnvironment, _notifier.getAlertaMessageList().get(0).get_environment());
    }

    private static class MockAlertaSelfHealingNotifier extends AlertaSelfHealingNotifier {
        private List<AlertaMessage> _alertaMessageList;

        final Map<AnomalyType, Boolean> _alertCalled;
        final Map<AnomalyType, Boolean> _autoFixTriggered;

        MockAlertaSelfHealingNotifier(Time time) {
            super(time);
            _alertCalled = new HashMap<>(KafkaAnomalyType.cachedValues().size());
            _autoFixTriggered = new HashMap<>(KafkaAnomalyType.cachedValues().size());
            for (AnomalyType alertType : KafkaAnomalyType.cachedValues()) {
                _alertCalled.put(alertType, false);
                _autoFixTriggered.put(alertType, false);
            }
            _selfHealingEnabled.put(KafkaAnomalyType.BROKER_FAILURE, true);
            _alertaMessageList = new ArrayList<>();
        }


        @Override
        protected void sendAlertaMessage(AlertaMessage alertaMessage) throws IOException {
          _alertaMessageList.add(alertaMessage);
        }

        List<AlertaMessage> getAlertaMessageList() {
            return _alertaMessageList;
        }
    }

}
