package com.thfund.client.router.compiler;

/**
 * @author ThomasWang
 * @since 2017/5/28 23:21
 */

public class Constants {
    // Java type
    public static final String PROJECT = "ThRouter";
    private static final String LANG = "java.lang";
    public static final String BYTE = LANG + ".Byte";
    public static final String SHORT = LANG + ".Short";
    public static final String INTEGER = LANG + ".Integer";
    public static final String LONG = LANG + ".Long";
    public static final String FLOAT = LANG + ".Float";
    public static final String DOUBEL = LANG + ".Double";
    public static final String BOOLEAN = LANG + ".Boolean";
    public static final String STRING = LANG + ".String";

    // Log
    public static final String PREFIX_OF_LOGGER = PROJECT + "::Compiler ";
    public static final String PARCELABLE = "android.os.Parcelable";
    public static final String ANNOTATION_TYPE_ROUTE= "com.thfund.client.router.annotation.Route";
}
