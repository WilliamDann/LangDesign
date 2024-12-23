package jlox;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    static boolean hadError        = false;
    static boolean hadRuntimeError = false;

    private static final Interperter interperter = new Interperter();

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }
        else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError)
            System.exit(65);
        if (hadRuntimeError)
            System.exit(70);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader    br  = new BufferedReader(isr);

        for (;;)
        {
            System.out.print("> ");
            String line = br.readLine();
            
            if (line == null)
                break;
        
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner s          = new Scanner(source);
        List<Token> tokens = s.scanTokens();
        Parser      parser = new Parser(tokens);
        Expr        expr   = parser.parse();

        if (hadError) return;

        interperter.interpret(expr);
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + " ]");
        hadRuntimeError = true;
    }

    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at ", message);
        }
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line  " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}