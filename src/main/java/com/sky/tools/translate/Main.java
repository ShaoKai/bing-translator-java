package com.sky.tools.translate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private final static String API_KEY = "";
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		Map<String, String> propMap = new LinkedHashMap<String, String>();
		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("messages/message.properties")));
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.length() > 0) {
					String[] items = sCurrentLine.split("=");
					propMap.put(items[0], items[1]);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		String[] locales = new String[] { "zh-CHT", "id" };
		for (String locale : locales) {
			BufferedWriter out = null;
			try {
				FileWriter fstream = new FileWriter("src/main/resources/messages/message" + locale + ".properties", true);
				out = new BufferedWriter(fstream);

				ITranslatorClient client = new TranslatorClient(API_KEY, locale);
				for (Map.Entry<?, ?> entry : propMap.entrySet()) {
					String key = String.valueOf(entry.getKey());
					String value = String.valueOf(entry.getValue());
					logger.info(key + "=" + client.translate(value));
					out.write(key + "=" + client.translate(value) + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
