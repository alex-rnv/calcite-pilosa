package com.alexrnv.calcite.adapter.pilosa.module;

import org.apache.commons.cli.*;

public class PilosaCommandLine extends CommandLine {

    public static final String PORT_PARAM = "port";
    public static final String MODEL_URI_PARAM = "model-uri";

    public static CommandLine init(String[] args) {
        Options options = new Options();
        options.addOption("p", PORT_PARAM, true, "Port to listen on")
                .addOption("m", MODEL_URI_PARAM, true, "Calcite model file URI");

        CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse( options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
