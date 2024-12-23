package jlox;

public class Interperter implements Expr.Visitor<Object> {
    public void interpret(Expr expr) {
        try {
            Object value = evaluate(expr);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNunmberOperand(expr.operator, right);
                return -(double)right;
            default:
                break;
        }

        // unreachable 
        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left  = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case GREATER:
                checkNunmberOperand(expr.operator, left, right);
                
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNunmberOperand(expr.operator, left, right);
                
                return (double)left >= (double)right;
            case LESS:
                checkNunmberOperand(expr.operator, left, right);
                
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNunmberOperand(expr.operator, left, right);
                
                return (double)left <= (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }

                throw new RuntimeError(expr.operator, "Operands must both be numbers or strings.");
            case MINUS:
                checkNunmberOperand(expr.operator, left, right);

                return (double)left - (double)right;
            case SLASH:
                checkNunmberOperand(expr.operator, left, right);

                return (double)left / (double)right;
            case STAR:
                checkNunmberOperand(expr.operator, left, right);
                
                return (double)left * (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            default:
                break;
        }

        // unreachable
        return null;
    }

    private boolean isTruthy(Object object) {
        if (object == null)            return false;
        if (object instanceof Boolean) return (boolean)object;

        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null)              return false;

        return a.equals(b);
    }

    private void checkNunmberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;

        throw new RuntimeError(operator, "Operand must be a number");
    }

    private void checkNunmberOperand(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;

        throw new RuntimeError(operator, "Operands must both be numbers or strings.");
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }
}
