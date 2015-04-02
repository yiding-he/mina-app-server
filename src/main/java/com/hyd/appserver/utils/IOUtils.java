package com.hyd.appserver.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * (description)
 *
 * @author yiding.he
 */
public class IOUtils {

    public static byte[] readStreamAndClose(InputStream is) throws IOException {
        if (is == null) {
            return new byte[0];
        }

        byte[] buffer = new byte[4096];
        byte[] content = new byte[0];
        int count;

        try {
            while ((count = is.read(buffer)) != -1) {
                byte[] newcontent = new byte[content.length + count];
                System.arraycopy(content, 0, newcontent, 0, content.length);
                System.arraycopy(buffer, 0, newcontent, content.length, count);
                content = newcontent;
            }

            return content;
        } finally {
            is.close();
        }
    }
}
