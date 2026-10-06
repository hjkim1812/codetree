import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
    static int N;
    static int r, c, d;

    static int[][] A;
    static boolean[][] visited;
    
    static final int[] dr = {0, 1, 0, -1}; // 좌 하 우 상
    static final int[] dc = {-1, 0, 1, 0};
    
    static final int[] dMapper = {0, 3, 1, 0, 2};
    
    public static void main(String[] args) throws IOException {
    
        // 관리해야할 것
        // 맵
        // 고래 위치, 방향
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        
        N = Integer.parseInt(st.nextToken());
        r = Integer.parseInt(st.nextToken());
        c = Integer.parseInt(st.nextToken());
        d = Integer.parseInt(st.nextToken());
        d = dMapper[d];
        
        HashMap<Integer, Integer> d_map = new HashMap<>(); // 방향배열 매퍼
        d_map.put(1,3);
        d_map.put(2,1);
        d_map.put(3,0);
        d_map.put(4,2);
        
        A = new int[N+1][N+1];
        visited = new boolean[N+1][N+1];
        
        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            A[i] = new int[N+1];
            visited[i] = new boolean[N+1];
            for (int j = 1; j <= N; j++) {
                A[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        
        visited[r][c] = true;
        System.out.println(r + " " + c);
        
        while (true) {
            while (step_1()) {
                System.out.println(r + " " + c);
            }
            
            if (!step_2()) {
                break;
            }
            
            System.out.println(r + " " + c);
        }
    }
    
    public static boolean inRange(int r, int c) {
        return (r > 0 && r <= N && c > 0 && c <=N);
    }
    
    public static int[][] bfs(int r, int c) {
        Queue<Integer> q = new ArrayDeque<>();
        int[][] dist = new int[N+1][N+1];
        
        for (int i = 1; i <= N; i++) {
            dist[i] = new int[N+1];
            for (int j = 1; j <= N; j++) {
                dist[i][j] = -1;
            }
        }
        
        q.offer(r * 100 + c);
        dist[r][c] = 0;
        
        while (!q.isEmpty()) {
            int curr = q.poll();
            r = curr / 100;
            c = curr % 100;
            
            for (int dir = 0; dir < 4; dir++) {
                int nr = r + dr[dir];
                int nc = c + dc[dir];
                if (inRange(nr, nc) && A[nr][nc] == 0 && dist[nr][nc] == -1) {
                    dist[nr][nc] = dist[r][c] + 1;
                    q.offer(nr * 100 + nc);
                }
            }
        }
        
        return dist;
    }
    
    public static boolean step_1() {
        /*
    1. 인접 탐험
        고래의 현재 방향 처리??
        방문하지 않은 인접 칸 있다면:
            1. 현재 방향 직진
            2. 좌회전 후 직진
            3. 우회전 후 직진
            4. 180도 회전 후 직진

    이동 후 방향 갱신
    방문 가능 인접칸 없을 때까지 반복
         */
        
        for (int nd : new int[]{d, (d + 1) % 4, (d + 3) % 4, (d + 2) % 4}) {
            int nr = r + dr[nd];
            int nc = c + dc[nd];
            
            if (inRange(nr,nc) && A[nr][nc] == 0 && !visited[nr][nc]) {
                r = nr;
                c = nc;
                d = nd;
                visited[r][c] = true;
                return true;
            }
        }
        return false;
    }
    
    public static boolean step_2() {
        /*
        2. 가장 가까운 바다로 이동
        인접 탐험 안되면 -> 가장 가까운 바다로 이동
        행번호 작은순 / 열번호 작은순
        좌하우상
        
        ㄱ. BFS로 타겟 위치 지정
        ㄴ. 타겟 위치에서 역방향 BFS로 현재 위치에서의 우선순위에 따른 최단경로 따름
        */
        int[][] dist = new int[N+1][N+1];
        for (int i = 1; i <= N; i++) {
            dist[i] = new int[N+1];
        }
        dist = bfs(r, c);
        
        int tr = 0;
        int tc = 0;
        int tdist = N * N;
        
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <=N; j++) {
                if (!visited[i][j] && dist[i][j] != -1 && dist[i][j] < tdist) {
                    tr = i;
                    tc = j;
                    tdist = dist[i][j];
                }
            }
        }
        
        if (tr == 0) {
            return false;
        }
        
        dist = bfs(tr, tc);
        while (r != tr || c != tc) {
            for (int dir = 0; dir < 4; dir++) {
                int nr = r + dr[dir];
                int nc = c + dc[dir];
                if (inRange(nr, nc) && (dist[r][c] == dist[nr][nc] + 1)) {
                    r = nr;
                    c = nc;
                    d = dir;
                    break;
                }
            }
        }
        
        visited[r][c] = true;
        
//        System.out.println(tr+ " " +  tc + " "  + tdist);
        
        /*
        for (int i = 1; i <= N; i++) {
            System.out.println(Arrays.toString(dist[i]));
        }*/
        
        return true;
    }
    
}

/*
N x N 1-based 이동 가능 
이동 가능 0, 불가능 1



2. 가장 가까운 바다로 이동
    인접 탐험 안되면 -> 가장 가까운 바다로 이동
    행번호 작은순 / 열번호 작은순
    좌하우상
    
    ㄱ. BFS로 타겟 위치 지정
    ㄴ. 타겟 위치에서 역방향 BFS로 현재 위치에서의 우선순위에 따른 최단경로 따름
    
도착시 1번부터 반복

종료조건:
    모든 바다 탐험시
종료시:
    방문 바다칸 방문 순서대로 출력(시작포함)
    
    
*/