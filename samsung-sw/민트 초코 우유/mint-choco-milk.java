import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
    static int N, T;

    static int[][] map;
    static int[][] B;

    static final int[] dr = { -1, 1, 0, 0 };
    static final int[] dc = { 0, 0, -1, 1 };
    
    static final boolean debug = false;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        T = Integer.parseInt(st.nextToken());

        char[][] rawMap = new char[N][N];

        for (int i = 0; i < N; i++) {
            String str = br.readLine();
            rawMap[i] = str.toCharArray();
        }

        int MINT = 4;
        int CHOCO = 2;
        int MILK = 1;

        map = new int[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (rawMap[i][j] == 'T') {
                    map[i][j] = MINT;
                }

                else if (rawMap[i][j] == 'C') {
                    map[i][j] = CHOCO;
                }

                else
                    map[i][j] = MILK;
            }
        }

        B = new int[N][N];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                B[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        
        printer(debug);

        for (int t = 1; t <= T; t++) {
            morning();
            if (debug) System.out.println("------morning-------");
            printer(debug);
            ArrayList<int[]> pList = noon();
            if (debug) System.out.println("------noon-------");
            printer(debug);
            evening(pList);
            if (debug) System.out.println("------evening-------");
            printer(debug);
            printAll();
        }

    }
    
    public static void printer(boolean check) {
        if (check) {
            for (int i = 0; i < N; i++) {
                System.out.println(Arrays.toString(B[i]));
            }
        }
    }

    public static void morning() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                B[i][j] += 1;
            }
        }
    }
    

    public static ArrayList<int[]> noon() {
        boolean[][] visited = new boolean[N][N];
        ArrayList<int[]> pList = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (!visited[i][j]) {
                    int[] p = floodfill(i, j, visited);
                    pList.add(p);
                }
            }
        }
        return pList;
    }

    public static boolean inRange(int r, int c) {
        return r >= 0 && r < N && c >= 0 && c < N;
    }

    public static int[] floodfill(int r, int c, boolean[][] visited) {
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(r * 100 + c);
        visited[r][c] = true;
        B[r][c]--;
        int pR = r;
        int pC = c;
        int cnt = 1;

        while (!q.isEmpty()) {
            int curr = q.poll();
            int cr = curr / 100;
            int cc = curr % 100;

            if (B[cr][cc] > B[pR][pC] || (B[cr][cc] == B[pR][pC] && cr < pR)
                    || (B[cr][cc] == B[pR][pC] && cr == pR && cc < pC)) {
                pR = cr;
                pC = cc;
            }

            for (int d = 0; d < 4; d++) {
                int nr = cr + dr[d];
                int nc = cc + dc[d];

                if (!inRange(nr, nc))
                    continue;
                
                if (visited[nr][nc])
                    continue;
                
                if (map[nr][nc] != map[cr][cc])
                    continue;
                
                q.offer(nr * 100 + nc);
                visited[nr][nc] = true;
                B[nr][nc]--;
                cnt++;
            }
        }
        B[pR][pC] += cnt;
        return new int[] {pR, pC};
    }

    public static void evening(ArrayList<int[]> pList) {
        if (debug) System.out.println("------evening start------");
        Collections.sort(pList, new Comparator<int[]>() {
            @Override
            public int compare(int[] a, int[] b) {
                int ar = a[0], ac = a[1];
                int br = b[0], bc = b[1];
                
                int tierA = Integer.bitCount(map[ar][ac]);
                int tierB = Integer.bitCount(map[br][bc]);
                
                if (tierA != tierB) {
                    return Integer.compare(tierA, tierB);
                }
                
                else if (B[ar][ac] != B[br][bc]) {
                    return Integer.compare(B[br][bc], B[ar][ac]);
                }
                
                else if (ar != br) {
                    return Integer.compare(ar, br);
                }
                
                else return Integer.compare(ac, bc);
            }
        });
        
        if (debug) System.out.println("------sort done------");
        boolean[][] defend = new boolean[N][N];
        
        for (int[] p : pList) {
            int pr = p[0];
            int pc = p[1];
            if (!defend[pr][pc]) {
                spread(pr, pc, defend);
            }
            if (debug) System.out.println("---------spreading---------");
            printer(debug);
        }

    }
    
    public static void spread(int pr, int pc, boolean[][] defend) {
        int d = (B[pr][pc] % 4);
        int x = B[pr][pc] - 1;
        B[pr][pc] = 1;
        
        int r = pr;
        int c = pc;
        
        while (x>0) {
//            if (debug) System.out.println(r + " " + c);
            int nr = r + dr[d];
            int nc = c + dc[d];
            
            if (!inRange(nr, nc)) break;
            if (map[nr][nc] == map[pr][pc]) {
                r = nr;
                c = nc;
                continue;
            }

            int y = B[nr][nc];
            if (x > y) {
                //강한전파
                map[nr][nc] = map[pr][pc];
                x -= (y+1);
                B[nr][nc] += 1;
                defend[nr][nc] = true;
            }
            
            else if (x <= y) {
                //약한전파
                map[nr][nc] = (map[nr][nc] | map[pr][pc]);
                B[nr][nc] += x;
                x = 0;
                defend[nr][nc] = true;
            }
                    
            r = nr;
            c = nc;
            
        }
    }

    public static void printAll() {
        int[] cnt = new int[8];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int food = map[i][j];
//                if (debug) System.out.println("food : " + food);
                cnt[food] += B[i][j];
            }
        }
        
        System.out.println(cnt[7] + " " + cnt[6] + " " + cnt[5] + " " + cnt[3] + " " + cnt[1] + " " + cnt[2] + " " + cnt[4]);
    }

}
