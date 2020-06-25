package com.alexrnv.calcite.adapter.pilosa.it;

import static com.alexrnv.calcite.adapter.pilosa.it.PilosaContainer.PILOSA_API_PORT;

public class PilosaTestDBHelper {

    private final PilosaContainer pilosaContainer;
    private final String baseUrl;
    private final String indexQueryUri;

    public PilosaTestDBHelper(PilosaContainer pilosaContainer, String baseUrl, String indexQueryUri) {
        this.pilosaContainer = pilosaContainer;
        this.indexQueryUri = indexQueryUri;
        this.baseUrl = baseUrl;
    }

    public String getBaseEndpoint() {
        String pilosaUrl = getPilosaEndpoint();
        return String.format(baseUrl, pilosaUrl);
    }

    public String getQueryEndpoint() {
        String pilosaUrl = getPilosaEndpoint();
        String baseUrl = String.format(this.baseUrl, pilosaUrl);
        return baseUrl + indexQueryUri;
    }

    /**
     * @return pilosa host:port endpoint as seen from outside of container
     */
    private String getPilosaEndpoint() {
        return pilosaContainer.getContainerIpAddress() + ":" + pilosaContainer.getMappedPort(PILOSA_API_PORT);
    }
}
