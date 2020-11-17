package com.degradators.degradators.common.adapter;

import android.content.Context;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

public class VideoCacheSingleton {
        private static final int MAX_VIDEO_CACHE_SIZE_IN_BYTES = 200 * 1024 * 1024;  // 200MB

        private static Cache sInstance;

        public static Cache getInstance(Context context) {
            if (sInstance != null) return sInstance;
            else return sInstance = new SimpleCache(new File(context.getCacheDir(), "video"), new LeastRecentlyUsedCacheEvictor(MAX_VIDEO_CACHE_SIZE_IN_BYTES), new ExoDatabaseProvider(context));
        }

}
