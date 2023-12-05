package com.example.hangwei.data.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.example.hangwei.R;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 *    desc   : Glide 全局配置
 */
@GlideModule
public final class GlideConfig extends AppGlideModule {

    /** 本地图片缓存文件最大值 */
    private static final int IMAGE_DISK_CACHE_MAX_SIZE = 500 * 1024 * 1024;

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(100000, TimeUnit.SECONDS)//设置连接超时时间
            .readTimeout(200000, TimeUnit.SECONDS)//设置读取超时时间
            .build();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        // 读写外部缓存目录不需要申请存储权限
        File diskCacheFile = new File(context.getCacheDir(), "glide");
        // 如果这个路径是一个文件
        if (diskCacheFile.exists() && diskCacheFile.isFile()) {
            // 执行删除操作
            diskCacheFile.delete();
        }
        // 如果这个路径不存在
        if (!diskCacheFile.exists()) {
            // 创建多级目录
            diskCacheFile.mkdirs();
        }
        builder.setDiskCache(() -> DiskLruCacheWrapper.create(diskCacheFile, IMAGE_DISK_CACHE_MAX_SIZE));

        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();

        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);

        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));

        builder.setDefaultRequestOptions(new RequestOptions()
                // 设置默认加载中占位图
                .placeholder(R.drawable.image_loading_ic)
                // 设置默认加载出错占位图
                .error(R.drawable.image_error_ic));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        // Glide 默认使用的是 HttpURLConnection 来做网络请求，这里切换成更高效的 OkHttp
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpLoader.Factory((Call.Factory) okHttpClient));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}