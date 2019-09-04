public class threeSumDistinct {
  public static boolean checksum(int[] args) {
    for (int f = 0; f < args.length; f+=1) {
      for (int g = 0; g < args.length; g +=1) {
        for (int h = 0; h < args.length; h +=1) {
          if ((args[f] + args[g] + args[h] == 0) && (f!=g) && (g!=h) && (f!=h)) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
