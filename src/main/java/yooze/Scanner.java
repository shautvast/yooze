package yooze;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface Scanner {
	public List<InspectableClasspath> scanArchive(String archiveName) throws IOException;

	public List<InspectableClasspath> scanArchive(File file) throws IOException;
}
