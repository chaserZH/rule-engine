package com.tommy.rulesengine.constants;

public enum FileType {

    JSON("json"),
    YAML("yaml"),
    YML("yml"),
    PROPERTIES("properties");

    private final String format;

    FileType(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public static FileType fromFormat(String format) {
        for (FileType type : FileType.values()) {
            if (type.getFormat().equals(format)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No FileType found for format: " + format);
    }

    public static FileType fromSuffix(String suffix) {
        for (FileType type : FileType.values()) {
            if (type.getFormat().equals(suffix)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No FileType found for suffix: " + suffix);
    }

    public static FileType fromFilename(String filename) {
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        return fromSuffix(suffix);
    }
}
