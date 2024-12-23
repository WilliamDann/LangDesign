package util;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: GenAst <output_dir>");
            System.exit(64);
        }

        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
            "Binary : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal : Object value",
            "Unary : Token operator, Expr right"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path    = outputDir + "/" + baseName + ".java";
        PrintWriter pw = new PrintWriter(path, "UTF-8");
        
        pw.println("package jlox;");
        pw.println("import java.util.List;");
        pw.println();
        pw.println("abstract class " + baseName + " {");

        defineVisitor(pw, baseName, types);

        for(String type : types) {
            String className = type.split(":")[0].trim();
            String fields    = type.split(":")[1].trim();
            defineType(pw, baseName, className, fields);
        }
        pw.println("    abstract <R> R accept(Visitor<R> visitor);");

        pw.println("}");
        pw.close();
    }

    private static void defineType(PrintWriter pw, String baseName, String className, String fieldList) {
        // class header
        pw.println("    static class " + className + " extends " + baseName + " {");
        // pw.println(" " + className);
        pw.println("        public " + className + "(" + fieldList + ") {");
        
        // constructor
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            pw.println("            this." + name + " = " + name + ";");
        }

        pw.println("        }");

        // visitor pattern
        pw.println();
        pw.println("        @Override");
        pw.println("        <R> R accept(Visitor<R> visitor) {");
        pw.println("            return visitor.visit" + className + baseName + "(this);");
        pw.println("        }");

        // fields
        pw.println();
        for (String field : fields) {
            pw.println("        final " + field + ";");
        }

        pw.println("    }\n");
    }

    private static void defineVisitor(PrintWriter pw, String baseName, List<String> types) {
        pw.println("    interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            pw.println("        R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }
        
        pw.println("    }");
    }
}
