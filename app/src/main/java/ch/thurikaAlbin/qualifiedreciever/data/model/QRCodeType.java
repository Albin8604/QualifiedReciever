package ch.thurikaAlbin.qualifiedreciever.data.model;

/**
 * @author Thurika & Albin
 * @since 17.11.2022
 * Enum for the QRCodeTypes
 */
public enum QRCodeType {
    Scanned ("Scanned"),
    GeneratedUrl ("Generated - URL"),
    GeneratedWIFI ("Generated - WIFI");

    final String name;

    /**
     * Constructor
     * @param name name of the qr type
     */
    QRCodeType(String name) {
        this.name = name;
    }

    /**
     * Getter
     * @return name
     */
    public String getName() {
        return name;
    }
}
