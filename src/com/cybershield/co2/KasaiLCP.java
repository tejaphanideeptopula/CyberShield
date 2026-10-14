package com.cybershield.co2;
public class KasaiLCP {
    public static int[] buildLCP(String text,int[] suffixArray) {
        int n=text.length();
        int[] rank=new int[n];
        int[] lcp=new int[n];
        for (int i=0;i<n;i++) {
            rank[suffixArray[i]]=i;
        }
        int k=0;
        for (int i=0;i<n;i++) {
            int currentRank=rank[i];
            if (currentRank==n-1) {
                k=0;
                continue;
            }
            int j=suffixArray[currentRank+1];
            while (i+k<n&&j+k<n&&text.charAt(i+k)==text.charAt(j+k)) {
                k++;
            }
            lcp[currentRank]=k;
            if (k>0) {
                k--;
            }
        }
        return lcp;
    }
    public static void print(int[] suffixArray,int[] lcp,int limit) {
        System.out.println();
        System.out.println("========== KASAI LCP ==========");
        int count=Math.min(limit,suffixArray.length);
        for (int i=0;i<count;i++) {
            System.out.println("SA["+i+"] = "+suffixArray[i]+"    LCP = "+lcp[i]);
        }
    }
}