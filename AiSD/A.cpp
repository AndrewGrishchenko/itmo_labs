#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <vector>

using namespace std;

int main() {
  freopen("agro.in", "r", stdin);
  freopen("agro.out", "w", stdout);

  int n;
  cin >> n;

  vector<int> a(n);

  int firstPos = 0;
  int secondPos = 0;
  int currentLength = 0;

  int ansFirst = 0;
  int ansSecond = 0;
  int maxLength = 0;

  for (int i = 0; i < n; i++) {
    cin >> a[i];

    if (i == 0) {
      firstPos = i + 1;
      secondPos = i + 1;
      currentLength += 1;
    } else if (i == 1) {
      secondPos = i + 1;
      currentLength += 1;
    } else {
      if (a[i] == a[i - 1] && a[i] == a[i - 2]) {
        if (currentLength > maxLength) {
          maxLength = currentLength;
          ansFirst = firstPos;
          ansSecond = secondPos;
        }
        currentLength = 2;
        firstPos = i;
      } else {
        secondPos = i + 1;
        currentLength += 1;
      }
    }
  }

  if (currentLength > maxLength) {
    ansFirst = firstPos;
    ansSecond = secondPos;
  }
  cout << ansFirst << " " << ansSecond;
}