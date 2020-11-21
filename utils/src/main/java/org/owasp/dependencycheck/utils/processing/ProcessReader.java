/*
 * This file is part of dependency-check-utils.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2020 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.utils.processing;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 * Utility to read the output from a `Process` and places the output into
 * provide storage containers.
 *
 * @author Jeremy Long
 */
public class ProcessReader implements AutoCloseable {

    /**
     * A reference to the process that will be read.
     */
    private final Process process;

    /**
     * Reader for the error stream.
     */
    private Gobbler errorGobbler = null;
    /**
     * Reader for the input stream.
     */
    private Gobbler inputGobbler = null;
    /**
     * Processor for the input stream.
     */
    private Processor<InputStream> processor = null;
    /**
     * Output store for the data read from the input stream; only used when
     * there is no external processor provided.
     */
    private Store<String> outputStore;
    /**
     * Output store for the data read from the error stream.
     */
    private final Store<String> errorStore;
    /**
     * Output store for any IO Exceptions thrown during processing.
     */
    private final ExceptionStore<IOException> exceptionStore;
    /**
     * A list of threads that were started.
     */
    private final List<Thread> threads = new ArrayList<>();

    /**
     * Creates a new reader for the given process. The output from the process
     * is written to the provided stores.
     *
     * @param process the process to read from
     * @param output receives the content from the output stream
     * @param error receives the content of the error stream
     * @param exception receive any exceptions thrown during processing
     */
    public ProcessReader(Process process, Store<String> output, Store<String> error, ExceptionStore<IOException> exception) {
        this(process, error, exception, null);
        this.outputStore = output;
    }

    /**
     * Creates a new reader for the given process. The output from the process
     * is written to the provided stores.
     *
     * @param process the process to read from
     * @param error receives the content of the error stream
     * @param exception receive any exceptions thrown during processing
     * @param processor used to process the input stream from the process
     */
    public ProcessReader(Process process, Store<String> error, ExceptionStore<IOException> exception, Processor<InputStream> processor) {
        this.process = process;
        this.processor = processor;
        this.outputStore = null;
        this.errorStore = error;
        this.exceptionStore = exception;
    }

    /**
     * Starts the processing of the `process`.
     */
    public void start() {
        errorGobbler = new Gobbler(process.getErrorStream());
        startProcessor(errorGobbler);
        if (outputStore != null) {
            inputGobbler = new Gobbler(process.getInputStream());
            startProcessor(inputGobbler);
        } else if (processor != null) {
            processor.put(process.getInputStream());
            startProcessor(processor);
        }
    }

    private void startProcessor(Processor p) {
        if (p != null) {
            final Thread t = new Thread(p);
            threads.add(t);
            t.start();
        }
    }

    @Override
    public void close() throws InterruptedException {
        process.waitFor();
        for (Thread thread : threads) {
            thread.join();
        }
        errorStore.put(errorGobbler.getText());
        if (errorGobbler.getException() != null) {
            exceptionStore.put(errorGobbler.getException());
        } else if (inputGobbler != null && inputGobbler.getException() != null) {
            exceptionStore.put(inputGobbler.getException());
        }
        errorStore.put(errorGobbler.getText());
        if (outputStore != null) {
            outputStore.put(inputGobbler.getText());
        }
    }

    class Gobbler extends Processor<InputStream> {

        /**
         * A store for an exception - if one is thrown during processing.
         */
        private IOException exception;
        /**
         * A store for the text read from the input stream.
         */
        private String text;

        Gobbler(InputStream inputStream) {
            put(inputStream);
        }

        @Override
        public void run() {
            try {
                final InputStream inputStream = retrieve();
                if (inputStream.available() > 0) {
                    text = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
                }

            } catch (IOException ex) {
                exception = ex;
            }
        }

        public IOException getException() {
            return exception;
        }

        public String getText() {
            return text;
        }
    }
}
