package com.flightbuddy.airports;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;

@Service
public class AirportService {

	Logger log = LoggerFactory.getLogger(AirportService.class);
	
	public List<String> getAirportList() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL airportsFileUrl = classLoader.getResource("airports.txt");
		String airportsFilePath = airportsFileUrl.getFile();
		File airportsFile = new File(airportsFilePath);
		if (!airportsFile.exists()) {
			Path airportsPath = Paths.get("/home/ubuntu/flightbuddyData/airports.txt");
			airportsFile = airportsPath.toFile();
		}
		try {
			return Files.readLines(airportsFile, Charset.defaultCharset());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return Collections.emptyList();
	}
}
