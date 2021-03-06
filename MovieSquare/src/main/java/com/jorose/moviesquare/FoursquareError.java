package com.jorose.moviesquare;

/**
 * Created by jrose on 8/16/13.
 */
public class FoursquareError extends Throwable {

    private static final long serialVersionUID = 1L;

    private int mErrorCode = 0;
    private String mErrorType;

    public FoursquareError(String message) {
        super(message);
    }

    public FoursquareError(String message, String type, int code) {
        super(message);
        mErrorType = type;
        mErrorCode = code;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorType() {
        return mErrorType;
    }

}
