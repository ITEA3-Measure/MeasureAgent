package org.measure.platform.agent.smmengine.api;

import org.measure.smm.log.MeasureLog;
import org.measure.smm.measure.api.IDirectMeasure;
import org.measure.smm.remote.RemoteMeasureInstance;

public interface IRemoteExecutionService {
	 public MeasureLog executeMeasure(RemoteMeasureInstance measureData,IDirectMeasure measureImpl);
	 public void sendExecutionResult(MeasureLog executionLog);
}
