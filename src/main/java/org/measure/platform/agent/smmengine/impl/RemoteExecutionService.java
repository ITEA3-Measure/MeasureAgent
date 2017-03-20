package org.measure.platform.agent.smmengine.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.print.attribute.HashAttributeSet;

import org.measure.platform.agent.repository.api.IMeasureCatalogueService;
import org.measure.platform.agent.smmengine.api.IRemoteExecutionService;
import org.measure.smm.log.MeasureLog;
import org.measure.smm.measure.api.IDirectMeasure;
import org.measure.smm.measure.api.IMeasurement;
import org.measure.smm.remote.RemoteMeasureInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteExecutionService implements IRemoteExecutionService {

	private static final String EXECUTION_API = "/api/remote-measure/measure-execution";
	@Value("${measure.server.adress}")
	private String serverAdress;

	@Value("${measure.callback.adress}")
	private String callbackAdress;

	@Value("${measure.agent.name}")
	private String agentName;

	private final Logger log = LoggerFactory.getLogger(RemoteExecutionService.class);

	@Override
	public MeasureLog executeMeasure(RemoteMeasureInstance measureData, IDirectMeasure measure) {

		MeasureLog executionLog = new MeasureLog();

		executionLog.setMeasureInstanceName(measureData.getInstanceName());
		executionLog.setMeasureName(measureData.getMeasureName());
		executionLog.setMeasureInstanceId(measureData.getMeasureId());

		try {
			if (measure != null) {
				Map<String, String> ollProperties = new HashMap<>(measureData.getProperties());
				for (String key : measureData.getProperties().keySet()) {
					measure.getProperties().put(key, measureData.getProperties().get(key));
				}

				Date start = new Date();
				List<IMeasurement> measurements = measure.getMeasurement();

				for (String key : ollProperties.keySet()) {
					if (!ollProperties.get(key).equals(measure.getProperties().get(key))) {
						executionLog.getUpdatedParameters().put(key, measure.getProperties().get(key));
					}
				}

				executionLog.setExectionDate(new Date());
				executionLog.setExecutionTime(new Date().getTime() - start.getTime());
				executionLog.setMesurement(measurements);
				executionLog.setSuccess(true);

				// Store Updated Properties
				measureData.setProperties(measure.getProperties());

			} else {
				executionLog.setExceptionMessage("Measure Unknown on Agent");
				executionLog.setSuccess(false);
			}
		} catch (Exception e) {
			log.error("Execution Failled [" + measureData.getMeasureName() + "] :" + e.getMessage());
			e.printStackTrace();
			executionLog.setExceptionMessage(e.getMessage());
			executionLog.setSuccess(false);
		}

		sendExecutionResult(executionLog);

		return executionLog;
	}

	@Override
	public void sendExecutionResult(MeasureLog executionLog) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.put("http://" + serverAdress + EXECUTION_API, executionLog);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Send Execution Error : " + e.getMessage());
		}
	}

}
