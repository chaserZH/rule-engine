package com.tommy.rulesengine.parser;

import com.tommy.rulesengine.constants.FileType;

public enum ParserType {

    YAML("yaml"),
    JSON("json"),
    PROPERTIES("properties"),
    ;

    private final String parserType;

    ParserType(String parserType) {
        this.parserType = parserType;
    }

    public String getParserType() {
        return parserType;
    }

    public static ParserType getByParserType(String parserType) {
        for (ParserType type : values()) {
            if (type.getParserType().equals(parserType)) {
                return type;
            }
        }
        return null;
    }

    public static boolean isSupport(String parserType) {
        return getByParserType(parserType) != null;
    }

    public static ParserType convertFromFileType(FileType fileType) {
        switch (fileType) {
            case JSON:
                return JSON;
            case YAML:
                return YAML;
            case YML:
                return YAML;
            case PROPERTIES:
                return PROPERTIES;
            default:
                throw new IllegalArgumentException("No ParserType found for fileType: " + fileType);
        }
    }
}
