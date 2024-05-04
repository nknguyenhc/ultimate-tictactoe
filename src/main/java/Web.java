import de.mirkosertic.bytecoder.api.Export;

public class Web {
    @Export("getResponse")
    public static String getResponse() {
        return String.format("Number: %d", 0);
    }

    public static void main(String[] args) {}
}
