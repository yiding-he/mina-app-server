package com.hyd.appserver.json;

import com.hyd.appserver.Request;

/**
 * (description)
 *
 * @author yiding.he
 */
public class JsonRequestMessage extends Request {

    private boolean valid = true;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
