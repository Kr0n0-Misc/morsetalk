package com.blueta.morsetransmitter;

import java.util.ArrayList;
import java.util.HashMap;

public class EnglishMorseTrans {
    static final String NO_MATCH = "No Match";
    static final HashMap<String, String> englishMorseMap;
    static final HashMap<String, String> englishMorseReverseMap;
    ArrayList<MorseCodeElement> morseData;

    static {
        englishMorseMap = new HashMap();
        englishMorseMap.put("a", ".-");
        englishMorseMap.put("b", "-...");
        englishMorseMap.put("c", "-.-.");
        englishMorseMap.put("d", "-..");
        englishMorseMap.put("e", ".");
        englishMorseMap.put("f", "..-.");
        englishMorseMap.put("g", "--.");
        englishMorseMap.put("h", "....");
        englishMorseMap.put("i", "..");
        englishMorseMap.put("j", ".---");
        englishMorseMap.put("k", "-.-");
        englishMorseMap.put("l", ".-..");
        englishMorseMap.put("m", "--");
        englishMorseMap.put("n", "-.");
        englishMorseMap.put("o", "---");
        englishMorseMap.put("p", ".--.");
        englishMorseMap.put("q", "--.-");
        englishMorseMap.put("r", ".-.");
        englishMorseMap.put("s", "...");
        englishMorseMap.put("t", "-");
        englishMorseMap.put("u", "..-");
        englishMorseMap.put("v", "...-");
        englishMorseMap.put("w", ".--");
        englishMorseMap.put("x", "-..-");
        englishMorseMap.put("y", "-.--");
        englishMorseMap.put("z", "--..");
        englishMorseMap.put("1", ".----");
        englishMorseMap.put("2", "..---");
        englishMorseMap.put("3", "...--");
        englishMorseMap.put("4", "....-");
        englishMorseMap.put("5", ".....");
        englishMorseMap.put("6", "-....");
        englishMorseMap.put("7", "--...");
        englishMorseMap.put("8", "---..");
        englishMorseMap.put("9", "----.");
        englishMorseMap.put("0", "-----");
        englishMorseMap.put(".", ".-.-.-");
        englishMorseMap.put(",", "--..--");
        englishMorseMap.put("?", "..--..");
        englishMorseMap.put("/", ".--.-");
        englishMorseMap.put(":", "---...");
        englishMorseMap.put(";", "-.-.-.");
        englishMorseMap.put("'", ".-----");
        englishMorseMap.put("\"", ".-..-.");
        englishMorseMap.put("(", "-.--.");
        englishMorseMap.put(")", "-.--.-");
        englishMorseMap.put("-", "--...-");
        englishMorseMap.put("$", "...-..-");
        englishMorseMap.put("_", "..--.-");
        englishMorseMap.put(" ", " ");
        englishMorseReverseMap = new HashMap();
        englishMorseReverseMap.put(".-", "a");
        englishMorseReverseMap.put("-...", "b");
        englishMorseReverseMap.put("-.-.", "c");
        englishMorseReverseMap.put("-..", "d");
        englishMorseReverseMap.put(".", "e");
        englishMorseReverseMap.put("..-.", "f");
        englishMorseReverseMap.put("--.", "g");
        englishMorseReverseMap.put("....", "h");
        englishMorseReverseMap.put("..", "i");
        englishMorseReverseMap.put(".---", "j");
        englishMorseReverseMap.put("-.-", "k");
        englishMorseReverseMap.put(".-..", "l");
        englishMorseReverseMap.put("--", "m");
        englishMorseReverseMap.put("-.", "n");
        englishMorseReverseMap.put("---", "o");
        englishMorseReverseMap.put(".--.", "p");
        englishMorseReverseMap.put("--.-", "q");
        englishMorseReverseMap.put(".-.", "r");
        englishMorseReverseMap.put("...", "s");
        englishMorseReverseMap.put("-", "t");
        englishMorseReverseMap.put("..-", "u");
        englishMorseReverseMap.put("...-", "v");
        englishMorseReverseMap.put(".--", "w");
        englishMorseReverseMap.put("-..-", "x");
        englishMorseReverseMap.put("-.--", "y");
        englishMorseReverseMap.put("--..", "z");
        englishMorseReverseMap.put(".----", "1");
        englishMorseReverseMap.put("..---", "2");
        englishMorseReverseMap.put("...--", "3");
        englishMorseReverseMap.put("....-", "4");
        englishMorseReverseMap.put(".....", "5");
        englishMorseReverseMap.put("-....", "6");
        englishMorseReverseMap.put("--...", "7");
        englishMorseReverseMap.put("---..", "8");
        englishMorseReverseMap.put("----.", "9");
        englishMorseReverseMap.put("-----", "0");
        englishMorseReverseMap.put("-----", ".");
        englishMorseReverseMap.put("--..--", ",");
        englishMorseReverseMap.put("..--..", "?");
        englishMorseReverseMap.put(".--.-", "/");
        englishMorseReverseMap.put("---...", ":");
        englishMorseReverseMap.put("-.-.-.", ";");
        englishMorseReverseMap.put(".-----", "'");
        englishMorseReverseMap.put(".-..-.", "\"");
        englishMorseReverseMap.put("-.--.", "(");
        englishMorseReverseMap.put("-.--.-", ")");
        englishMorseReverseMap.put("--...-", "-");
        englishMorseReverseMap.put("...-..-", "$");
        englishMorseReverseMap.put("..--.-", "_");
        englishMorseReverseMap.put(" ", " ");
    }

    ArrayList<MorseCodeElement> EncodeToMorseCodeData(String source) {
        this.morseData = new ArrayList();
        int sourceLength = source.length();
        int indexOfElment = 0;
        String lowCaseSource = source.toLowerCase();
        for (int index = 0; index < sourceLength; index++) {
            MorseCodeElement morseDataElement = new MorseCodeElement();
            morseDataElement.Character = new String(new char[]{lowCaseSource.charAt(index)});
            String b = (String) englishMorseMap.get(morseDataElement.Character);
            if (b != null) {
                morseDataElement.index = indexOfElment;
                indexOfElment++;
                morseDataElement.MorseCode = new String(b);
                this.morseData.add(morseDataElement);
            }
        }
        return this.morseData;
    }

    String DecodToAlphabet(String source) {
        return (String) englishMorseReverseMap.get(source);
    }
}
