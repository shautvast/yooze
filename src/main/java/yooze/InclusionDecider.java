package yooze;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InclusionDecider {
	private static Logger log = LoggerFactory.getLogger(InclusionDecider.class);
	private Pattern[] packageIncludePatterns;
	private Pattern[] packageExcludePatterns;

	public boolean shouldSkip(String className) {
		if (!isIncluded(className)) {
			log.debug("skipping {}", className);
			return true;
		}
		if (isExcluded(className)) {
			log.debug("skipping {}", className);
			return true;
		}

		return false;
	}

	public boolean isExcluded(String className) {
		if (packageExcludePatterns != null) {
			for (Pattern excludePattern : packageExcludePatterns) {
				Matcher matcher = excludePattern.matcher(className);
				if (matcher.find()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isIncluded(String className) {
		if (packageIncludePatterns != null) {
			for (Pattern includePattern : packageIncludePatterns) {
				Matcher matcher = includePattern.matcher(className);
				if (matcher.find()) {
					return true;
				}
			}
		}
		return false;
	}

	public void setPackageIncludePatterns(String... packageIncludePatterns) {
		if (packageIncludePatterns != null) {
			this.packageIncludePatterns = new Pattern[packageIncludePatterns.length];
			int i = 0;
			for (String pattern : packageIncludePatterns) {
				this.packageIncludePatterns[i++] = Pattern.compile(pattern);
			}
		}
	}

	public void setPackageExcludePatterns(String... packageExcludePatterns) {
		if (packageExcludePatterns != null) {
			this.packageExcludePatterns = new Pattern[packageExcludePatterns.length];
			int i = 0;
			for (String pattern : packageExcludePatterns) {
				this.packageExcludePatterns[i++] = Pattern.compile(pattern);
			}
		}
	}
}
