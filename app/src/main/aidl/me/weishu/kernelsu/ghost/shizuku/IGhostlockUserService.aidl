package me.weishu.kernelsu.ghost.shizuku;

import me.weishu.kernelsu.ghost.shizuku.IGhostlockCallback;
import me.weishu.kernelsu.ghost.shizuku.IGhostlockStatusCallback;

interface IGhostlockUserService {
    void destroy() = 16777114;
    void runExploit(int primaryCpu, int consumerCpu, boolean safeMode, boolean forceAttack, in byte[] profileBlob, @nullable String debugDir, IGhostlockCallback callback, IGhostlockStatusCallback statusCallback) = 2;
}
