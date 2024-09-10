import java.util.HashMap;

public class JSONBuilder {
    private StringBuilder json = new StringBuilder();
    private HashMap<String, String> values = new HashMap<>();

    public JSONBuilder () {
        json.append("{\n");
    }

    public JSONBuilder addValue (String key, String value) {
        values.put(key, value);
        return this;
    }

    public String build () {
        int i = 0;
        for (String key : values.keySet()) {
            json.append("\"")
                .append(key)
                .append("\": \"")
                .append(values.get(key))
                .append("\"");
            
            if (i != values.size() - 1) {
                json.append(",");
            }
            json.append("\n");
            i++;
        }

        json.append("}");

        return json.toString();
    }
}
