package com.jjdevbros.castellan.common.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.codehaus.jackson.JsonNode;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by lordbritishix on 15/09/15.
 */
@Singleton
public class JsonGroupLookup {
    private final JsonNode lookup;

    @Inject
    public JsonGroupLookup(@Named("lookup.list") JsonNode lookup) {
        this.lookup = lookup;
    }

    public String getGroupForName(String name) {
        if (lookup == null) {
            return  "Others";
        }

        Iterator<Map.Entry<String, JsonNode>> iter = lookup.getFields();

        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> next = iter.next();
            JsonNode value = next.getValue();
            String key = next.getKey();
            if (value.isArray()) {
                for (int x = 0; x < value.size(); ++x) {
                    if (value.get(x).asText().equals(name)) {
                        return key;
                    }
                }
            }
        }

        return  "Others";
    }
}
