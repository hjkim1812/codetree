import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class Main {

    static int N, K, L;

    static int[][] floor;
    static boolean[][] moveable;

    static int[][] robotCoords;

    static final int[] dr = {0, 1, 0, -1}; // 우 하 좌 상
    static final int[] dc = {1, 0, -1, 0};

    static final int[] dirBFS = {3, 2, 0, 1};

    public static boolean inRange(int r, int c){
        return r > 0 && r <= N && c > 0 && c <= N;
    }
    
    public static int[] robotBFS(int r, int c){
        int[][] dist = new int[N+1][N+1];

        for (int i = 1; i <= N; i++){
            Arrays.fill(dist[i], -1);
        }
        
        int minDist = Integer.MAX_VALUE;
        int minR = N;
        int minC = N;

        Queue<Integer> q = new ArrayDeque<>();
        q.offer(r * 100 + c);
        dist[r][c] = 0;

        while (!q.isEmpty()){
            int curr = q.poll();
            int cr = curr / 100;
            int cc = curr % 100;
            
            if (floor[cr][cc] > 0){
                if (dist[cr][cc] < minDist){
                    minDist = dist[cr][cc];
                    minR = cr;
                    minC = cc;
                }
                if (dist[cr][cc] == minDist){
                    if (cr < minR){
                        minR = cr;
                        minC = cc;
                    }
                    
                    if (cr == minR){
                        if (cc < minC){
                            minR = cr;
                            minC = cc;
                        }
                    }
                }
            }

            for (int d = 0; d < 4; d++){
                int nr = cr + dr[d];
                int nc = cc + dc[d];
                
                if (!inRange(nr, nc)) continue;
                if (!moveable[nr][nc]) continue;
                if (dist[nr][nc] != -1) continue;

                q.offer(nr * 100 + nc);
                dist[nr][nc] = dist[cr][cc] + 1;
            }
        }

        if (minDist == Integer.MAX_VALUE){
            return new int[] {r, c};
        }
        return new int[] {minR, minC};
    }

    public static int[] dirDict(int d){
        int[] dirs;
        switch (d) {
            case 0:
                dirs = new int[] {3, 0, 1};
                break;
            case 1:
                dirs = new int[] {0, 1, 2};
                break;
            case 2:
                dirs = new int[] {1, 2, 3};
                break;
            default:
                dirs = new int[] {2, 3, 0};
                break;
        }
        return dirs;
    }

    public static int calcDust(int r, int c, int d){
        int[] dirs = dirDict(d);

        int dust = Math.min(floor[r][c], 20);
        for (int dir : dirs){
            int nr = r + dr[dir];
            int nc = c + dc[dir];
            if (inRange(nr, nc) && floor[nr][nc] > 0){
                dust += Math.min(floor[nr][nc], 20);
            }
        }
        return dust;
    }

    public static void cleanDust(int r, int c, int d){
        int[] dirs = dirDict(d);
        floor[r][c] = Math.max(floor[r][c] - 20, 0);

        for (int dir : dirs){
            int nr = r + dr[dir];
            int nc = c + dc[dir];
            if (!inRange(nr, nc)) continue;
            if (floor[nr][nc] == -1) continue;
            
            floor[nr][nc] = Math.max(floor[nr][nc] - 20, 0);
        }
    }

    public static void diffuse(){
        int[][] addDust = new int[N+1][N+1];
        
        for (int i = 1; i <= N; i++){
            for (int j = 1; j <= N; j++){
                if (floor[i][j] != 0) continue;

                int dust = 0;
                for (int d = 0; d < 4; d++){
                    int nr = i + dr[d];
                    int nc = j + dc[d];

                    if (!inRange(nr, nc)) continue;
                    if (floor[nr][nc] == -1) continue;
                    
                    dust += floor[nr][nc];
                }

                int diffDust = dust / 10;
                addDust[i][j] += diffDust;
            }
        }

        for (int i = 1; i <= N; i++){
            for (int j = 1; j <= N; j++){
                floor[i][j] += addDust[i][j];
            }
        }
    }


    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        boolean debug = false;

        N = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        L = Integer.parseInt(st.nextToken());

        floor = new int[N+1][N+1];
        moveable = new boolean[N+1][N+1];
        robotCoords = new int[K][2];
        
        for (int i = 1; i <= N; i++){
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++){
                int obj = Integer.parseInt(st.nextToken());
                if (obj >= 0){
                    moveable[i][j] = true;
                }
                floor[i][j] = obj;
            }
        }

        if (debug){
            System.out.println("----------map---------");
            for (int i = 1; i <= N; i++){
                System.out.println(Arrays.toString(floor[i]));
            }
        }

        for (int k = 0; k < K; k++){
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());

            robotCoords[k][0] = r;
            robotCoords[k][1] = c;

            moveable[r][c] = false;
        }

        

        for (int turn = 0; turn < L; turn++){

            int answer = 0;
            // 1. 청소기 이동
            
            for (int k = 0; k < K; k++){
                int sr = robotCoords[k][0];
                int sc = robotCoords[k][1];

                // System.out.println("sr " + sr + " sc" + sc);

                int[] movedCoord = robotBFS(sr, sc);

                int er = movedCoord[0];
                int ec = movedCoord[1];

                // System.out.println("er " + er + " ec" + ec);

                moveable[sr][sc] = true;
                moveable[er][ec] = false;

                robotCoords[k][0] = er;
                robotCoords[k][1] = ec;
            }

            if (debug) {

                int[][] robotMap = new int[N+1][N+1];
                for (int k = 0; k < K; k++){
                    robotMap[robotCoords[k][0]][robotCoords[k][1]] = 1;
                }

                System.out.println("----------map---------");

                for (int i = 1; i <= N; i++){
                    System.out.println(Arrays.toString(robotMap[i]));
                }
            }
            // 2. 청소

            for (int k = 0; k < K; k++){
                int cr = robotCoords[k][0];
                int cc = robotCoords[k][1];

                int maxDir = 0;
                int maxDust = 0;

                for (int d = 0; d < 4; d++){
                    int dust = calcDust(cr, cc, d);
                    if (dust > maxDust){
                        maxDust = dust;
                        maxDir = d;
                    }
                }
                cleanDust(cr, cc, maxDir);
            }

            if (debug) {
                System.out.println("----------map---------");
                for (int i = 1; i <= N; i++){
                    System.out.println(Arrays.toString(floor[i]));
                }
            }

            // 3. 먼지축적

            for (int i = 1; i <= N; i++){
                for (int j = 1; j <= N; j++){
                    if (floor[i][j] > 0){
                        floor[i][j] += 5;
                    }
                }
            }
            if (debug) {
                System.out.println("----------map---------");
                for (int i = 1; i <= N; i++){
                    System.out.println(Arrays.toString(floor[i]));
                }
            }

            // 4. 먼지 확산

            diffuse();

            if (debug) {
                System.out.println("----------map---------");
                for (int i = 1; i <= N; i++){
                    System.out.println(Arrays.toString(floor[i]));
                }
            }
            
            for (int i = 1; i <= N; i++){
                for (int j = 1; j <= N; j++){
                    if (floor[i][j] == -1) continue;
                    answer += floor[i][j];
                }
            }

            System.out.println(answer);

            if (answer == 0){
                break;
            }

        }



        

    }
}