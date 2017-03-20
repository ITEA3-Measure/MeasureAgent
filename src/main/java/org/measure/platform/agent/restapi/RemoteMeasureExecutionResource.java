package org.measure.platform.agent.restapi;

import javax.inject.Inject;
import javax.validation.Valid;

import org.measure.platform.agent.smmengine.api.IRemoteExecutionService;
import org.measure.smm.log.MeasureLog;
import org.measure.smm.remote.RemoteMeasureInstance;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/measure-agent")
public class RemoteMeasureExecutionResource {

	@Inject
	private IRemoteExecutionService executionService;

    @PostMapping("/measure-execution")
    public MeasureLog executeMeasure(@Valid @RequestBody RemoteMeasureInstance measureData) {     
    	//return executionService.executeMeasure(measureData);
    	
    	return null;
    }
}
