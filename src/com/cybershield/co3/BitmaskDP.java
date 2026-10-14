package com.cybershield.co3;
import java.util.ArrayList;
import java.util.List;
public class BitmaskDP {
    static class Rule {
        String name;
        int mask;
        int cost;
        Rule(String name,int mask,int cost) {
            this.name=name;
            this.mask=mask;
            this.cost=cost;
        }
    }
    public static void solve() {
        String[] conditions={"Protocol Analysis","Threat Analysis","Port Analysis","Family Analysis","Prediction Analysis"};
        Rule[] rules={
            new Rule("Network Traffic Rule",(1<<0)|(1<<2),4),
            new Rule("Threat Detection Rule",(1<<1)|(1<<4),5),
            new Rule("Ransomware Family Rule",(1<<3)|(1<<1),6),
            new Rule("Complete Security Rule",(1<<0)|(1<<1)|(1<<2)|(1<<3)|(1<<4),10)
        };
        int totalMasks=1<<conditions.length;
        int INF=Integer.MAX_VALUE/4;
        int[] dp=new int[totalMasks];
        int[] previous=new int[totalMasks];
        int[] selected=new int[totalMasks];
        for (int i=0;i<totalMasks;i++) {
            dp[i]=INF;
            previous[i]=-1;
            selected[i]=-1;
        }
        dp[0]=0;
        for (int mask=0;mask<totalMasks;mask++) {
            if (dp[mask]==INF) {
                continue;
            }
            for (int i=0;i<rules.length;i++) {
                int newMask=mask|rules[i].mask;
                int newCost=dp[mask]+rules[i].cost;
                if (newCost<dp[newMask]) {
                    dp[newMask]=newCost;
                    previous[newMask]=mask;
                    selected[newMask]=i;
                }
            }
        }
        int target=totalMasks-1;
        System.out.println();
        System.out.println("========== BITMASK DP ==========");
        System.out.println("Security Conditions:");
        for (int i=0;i<conditions.length;i++) {
            System.out.println((i+1)+". "+conditions[i]);
        }
        System.out.println();
        System.out.println("Minimum Analysis Cost: "+dp[target]);
        List<String> selectedRules=new ArrayList<>();
        int current=target;
        while (current!=0) {
            int ruleIndex=selected[current];
            if (ruleIndex==-1) {
                break;
            }
            selectedRules.add(rules[ruleIndex].name);
            current=previous[current];
        }
        System.out.println("Selected Rules:");
        for (int i=selectedRules.size()-1;i>=0;i--) {
            System.out.println("- "+selectedRules.get(i));
        }
    }
}