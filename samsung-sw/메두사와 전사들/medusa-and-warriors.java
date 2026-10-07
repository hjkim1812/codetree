import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.StringTokenizer;

class Warrior {
    int r, c;
    boolean alive;
    boolean stone;

    Warrior(int r, int c) {
        this.r = r;
        this.c = c;
        alive = true;
        stone = false;
    }
}

public class Main {
    
    static int N, M;
    static int SR, SC;
    static int ER, EC;
    
    static Warrior[] warriors;
    
    static int[][] map;
    
    static final int[] dr = {-1, 1, 0, 0}; // 상 하 좌 우
    static final int[] dc = {0, 0, -1, 1};
    
    static final int[] dr2 = {0, 0, -1, 1}; // 좌 우 상 하
    static final int[] dc2 = {-1, 1, 0, 0};
    
    static final int[][] drSights = { // 상하좌우 방향에 따라 각 중앙 / 왼쪽대각 / 오른쪽대각
            {-1, -1, -1},
            {1, 1, 1},
            {0, 1, -1},
            {0, -1, 1}
    };
    
    static final int[][] dcSights = {
            {0, -1, 1},
            {0, 1, -1},
            {-1, -1, -1},
            {1, 1, 1}
    };
    
    static boolean inRange(int r, int c){
        return (r >= 0 && r < N && c >= 0 && c < N);
    }
    
    static int[][] distToPark(int r, int c){
        int[][] dist = new int[N][N];
        for (int i = 0; i < N; i++) {
            Arrays.fill(dist[i], -1);
        }
        
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(r * N + c);
        dist[r][c] = 0;
        
        while (!q.isEmpty()) {
            int curr = q.poll();
            int cr = curr / N;
            int cc = curr % N;
            
            for (int d = 0; d < 4; d++) {
                int nr = cr + dr[d];
                int nc = cc + dc[d];
                
                if (inRange(nr, nc) && dist[nr][nc] == -1 && map[nr][nc] == 0) {
                    dist[nr][nc] = dist[cr][cc] + 1;
                    q.offer(nr*N + nc);
                }
            }
        }
        return dist;
    }
    
    static boolean moveMedusa(int r, int c, int[][] dist) {
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d];
            int nc = c + dc[d];
            
            if (!inRange(nr, nc)) continue;
            
