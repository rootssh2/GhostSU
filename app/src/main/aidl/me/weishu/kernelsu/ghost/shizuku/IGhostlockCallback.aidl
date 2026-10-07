package me.weishu.kernelsu.ghost.shizuku;

oneway interface IGhostlockCallback {
    void onLog(String line);
    void onComplete(int exitCode);
}
