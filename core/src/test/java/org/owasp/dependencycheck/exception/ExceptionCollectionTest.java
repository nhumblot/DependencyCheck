package org.owasp.dependencycheck.exception;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ExceptionCollectionTest {

    @Test
    public void shouldGetMessageTolerateNullMessages() {
        // Given
        NullPointerException npe = new NullPointerException();
        List<Throwable> throwables = Arrays.asList(npe);
        ExceptionCollection collection = new ExceptionCollection(throwables);

        String expectedOutput = "One or more exceptions occurred during analysis:\n" + "\tNullPointerException: ";

        // When
        String output = collection.getMessage();

        // Then
        assertEquals(expectedOutput, output);
    }

}
