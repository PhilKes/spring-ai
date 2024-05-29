/*
 * Copyright 2024 - 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.ai.autoconfigure.vectorstore.gemfire;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.GemFireVectorStore;
import org.springframework.ai.vectorstore.GemFireVectorStore.GemFireVectorStoreConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author Philipp Kessler
 */
@AutoConfiguration
@ConditionalOnClass({ GemFireVectorStore.class, EmbeddingModel.class })
@EnableConfigurationProperties({ GemFireVectorStoreProperties.class })
public class GemFireVectorStoreAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(GemFireConnectionDetails.class)
	public PropertiesGemFireConnectionDetails gemFireConnectionDetails(GemFireVectorStoreProperties properties) {
		return new PropertiesGemFireConnectionDetails(properties);
	}

	@Bean
	@ConditionalOnMissingBean
	public GemFireVectorStore vectorStore(EmbeddingModel embeddingModel, GemFireConnectionDetails connectionDetails,
			GemFireVectorStoreProperties properties) {
		var vectoreStoreConfig = GemFireVectorStoreConfig.builder()
			.withHost(connectionDetails.getHost())
			.withPort(connectionDetails.getPort())
			.withIndex(properties.getIndex())
			.withDocumentField(properties.getDocumentField())
			.withTopK(properties.getTopK())
			.withTopKPerBucket(properties.getTopKPerBucket())
			.withSslEnabled(properties.isSslEnabled())
			.withConnectionTimeout(properties.getConnectionTimeout())
			.withRequestTimeout(properties.getRequestTimeout())
			.build();

		var vectorStore = new GemFireVectorStore(vectoreStoreConfig, embeddingModel);

		vectorStore.setIndexName(properties.getIndex());

		return vectorStore;
	}

	private static class PropertiesGemFireConnectionDetails implements GemFireConnectionDetails {

		private final GemFireVectorStoreProperties properties;

		public PropertiesGemFireConnectionDetails(GemFireVectorStoreProperties properties) {
			this.properties = properties;
		}

		@Override
		public String getHost() {
			return properties.getHost();
		}

		@Override
		public int getPort() {
			return properties.getPort();
		}

	}

}
