#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <set>
#include <stack>
#include <vector>

using namespace std;

const int MAXN = 105;

vector<int> g[MAXN], gt[MAXN];
stack<int> order;
vector<bool> used(MAXN);
vector<int> comp(MAXN);
vector<set<int>> scc_g;
vector<int> in_degree;

void dfs1(int v) {
  used[v] = true;
  for (auto u : g[v]) {
    if (!used[u]) {
      dfs1(u);
    }
  }
  order.push(v);
}

void dfs2(int v, int c) {
  comp[v] = c;
  for (auto u : gt[v]) {
    if (comp[u] == -1) {
      dfs2(u, c);
    }
  }
}

int main() {
  freopen("input.txt", "r", stdin);
  freopen("output.txt", "w", stdout);

  int n;
  cin >> n;
  vector<int> a(n + 1);
  for (int i = 1; i <= n; i++) {
    cin >> a[i];
    g[a[i]].push_back(i);
    gt[i].push_back(a[i]);
  }

  fill(used.begin(), used.end(), false);
  for (int i = 1; i <= n; i++) {
    if (!used[i]) {
      dfs1(i);
    }
  }

  fill(comp.begin(), comp.end(), -1);
  int c = 0;
  while (!order.empty()) {
    int v = order.top();
    order.pop();
    if (comp[v] == -1) {
      dfs2(v, c++);
    }
  }

  scc_g.resize(c);
  in_degree.assign(c, 0);
  for (int v = 1; v <= n; v++) {
    for (auto u : g[v]) {
      if (comp[v] != comp[u]) {
        if (scc_g[comp[v]].find(comp[u]) == scc_g[comp[v]].end()) {
          scc_g[comp[v]].insert(comp[u]);
          in_degree[comp[u]]++;
        }
      }
    }
  }

  int ans = 0;
  for (int i = 0; i < c; i++) {
    if (in_degree[i] == 0) {
      ans++;
    }
  }

  cout << ans;
}