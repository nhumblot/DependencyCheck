package org.owasp.dependencycheck.data.update;

import static org.junit.Assert.*;

import org.junit.Test;

public class NvdCveUpdaterTest {

    @Test
    public void should_getMetaUrl_process_gz_url() {
        // Given
        NvdCveUpdater updater = new NvdCveUpdater();

        String input = "https://nvd.nist.gov/feeds/json/cve/1.1/nvdcve-1.1-modified.json.gz";

        String expectedOutput = "https://nvd.nist.gov/feeds/json/cve/1.1/nvdcve-1.1-modified.meta";

        // When
        String output = updater.getMetaUrl(input);

        // Then
        assertEquals(expectedOutput, output);
    }

    @Test
    public void should_getMetaUrl_process_zip_url() {
        // Given
        NvdCveUpdater updater = new NvdCveUpdater();

        String input = "https://nvd.nist.gov/feeds/json/cve/1.1/nvdcve-1.1-modified.json.zip";

        String expectedOutput = "https://nvd.nist.gov/feeds/json/cve/1.1/nvdcve-1.1-modified.meta";

        // When
        String output = updater.getMetaUrl(input);

        // Then
        assertEquals(expectedOutput, output);
    }
}
