package com.andrewfesta.doublesolitare;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="doublesolitare")
public class DoubleSolitareConfig {
	
	private DoubleSolitareDebugProperties debug = new DoubleSolitareDebugProperties();

	
	@Bean
	public DoubleSolitareDebugProperties getDebug() {
		return debug;
	}

	public void setDebug(DoubleSolitareDebugProperties doublesolitare) {
		this.debug = doublesolitare;
	}

	public static class DoubleSolitareDebugProperties {
		private boolean shuffle=true;
		private boolean additionalResponseOutput=false;

		public boolean isShuffle() {
			return shuffle;
		}

		public void setShuffle(boolean shuffle) {
			this.shuffle = shuffle;
		}

		public boolean isAdditionalResponseOutput() {
			return additionalResponseOutput;
		}

		/**
		 * Adds additional properties to REST responses
		 * @param debugOutput
		 */
		public void setAdditionalResponseOutput(boolean debugOutput) {
			this.additionalResponseOutput = debugOutput;
		}		
		
	}
}
