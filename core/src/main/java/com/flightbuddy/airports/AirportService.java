package com.flightbuddy.airports;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;

@Service
@Slf4j
class AirportService {

	private static final String AIRPORTS_PATH = "/home/ubuntu/flightbuddyData/airports.txt";

	public List<String> getAirportList() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL airportsFileUrl = classLoader.getResource("airports.txt");
		if (airportsFileUrl != null) {
			String airportsFilePath = airportsFileUrl.getFile();
			File airportsFile = getFile(airportsFilePath);
			try {
				return Files.readLines(airportsFile, Charset.defaultCharset());
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		return Collections.emptyList();
	}

	private File getFile(String airportsFilePath) {
		File airportsFile = new File(airportsFilePath);
		if (!airportsFile.exists()) {
            Path airportsPath = Paths.get(AIRPORTS_PATH);
            airportsFile = airportsPath.toFile();
        }
		return airportsFile;
	}
}
