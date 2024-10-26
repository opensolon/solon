package org.noear.solon.data.sqlink.core.api;

class StopWatch
{
    public enum Style
    {
        NANO,
        MILLISECOND
    }

    public static void setStyle(Style style)
    {
        StopWatch.style = style;
    }

    private static Style style = Style.NANO;
    private static long click;

    public static void start()
    {
        switch (style)
        {
            case NANO:
                click = System.nanoTime();
                break;
            case MILLISECOND:
                click = System.currentTimeMillis();
                break;
        }
    }

    public static void end(String make)
    {
        switch (style)
        {
            case NANO:
                System.out.printf(make + "耗时%d纳秒%n", System.nanoTime() - click);
                break;
            case MILLISECOND:
                System.out.printf(make + "耗时%d毫秒%n", System.currentTimeMillis() - click);
                break;
        }
    }

    public static void end()
    {
        switch (style)
        {
            case NANO:
                System.out.println(System.nanoTime() - click);
                break;
            case MILLISECOND:
                System.out.println(System.currentTimeMillis() - click);
                break;
        }
    }
}
