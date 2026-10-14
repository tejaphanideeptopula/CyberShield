package com.cybershield.co1;

import java.util.*;

public class AhoCorasick {
    static class Node {
        Map<Character,Integer> next=new HashMap<>();
        int failure=0;
        List<String> output=new ArrayList<>();
        List<String> ownOutput=new ArrayList<>();
    }

    private final List<Node> nodes=new ArrayList<>();
    private boolean built;

    public AhoCorasick() {
        nodes.add(new Node());
    }
    public void addPattern(String pattern) {
        if(pattern==null||pattern.isEmpty()) 
        	return;
        int current=0;
        for(char c:pattern.toCharArray()) {
            if(!nodes.get(current).next.containsKey(c)) {
                nodes.get(current).next.put(c,nodes.size());
                nodes.add(new Node());
            }
            current=nodes.get(current).next.get(c);
        }
        if (!nodes.get(current).ownOutput.contains(pattern)) nodes.get(current).ownOutput.add(pattern);
        built=false;
    }

    public void build() {
        if (built) return;
        for (Node node : nodes) {
            node.output.clear();
            node.output.addAll(node.ownOutput);
            node.failure=0;
        }
        Queue<Integer> queue=new LinkedList<>();
        for(int child:nodes.get(0).next.values()) {
            nodes.get(child).failure=0;
            queue.add(child);
        }

        while(!queue.isEmpty()) {
            int current=queue.poll();
            for(Map.Entry<Character,Integer> entry:nodes.get(current).next.entrySet()) {
                char c=entry.getKey();
                int child=entry.getValue();
                int failure=nodes.get(current).failure;

                while(failure!=0&&!nodes.get(failure).next.containsKey(c)) {
                    failure=nodes.get(failure).failure;
                }

                if(nodes.get(failure).next.containsKey(c)) {
                    nodes.get(child).failure=nodes.get(failure).next.get(c);
                } else {
                    nodes.get(child).failure=0;
                }

                nodes.get(child).output.addAll(
                    nodes.get(nodes.get(child).failure).output
                );
                queue.add(child);
            }
        }
        built=true;
    }

    public Map<String,Integer> search(String text) {
        Map<String,Integer> result=new LinkedHashMap<>();
        if (text == null) return result;
        int current=0;

        for(int i=0;i<text.length();i++) {
            char c=text.charAt(i);

            while(current!=0&&!nodes.get(current).next.containsKey(c)) {
                current=nodes.get(current).failure;
            }

            if(nodes.get(current).next.containsKey(c)) {
                current=nodes.get(current).next.get(c);
            } else {
                current=0;
            }

            for(String pattern:nodes.get(current).output) {
                result.put(pattern,result.getOrDefault(pattern,0)+1);
            }
        }

        return result;
    }
}
