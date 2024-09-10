import java.util.HashMap;

public class Parameters {
    private String url;
    private HashMap<String, String> params = new HashMap<>();

    public static Parameters fromURI (String URI) {
        // /point?x=1&y=2&r=3
        Parameters params = new Parameters();

        String[] url = URI.split("\\?");
        params.setURL(url[0].replace("/", ""));

        if (url.length == 2) {
            String[] parts = url[1].split("\\&");
            for (String part : parts) {
                String[] value = part.split("\\=");
                params.addValue(value[0], value[1]);
            }
        }

        return params;
    }

    public void addValue (String key, String value) {
        if (params.containsKey(key)) return;
        params.put(key, value);
    }

    public String get (String key) {
        return params.get(key);
    }

    public void setURL (String url) {
        this.url = url;
    }

    public String getURL () {
        return url;
    }
}
