package o_project_compiler.methodsignature;

import o_project_compiler.ast.ActParsEnd;
import o_project_compiler.ast.ActParsStart;
import o_project_compiler.ast.IdentDesignator;
import o_project_compiler.ast.MemberAccessDesignator;
import o_project_compiler.ast.MultipleExprExprList;
import o_project_compiler.ast.SingleExprExprList;
import o_project_compiler.ast.VisitorAdaptor;
import o_project_compiler.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class MethodSignatureGenerator extends VisitorAdaptor {

    private MethodSignature methodSignature;
    private int level = -1;

    public MethodSignature getMethodSignature() {
        return methodSignature;
    }

    public void visit(IdentDesignator identDesignator) {
        if (methodSignature == null) {
            methodSignature = new GlobalMethodSignature(identDesignator.obj.getName());
        }
    }

    public void visit(MemberAccessDesignator memberAccessDesignator) {
        if (methodSignature == null) {
            methodSignature = new ClassMethodSignature(memberAccessDesignator.obj.getName(),
                    memberAccessDesignator.getDesignatorStart().obj.getType());
        }
    }

    public void visit(ActParsStart actParsStart) {
        level++;
    }

    public void visit(ActParsEnd actParsEnd) {
        level--;
    }

    public void visit(MultipleExprExprList multipleExprExprList) {
        if (level == 0) {
            methodSignature.addParameter(multipleExprExprList.getExpr().obj);
            if (multipleExprExprList.getExpr().obj.getType() == Tab.noType
                    && multipleExprExprList.getExpr().obj.getKind() != Obj.Meth) {
                methodSignature.setContainsUndeclaredType();
            }
        }
    }

    public void visit(SingleExprExprList singleExprExprList) {
        if (level == 0) {
            methodSignature.addParameter(singleExprExprList.getExpr().obj);
            if (singleExprExprList.getExpr().obj.getType() == Tab.noType
                    && singleExprExprList.getExpr().obj.getKind() != Obj.Meth) {
                methodSignature.setContainsUndeclaredType();
            }
        }
    }

}
