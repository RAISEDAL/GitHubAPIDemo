package ca.dal.cs.raise.github.api.demo.core;

public class TextCleaner {

	public static String[] cleanText(String text) {
		return text.split("\\p{Punct}+|\\s+");
	}

}
