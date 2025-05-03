#define _CRT_SECURE_NO_WARNINGS

#include <algorithm>
#include <iostream>
#include <vector>

using namespace std;

int main() {
  freopen("number.in", "r", stdin);
  freopen("number.out", "w", stdout);

  vector<string> vec;
  string tmp;
  while (cin >> tmp) {
    vec.push_back(tmp);
  }

  sort(vec.begin(), vec.end(), [](const string& a, const string& b) { return a + b > b + a; });

  for (auto s : vec) {
    cout << s;
  }
}
