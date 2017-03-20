package org.measure.platform.agent.repository.api;

import java.util.List;

import org.measure.smm.measure.api.IDirectMeasure;
import org.measure.smm.measure.model.SMMMeasure;

/**
 * Service Interface for managing measure repository.
 */
public interface IMeasureCatalogueService {
		
	public List<SMMMeasure> getAllMeasures();
	
	public SMMMeasure getMeasure(String measureId);
	
	public IDirectMeasure getMeasureImplementation(String measureId) throws Exception;

}
