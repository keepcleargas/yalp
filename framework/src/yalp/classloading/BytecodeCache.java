package yalp.classloading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import yalp.Logger;
import yalp.Yalp;
import yalp.YalpPlugin;

/**
 * Used to speed up compilation time
 */
public class BytecodeCache {

    /**
     * Delete the bytecode
     * @param name Cache name
     */
    public static void deleteBytecode(String name) {
        try {
            if (!Yalp.initialized || Yalp.tmpDir == null || Yalp.readOnlyTmp || !Yalp.configuration.getProperty("yalp.bytecodeCache", "true").equals("true")) {
                return;
            }
            File f = cacheFile(name.replace("/", "_").replace("{", "_").replace("}", "_").replace(":", "_"));
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieve the bytecode if source has not changed
     * @param name The cache name
     * @param source The source code
     * @return The bytecode
     */
    public static byte[] getBytecode(String name, String source) {
        try {
            if (!Yalp.initialized || Yalp.tmpDir == null || !Yalp.configuration.getProperty("yalp.bytecodeCache", "true").equals("true")) {
                return null;
            }
            File f = cacheFile(name.replace("/", "_").replace("{", "_").replace("}", "_").replace(":", "_"));
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                // Read hash
                int offset = 0;
                int read = -1;
                StringBuilder hash = new StringBuilder();
                while ((read = fis.read()) != 0) {
                    hash.append((char) read);
                    offset++;
                }
                if (!hash(source).equals(hash.toString())) {

                    if (Logger.isTraceEnabled()) {
                        Logger.trace("Bytecode too old (%s != %s)", hash, hash(source));
                    }
                    return null;
                }
                byte[] byteCode = new byte[(int) f.length() - (offset + 1)];
                fis.read(byteCode);
                fis.close();
                return byteCode;
            }

            if (Logger.isTraceEnabled()) {
                Logger.trace("Cache MISS for %s", name);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cache the bytecode
     * @param byteCode The bytecode
     * @param name The cache name
     * @param source The corresponding source
     */
    public static void cacheBytecode(byte[] byteCode, String name, String source) {
        try {
            if (!Yalp.initialized || Yalp.tmpDir == null || Yalp.readOnlyTmp || !Yalp.configuration.getProperty("yalp.bytecodeCache", "true").equals("true")) {
                return;
            }
            File f = cacheFile(name.replace("/", "_").replace("{", "_").replace("}", "_").replace(":", "_"));
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(hash(source).getBytes("utf-8"));
            fos.write(0);
            fos.write(byteCode);
            fos.close();

            // emit bytecode to standard class layout as well
            if(!name.contains("/") && !name.contains("{")) {
                f = new File(Yalp.tmpDir, "classes/"+(name.replace(".", "/"))+".class");
                f.getParentFile().mkdirs();
                fos = new FileOutputStream(f);
                fos.write(byteCode);
                fos.close();
            }

            if (Logger.isTraceEnabled()) {
                Logger.trace("%s cached", name);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Build a hash of the source code.
     * To efficiently track source code modifications.
     */
    static String hash(String text) {
        try {
            StringBuffer plugins = new StringBuffer();
            for(YalpPlugin plugin : Yalp.pluginCollection.getEnabledPlugins()) {
                plugins.append(plugin.getClass().getName());
            }
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update((Yalp.version + plugins.toString() + text).getBytes("utf-8"));
            byte[] digest = messageDigest.digest();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < digest.length; ++i) {
                int value = digest[i];
                if (value < 0) {
                    value += 256;
                }
                builder.append(Integer.toHexString(value));
            }
            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieve the real file that will be used as cache.
     */
    static File cacheFile(String id) {
        File dir = new File(Yalp.tmpDir, "bytecode/" + Yalp.mode.name());
        if (!dir.exists() && Yalp.tmpDir != null && !Yalp.readOnlyTmp) {
            dir.mkdirs();
        }
        return new File(dir, id);
    }
}