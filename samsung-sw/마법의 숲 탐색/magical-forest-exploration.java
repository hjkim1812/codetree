import java.io.*;
import java.util.*;

public class Main {
    static int R, C, K;
    
    static int[] exitD;
    static int[] startC;
    
    static int[][] golemMap;
    static boolean[][] fairyMap;
    static boolean[][] exitMap;
    
    static final int[] dr = {-1, 0, 1, 0};
    static final int[] dc = {0, 1, 0, -1}; // 북 동 남 서
    
    public static boolean inRange(int r, int c){
        return (r >=0  && r < R+3 && c > 0 && c <= C);
    }

    public static boolean southMoveable(int r, int c){
        for (int d : new int[]{1, 2, 3}){
            int golemR = r + dr[d];
            int golemC = c + dc[d];
            
            if (!inRange(golemR + 1, golemC)) return false;
            if (golemMap[golemR + 1][golemC] != 0) return false;
        }
        return true;
    }

    public static boolean westMoveable(int r, int c){
        for (int d : new int[] {2, 3, 0}){
            int golemR = r + dr[d];
            int golemC = c + dc[d];

            if (!inRange(golemR, golemC - 1)) return false;
            if (golemMap[golemR][golemC - 1] != 0) return false;

            if (d == 2 || d == 3){
                if (!inRange(golemR + 1, golemC - 1)) return false;
                if (golemMap[golemR + 1][golemC - 1] != 0) return false;
            }
        }
        return true;
    }

    public static boolean eastMoveable(int r, int c){
        for (int d : new int[] {0, 1, 2}){
            int golemR = r + dr[d];
            int golemC = c + dc[d];

            if (!inRange(golemR, golemC + 1)) return false;
            if (golemMap[golemR][golemC + 1] != 0) return false;

            if (d == 1 || d == 2){
                if (!inRange(golemR + 1, golemC + 1)) return false;
                if (golemMap[golemR + 1][golemC + 1] != 0) return false;
            }
        }
        return true;
    }

    // public static int[] moveSouth(int r, int c, int d){
    //     int golemId = golemMap[r][c];
    //     golemMap[r][c] = 0;
        
    //     for (int dir = 0; dir < 4; dir++){
    //         int nr = r + dr[dir];
    //         int nc = c + dc[dir];

    //         golemMap[nr][nc] = 0;
    //         if (exitMap[nr][nc]) {
    //             exitMap[nr][nc] = false;
    //         }

    //         golemMap[nr + 1][nc] = golemId;

    //         if (dir == d) exitMap[nr+1][nc] = true;
    //     }

    //     golemMap[r+1][c] = golemId;
    //     return new int[] {r+1, c, d};
    // }

    // public static int[] moveWest(int r, int c, int d){
    //     int golemId = golemMap[r][c];
    //     golemMap[r][c] = 0;
        
    //     for (int dir = 0; dir < 4; dir++){
    //         int nr = r + dr[dir];
    //         int nc = c + dc[dir];

    //         golemMap[nr][nc] = 0;
    //         if (exitMap[nr][nc]) {
    //             exitMap[nr][nc] = false;
    //         }

    //         golemMap[nr + 1][nc - 1] = golemId;

    //         if (dir == (d+3)%4) exitMap[nr][nc] = true;
    //     }

    //     golemMap[r+1][c-1] = golemId;
    //     return new int[] {r+1, c-1, (d+3)%4};
    // }

    // public static int[] moveEast(int r, int c, int d){
    //     int golemId = golemMap[r][c];
    //     golemMap[r][c] = 0;
        
    //     for (int dir = 0; dir < 4; dir++){
    //         int nr = r + dr[dir];
    //         int nc = c + dc[dir];

    //         golemMap[nr][nc] = 0;
    //         if (exitMap[nr][nc]) {
    //             exitMap[nr][nc] = false;
    //         }

    //         golemMap[nr + 1][nc + 1] = golemId;

    //         if (dir == (d+3)%4) exitMap[nr+1][nc+1] = true;
    //     }

    //     golemMap[r+1][c-1] = golemId;
    //     return new int[] {r+1, c-1, (d+3)%4};
    // }

    public static boolean[][] fairyBFS(int r, int c){
        boolean[][] visited = new boolean[R+3][C+1];
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(r * 100 + c);
        visited[r][c] = true;

        while (!q.isEmpty()){
            int curr = q.poll();
            int cr = curr / 100;
            int cc = curr % 100;

            for (int d  = 0; d < 4; d++){
                int nr = cr + dr[d];
                int nc = cc + dc[d];
                
                if (!inRange(nr, nc)) continue;

                if (visited[nr][nc]) continue;

                if (golemMap[nr][nc] == 0) continue;

                if (exitMap[cr][cc]){
                    visited[nr][nc] = true;
                    q.offer(nr * 100 + nc);
                }
                else if (golemMap[cr][cc] == golemMap[nr][nc]){
                    visited[nr][nc] = true;
                    q.offer(nr * 100 + nc);
                }
            }
        }
        return visited;
    }


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        
        golemMap = new int[R + 3][C + 1];
        exitMap = new boolean[R + 3][C + 1];
        fairyMap = new boolean[R + 3][C + 1];
        
        startC = new int[K+1];
        exitD = new int[K+1];

        
        for (int k = 1; k <= K; k++) {
            st = new StringTokenizer(br.readLine());
            startC[k] = Integer.parseInt(st.nextToken());
            exitD[k] = Integer.parseInt(st.nextToken());
        }

        int rowSum = 0;

        boolean debug = false;

        //이동 시작
        for (int k = 1; k <= K; k++){
            // System.out.println("start moving");
            
            //출발
            int c = startC[k];
            int r = 1;
            int dExit = exitD[k];

            while (true) {
                if (southMoveable(r, c)) {
                    r++;
                } else if (westMoveable(r, c)) {
                    r++;
                    c--;
                    dExit = (dExit + 3) % 4;
                } else if (eastMoveable(r, c)) {
                    r++;
                    c++;
                    dExit = (dExit + 1) % 4;
                } else {
                    break;
                }
            }

            golemMap[r][c] = k;

            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d];
                int nc = c + dc[d];
                golemMap[nr][nc] = k;
            }

            exitMap[r + dr[dExit]][c + dc[dExit]] = true;
            
            if (debug){
                System.out.println("-----------golem stack---------");
                for (int i = 3; i <= R+2; i++){
                    System.out.println(Arrays.toString(golemMap[i]));
                }

                for (int i = 3; i <= R+2; i++){
                    
                    System.out.println(Arrays.toString(exitMap[i]));
                    

                }
            }

            if (debug && k == 3) break;

            if (r <= 3){
                for (int i = 0; i < R+3; i++){
                    Arrays.fill(golemMap[i], 0);
                    Arrays.fill(fairyMap[i], false);
                    Arrays.fill(exitMap[i], false);
                }
                continue;
            }

            boolean[][] thisFairy = fairyBFS(r, c);
            
            boolean decidedFairy = false;
            for (int i = R+2; i >= 1; i--){
                for (int j = 1; j <= C; j++){
                    if (thisFairy[i][j]){
                        fairyMap[i][j] = true;
                        decidedFairy = true;
                        rowSum += i-2;
                        // System.out.println("row number " + (i - 2) + " for fairy #"+k);
                        break;
                    }
                }
                if (decidedFairy) break;
            }
            // System.out.println("cumulated Fairy row for golem #" + k + " : " + rowSum);
        }

        System.out.println(rowSum);
        
    }

}
