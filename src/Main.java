import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.text.DecimalFormat;

public class Main {
    static double dom = 0, error = 0, perror = 0, period;
    static int num = 50;
    public static void main(String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(new File("src/NASDAQ100.txt")));
        Map<Integer,Double[]> graph = new HashMap<>();
        String ln; int n = 0;
        while((ln = br.readLine()) != null){
            String[] val = ln.split("  ");
            if(val.length>2) continue;
            graph.put(n, new Double[] {dateToNum(val[0]), Double.parseDouble(val[1])});
            n++;
        }
        /*for(int i = num; i < graph.size(); i+=10){
            System.out.print("("+graph.get(i)[0]+", "+graph.get(i)[1]+"), ");
        }*/
        //System.out.println(Arrays.toString(findOptParams(graph)));
        printEquation(optimize(graph, 20, 1, 1000, 3),20);
        System.out.println(error+", "+perror);
    }
    static double[] optimize(Map<Integer,Double[]> g, int n, int m, int t, double p){
        double minx = g.get(0)[0], maxx = g.get(0)[0];
        double miny = g.get(0)[1], maxy = g.get(0)[1];
        for(int i = 1; i < num; i++) {
            if (g.get(i)[0] < minx) minx = g.get(i)[0];
            if (g.get(i)[0] > maxx) maxx = g.get(i)[0];
            if (g.get(i)[1] < miny) miny = g.get(i)[1];
            if (g.get(i)[1] > maxy) maxy = g.get(i)[1];
        }
        dom = maxx-minx; period = dom*p;
        double[] c = new double[n+m];
        for(int i = 0; i < n+m; i++) c[i] = Math.random()*(maxy-miny);
        for(int a = 0; a < t; a++){ //for each iteration
            for(int k = 0; k < n+m; k++) { //for each coefficient
                double[] x = new double[3], y = new double[3];
                for(int l = 0; l < 3; l++){
                    x[l] = c[k];
                    double[] val = new double[num]; //value of cosine series for each point
                    for (int i = 0; i < num; i++) { //for every point
                        for (int j = 0; j < n; j++) //for each cosine (c[j+n-1]*Math.pow(g.get(i)[0],j-1));
                            val[i] += (c[j]*Math.cos((((j+1)*2*Math.PI)/period)*g.get(i)[0]));
                        for (int j = n; j < m+n; j++) val[i] += c[j]*Math.pow(g.get(i)[0],j-n);
                        y[l] += Math.pow(val[i] - g.get(i)[1], 2);
                    }
                    y[l] /= num;
                    c[k] = Math.random()*(maxy-miny);
                }
                double n1 = y[2]-y[1], n2 = Math.pow(x[1],2)-Math.pow(x[0],2);
                double n3 = Math.pow(x[2],2)-Math.pow(x[1],2), n4 = y[1]-y[0];
                double n5 = x[1]-x[0], n6 = x[2]-x[1];
                c[k] = .5*(((n1*n2)-(n3*n4))/((n1*n5)-(n6*n4)));
            }
        }
        for(int i = 0; i < num; i++){
            double val = 0;
            for (int j = 0; j < n; j++) //for each cosine (c[j+n-1]*Math.pow(g.get(i)[0],j-1));
                val += (c[j]*Math.cos((((j+1)*2*Math.PI)/period)*g.get(i)[0]));
            for (int j = n; j < m+n; j++) val += c[j]*Math.pow(g.get(i)[0],j-n);
            error += Math.pow(val-g.get(i)[1],2);
        }
        error /= num;
        for(int i = num; i < g.size(); i+=10){
            double val = 0;
            for (int j = 0; j < n; j++) //for each cosine (c[j+n-1]*Math.pow(g.get(i)[0],j-1));
                val += (c[j]*Math.cos((((j+1)*2*Math.PI)/period)*g.get(i)[0]));
            for (int j = n; j < m+n; j++) val += c[j]*Math.pow(g.get(i)[0],j-n);
            perror += Math.pow(val-g.get(i)[1],2);
        }
        perror /= Math.ceil((g.size()-num)/10);
        return c;
    }
    static int[] findOptParams(Map<Integer,Double[]> g){
        int cos = 20, tpx = 2, itr = 3000, per = 3;
        /*System.out.println("\nCosines vs Error");
        for(int i = 1; i <= 40; i++){
            optimize(g, i, tpx, itr, per);
            System.out.print("("+i+", "+perror+"), ");
        } NO CORRELATION */
        /* System.out.println("\nDegrees vs Error");
        for(int i = 1; i <= 20; i++){
            optimize(g, cos, i, itr, per);
            System.out.print("("+i+", "+perror+"), ");
        } INVERSE RELATIONSHIP */
        /*System.out.println("\nIterations vs Error");
        for(int i = 20; i <= 2000; i+=20){
            optimize(g, cos, tpx, i, per);
            System.out.print("("+i+", "+perror+"), ");
        } NO CORRELATION */
        System.out.println("\nPeriod Scale vs Error");
        for (int i = 1; i <= 50; i++) {
            optimize(g, cos, tpx, itr, i);
            System.out.print("(" + i + ", " + perror + "), ");
        }
        //printEquation(optimize(g, cos, tpx, itr, per), cos);
        return new int[] {cos, tpx, itr, per};
    }
    static void printEquation(double[] a, int n){
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(10);
        String p = "";
        for(int i = 0; i < n; i++)
            p += df.format(a[i])+"cos("+df.format(((i+1)*2*Math.PI)/period)+"x) + ";
        for(int i = n; i < a.length-1; i++)
            p += df.format(a[i])+"x^"+(i-n)+" + ";
        p += df.format(a[a.length-1])+"x^"+(a.length-n-1);
        System.out.println(p);
    }
    static double dateToNum(String date){
        int r = 0;
        String[] t = date.split("-");
        r += Math.floor((Integer.parseInt(t[0])-1986)*365.25+.5);
        r += Integer.parseInt(t[2]);
        int month = Integer.parseInt(t[1]);
        if(month == 1) return r; r += 31;
        if(month == 2) return r;
        r += Integer.parseInt(t[0])%4==0?29:28;
        if(month == 3) return r; r += 31;
        if(month == 4) return r; r += 30;
        if(month == 5) return r; r += 31;
        if(month == 6) return r; r += 30;
        if(month == 7) return r; r += 31;
        if(month == 8) return r; r += 31;
        if(month == 9) return r; r += 30;
        if(month == 10) return r; r += 31;
        if(month == 11) return r; r += 30;
        return r;
    }
}
//greater number of cosines leads to longer period at peak error, also greater peak error