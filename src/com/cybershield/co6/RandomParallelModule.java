package com.cybershield.co6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Small reproducible randomized and parallel demonstrations. */
public final class RandomParallelModule {
    private RandomParallelModule() { }
    public static void run(String csvPath, List<String> recordLines) {
        for (int choice = 1; choice <= 6; choice++) runChoice(choice, csvPath, recordLines);
    }
    public static void runChoice(int choice, String csvPath, List<String> recordLines) {
        switch (choice) {
            case 1:
                System.out.println("Miller-Rabin (Monte Carlo) examples, certainty 20:");
                for(String n:new String[]{"2","17","21","1000000007"}) System.out.println(n+" -> probable prime: "+isProbablePrime(new BigInteger(n),20));
                System.out.println("A positive result is probable, not a proof; theoretical error bound per independent uniform base is at most 1/4.");
                break;
            case 2:
                int[] input={9,4,7,3,8,2,6,1,5};int[] sorted=input.clone();quickSort(sorted,new Random(2026));int[] oracle=input.clone();Arrays.sort(oracle);
                System.out.println("Randomized QuickSort: "+Arrays.toString(sorted)+"; Arrays.sort agrees="+Arrays.equals(sorted,oracle)+" [expected O(n log n), worst O(n^2)]");
                break;
            case 3:
                List<String> sample=reservoirSample(csvPath,3,20261013L);System.out.println("Reservoir sample of up to 3 actual CSV rows (seed 20261013):");for(String row:sample)System.out.println("  "+row);
                break;
            case 4:
                int sequential=recordLines.size();int parallel=parallelCount(recordLines);System.out.println("Parallel dataset record count="+parallel+", sequential count="+sequential+", match="+(parallel==sequential)+" [O(n) total work]");
                break;
            case 5:
                int[] scan=exclusiveBlelloch(new int[]{3,1,7,0,4,1,6,3});System.out.println("Blelloch exclusive scan [3,1,7,0,4,1,6,3] -> "+Arrays.toString(scan)+" [O(n) work, O(log n) depth]");
                break;
            case 6:
                System.out.println("Brent illustration: work W=20, depth D=7, processors P=3; T_P >= max(ceil(W/P),D) = "+Math.max((20+2)/3,7)+" steps (idealized fork-join model).");
                break;
            default:
                System.out.println("Unknown CO6 algorithm choice.");
        }
    }
    public static boolean isProbablePrime(BigInteger n,int rounds){if(n.compareTo(BigInteger.TWO)<0)return false;if(n.equals(BigInteger.TWO)||n.equals(BigInteger.valueOf(3)))return true;if(!n.testBit(0))return false;BigInteger d=n.subtract(BigInteger.ONE);int s=d.getLowestSetBit();d=d.shiftRight(s);Random r=new Random(19);for(int i=0;i<Math.max(1,rounds);i++){BigInteger a;do{a=new BigInteger(n.bitLength(),r);}while(a.compareTo(BigInteger.TWO)<0||a.compareTo(n.subtract(BigInteger.TWO))>0);BigInteger x=a.modPow(d,n);if(x.equals(BigInteger.ONE)||x.equals(n.subtract(BigInteger.ONE)))continue;boolean witness=true;for(int j=1;j<s;j++){x=x.multiply(x).mod(n);if(x.equals(n.subtract(BigInteger.ONE))){witness=false;break;}}if(witness)return false;}return true;}
    public static void quickSort(int[] a,Random random){quickSort(a,0,a.length-1,random);}private static void quickSort(int[] a,int lo,int hi,Random r){if(lo>=hi)return;int p=lo+r.nextInt(hi-lo+1);swap(a,p,hi);int pivot=a[hi],i=lo;for(int j=lo;j<hi;j++)if(a[j]<=pivot)swap(a,i++,j);swap(a,i,hi);quickSort(a,lo,i-1,r);quickSort(a,i+1,hi,r);}private static void swap(int[]a,int i,int j){int x=a[i];a[i]=a[j];a[j]=x;}
    /** Standard reservoir sampling over CSV data records; the header is excluded. */
    public static List<String> reservoirSample(String path,int k,long seed){List<String> sample=new ArrayList<>();if(k<=0)return sample;Random random=new Random(seed);try(BufferedReader in=new BufferedReader(new FileReader(path))){in.readLine();String line;long seen=0;while((line=in.readLine())!=null){if(line.trim().isEmpty())continue;seen++;if(sample.size()<k)sample.add(line);else{long slot=nextLong(random,seen);if(slot<k)sample.set((int)slot,line);}}}catch(IOException e){System.out.println("Reservoir sample unavailable: "+e.getMessage());}return sample;}
    private static long nextLong(Random r,long bound){long bits,val;do{bits=r.nextLong()>>>1;val=bits%bound;}while(bits-val+(bound-1)<0);return val;}
    public static int parallelCount(List<String> records){if(records.isEmpty())return 0;int tasks=Math.min(Runtime.getRuntime().availableProcessors(),records.size());ExecutorService pool=Executors.newFixedThreadPool(tasks);try{List<Callable<Integer>> jobs=new ArrayList<>();int chunk=(records.size()+tasks-1)/tasks;for(int from=0;from<records.size();from+=chunk){final int start=from,end=Math.min(records.size(),from+chunk);jobs.add(()->{int localCount=0;for(int i=start;i<end;i++)if(records.get(i)!=null)localCount++;return localCount;});}int sum=0;for(Future<Integer> result:pool.invokeAll(jobs))sum+=result.get();return sum;}catch(Exception e){throw new IllegalStateException("Parallel count failed",e);}finally{pool.shutdown();}}
    /** Exclusive prefix sums; pads internally to a power of two and returns input length. */
    public static int[] exclusiveBlelloch(int[] input){if(input.length==0)return new int[0];int n=1;while(n<input.length)n*=2;int[] a=new int[n];System.arraycopy(input,0,a,0,input.length);for(int stride=1;stride<n;stride*=2)for(int i=0;i<n;i+=2*stride)a[i+2*stride-1]+=a[i+stride-1];a[n-1]=0;for(int stride=n/2;stride>=1;stride/=2)for(int i=0;i<n;i+=2*stride){int left=i+stride-1,right=i+2*stride-1;int t=a[left];a[left]=a[right];a[right]+=t;}return Arrays.copyOf(a,input.length);}
}
