package org.example.product.datasource;

public class DataSourceContextHolder {
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static final String MASTER = "master";
    public static final String SLAVE = "slave";

    public static void setMaster() { CONTEXT.set(MASTER); }
    public static void setSlave()  { CONTEXT.set(SLAVE); }
    public static String get()     { return CONTEXT.get(); }
    public static void clear()     { CONTEXT.remove(); }
}