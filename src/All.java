import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class All {

	static final String JAVA_22_LAUNCHER = "/home/nipa/.sdkman/candidates/java/22/bin/java";

	public static void main(String[] args) throws IOException {
		try (var solutions = Files.list(Path.of("src"))) {
			solutions
					.filter(solution -> !solution.toString().endsWith("All.java"))
					.sorted()
					.forEach(solution -> {
						try {
							System.out.println("\n--- " + solution.getFileName() + " ---");
							new ProcessBuilder(
									JAVA_22_LAUNCHER, "--enable-preview", "--source", "22", "-ea", solution.toAbsolutePath().toString())
									.inheritIO()
									.start()
									.waitFor();
						} catch (IOException | InterruptedException ex) {
							System.out.println(ex.getMessage());
						}
					});
		}
	}

}