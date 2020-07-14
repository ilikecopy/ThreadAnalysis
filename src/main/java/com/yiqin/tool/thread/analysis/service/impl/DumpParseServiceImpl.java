package com.yiqin.tool.thread.analysis.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.yiqin.tool.thread.analysis.model.StackMethod;
import com.yiqin.tool.thread.analysis.model.ThreadDump;
import com.yiqin.tool.thread.analysis.model.ThreadInfo;
import com.yiqin.tool.thread.analysis.model.ThreadLock;
import com.yiqin.tool.thread.analysis.service.DumpParseService;

@Service
public class DumpParseServiceImpl implements DumpParseService {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		DumpParseService service = new DumpParseServiceImpl();
		ThreadDump dump = service.parse(new FileInputStream(new File("src/main/resources/samples/thread.txt")));
		System.out.println(dump);
	}
	
	//Thread dump header
	private static final Pattern HEADER_THREAD_ID = Pattern.compile("^(\\d+):$");
	private static final Pattern HEADER_GENERATE_TIME = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$");
	private static final Pattern HEADER_JDK = Pattern.compile("^Full thread dump (.+ (\\(.+\\))):$"); 
	
	//Thread stack part
	private static final Pattern THREAD_LINE_PATTERN = Pattern.compile("^\"(?<name>.+)\"\\s+(?:#[0-9]+\\s+)?(?<daemon>daemon\\s+)?(prio=(?<prio>[0-9]+)\\s+)?(?<osPrio>os_prio=-?[0-9]+\\s+)?(?<cpu>cpu=-?[0-9\\.]+ms\\s+)?(?<elapsed>elapsed=-?[0-9\\.]+s\\s+)?tid=(?<tid>0x[0-9a-f]+)\\s+nid=(?<nid>0x[0-9a-f]+)\\s+(?<other>.+)$");
	private static final Pattern JVM_THREAD_LINE_PATTERN = Pattern.compile("^\"(.+)\" os_prio=(\\d+) tid=(0x[0-9a-f]+) nid=(0x[0-9a-f]+) (.*)$");
	private static final Pattern THREAD_STATE_PATTERN = Pattern.compile("^\\s+java\\.lang\\.Thread\\.State: ([^ ]+)(?: \\((.+)\\))?");
	private static final Pattern THREAD_STACK_LINE_JAVA_METHOD_PATTERN = Pattern.compile("^\\s+at (.+\\..+)\\((.+\\.\\w+):(\\d{1,})\\)$");
	private static final Pattern THREAD_STACK_LINE_JAVA_UNKNOWN_SOURCE_PATTERN = Pattern.compile("^\\s+at (.+\\..+)\\(Unknown Source\\)$");
	private static final Pattern THREAD_STACK_LINE_JAVA_GENERATED_PATTERN = Pattern.compile("^\\s+at (.+\\..+)\\(<generated>\\)$");
	private static final Pattern THREAD_STACK_LINE_NATIVE_METHOD_PATTERN = Pattern.compile("^\\s+at (.+\\..+)\\(Native Method\\)$");
	private static final Pattern THREAD_STACK_LINE_LOCK_PATTERN = Pattern.compile("^\\s+- locked <(0x[0-9a-f]{8,})> \\(a (.+)\\)$");
	private static final Pattern THREAD_STACK_LINE_LOCK_ELIMINATED_PATTERN = Pattern.compile("^\\s+- eliminated <(0x[0-9a-f]{8,})> \\(a (.+)\\)$");
	private static final Pattern THREAD_STACK_LINE_WAITING_TO_LOCK_PATTERN = Pattern.compile("^\\s+- waiting to lock <(0x[0-9a-f]{8,})> \\(a (.+)\\)$");
	private static final Pattern THREAD_STACK_LINE_WAITING_ON_PATTERN = Pattern.compile("^\\s+- waiting on <(0x[0-9a-f]{8,})> \\(a (.+)\\)$");
	private static final Pattern THREAD_STACK_LINE_PARKING_TO_WAIT_PATTERN = Pattern.compile("^\\s+- parking to wait for  <(0x[0-9a-f]{8,})> \\(a (.+)\\)$");
	private static final Pattern THREAD_STACK_LINE_LOCKED_OWNABLE_SYNCHRONIZERS_PATTERNN = Pattern.compile("^\\s+Locked ownable synchronizers:$");
	private static final Pattern THREAD_STACK_LINE_LOCKED_OWNABLE_SYNCHRONIZERS_LOCK_PATTERNN = Pattern.compile("^\\s+- <(0x[0-9a-f]{8,})> \\(a (.+)\\)$");
	private static final Pattern THREAD_STACK_LINE_LOCKED_OWNABLE_SYNCHRONIZERS_LOCK_NONE_PATTERNN = Pattern.compile("^\\s+- None$");//- None
	private static final Pattern THREAD_STACK_LINE_JNI_GLOBAL_REF_PATTERNN = Pattern.compile("^JNI global references: (\\d+)$");//JNI global references: 1822
	private static final Pattern THREAD_STACK_LINE_FOUND_JAVA_LEVEL_DEADLOCK_PATTERNN = Pattern.compile("^Found one Java-level deadlock:$");//Found one Java-level deadlock:
	
	public ThreadDump parse(InputStream inputStream) throws IOException {
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			Matcher matcher = null;
			String line = null;
			ThreadDump dump = new ThreadDump();
			
			//read header part
			for (line = reader.readLine(); null != line && !StringUtils.isEmpty(line.trim()); line = reader.readLine()) {
				matcher = HEADER_THREAD_ID.matcher(line);
				if (matcher.matches()) {
					dump.setDumpId(Long.parseLong(matcher.group(1)));
					continue;
				}
				
				matcher = HEADER_GENERATE_TIME.matcher(line);
				if (matcher.matches()) {
					dump.setGenerateTime(line.trim());
					continue;
				}
				
				matcher = HEADER_JDK.matcher(line);
				if (matcher.matches()) {
					dump.setJdkInfo(matcher.group(1));
					continue;
				}
				
				System.out.println("error -> can't parse this line: \t" + line);
			}
			
			//thread stacktrace
			boolean jumpToOuterLoop = false;
			boolean alreadyRead = false;
			//ThreadInfo curThreadInfo = null;
			for (line = reader.readLine(); null != line; line = alreadyRead ? line :reader.readLine()) {//outer loop
				alreadyRead = false;

				//thread meta data line
				matcher = THREAD_LINE_PATTERN.matcher(line);
				if (matcher.matches()) {
					//set thread meta data
					ThreadInfo ti = new ThreadInfo();
					ti.setName(matcher.group("name"));
					ti.setDaemon(null == matcher.group("daemon") ? false : true);
					ti.setPrio(null == matcher.group("prio") ? -1 : Integer.parseInt(matcher.group("prio")));
					ti.setOsPrio(matcher.group("osPrio"));
					ti.setCpu(matcher.group("cpu"));
					ti.setElapsed(matcher.group("elapsed"));
					ti.setTid(matcher.group("tid"));
					ti.setNid(matcher.group("nid"));
					ti.setOther(matcher.group("other"));
					dump.getThreadIdMap().put(ti.getTid(), ti);
					//curThreadInfo = ti;
					StringBuilder threadRaw = new StringBuilder(line);
					
					line = reader.readLine();
					matcher = THREAD_STATE_PATTERN.matcher(line);
					if (matcher.matches()) {
						//set thread state
						ti.setState(matcher.group(1));
						threadRaw.append(line);
						
						List<StackMethod> stack = new ArrayList<StackMethod>();
						ti.setStack(stack);
						for (line = reader.readLine(); null != line; line = reader.readLine()) {//inner loop
							threadRaw.append(line);
							matcher = THREAD_STACK_LINE_JAVA_METHOD_PATTERN.matcher(line);
							if (matcher.matches()) {
								StackMethod stackLine = new StackMethod();
								stackLine.setRaw(line);
								stackLine.setMethod(matcher.group(1));
								stackLine.setClassName(matcher.group(2));
								stackLine.setLineNum(Integer.parseInt(matcher.group(3)));
								stack.add(stackLine);
								continue;
							}
							
							matcher = THREAD_STACK_LINE_NATIVE_METHOD_PATTERN.matcher(line);
							if (matcher.matches()) {
								StackMethod stackLine = new StackMethod();
								stackLine.setRaw(line);
								stackLine.setMethod(matcher.group(1));
								stackLine.setClassName("Native Method");
								stack.add(stackLine);
								continue;
							}
							
							matcher = THREAD_STACK_LINE_LOCK_PATTERN.matcher(line);
							if (matcher.matches()) {
								ThreadLock lock = new ThreadLock();
								lock.setRaw(line);
								lock.setObjectId(matcher.group(1));
								lock.setClassName(matcher.group(2));
								
								//TODO handle Lock
								continue;
							}
							
							matcher = THREAD_STACK_LINE_LOCK_ELIMINATED_PATTERN.matcher(line);
							if (matcher.matches()) {
								//do nothing, as this is lock elimiated, just ignore
								continue;
							}
							
							matcher = THREAD_STACK_LINE_WAITING_TO_LOCK_PATTERN.matcher(line);
							if (matcher.matches()) {
								ThreadLock lock = new ThreadLock();
								lock.setRaw(line);
								lock.setObjectId(matcher.group(1));
								lock.setClassName(matcher.group(2));
								//TODO handle lock
								continue;
							}
							
							matcher = THREAD_STACK_LINE_WAITING_ON_PATTERN.matcher(line);
							if (matcher.matches()) {
								ThreadLock lock = new ThreadLock();
								lock.setRaw(line);
								lock.setObjectId(matcher.group(1));
								lock.setClassName(matcher.group(2));
								//TODO handle lock
								continue;
							}
							
							matcher = THREAD_STACK_LINE_PARKING_TO_WAIT_PATTERN.matcher(line);
							if (matcher.matches()) {
								ThreadLock lock = new ThreadLock();
								lock.setRaw(line);
								lock.setObjectId(matcher.group(1));
								lock.setClassName(matcher.group(2));
								//TODO handle lock
								continue;
							}
							
							matcher = THREAD_STACK_LINE_JAVA_UNKNOWN_SOURCE_PATTERN.matcher(line);
							if (matcher.matches()) {
								StackMethod stackLine = new StackMethod();
								stackLine.setRaw(line);
								stackLine.setMethod(matcher.group(1));
								stackLine.setClassName("Unknown Source");
								stack.add(stackLine);
								continue;
							}
							
							matcher = THREAD_STACK_LINE_JAVA_GENERATED_PATTERN.matcher(line);
							if (matcher.matches()) {
								StackMethod stackLine = new StackMethod();
								stackLine.setRaw(line);
								stackLine.setMethod(matcher.group(1));
								stackLine.setClassName("<generated>");
								stack.add(stackLine);
								continue;
							}

							if (StringUtils.isEmpty(line.trim())) {//blank line
								jumpToOuterLoop = true;
								break;//jump to outer loop
							} else {
								//no match
								System.err.println("not match any1: \t" + line);
							}
						}
					} else {

						//start a new thread
						alreadyRead = true;
						continue;
					}
				}
				
				if (jumpToOuterLoop) {
					jumpToOuterLoop = false;
					continue;
				}
				
				if (null == line || StringUtils.isEmpty(line.trim())) {//blank line
					continue;
				}
				
				matcher = JVM_THREAD_LINE_PATTERN.matcher(line);//JVM thread
				if (matcher.matches()) {
					//set thread meta data
					System.out.println("catch: \t" + line);//TODO 
					continue;
				}
				
				matcher = THREAD_STACK_LINE_LOCKED_OWNABLE_SYNCHRONIZERS_PATTERNN.matcher(line);//locked ownable synchronizers
				if (matcher.matches()) {
					String previousLine = line;
					line = reader.readLine();
					matcher = THREAD_STACK_LINE_LOCKED_OWNABLE_SYNCHRONIZERS_LOCK_PATTERNN.matcher(line);
					if (matcher.matches()) {
						//set synchronizers lock id
						ThreadLock lock = new ThreadLock();
						lock.setRaw(previousLine + "\n" + line);
						lock.setObjectId(matcher.group(1));
						lock.setClassName(matcher.group(2));
						
						//TODO handle Lock 
					} else {
						matcher = THREAD_STACK_LINE_LOCKED_OWNABLE_SYNCHRONIZERS_LOCK_NONE_PATTERNN.matcher(line);
						if (matcher.matches()) {
							//set empty lock id
							ThreadLock lock = new ThreadLock();
							lock.setRaw(previousLine + "\n" + line);
							
							//TODO handle Lock 
						} else {
							System.err.println("error -> must be lock id, but :\t" + line);
						}
					}
					continue;
				}
				
				matcher = THREAD_STACK_LINE_JNI_GLOBAL_REF_PATTERNN.matcher(line);
				if (matcher.matches()) {
					//set JNI global references
					continue;
				}
				
				matcher = THREAD_STACK_LINE_FOUND_JAVA_LEVEL_DEADLOCK_PATTERNN.matcher(line);
				if (matcher.matches()) {
					//Found one Java-level deadlock: -> start
					//Found 2 deadlocks. -> end
					//String curThreadName = null;
					//ThreadLock lock = null;
					
					do {
						//TODO handle the deadlock part
					} while (!matcher.matches());
					//dump.setJavaDeadLocks(deadlocks);
					continue;
				}
				
				System.err.println("not match any2: \t" + line);//TODO how to notity in dev mod?
			}
			
			return dump;
		}
	}
}
