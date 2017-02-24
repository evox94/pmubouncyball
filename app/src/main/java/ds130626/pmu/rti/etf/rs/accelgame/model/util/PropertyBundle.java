package ds130626.pmu.rti.etf.rs.accelgame.model.util;

import java.util.Map;

/**
 * Created by smiljan on 1/31/17.
 */

public class PropertyBundle {
    private Map<String, ?> properties;

    public PropertyBundle(Map<String, ?> properties) {
        this.properties = properties;
    }

    private String getObj(String key, Object defaultValue) {
        Object ret = properties.get(key);
        return ret == null ? defaultValue.toString() : ret.toString();
    }

    public int getInt(String key, int defaultValue) {
        return Integer.parseInt(getObj(key, defaultValue));
    }


    public float getFloat(String key, float defaultValue) {
        return Float.parseFloat(getObj(key, defaultValue));
    }

    public String getString(String key, String defaultValue) {
        return (String) getObj(key, defaultValue);
    }
}
