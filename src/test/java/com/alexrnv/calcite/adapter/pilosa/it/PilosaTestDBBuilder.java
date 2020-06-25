package com.alexrnv.calcite.adapter.pilosa.it;

import com.github.dockerjava.api.model.ContainerNetwork;
import org.junit.Assert;
import org.testcontainers.containers.Container;

import java.io.IOException;
import java.util.Collection;

import static com.alexrnv.calcite.adapter.pilosa.it.PilosaContainer.PILOSA_API_PORT;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class PilosaTestDBBuilder {

    private static final String BASE_PILOSA_URL = "http://%s";
    private static final String QUERY_REPOSITORY_INDEX_URI = "/index/repository/query";
    private static final String CREATE_REPOSITORY_INDEX_URL = "http://%s/index/repository";
    private static final String CREATE_STARGAZER_FIELD_URL = "http://%s/index/repository/field/stargazer";
    private static final String CREATE_LANGUAGE_FIELD_URL = "http://%s/index/repository/field/language";
    private static final String PILOSA_API_RESPONSE_SUCCESS = "{\"success\":true}";
    private static final String STARGAZER_FIELD_OPTIONS = "{\"options\": {\"type\": \"time\", \"timeQuantum\": \"YMD\"}}";

    private final PilosaContainer pilosa;

    PilosaTestDBBuilder(PilosaContainer pilosa) {
        this.pilosa = pilosa;
    }

    /**
     * Runs through these steps:
     * #https://www.pilosa.com/docs/latest/getting-started/#sample-project
     * to prepare "stargazer" database.
     */
    PilosaTestDBHelper setUpTestDB() {
        String endpoint = getInternalPilosaEndpoint();
        try {
            createRepositoryIndex(endpoint);
            createStargazerField(endpoint);
            createLanguageField(endpoint);
            importStargazerTestData(endpoint);
            importLanguageTestData(endpoint);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new PilosaTestDBHelper(pilosa, BASE_PILOSA_URL, QUERY_REPOSITORY_INDEX_URI);
    }

    /**
     * @return pilosa host:port endpoint as seen from inside the container (for "docker exec..." calls)
     */
    private String getInternalPilosaEndpoint() {
        Collection<ContainerNetwork> networks = pilosa.getContainerInfo().getNetworkSettings().getNetworks().values();
        if (networks.size() != 1) {
            throw new RuntimeException("one default network is expected");
        }
        ContainerNetwork network = networks.iterator().next();
        String internalPilosaIP = network.getIpAddress();
        return internalPilosaIP + ":" + PILOSA_API_PORT;
    }

    private void createRepositoryIndex(String endpoint) throws IOException, InterruptedException {
        String createIndexUrl = String.format(CREATE_REPOSITORY_INDEX_URL, endpoint);
        Container.ExecResult result = pilosa.execInContainer("curl", createIndexUrl, "-X", "POST");
        Assert.assertEquals(0, result.getExitCode());
        assertThatJson(PILOSA_API_RESPONSE_SUCCESS).isEqualTo(result.getStdout());
    }

    private void createStargazerField(String endpoint) throws IOException, InterruptedException {
        String createStargazerFieldUrl = String.format(CREATE_STARGAZER_FIELD_URL, endpoint);
        Container.ExecResult result = pilosa.execInContainer("curl", createStargazerFieldUrl, "-X", "POST", "-d", STARGAZER_FIELD_OPTIONS);
        Assert.assertEquals(0, result.getExitCode());
        assertThatJson(PILOSA_API_RESPONSE_SUCCESS).isEqualTo(result.getStdout());
    }

    private void createLanguageField(String endpoint) throws IOException, InterruptedException {
        String createLanguageFieldUrl = String.format(CREATE_LANGUAGE_FIELD_URL, endpoint);
        Container.ExecResult result = pilosa.execInContainer("curl", createLanguageFieldUrl, "-X", "POST");
        Assert.assertEquals(0, result.getExitCode());
        assertThatJson(PILOSA_API_RESPONSE_SUCCESS).isEqualTo(result.getStdout());
    }

    private void importStargazerTestData(String endpoint) throws IOException, InterruptedException {
        String host = endpoint.substring(0, endpoint.indexOf(":"));
        Container.ExecResult result = pilosa.execInContainer("curl", "-O", "https://raw.githubusercontent.com/pilosa/getting-started/master/stargazer.csv");
        Assert.assertEquals(0, result.getExitCode());
        result = pilosa.execInContainer("/pilosa", "import", "-i", "repository", "--host", host, "-f", "stargazer", "/stargazer.csv");
        Assert.assertEquals(0, result.getExitCode());
    }

    private void importLanguageTestData(String endpoint) throws IOException, InterruptedException {
        String host = endpoint.substring(0, endpoint.indexOf(":"));
        Container.ExecResult result = pilosa.execInContainer("curl", "-O", "https://raw.githubusercontent.com/pilosa/getting-started/master/language.csv");
        Assert.assertEquals(0, result.getExitCode());
        result = pilosa.execInContainer("/pilosa", "import", "-i", "repository", "--host", host, "-f", "language", "/language.csv");
        Assert.assertEquals(0, result.getExitCode());
    }

}
