# How to capture thread dump

1. open terminal, run jcmd command, you will see the Java process id and command, here the pid is 57853
```bash
hostname:~ ilikecopy$ jcmd
57853 com.yiqin.tool.thread.analysis.ThreadAnalysisApplication --spring.output.ansi.enabled=always
```

2. capture thread dump
```bash
jcmd 57853 Thread.print > ~/Download/thread.txt
```

3. then check the thread.txt in your Download folder
