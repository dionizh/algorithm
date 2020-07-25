/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

public class GreatCircle {
    public static void main(String[] args) {
        double x1 = Math.toRadians(Double.parseDouble(args[0]));
        double y1 = Math.toRadians(Double.parseDouble(args[1]));
        double x2 = Math.toRadians(Double.parseDouble(args[2]));
        double y2 = Math.toRadians(Double.parseDouble(args[3]));

        double sin1 = Math.sin((x2 - x1) / 2.0);
        double val1 = Math.pow(sin1, 2.0);
        double sin2 = Math.sin((y2 - y1) / 2.0);
        double val2 = Math.pow(sin2, 2.0);
        double val3 = val1 + Math.cos(x1) * Math.cos(x2) * val2;
        double dist = 2 * 6371.0 * Math.asin(Math.sqrt(val3));
        System.out.println(dist + " kilometers");
    }
}
