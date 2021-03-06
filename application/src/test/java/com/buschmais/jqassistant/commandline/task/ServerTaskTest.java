package com.buschmais.jqassistant.commandline.task;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

import com.buschmais.jqassistant.commandline.CliExecutionException;
import com.buschmais.jqassistant.core.plugin.api.PluginRepository;
import com.buschmais.jqassistant.core.store.impl.EmbeddedGraphStore;
import com.buschmais.jqassistant.neo4j.backend.bootstrap.EmbeddedNeo4jServer;

import org.apache.commons.cli.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ServerTaskTest {

    @Mock
    PluginRepository pluginRepository;

    @Mock
    private EmbeddedGraphStore store;

    @Mock
    private EmbeddedNeo4jServer server;

    @BeforeEach
    public final void setUp() {
        doReturn(server).when(store).getServer();
    }

    private ServerTask serverTask = new ServerTask();

    @Test
    public void daemon() throws CliExecutionException, ParseException {
        startServer("-daemon");

        verify(server).start();
    }

    @Test
    public void standalone() throws CliExecutionException, ParseException {
        String data = "\r\n";
        InputStream stdin = System.in;
        try {
            System.setIn(new ByteArrayInputStream(data.getBytes()));
            startServer(new String[] {});
        } finally {
            System.setIn(stdin);

        }

        verify(server).start();
        verify(server).stop();
    }

    private void startServer(String... arguments) throws ParseException, CliExecutionException {
        Options options = new Options();
        for (Option option : serverTask.getOptions()) {
            options.addOption(option);
        }
        CommandLineParser parser = new BasicParser();
        CommandLine commandLine = parser.parse(options, arguments);

        serverTask.initialize(pluginRepository, new HashMap<>());
        serverTask.withOptions(commandLine);
        serverTask.executeTask(store);
    }

}
