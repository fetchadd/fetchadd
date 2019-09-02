import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

import java.io.FileInputStream;
import java.io.InputStream;

public class Test {
    public static void main(String[] args) throws Exception {
        // Create lexer/parser to build trees from stdin
        String inputFile = null;

        if (args.length > 0) {
            inputFile = args[0];
        }

        InputStream is = System.in;

        if (inputFile != null) {
            is = new FileInputStream(inputFile);
        }

        ANTLRInputStream stream = new ANTLRInputStream(is);

        VecMathLexer lex = new VecMathLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        VecMathParser p = new VecMathParser(tokens);
         p.prog();

    }
}
