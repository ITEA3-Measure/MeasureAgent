package org.measure.platform.agent.smmengine.api;

import java.util.Set;

import org.measure.smm.remote.RemoteMeasureInstance;

public interface IShedulingService {

	Boolean scheduleMeasure(RemoteMeasureInstance measure);

	Boolean removeMeasure(Long measureInstanceId);

	Boolean isShedule(Long measureInstanceId);

	Set<Long> getSheduledMeasures();

}