            if (dist[nr][nc] == dist[r][c] - 1) {
                SR = nr;
                SC = nc;
                for (int m = 0; m < M; m++) {
                    Warrior w = warriors[m];
                    if (!w.alive) continue;
                    if (w.r == SR && w.c == SC) {
                        w.alive = false;
                    }
                    
                }
                return true;
            }
        }
        return false;
    }
    
    static boolean[][] chooseDir() {
        int maxCnt = -1;
        int[][] warriorCnt = new int[N][N];
        boolean[][] output = new boolean[N][N];

        for (int m = 0; m < M; m++) {
            Warrior w = warriors[m];
            if (!w.alive) continue;
            warriorCnt[w.r][w.c] += 1;
        }

        int[] types = {0, 1, 2};

        for (int d = 0; d < 4; d++) {
            int tempCnt = 0;
            boolean[][] inSight = new boolean[N][N];
            Queue<int[]> q = new ArrayDeque<>();

            // 메두사 바로 앞 3개 노드에서 시작
            for (int type : types) {
                int nr = SR + drSights[d][type];
                int nc = SC + dcSights[d][type];

                if (!inRange(nr, nc)) continue;

                inSight[nr][nc] = true;
                q.offer(new int[]{nr, nc, type});
            }

            while (!q.isEmpty()) {
                int[] curr = q.poll();
                int cr = curr[0];
                int cc = curr[1];
                int ct = curr[2];

                int[] nDirs;

                if (ct == 0) {
                    nDirs = new int[]{0};
                } else if (ct == 1) {
                    nDirs = new int[]{0, 1};
                } else {
                    nDirs = new int[]{0, 2};
                }

                for (int nd : nDirs) {
                    int nr = cr + drSights[d][nd];
                    int nc = cc + dcSights[d][nd];

                    if (!inRange(nr, nc)) continue;

                    if (!inSight[nr][nc]) {
                        inSight[nr][nc] = true;
                        q.offer(new int[]{nr, nc, ct});
                    }
                }
            }
            
            for (Warrior w : warriors) {
                if (!w.alive) continue;
                if (inSight[w.r][w.c]) {

                    int type = typeIndicator(SR, SC, w.r, w.c, d);
                    q.offer(new int[]{w.r, w.c, type});

                    while (!q.isEmpty()) {
                        int[] curr = q.poll();
                        int cr = curr[0];
                        int cc = curr[1];
                        int ct = curr[2];
                        
                        int[] nDirs;

                        if (ct == 0) {
                            nDirs = new int[]{0};
                        } else if (ct == 1) {
                            nDirs = new int[]{0, 1};
                        } else {
                            nDirs = new int[]{0, 2};
                        }

                        for (int nd : nDirs) {
                            int nr = cr + drSights[d][nd];
                            int nc = cc + dcSights[d][nd];

                            if (!inRange(nr, nc)) continue;

                            if (inSight[nr][nc]) {
                                inSight[nr][nc] = false;
                                q.offer(new int[]{nr, nc, ct});
                            }
                        }
                    }
                    
                    
                }
            }
            
            for (Warrior w : warriors) {
                if (!w.alive) continue;
                if (inSight[w.r][w.c]) {
                    tempCnt++;
                }
            }
            

            
            if (tempCnt > maxCnt) {
                maxCnt = tempCnt;
                output = inSight;
            }
            
        }

        return output;
    }
    
    static int typeIndicator(int sr, int sc, int wr, int wc, int dir) {
        int vr = wr - sr, vc = wc - sc;
        int str = dr[dir], stc = dc[dir];
        
        int cross = str * vc - stc * vr;
        
        if (cross > 0) return 1;
        if (cross == 0) return 0;
        return 2;
    }
    
    static int dist(int sr, int sc, int er, int ec) {
        return Math.abs(sr - er) + Math.abs(sc - ec);
    }
    
    static int[] moveWarriors(boolean[][] sight) {
        int[] moveAndAttack = new int[2];
        for (Warrior w: warriors) {
            if (!w.alive) continue;
            if (w.stone) continue; 
            
            int r = w.r;
            int c = w.c;
            int dist = dist(SR, SC, r, c);
            
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d];
                int nc = c + dc[d];
                if (inRange(nr, nc) && !sight[nr][nc] && dist == dist(nr, nc, SR, SC) + 1) {
                    w.r = nr;
                    w.c = nc;
                    r = nr;
                    c = nc;
                    moveAndAttack[0]++;
                    if (nr == SR && nc == SC) {
                        moveAndAttack[1]++;
                        w.alive = false;
                    }
                    break;
                }
            }
            
            dist = dist(SR, SC, r, c);
            if (!w.alive) continue;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr2[d];
                int nc = c + dc2[d];
                if (inRange(nr, nc) && !sight[nr][nc] && dist == dist(nr, nc, SR, SC) + 1) {
                    w.r = nr;
                    w.c = nc;
                    moveAndAttack[0]++;
                    if (nr == SR && nc == SC) {
                        moveAndAttack[1]++;
                        w.alive = false;
                    }
                    break;
                }
            }
        }
        return moveAndAttack;
    }
    
    static void reset() {
        for (Warrior w : warriors) {
            if (w.alive && w.stone) {
                w.stone = false;
            }
        }
    }
    
    
    public static void main(String[] args) throws IOException {
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         StringTokenizer st = new StringTokenizer(br.readLine());
         
         N = Integer.parseInt(st.nextToken());
         M = Integer.parseInt(st.nextToken());
         
         st = new StringTokenizer(br.readLine());
         
         SR = Integer.parseInt(st.nextToken());
         SC = Integer.parseInt(st.nextToken());
         ER = Integer.parseInt(st.nextToken());
         EC = Integer.parseInt(st.nextToken());
         
         st = new StringTokenizer(br.readLine());
         
         warriors = new Warrior[M];
         
         for (int m = 0; m < M; m++) {
             int r = Integer.parseInt(st.nextToken());
             int c = Integer.parseInt(st.nextToken());
             Warrior w = new Warrior(r,c);
             warriors[m] = w;
         }
         
         map = new int[N][N];
         
         for (int i = 0; i < N; i++) {
             st = new StringTokenizer(br.readLine());
             for (int j = 0; j < N; j++) {
                 map[i][j] = Integer.parseInt(st.nextToken());
             }
         }
         
         int[][] dist = distToPark(ER, EC);
         if (dist[SR][SC] == -1) {
             System.out.println(-1);
             return;
         }
         
         while (true) {
             moveMedusa(SR, SC, dist);
             if (SR == ER && SC == EC) {
                 System.out.println(0);
                 break;
             }
             
             boolean[][] sight = chooseDir();

             int stoneCnt = 0;

             for (Warrior w : warriors) {
                 if (!w.alive) continue;

                 if (sight[w.r][w.c]) {
                     w.stone = true;
                     stoneCnt++;
                 }
             }
             
             int[] moveAndAttack = moveWarriors(sight);
             
             reset();
             
             System.out.println(moveAndAttack[0] + " " + stoneCnt+ " " + moveAndAttack[1]);
         }
    }
}
