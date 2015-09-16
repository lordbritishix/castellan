package com.jjdevbros.castellan.common.database;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lordbritishix on 15/09/15.
 */
public class JsonGroupLookupTest {
    private JsonGroupLookup fixture;

    @Test
    public void testGetGroupForNameForHappyCase() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode lookup = mapper.readTree("{\n" +
                                            "    \"Halifax\": [\n" +
                                            "      \"Jim\",\n" +
                                            "      \"Jen\",\n" +
                                            "      \"IEUser\"\n" +
                                            "    ],\n" +
                                            "    \"Calgary\": [\n" +
                                            "      \"Jeff\",\n" +
                                            "      \"Jacob\",\n" +
                                            "      \"IEUser\"\n" +
                                            "    ]\n" +
                                            "  }\n" +
                                            "}");

        fixture = new JsonGroupLookup(lookup);

        assertThat(fixture.getGroupForName("Jim"), is("Halifax"));
        assertThat(fixture.getGroupForName("IEUser"), is("Halifax"));
        assertThat(fixture.getGroupForName("Jeff"), is("Calgary"));
        assertThat(fixture.getGroupForName("None"), is("Others"));
    }

    @Test
    public void testGetGroupForNameForEmptyCase() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode lookup = mapper.readTree("{\n" +
                "    \"groups\": \"\"\n" +
                "}");

        fixture = new JsonGroupLookup(lookup);

        assertThat(fixture.getGroupForName("Jim"), is("Others"));
    }

}
