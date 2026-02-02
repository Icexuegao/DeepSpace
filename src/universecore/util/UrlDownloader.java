package universecore.util;

import arc.Core;
import arc.files.Fi;
import arc.func.Cons;
import arc.func.ConsT;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.util.Http;
import arc.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

public class UrlDownloader {
  private static final OrderedMap<String, String> urlReplacers = new OrderedMap<>();

  public static void setMirror(String source, String to) {
    urlReplacers.put(source, to);
  }

  public static void removeMirror(String source) {
    urlReplacers.remove(source);
  }

  public static void clearMirrors() {
    urlReplacers.clear();
  }

  public static void retryDown(String url, ConsT<Http.HttpResponse, Exception> resultHandler, int maxRetry, Cons<Throwable> errHandler) {
    int[] counter = {0};
    Runnable[] get = new Runnable[1];

    for (ObjectMap.Entry<String, String> entry : urlReplacers) {
      if (url.startsWith(entry.key)) {
        url = url.replaceFirst(entry.key, entry.value);
      }
    }

    String realUrl = url;
    get[0] = () -> Http.get(realUrl, resultHandler, e -> {
      if (counter[0]++ <= maxRetry) get[0].run();
      else errHandler.get(e);
    });
    get[0].run();
  }

  public static float[] downloadToStream(String url, OutputStream stream) {
    float[] progress = new float[1];
    retryDown(url, res -> {
      try (stream) {
        InputStream in = res.getResultAsStream();
        long total = res.getContentLength();

        int curr = 0;
        for (int b = in.read(); b != -1; b = in.read()) {
          curr++;
          stream.write(b);
          progress[0] = (float) curr / total;
        }
      }
    }, 5, Log::err);
    return progress;
  }

  public static float[] downloadToFile(String url, Fi file) {
    return downloadToStream(url, file.write());
  }

  public static TextureRegion downloadImg(String url, TextureRegion errDef) {
    TextureRegion result = new TextureRegion(errDef);

    retryDown(url, res -> {
      Pixmap pix = new Pixmap(res.getResult());
      Core.app.post(() -> {
        try {
          Texture tex = new Texture(pix);
          tex.setFilter(Texture.TextureFilter.linear);
          result.set(tex);
          pix.dispose();
        } catch (Exception e) {
          Log.err(e);
        }
      });
    }, 5, Log::err);

    return result;
  }
}
