package ch.thurikaAlbin.qualifiedreciever.data.model;

public enum QRCodeType {
    Scanned ("Scanned"),
    GeneratedUrl ("Generated - URL"),
    GeneratedWIFI ("Generated - WIFI");

    final String name;

    QRCodeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
