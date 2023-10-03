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

package org.apache.shardingsphere.globalclock.core.rule;

import lombok.Getter;
import org.apache.shardingsphere.globalclock.api.config.GlobalClockRuleConfiguration;
import org.apache.shardingsphere.globalclock.core.provider.GlobalClockProvider;
import org.apache.shardingsphere.infra.database.DatabaseTypeEngine;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.metadata.database.ShardingSphereDatabase;
import org.apache.shardingsphere.infra.rule.identifier.scope.GlobalRule;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.transaction.spi.TransactionHook;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Global clock rule.
 */
public final class GlobalClockRule implements GlobalRule {
    
    @Getter
    private final GlobalClockRuleConfiguration configuration;
    
    public GlobalClockRule(final GlobalClockRuleConfiguration ruleConfig, final Map<String, ShardingSphereDatabase> databases) {
        configuration = ruleConfig;
        if (ruleConfig.isEnabled()) {
            TypedSPILoader.getService(GlobalClockProvider.class, getGlobalClockProviderType(), configuration.getProps());
            TypedSPILoader.getService(TransactionHook.class, "GLOBAL_CLOCK", createProperties(databases));
        }
    }
    
    private Properties createProperties(final Map<String, ShardingSphereDatabase> databases) {
        Properties result = new Properties();
        DatabaseType storageType = findStorageType(databases.values()).orElseGet(() -> DatabaseTypeEngine.getStorageType(Collections.emptyList()));
        result.setProperty("trunkType", storageType.getTrunkDatabaseType().orElse(storageType).getType());
        result.setProperty("enabled", String.valueOf(configuration.isEnabled()));
        result.setProperty("type", configuration.getType());
        result.setProperty("provider", configuration.getProvider());
        return result;
    }
    
    private Optional<DatabaseType> findStorageType(final Collection<ShardingSphereDatabase> databases) {
        return databases.stream()
                .flatMap(each -> each.getResourceMetaData().getStorageUnitMetaDataMap().values().stream()).findFirst().map(each -> each.getStorageUnit().getStorageType());
    }
    
    /**
     * Get global clock provider type.
     * 
     * @return global clock provider type
     */
    public String getGlobalClockProviderType() {
        return String.join(".", configuration.getType(), configuration.getProvider());
    }
}
