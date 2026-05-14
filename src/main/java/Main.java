import java.io.PrintWriter;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

public final class Main {

    private static final String[] TEST_CLASS_NAMES = {
        "MatchmakingWindowTest",
        "RatingAvlTreeTest",
        "MatchmakingQueueTest",
    };

    public static void main(String[] args) throws ClassNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        LauncherDiscoveryRequestBuilder b = LauncherDiscoveryRequestBuilder.request();
        for (String name : TEST_CLASS_NAMES) {
            b.selectors(DiscoverySelectors.selectClass(Class.forName(name, true, cl)));
        }
        LauncherDiscoveryRequest request = b.build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        PrintWriter out = new PrintWriter(System.out, true);
        listener.getSummary().printFailuresTo(out);
        listener.getSummary().printTo(out);

        long failures = listener.getSummary().getTotalFailureCount();
        if (failures > 0) {
            System.exit(1);
        }
    }

    private Main() {}
}
