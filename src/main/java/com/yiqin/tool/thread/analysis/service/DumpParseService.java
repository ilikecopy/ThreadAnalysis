package com.yiqin.tool.thread.analysis.service;

import java.io.IOException;
import java.io.InputStream;

import com.yiqin.tool.thread.analysis.model.ThreadDump;

public interface DumpParseService {

	public ThreadDump parse(InputStream inputStream) throws IOException;
}
