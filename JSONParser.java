import java.util.*;
public class JSONParser {

    public static Map<String, Object> parse(String json) {
        String noWhitespace = json.replaceAll("\\s+", ""); // remove whitespace
        char[] charArray = noWhitespace.toCharArray();
        int[] index = {0}; // using array as workaround for pass by reference
        return parseObject(charArray, index);
    }

    private static Map<String, Object> parseObject(char[] charArray, int[] index) {
        Map<String, Object> jsonObject = new HashMap<>();
        index[0]++; // Move past the opening curly brace '{'
        while (charArray[index[0]] != '}') {
            // Parse key
            String key = parseString(charArray, index);
            index[0]++; // Move past the colon ':'

            // Parse value
            Object value = parseValue(charArray, index);
            jsonObject.put(key, value);

            // Move to the next key-value pair
            if (charArray[index[0]] == ',') {
                index[0]++;
            }
        }
        return jsonObject;
    }

    private static List<Object> parseArray(char[] charArray, int[] index) {
        List<Object> jsonArray = new ArrayList<>();
        index[0]++; // Move past the opening square bracket '['
        while (charArray[index[0]] != ']') {
            // Parse value
            Object value = parseValue(charArray, index);
            jsonArray.add(value);

            // Move to the next element
            if (charArray[index[0]] == ',') {
                index[0]++;
            }
        }
        index[0]++; // Move past closing square bracket
        return jsonArray;
    }

    private static String parseString(char[] charArray, int[] index) {
        StringBuilder builder = new StringBuilder();
        index[0]++; // Move past the opening double quote '"'
        while (charArray[index[0]] != '\"') {
            builder.append(charArray[index[0]]);
            index[0]++;
        }
        index[0]++; // Move past the closing double quote '"'
        return builder.toString();
    }

    private static Number parseNumber(char[] charArray, int[] index) {
        StringBuilder builder = new StringBuilder();
        while (Character.isDigit(charArray[index[0]]) || charArray[index[0]] == '-' || charArray[index[0]] == '.') {
            builder.append(charArray[index[0]]);
            index[0]++;
        }
        String numberStr = builder.toString();
        if (numberStr.contains(".")) {
            return Double.parseDouble(numberStr);
        } else {
            return Integer.parseInt(numberStr);
        }
    }

    private static boolean parseBoolean(char[] charArray, int[] index) {
        String boolStr = charArray[index[0]] == 't' ? "true" : "false";
        index[0]++;
        return Boolean.parseBoolean(boolStr);
    }



    private static Object parseValue(char[] charArray, int[] index) {
        char currentChar = charArray[index[0]];
        if (currentChar == '{') {
            return parseObject(charArray, index);
        } else if (currentChar == '[') {
            return parseArray(charArray, index);
        } else if (Character.isDigit(currentChar) || currentChar == '-') {
            return parseNumber(charArray, index);
        } else if (currentChar == '\"') {
            return parseString(charArray, index);
        } else if (currentChar == 't' || currentChar == 'f') {
            return parseBoolean(charArray, index);
        } else if (currentChar == 'n') {
            index[0]++;
            return null;
        } else {
            throw new RuntimeException("Invalid JSON format: " + currentChar);
        }
    }

    @SuppressWarnings("unchecked")
	public static void main(String[] args){
        String input = "{\"debug\":\"on\",\"window\":{\"title\":\"sample\",\"size\":500}}";

        Map<String, Object> output = JSONParser.parse(input);
        assert output.get("debug").equals("on") : "Debug is off.";
        assert ((Map<String, Object>) (output.get("window"))).get("title").equals("sample") : "Title of window is not sample.";
        assert (((Map<String, Object>) (output.get("window"))).get("size").equals(500)) : "Size of window is not 500.";
	}
}