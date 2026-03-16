import manager.FYPEvaluator;

public class FYP {
    public static void main(String[] args) {
        String infile = args[0];
        String outfile = String.format("result.%s.txt", args[0]);
        new FYPEvaluator().evaluate(infile, outfile);
    }
}
