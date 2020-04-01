import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class Main2 {
    static double dom = 0, error = 0, period;
    public static void main(String[] args) throws Exception{
        Map<Integer,Double[]> graph = loadData();
        printEquation(optimize(graph, 20, 1000, 3));
        System.out.println(error);
        /*for(int i = 1; i <= 50; i++){
            optimize(graph, 25, 100, i);
            //printEquation(optimize(graph, i, 100));
            //System.out.print("("+i+", "+error+"), ");
        }*/
        //System.out.println(Arrays.toString(coe));
    }
    static double[] optimize(Map<Integer,Double[]> g, int n, int t, double p){
        int num = 50;
        double minx = g.get(0)[0], maxx = g.get(0)[0];
        double miny = g.get(0)[1], maxy = g.get(0)[1];
        //System.out.print("("+g.get(0)[0]+", "+g.get(0)[1]+")");
        for(int i = 1; i < num; i++) {
            //System.out.print(", ("+g.get(i)[0]+", "+g.get(i)[1]+")");
            if (g.get(i)[1] < miny) miny = g.get(i)[1];
            if (g.get(i)[1] > maxy) maxy = g.get(i)[1];
            if (g.get(i)[0] < minx) minx = g.get(i)[0];
            if (g.get(i)[0] > maxx) maxx = g.get(i)[0];
        }
        dom = maxx-minx; period = dom*p;
        double[] c = new double[n+1];
        for(int i = 0; i < n+1; i++) c[i] = Math.random()*(maxy-miny);
        for(int a = 0; a < t; a++){ //for each iteration
            for(int k = 0; k < n+1; k++) { //for each coefficient
                double[] x = new double[3], y = new double[3];
                for(int l = 0; l < 3; l++){
                    x[l] = c[k];
                    double[] val = new double[num]; //value of cosine series for each point
                    for (int i = 0; i < num; i++) { //for every point
                        for (int j = 1; j <= n; j++) { //for each cosine
                            val[i] += c[j - 1] * Math.cos(((j*2*Math.PI)/period) * g.get(i)[0]);
                            //System.out.println((maxx-minx)/j);
                        }
                        val[i] += c[n];
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
            for(int j = 1; j <= n; j++)
                val += c[j - 1] * Math.cos(((j*2*Math.PI)/period) * g.get(i)[0]);
            val += c[n];
            error += Math.pow(val-g.get(i)[1],2);
        }
        error /= num;
        return c;
    }
    static void printEquation(double[] a){
        String p = "";
        for(int i = 0; i < a.length-1; i++){
            p += a[i]+"cos("+(((i+1)*2*Math.PI)/period)+"x) + ";
        }
        p += a[a.length-1];
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
    static Map<Integer,Double[]> loadData() throws Exception{
        File file = new File("src/NASDAQ100.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        Map<Integer,Double[]> graph = new HashMap<Integer,Double[]>();
        String ln = ""; int n = 0;
        while((ln = br.readLine()) != null){
            String[] val = ln.split("  ");
            if(val.length>2) continue;
            graph.put(n, new Double[] {dateToNum(val[0]), Double.parseDouble(val[1])});
            n++;
        }
        return graph;
    }
}
//greater number of cosines leads to longer period at peak error, also greater peak error