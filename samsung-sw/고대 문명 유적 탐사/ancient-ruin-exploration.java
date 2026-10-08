import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.StringTokenizer;

class RelicResult{
    int value;
    int[][] rFloor;
    
    public RelicResult(int value, int[][] rFloor) {
        super();
        this.value = value;
        this.rFloor = rFloor;
    }
}

public class Main {
    static int K, M;
    static int[][] floor;
    static int[] numbers;
    
    static int pointerM;
    
    static final int[] dr = {-1, -1, 0, 1, 1, 1, 0, -1}; // 12시방향부터 시계방향
    static final int[] dc = {0, 1, 1, 1, 0, -1, -1, -1};
    
    static final int[] rotDegrees = {90, 180, 270};
    
    
    static int[][] turn(int r, int c, int angle){
        int[][] rFloor = new int[5][5];
        
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                rFloor[i][j] = floor[i][j];
            }
        }
        
        for (int d = 0; d < 8; d++) {
            int nr = r + dr[d];
            int nc = c + dc[d];
            
            int rotIdx = 0;
            
            if (angle == 90) rotIdx = 6;
            else if (angle == 180) rotIdx = 4;
            else rotIdx = 2;
            
            int or = r + dr[(d+rotIdx)%8];
            int oc = c + dc[(d+rotIdx)%8];
            
            rFloor[nr][nc] = floor[or][oc];
        }
        
        return rFloor;
    }
    
    static RelicResult findRelics(int[][] rFloor) {
        boolean[][] visited = new boolean[5][5];
        int value = 0;
        
        Queue<Integer> q = new ArrayDeque<>();
        
        
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Queue<Integer> qCoord = new ArrayDeque<>();
                
                if (visited[i][j]) continue;
                
                int cnt = 0;
                
                int number = rFloor[i][j];
                q.offer(i * 5 + j);
                visited[i][j] = true;
                
                while (!q.isEmpty()) {
                    int curr = q.poll();
                    int cr = curr / 5;
                    int cc = curr % 5;
                    qCoord.offer(curr);
                    
                    cnt++;
                    
                    for (int d = 0; d < 8; d += 2) {
                        int nr = cr + dr[d];
                        int nc = cc + dc[d];
                        
                        if (!inRange(nr, nc)) continue;
                        
                        if (!visited[nr][nc] && rFloor[nr][nc] == number) {
                            q.offer(nr * 5 + nc);
                            visited[nr][nc] = true;
                        }
                    }
                }
                
                if (cnt >= 3) {
                    value += cnt;
                    while (!qCoord.isEmpty()) {
                        int coord = qCoord.poll();
                        rFloor[coord/5][coord%5] = 0;
                    }
                    
                }
            }
        }
        return new RelicResult(value, rFloor);
    }
    
    static boolean inRange(int r, int c) {
        return (r >= 0 && r < 5 && c >= 0 && c < 5);
    }
    
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        
        pointerM = 0;
        
        floor = new int[5][5];
        
        for (int i = 0; i < 5; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < 5; j++) {
                floor[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        
        numbers = new int[M];
        st = new StringTokenizer(br.readLine());
        
        for (int i = 0; i < M; i++) {
            numbers[i] = Integer.parseInt(st.nextToken());
        }
        
        StringBuilder sb = new StringBuilder();
        
        for (int turn = 1; turn <= K; turn++) {
            
            int turnValue = 0;
            int maxValue = -1;
            int[][] bestFloor = new int[5][5];
            
            
            for (int angle : rotDegrees) {
                for (int j = 1; j < 4; j++) {
                    for (int i = 1; i < 4; i++){
                        int[][] rFloor = turn(i,j,angle);
                        
//                        System.out.println("--------map--------");
//                        for (int row = 0; row < 5; row++) {
//                            System.out.println(Arrays.toString(rFloor[row]));
//                        }
                        
                        RelicResult res = findRelics(rFloor);
                        int value = res.value;
                        int[][] remain = res.rFloor;
                        
//                        System.out.println("value : " + value);
                        
                        if (value > maxValue) {
                            maxValue = value;
                            bestFloor = remain;
                        }
                    }
                }
            }
            
            turnValue += maxValue;
//            System.out.println("value after rotations : " + maxValue);
            
            while (true) {
                for (int j = 0; j < 5; j++) {
                    for (int i = 4; i >= 0; i--) {
                        if (bestFloor[i][j] == 0) {
                            bestFloor[i][j] = numbers[pointerM++];
                        }
                    }
                }
                
                RelicResult res2 = findRelics(bestFloor);
                
                if (res2.value == 0) break;
                
                turnValue += res2.value;
                bestFloor = res2.rFloor;
                
            }

            if (turnValue == 0) break;
            
            sb.append(turnValue + " ");
            
            floor = bestFloor;
        }
        System.out.println(sb);
    }
}
