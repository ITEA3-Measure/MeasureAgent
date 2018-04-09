package org.measure.platform.agent.repository.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.xml.bind.JAXBException;

import org.measure.platform.agent.repository.api.IMeasureCatalogueService;
import org.measure.smm.measure.api.IDirectMeasure;
import org.measure.smm.measure.api.IMeasure;
import org.measure.smm.measure.model.SMMMeasure;
import org.measure.smm.service.MeasurePackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
public class MeasureCatalogueService implements IMeasureCatalogueService {

	private final Logger log = LoggerFactory.getLogger(MeasureCatalogueService.class);

	@Value("${measure.repository.path}")
	private String measurePath;

	@Override
	public List<SMMMeasure> getAllMeasures() {
		List<SMMMeasure> result = new ArrayList<SMMMeasure>();
		try {
			File repository = new File(measurePath);
			if(repository.exists()){
				for (File file : repository.listFiles()) {
					result.add(MeasurePackager.getMeasureData(file.toPath().resolve(MeasurePackager.MEATADATAFILE)));
				}
			}		
		} catch (JAXBException | IOException e) {
			log.error(e.getLocalizedMessage());
		}
		return result;
	}

	@Override
	public IDirectMeasure getMeasureImplementation(String measureId) throws Exception {
		Path repository = new File(measurePath).toPath();
		Path measureImpl = repository.resolve(measureId);

		List<URL> jars;
		
		URL measureJar = getJars(measureImpl).get(0);
		jars = getJars(measureImpl.resolve("lib"));
		jars.add(measureJar);

		
		try (URLClassLoader loader = new URLClassLoader(jars.toArray(new URL[jars.size()]),
				IMeasure.class.getClassLoader())) {
			IMeasure result = null;
			for (URL jar : jars) {
				JarInputStream jarStream = new JarInputStream(new FileInputStream(new File(jar.getFile())));
				for (JarEntry jarEntry = jarStream.getNextJarEntry(); jarEntry != null; jarEntry = jarStream.getNextJarEntry()) {
					if (jarEntry.getName().endsWith(".class")) { //$NON-NLS-1$
						String metaclassNamespace = getNamespace(jarEntry.getName());
						Class<?> metaclass = loader.loadClass(metaclassNamespace);

						if (IMeasure.class.isAssignableFrom(metaclass)) {
							result = (IMeasure) metaclass.newInstance();
						}
					}
				}
			}

			return (IDirectMeasure) result;
		}
		
	}

	private String getNamespace(String jarEntryName) {
		String namespace = jarEntryName;
		namespace = namespace.replaceAll("/", "\\.");
		String separator = "/";

		int index = namespace.lastIndexOf(separator);
		namespace = namespace.substring(index + 1);

		if (namespace.endsWith(".class")) {
			namespace = namespace.substring(0, namespace.length() - 6);
		}
		return namespace;
	}

	private List<URL> getJars(Path measureImpl) throws MalformedURLException {
		List<URL> jars = new ArrayList<>();
		if (measureImpl.toFile().exists() && measureImpl.toFile().isDirectory()) {
			for (File sub : measureImpl.toFile().listFiles()) {
				if (sub.getName().endsWith("jar")) {
					jars.add(sub.toURI().toURL());
				}
			}
		}

		return jars;
	}

	@Override
	public SMMMeasure getMeasure(String measureId) {
		Path repository = new File(measurePath).toPath();
		Path measureData = repository.resolve(measureId).resolve(MeasurePackager.MEATADATAFILE);
		if (measureData.toFile().exists()) {
			try {
				return MeasurePackager.getMeasureData(measureData);
			} catch (JAXBException | IOException e) {
				log.error(e.getLocalizedMessage());
			}
		}
		return null;
	}

}
