import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Objects;


public class FileSystemWatcher implements Runnable {

	private final WatchService watchService;
	private final Path root;

	public FileSystemWatcher(WatchService watchService, Path root) {
		this.watchService = Objects.requireNonNull(watchService);
		if (!Files.isDirectory(root)) {
			throw new IllegalArgumentException(root.getFileName() + " is not a directory");
		}
		this.root = root;
	}
	
	private void registerTree(Path path) throws IOException {
		path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
		try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
			for (Path subPath : directoryStream) {
				if (Files.isDirectory(subPath)) {
					registerTree(subPath);
				}
			}
		} 
	}
	
	@Override
	public void run() {
		try {
			registerTree(root);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		for(;;) {
			WatchKey key = null;
			try {
				key = watchService.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (WatchEvent<?> event : key.pollEvents()) {
				System.err.println(event.kind());
			}
			
			key.reset();
		}

	}

}
