package code.Util;

import java.util.HashMap;

public class PostUtil {

	public static HashMap<String, String> postData(String payload) {
		HashMap<String, String> map = new HashMap<>();
		String[] attributes = payload.split("&");
		for (String attribute : attributes) {
			String[] parts = attribute.split("=");
			if (parts.length == 2)
				map.put(parts[0], parts[1]);
		}
		return map;
	}

}
