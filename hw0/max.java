public class max {
  public static int maxfunc(int[] args) {
    int index = 0;
    int answer = 0;
    while (index < args.length) {
      if (args[index] > answer) {
          answer = args[index];
      }
      index = index + 1;
    }
    return answer;
  }
  public static int main(int[] args) {
    return maxfunc(args);
  }
}
