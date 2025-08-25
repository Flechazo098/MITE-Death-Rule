package com.flechazo.mr;

public class MDRExpUtils {
    public static ExpSplit splitExp(int total, int keepPercent) {
        if (keepPercent < 0) keepPercent = 0;
        if (keepPercent > 100) keepPercent = 100;

        int keep = (int) Math.floor(total * (keepPercent / 100.0));
        int drop = total - keep;
        return new ExpSplit(keep, drop);
    }
    public record ExpSplit(int keep, int drop) {
    }
}
