package webapp;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.eclipse.jetty.util.URIUtil;
import org.json.JSONException;
import org.json.JSONObject;

// Boilerplate code. All static.
public class Static {

	public static JSONObject getJSON(BufferedReader reader) throws Exception {
		JSONObject jsonObject = null;
		StringBuffer jb = new StringBuffer();
		String line = null;

		try {
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
		}

		try {
			line = jb.toString();
			line = line.replaceAll("json=&", "");
			if (line.endsWith("&json=")) line = line.substring(0, line.length() - 6);
			// if form post then get json form field
			if (line.contains("json=")) {
				Map<String, String> m = splitToMap(line, "&", "=");
				line = m.get("json");
				line = URIUtil.decodePath(line);
				line = line.replace("+", " ");
				// line = URLDecoder.decode(line, "US-ASCII");

				jsonObject = new JSONObject(line);
				jsonObject.remove("json");
			} else {
				if ("".equals(line)) line = "{}";
				jsonObject = new JSONObject(line);
			}
		} catch (JSONException e) {
		} finally {
		}
		return jsonObject;
	}

	public static String getSaltString(int size) {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < size) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;

	}

	public static Map<String, String> splitToMap(String source, String entriesSeparator, String keyValueSeparator) {
		Map<String, String> map = new HashMap<String, String>();
		String[] entries = source.split(entriesSeparator);
		for (String entry : entries) {
			if (entry != null && entry.contains(keyValueSeparator)) {
				String[] keyValue = entry.split(keyValueSeparator);
				map.put(keyValue[0], keyValue[1]);
			}
		}
		return map;
	}

}
