#define _CRT_SECURE_NO_WARNINGS

#include <algorithm>
#include <iostream>
#include <queue>
#include <vector>

using namespace std;

const int dx[] = {-1, 0, 1, 0};
const int dy[] = {0, 1, 0, -1};
const char dir[] = {'N', 'E', 'S', 'W'};

int main() {
  freopen("input.txt", "r", stdin);
  freopen("output.txt", "w", stdout);

  int n, m, sx, sy, tx, ty;
  cin >> n >> m >> sx >> sy >> tx >> ty;
  sx--;
  sy--;
  tx--;
  ty--;

  vector<string> map(n);
  for (int i = 0; i < n; i++) {
    cin >> map[i];
  }

  vector<vector<int>> dist(n, vector<int>(m, 1e9));
  vector<vector<pair<int, int>>> parent(n, vector<pair<int, int>>(m, {-1, -1}));
  vector<vector<char>> move(n, vector<char>(m, 0));
  dist[sx][sy] = 0;

  priority_queue<tuple<int, int, int>, vector<tuple<int, int, int>>, greater<>> pq;
  pq.emplace(0, sx, sy);

  while (!pq.empty()) {
    auto [d, x, y] = pq.top();
    pq.pop();

    if (d > dist[x][y])
      continue;

    for (int i = 0; i < 4; i++) {
      int nx = x + dx[i];
      int ny = y + dy[i];

      if (nx < 0 || nx >= n || ny < 0 || ny >= m || map[nx][ny] == '#')
        continue;

      int cost = (map[nx][ny] == '.' ? 1 : 2);
      int new_dist = dist[x][y] + cost;

      if (new_dist < dist[nx][ny]) {
        dist[nx][ny] = new_dist;
        parent[nx][ny] = {x, y};
        move[nx][ny] = dir[i];
        pq.emplace(new_dist, nx, ny);
      }
    }
  }

  if (dist[tx][ty] == 1e9) {
    cout << -1;
    return 0;
  }

  cout << dist[tx][ty] << endl;

  string path;
  int x = tx, y = ty;
  while (x != sx || y != sy) {
    path += move[x][y];
    auto [px, py] = parent[x][y];
    x = px;
    y = py;
  }
  reverse(path.begin(), path.end());
  cout << path;
}