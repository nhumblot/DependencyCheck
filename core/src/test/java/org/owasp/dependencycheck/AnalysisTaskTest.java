package org.owasp.dependencycheck;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Test;
import org.owasp.dependencycheck.analyzer.FileTypeAnalyzer;
import org.owasp.dependencycheck.analyzer.HintAnalyzer;
import org.owasp.dependencycheck.dependency.Dependency;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AnalysisTaskTest extends BaseTest {

    @Mocked
    private FileTypeAnalyzer fileTypeAnalyzer;

    @Mocked
    private Dependency dependency;

    @Mocked
    private Engine engine;


    @Test
    public void shouldAnalyzeReturnsTrueForNonFileTypeAnalyzers() {
        AnalysisTask instance = new AnalysisTask(new HintAnalyzer(), null, null, null);
        boolean shouldAnalyze = instance.shouldAnalyze();
        assertTrue(shouldAnalyze);
    }

    @Test
    public void shouldAnalyzeReturnsTrueIfTheFileTypeAnalyzersAcceptsTheDependency() {
        String actualFilePath = "";
        final File dependencyFile = new File(actualFilePath);
        new Expectations() {{
            dependency.getActualFilePath();
            result = actualFilePath;
            minTimes = 0;

            dependency.getActualFile();
            result = dependencyFile;

            fileTypeAnalyzer.accept(dependencyFile);
            result = true;
        }};

        AnalysisTask analysisTask = new AnalysisTask(fileTypeAnalyzer, dependency, null, null);

        boolean shouldAnalyze = analysisTask.shouldAnalyze();
        assertTrue(shouldAnalyze);
    }

    @Test
    public void shouldAnalyzeReturnsFalseIfTheFileTypeAnalyzerDoesNotAcceptTheDependency() {
        String actualFilePath = "";
        final File dependencyFile = new File(actualFilePath);
        new Expectations() {{
            dependency.getActualFilePath();
            result = actualFilePath;
            minTimes = 0;

            dependency.getActualFile();
            result = dependencyFile;

            fileTypeAnalyzer.accept(dependencyFile);
            result = false;
        }};

        AnalysisTask analysisTask = new AnalysisTask(fileTypeAnalyzer, dependency, null, null);

        boolean shouldAnalyze = analysisTask.shouldAnalyze();
        assertFalse(shouldAnalyze);
    }

    @Test
    public void shouldAnalyzeReturnsFalseIfTheActualFilePathIsNullToPreventNullPointerException() {
        // Given
        new Expectations() {{
            dependency.getActualFilePath();
            result = null;
            minTimes = 0;

            dependency.getActualFile();
            result = new NullPointerException();
            minTimes = 0;
        }};

        AnalysisTask analysisTask = new AnalysisTask(fileTypeAnalyzer, dependency, null, null);

        // When
        boolean shouldAnalyze = analysisTask.shouldAnalyze();

        // Then
        assertFalse(shouldAnalyze);
    }

    @Test
    public void taskAnalyzes() throws Exception {
        final AnalysisTask analysisTask = new AnalysisTask(fileTypeAnalyzer, dependency, engine, null);
        new Expectations(analysisTask) {{
            analysisTask.shouldAnalyze();
            result = true;
        }};

        analysisTask.call();

        new Verifications() {{
            fileTypeAnalyzer.analyze(dependency, engine);
            times = 1;
        }};
    }

    @Test
    public void taskDoesNothingIfItShouldNotAnalyze() throws Exception {
        final AnalysisTask analysisTask = new AnalysisTask(fileTypeAnalyzer, dependency, engine, null);
        new Expectations(analysisTask) {{
            analysisTask.shouldAnalyze();
            result = false;
        }};

        analysisTask.call();

        new Verifications() {{
            fileTypeAnalyzer.analyze(dependency, engine);
            times = 0;
        }};
    }
}
