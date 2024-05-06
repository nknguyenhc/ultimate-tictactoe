import de.mirkosertic.bytecoder.api.Export;

import manager.WebManager;

public class Web {
    private static final WebManager webManager = new WebManager();

    @Export("getResponse")
    public static String getResponse(String input) {
        return webManager.getResponse(input);
    }

    public static void main(String[] args) {}
}
