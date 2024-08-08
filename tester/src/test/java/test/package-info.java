package test;

/*
about javacpp
https://github.com/bytedeco/javacpp/blob/master/src/main/java/org/bytedeco/javacpp/presets/javacpp.java
https://wjw465150.github.io/blog/java/my_data/JNI/JavaCPP%20%E6%8A%80%E6%9C%AF%E4%BD%BF%E7%94%A8%E7%BB%8F%E9%AA%8C%E6%80%BB%E7%BB%93/index.html


about openal
https://github.com/kcat/openal-soft/blob/master/examples/alrecord.c

将一个高清流复制为几个不同清晰度的流重新发布，其中音频不变

ffmpeg -re -i rtmp://server/live/high_FMLE_stream
-acodec copy -vcodec x264lib -s 640×360 -b 500k -vpre medium -vpre baseline rtmp://server/live/baseline_500k
-acodec copy -vcodec x264lib -s 480×272 -b 300k -vpre medium -vpre baseline rtmp://server/live/baseline_300k
-acodec copy -vcodec x264lib -s 320×200 -b 150k -vpre medium -vpre baseline rtmp://server/live/baseline_150k
-acodec libfaac -vn -ab 48k rtmp://server/live/audio_only_AAC_48k

 */