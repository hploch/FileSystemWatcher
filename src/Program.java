import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.WatchService;






public class Program {
	public static void main(String[] args) throws IOException {
		WatchService watchService = FileSystems.getDefault().newWatchService();
		
		new Thread(new FileSystemWatcher(watchService, Paths.get("c:/temp"))).run();
	}
}
