package kuona.web;

import java.io.File;
import java.io.IOException;

public class CollectorLauncher {

//    TODO: Read collectors from elasticsearch

    public static void launch() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "snapci-analyser.jar", "-u http://localhost:9000/metric/palaceintrigue-gonogo -n palaceintrigue-gonogo");
        pb.directory(new File("/Users/ffafara/Projects/java/kuona/out/artifacts/snapci_analyser_jar"));
        Process p = pb.start();
        System.out.println("Collector started");
    }

}
