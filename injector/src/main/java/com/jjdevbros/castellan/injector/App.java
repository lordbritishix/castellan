package com.jjdevbros.castellan.injector;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.slf4j.bridge.SLF4JBridgeHandler;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jjdevbros.castellan.common.guice.AttendanceReportModule;
import com.jjdevbros.castellan.common.model.WindowsLogEventId;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by lordbritishix on 12/09/15.
 */
@Slf4j
public class App {
    private static class InjectorParams {
        @Parameter(names = {"-c"},
                description = "Location of the config file", required = true)
        @Getter
        private String configFile;

        @Parameter(names = {"-t"},
                description = "Type of event to inject (Login or Logout)", required = true)
        @Getter
        private String type;

        @Parameter(names = {"-u"},
                description = "Username that owns the event", required = true)
        @Getter
        private String userName;

        @Parameter(names = {"-d"},
                description = "Date / time on when the event occurs (in ISO 8601 format). "
                            + "e.g. 2015-11-23T02:21:16+00:00. "
                            + "If not provided, then it uses the date / time today", required = false)
        @Getter
        private String dateTime;

        @Parameter(names = "-help", description = "Prints the help text", help = true)
        @Getter
        private boolean help;
    }

    public static void main(String[] args)
            throws ExecutionException, InterruptedException, IOException, BirtException {
        InjectorParams params = new InjectorParams();
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        Logger.getLogger("global").setLevel(Level.WARNING);

        JCommander jc = new JCommander(params, args);

        if (params.isHelp()) {
            jc.usage();
            return;
        }

        log.info("Injecting event..");

        Injector injector = Guice.createInjector(new AttendanceReportModule(params.getConfigFile()));
        EventWriter writer = injector.getInstance(EventWriter.class);

        LocalDateTime dateTime = null;

        if (params.getDateTime() != null) {
            dateTime = LocalDateTime.parse(params.getDateTime());
        }

        writer.writeEvent(
                params.getUserName(), WindowsLogEventId.fromString(params.getType()), Optional.ofNullable(dateTime));

        log.info("Injection done!");
    }
}
