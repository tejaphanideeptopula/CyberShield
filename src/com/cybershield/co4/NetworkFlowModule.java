package com.cybershield.co4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Small, labelled demonstration networks; capacities are illustrative only. */
public final class NetworkFlowModule {
    private static final int[][] DEMO_CAPACITY = {
        {0, 3, 2, 0, 0, 0}, {0, 0, 1, 2, 0, 0},
        {0, 0, 0, 0, 3, 0}, {0, 0, 0, 0, 1, 2},
        {0, 0, 0, 0, 0, 4}, {0, 0, 0, 0, 0, 0}
    };
    private static final boolean[][] DEMO_COMPATIBILITY = {
        {true,true,false},{true,false,true},{false,true,true}
    };
    private NetworkFlowModule() { }

    public static void run() {
        for (int choice = 1; choice <= 5; choice++) runChoice(choice);
    }

    public static void runChoice(int choice) {
        int[][] capacity = copy(DEMO_CAPACITY);
        System.out.println("Illustrative security-node network (capacities are demo units):");
        for (int i = 0; i < capacity.length; i++) System.out.println("node " + i + " -> " + Arrays.toString(capacity[i]));
        switch (choice) {
            case 1:
                System.out.println("Ford-Fulkerson (DFS) max flow: " + fordFulkerson(capacity, 0, 5) + " [O(E*maxFlow), O(V^2)]");
                break;
            case 2:
                System.out.println("Edmonds-Karp (BFS) max flow: " + edmondsKarp(capacity, 0, 5) + " [O(V*E^2), O(V^2)]");
                break;
            case 3:
                System.out.println("Dinic max flow: " + dinic(capacity, 0, 5) + " [general O(V^2*E), O(V^2)]");
                break;
            case 4:
                System.out.println("Analyst-alert compatibility matrix (rows analysts, columns alerts):");
                for (boolean[] row : DEMO_COMPATIBILITY) System.out.println(Arrays.toString(row));
                System.out.println("Maximum analyst-alert matching: " + maximumMatching(DEMO_COMPATIBILITY) + " [O(VE), O(V)]");
                break;
            case 5:
                int[][] residual = copy(capacity);
                int cutFlow = edmondsKarpResidual(residual, 0, 5);
                System.out.println("Min-cut edges: " + minCutEdges(capacity, residual, 0) + " (cut capacity " + cutFlow + ") [O(V^2) space]");
                break;
            default:
                System.out.println("Unknown CO4 algorithm choice.");
        }
    }

    public static int fordFulkerson(int[][] capacity, int source, int sink) {
        int[][] residual = copy(capacity); int flow = 0; int[] parent = new int[capacity.length];
        while (dfs(residual, source, sink, parent)) {
            int amount = Integer.MAX_VALUE;
            for (int v=sink; v!=source; v=parent[v]) amount=Math.min(amount,residual[parent[v]][v]);
            for (int v=sink; v!=source; v=parent[v]) { residual[parent[v]][v]-=amount; residual[v][parent[v]]+=amount; }
            flow += amount;
        }
        return flow;
    }
    private static boolean dfs(int[][] r,int u,int t,int[] p) {
        boolean[] seen=new boolean[r.length]; int[] stack=new int[r.length]; int top=0; stack[top++]=u; seen[u]=true; p[u]=-1;
        while(top>0) { int x=stack[--top]; for(int v=0;v<r.length;v++) if(!seen[v]&&r[x][v]>0) { p[v]=x; seen[v]=true; if(v==t)return true; stack[top++]=v; } }
        return false;
    }
    public static int edmondsKarp(int[][] c,int s,int t) { int[][] r=copy(c); return edmondsKarpResidual(r,s,t); }
    private static int edmondsKarpResidual(int[][] r,int s,int t) {
        int flow=0,n=r.length; int[] p=new int[n];
        while(true) { Arrays.fill(p,-1); boolean[] seen=new boolean[n]; int[] q=new int[n]; int h=0,z=0; q[z++]=s;seen[s]=true;
            while(h<z&&!seen[t]) { int u=q[h++]; for(int v=0;v<n;v++) if(!seen[v]&&r[u][v]>0){seen[v]=true;p[v]=u;q[z++]=v;} }
            if(!seen[t]) break; int a=Integer.MAX_VALUE; for(int v=t;v!=s;v=p[v])a=Math.min(a,r[p[v]][v]);
            for(int v=t;v!=s;v=p[v]){r[p[v]][v]-=a;r[v][p[v]]+=a;} flow+=a;
        } return flow;
    }
    public static int dinic(int[][] c,int s,int t) {
        int[][] r=copy(c); int flow=0,n=c.length; int[] level=new int[n];
        while(levels(r,s,t,level)) { int[] next=new int[n]; int pushed; while((pushed=send(r,s,t,Integer.MAX_VALUE,level,next))>0)flow+=pushed; }
        return flow;
    }
    private static boolean levels(int[][] r,int s,int t,int[] level) { Arrays.fill(level,-1); int[] q=new int[r.length];int h=0,z=0;q[z++]=s;level[s]=0;while(h<z){int u=q[h++];for(int v=0;v<r.length;v++)if(r[u][v]>0&&level[v]<0){level[v]=level[u]+1;q[z++]=v;}}return level[t]>=0; }
    private static int send(int[][] r,int u,int t,int f,int[] level,int[] next) { if(u==t)return f;for(;next[u]<r.length;next[u]++){int v=next[u];if(r[u][v]>0&&level[v]==level[u]+1){int x=send(r,v,t,Math.min(f,r[u][v]),level,next);if(x>0){r[u][v]-=x;r[v][u]+=x;return x;}}}return 0; }
    private static List<String> minCutEdges(int[][] c,int[][] r,int s) { boolean[] seen=new boolean[c.length];int[] q=new int[c.length];int h=0,z=0;q[z++]=s;seen[s]=true;while(h<z){int u=q[h++];for(int v=0;v<c.length;v++)if(r[u][v]>0&&!seen[v]){seen[v]=true;q[z++]=v;}}List<String> edges=new ArrayList<>();for(int u=0;u<c.length;u++)for(int v=0;v<c.length;v++)if(seen[u]&&!seen[v]&&c[u][v]>0)edges.add(u+"->"+v+"("+c[u][v]+")");return edges; }
    private static int[][] copy(int[][] a){int[][] b=new int[a.length][];for(int i=0;i<a.length;i++)b[i]=a[i].clone();return b;}
    public static int maximumMatching(boolean[][] graph) { int[] match=new int[graph[0].length];Arrays.fill(match,-1);int count=0;for(int u=0;u<graph.length;u++)if(augment(u,graph,new boolean[match.length],match))count++;return count; }
    private static boolean augment(int u,boolean[][] g,boolean[] seen,int[] m){for(int v=0;v<m.length;v++)if(g[u][v]&&!seen[v]){seen[v]=true;if(m[v]<0||augment(m[v],g,seen,m)){m[v]=u;return true;}}return false;}
}
