package code.DataStorage;

import java.io.File;
import java.io.IOException;

public class Settings {

	private static File file;

	public static void loadSettings() throws IOException {
		file = new File("../Settings");
		file.createNewFile();

		// ArrayList<String> settings = new Scanner(new
		// IOStream(file)).useDelimiter("\\Z").next().split("\\*");
	}

}
