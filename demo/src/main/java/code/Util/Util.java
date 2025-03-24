package code.Util;

public class Util {

	public static Double parseDouble(String input) {
		try {
			return Double.valueOf(input);
		} catch (Exception c) {
			System.out.println("String is not a double");
			return null;
		}
	}

}
