package yooze.scanner;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import yooze.InspectableClasspath;

public class ArchiveScannerTest {
	@InjectMocks
	private ArchiveScanner archiveScanner;

	@Mock
	private WarScanner warScanner;

	@Before
	public void createArchiveScanner() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void nothing() {
		List<InspectableClasspath> scanArchive = archiveScanner.scanArchive(".");

	}
}
