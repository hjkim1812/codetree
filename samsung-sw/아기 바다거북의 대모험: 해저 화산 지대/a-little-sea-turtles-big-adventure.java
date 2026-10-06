import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.StringTokenizer;

class turtle {
    int id;
    int r;
    int c;
    int escaped_turn;
    boolean escaped;
    boolean isAlive;

    public turtle(int id, int r, int c, boolean escaped, boolean isAlive) {
        this.id = id;
        this.r = r;
        this.c = c;
        this.escaped = escaped;
        this.escaped_turn = -1;
        this.isAlive = isAlive;
    }
}

class volcano {
    int r;
    int c;
    int P;
    int pressure;
    boolean exploded;

    public volcano(int r, int c, int p, int pressure, boolean exploded) {
        this.r = r;
        this.c = c;
        P = p;
        this.pressure = pressure;
        this.exploded = exploded;
    }
}

public class Main {

    static int N, M, K;

    static turtle[] turtles;
    static volcano[] volcanos;

    static final int[] dr = {0, 1, 0, -1};
    static final int[] dc = {1, 0, -1, 0};

    static int[][] map;
    static int[][] heat;
    static boolean[][] occupied;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        map = new int[N][N];
        heat = new int[N][N];
        occupied = new boolean[N][N];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        turtles = new turtle[M + 1];
        volcanos = new volcano[K];

        for (int m = 1; m <= M; m++) {
            st = new StringTokenizer(br.readLine());

            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());

            turtles[m] = new turtle(m, r, c, false, true);
            occupied[r][c] = true;
        }

        for (int k = 0; k < K; k++) {
            st = new StringTokenizer(br.readLine());

            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            int P = Integer.parseInt(st.nextToken());

            volcanos[k] = new volcano(r, c, P, 0, false);
        }

        for (int turn = 1; turn <= 100; turn++) {
            move_turtles(turn);
            pressurize();
            eruption();
            reset();
        }

        for (int id = 1; id <= M; id++) {
            System.out.println(turtles[id].escaped_turn);
        }
    }

    public static boolean inRange(int r, int c) {
        return r >= 0 && r < N && c >= 0 && c < N;
    }

    public static int[][] bfs(int r, int c) {
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

                if (inRange(nr, nc)
                        && dist[nr][nc] == -1
                        && map[nr][nc] == 0
                        && !occupied[nr][nc]) {

                    dist[nr][nc] = dist[cr][cc] + 1;
                    q.offer(nr * N + nc);
                }
            }
        }

        return dist;
    }

    public static void move_turtles(int turn) {
        for (int id = 1; id <= M; id++) {
            turtle t = turtles[id];

            if (!t.escaped && t.isAlive) {

                occupied[t.r][t.c] = false;

                int[][] dist = bfs(N - 1, N - 1);

                if (dist[t.r][t.c] != -1) {
                    for (int d = 0; d < 4; d++) {
                        int nr = t.r + dr[d];
                        int nc = t.c + dc[d];

                        if (inRange(nr, nc)
                                && dist[nr][nc] == dist[t.r][t.c] - 1) {

                            t.r = nr;
                            t.c = nc;
                            break;
                        }
                    }
                }

                if (t.r == N - 1 && t.c == N - 1) {
                    t.escaped = true;
                    t.escaped_turn = turn;
                } else {
                    occupied[t.r][t.c] = true;
                }
            }
        }
    }

    public static void pressurize() {
        for (int k = 0; k < K; k++) {
            volcano v = volcanos[k];
            v.pressure += 10;
        }
    }

    public static void eruption() {
        while (true) {
            boolean cascade = false;

            for (int k = 0; k < K; k++) {
                volcano v = volcanos[k];

                if (!v.exploded && v.pressure + heat[v.r][v.c] >= v.P) {
                    cascade = true;
                    v.exploded = true;

                    heat[v.r][v.c] += v.P;

                    for (int d = 0; d < 4; d++) {
                        int r = v.r;
                        int c = v.c;
                        int p = v.P;

                        while (true) {
                            p /= 2;

                            if (p == 0) {
                                break;
                            }

                            int nr = r + dr[d];
                            int nc = c + dc[d];

                            if (!inRange(nr, nc)) {
                                break;
                            }

                            if (map[nr][nc] == 1) {
                                break;
                            }

                            heat[nr][nc] += p;

                            r = nr;
                            c = nc;
                        }
                    }
                }
            }

            if (!cascade) {
                break;
            }
        }

        for (int id = 1; id <= M; id++) {
            turtle t = turtles[id];

            if (!t.escaped && t.isAlive && heat[t.r][t.c] >= 20) {
                t.isAlive = false;
                occupied[t.r][t.c] = true;
            }
        }
    }

    public static void reset() {
        for (int i = 0; i < N; i++) {
            Arrays.fill(heat[i], 0);
        }

        for (int k = 0; k < K; k++) {
            volcano v = volcanos[k];

            if (v.exploded) {
                v.pressure = 0;
                v.exploded = false;
            }
        }
    }
}