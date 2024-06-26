/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.agent.plugin.metrics.prometheus.exoprter;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.agent.plugin.metrics.core.exporter.MetricsExporter;

import java.util.Collections;
import java.util.List;

/**
 * Metrics exporter of Prometheus.
 */
@RequiredArgsConstructor
@Slf4j
public final class PrometheusMetricsExporter extends Collector {
    
    private final MetricsExporter exporter;
    
    @Override
    public List<MetricFamilySamples> collect() {
        try {
            return exporter.export("Prometheus")
                    .<List<MetricFamilySamples>>map(optional -> Collections.singletonList((GaugeMetricFamily) optional.getRawMetricFamilyObject())).orElse(Collections.emptyList());
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            log.warn("Collect metrics error: {}", ex.getMessage());
        }
        return Collections.emptyList();
    }
}
