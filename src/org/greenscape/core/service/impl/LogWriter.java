package org.greenscape.core.service.impl;

import java.util.Date;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

@Component
public class LogWriter implements LogListener {
	private LogReaderService logReaderService;

	@Activate
	void activate() {
		logReaderService.addLogListener(this);
	}

	@Reference
	public void setLogReaderService(LogReaderService logReaderService) {
		this.logReaderService = logReaderService;
	}

	public void unsetLogReaderService(LogReaderService logReaderService) {
		this.logReaderService = null;
	}

	@Override
	public void logged(LogEntry entry) {
		System.out.println(new Date(entry.getTime()) + " " + entry.getLevel() + " " + entry.getMessage());
		if (entry.getException() != null) {
			entry.getException().printStackTrace();
			System.out.println();
		}
	}

}
