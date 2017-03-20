package org.measure.platform.agent.serviceregistrator;

import javax.inject.Inject;

import org.measure.platform.agent.repository.api.IMeasureCatalogueService;
import org.measure.smm.measure.model.SMMMeasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class PlatformRegistrator implements SchedulingConfigurer {

	private static final String REGISTRATION_API = "/api/remote-measure/registration";
	@Value("${measure.server.adress}")
	private String serverAdress;

	@Value("${measure.callback.adress}")
	private String callbackAdress;

	@Value("${measure.agent.name}")
	private String agentName;

	@Inject
	private IMeasureCatalogueService catalogueService;

	private final Logger log = LoggerFactory.getLogger(PlatformRegistrator.class);

	@Bean()
	public ThreadPoolTaskScheduler taskScheduler() {
		return new ThreadPoolTaskScheduler();
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setTaskScheduler(taskScheduler());
		registerMeasures();
	}
	
	
	@Scheduled(fixedRate = 10000)
	public void reportCurrentTime() {
		registerMeasures();
	}
	
	private void registerMeasures(){
		for (SMMMeasure measure : catalogueService.getAllMeasures()) {

			measure.setCallbackAdress(callbackAdress);
			measure.setCallbackLable(agentName);
			measure.setName(measure.getName() + " (" + agentName + ")");

			RestTemplate restTemplate = new RestTemplate();
			try {
				log.info("Registration : " + measure.getName() + " http://" + serverAdress + REGISTRATION_API);
				restTemplate.put("http://" + serverAdress + REGISTRATION_API, measure);
				log.info("Measures Registres to Server[" + serverAdress + "] :" + measure.getName());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Measure Registration Error : " + e.getMessage());
				return;
			}
		}
	}



}
