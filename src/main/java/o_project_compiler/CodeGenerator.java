

package o_project_compiler;

import o_project_compiler.ast.VisitorAdaptor;
import o_project_compiler.ast.PrintExprStatement;
import o_project_compiler.ast.IdentDesignator;
import o_project_compiler.ast.ReadStatement;
import o_project_compiler.ast.MethodDecl;
import o_project_compiler.ast.IfThenStatement;
import o_project_compiler.ast.MethodCallDesignatorStatement;
import o_project_compiler.ast.ExprCondFactor;
import o_project_compiler.ast.BreakStatement;
import o_project_compiler.ast.BoolCond;

import o_project_compiler.ast.ContinueStatement;
import o_project_compiler.ast.NewScalarFactor;


import o_project_compiler.ast.Designator;
import o_project_compiler.ast.ConditionEnd;

import o_project_compiler.ast.MemberAccessDesignator;


import o_project_compiler.ast.AssignmentDesignatorStatement;
import o_project_compiler.ast.MethodCallFactor;
import o_project_compiler.ast.IfThenElseStatement;
import o_project_compiler.ast.ReturnExprStatement;
import o_project_compiler.ast.DoWhileStatement;


import o_project_compiler.ast.ClassDecl;

import o_project_compiler.ast.ConditionStart;

import o_project_compiler.ast.DesignatorFactor;


import o_project_compiler.ast.MethodName;
import o_project_compiler.ast.BoolFactor;


import o_project_compiler.ast.CharFactor;

import o_project_compiler.ast.IntFactor;

import o_project_compiler.ast.DoWhileStatementStart;
import o_project_compiler.ast.ActParsEnd;

import o_project_compiler.ast.ClassName;
import o_project_compiler.ast.SyntaxNode;
import o_project_compiler.ast.NewVectorFactor;

import o_project_compiler.ast.PrintExprIntConstStatement;
import o_project_compiler.ast.Else;
import o_project_compiler.ast.IdentDesignatorStart;
import o_project_compiler.ast.MemberAccessDesignatorStart;
import o_project_compiler.ast.ReturnNothingStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import o_project_compiler.exceptions.WrongObjectException;
import o_project_compiler.exceptions.WrongStructureException;
import o_project_compiler.inheritancetree.InheritanceTree;
import o_project_compiler.inheritancetree.InheritanceTreeNode;
import o_project_compiler.symboltable.Tab;
import o_project_compiler.util.Utils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;


public class CodeGenerator extends VisitorAdaptor {

    private enum RuntimeError {
        DYNAMIC_TRACE_WITHOUT_RETURN(1), VECTOR_OPERATION_ERROR(2);

        private final int code;

        RuntimeError(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    private int mainPc;
    private Obj currentClassObj = rs.etf.pp1.symboltable.Tab.noObj;
    private final Stack<Integer> currentDoWhileStartAddress = new Stack<>();
    private final Stack<Integer> currentSkipElseJump = new Stack<>();
    private final Stack<List<Integer>> currentBreakJumps = new Stack<>();
    private final Stack<List<Integer>> currentContinueJumps = new Stack<>();
    private final Stack<List<Integer>> currentNextCondTermJumps  = new Stack<>();
    private final List<Integer> currentSkipNextCondTerumps = new ArrayList<>();
    private int currentConditionalJump = 0;
    private final Stack<Obj> thisParameterObjs = new Stack<>();
    private final Map<Obj, List<Integer>> notYetDeclaredMethod = new HashMap<>();

    public int getMainPc() {
        return mainPc;
    }



    public void generateMethodInvocationCode(Obj overriddenMethod) {
        List<Integer> jmpAddresses = new ArrayList<>();
        int jccAddress;
        List<Obj> leafClasses = Tab.getLeafClasses();
        List<Obj> filteredLeafClasses = new ArrayList<>();
        for (Obj clss : leafClasses) {
            for (Obj member : clss.getType().getMembers()) {
                if (member.getKind() == Obj.Meth) {
                    try {
                        if (Utils.haveSameSignatures(member, overriddenMethod)) {
                            filteredLeafClasses.add(clss);
                        }
                    } catch (WrongObjectException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        for (Obj clss : filteredLeafClasses) {
            Code.put(Code.dup);
            Code.put(Code.getfield);
            Code.put2(1);
            Code.load(new Obj(Obj.Con, "", Tab.intType, clss.getLevel(), 0));
            Code.put(Code.jcc + Code.ne);
            jccAddress = Code.pc;
            Code.put2(0);
            Code.put(Code.pop);
            Code.put(Code.call);
            try {
                Obj method = InheritanceTree.getTreeNode(clss).getVMT().getSameSignatureMethod(overriddenMethod);
                int addr = method.getAdr();
                if (addr != 0) {
                    Code.put2(addr - Code.pc + 1);
                } else {
                    if (notYetDeclaredMethod.containsKey(method)) {
                        List<Integer> list = notYetDeclaredMethod.get(method);
                        list.add(Code.pc);
                    } else {
                        List<Integer> list = new ArrayList<>();
                        list.add(Code.pc);
                        notYetDeclaredMethod.put(method, list);
                    }
                    Code.put2(0);
                }
            } catch (WrongObjectException | WrongStructureException e) {
                e.printStackTrace();
            }
            Code.put(Code.jmp);
            jmpAddresses.add(Code.pc);
            Code.put2(0);
            Code.fixup(jccAddress);
        }

        Code.put(Code.getfield);
        Code.put2(0);

        Code.put(Code.invokevirtual);
        String methodSignature;
        try {
            methodSignature = Utils.getCompactClassMethodSignature(overriddenMethod);
        } catch (WrongObjectException e) {
            methodSignature = null;
            e.printStackTrace();
        }
        for (int i = 0; i < (methodSignature != null ? methodSignature.length() : 0); i++) {
            Code.put4(methodSignature.charAt(i));
        }
        Code.put4(-1);
        for (int address : jmpAddresses) {
            Code.fixup(address);
        }
    }

    private class ThisParameterLoader extends CodeGenerator {


        public void visit(IdentDesignator identDesignator) {
            int identDesignatorKind = identDesignator.obj.getKind();
            Obj obj;
            if (!currentClassObj.equals(rs.etf.pp1.symboltable.Tab.noObj)) {
                obj = new Obj(Obj.Var, SemanticAnalyzer.THIS, currentClassObj.getType(), 0, 1);
                if (identDesignatorKind == Obj.Fld) {
                    Code.load(obj);
                }
                if (identDesignatorKind == Obj.Meth) {
                    Struct superclass = currentClassObj.getType();
                    boolean found = false;
                    while (superclass != null) {
                        if (superclass.getMembersTable().searchKey(identDesignator.obj.getName()) != null) {
                            found = true;
                            break;
                        }
                        superclass = superclass.getElemType();
                    }
                    if (found) {
                        Code.load(obj);
                    }
                }
            }
        }


        public void visit(MemberAccessDesignator memberAccessDesignator) {
            Code.load(memberAccessDesignator.getDesignatorStart().obj);
        }

    }


    public void visit(ClassName className) {
        currentClassObj = className.obj;
    }


    public void visit(ClassDecl classDecl) {
        currentClassObj = rs.etf.pp1.symboltable.Tab.noObj;
    }


    public void visit(MethodName methodName) {
        Obj methodNameObj = methodName.obj;
        methodNameObj.setAdr(Code.pc);
        if (notYetDeclaredMethod.containsKey(methodNameObj)) {
            List<Integer> list = notYetDeclaredMethod.get(methodNameObj);
            for (int addr : list) {
                Code.fixup(addr);
            }
        }
        if (methodNameObj.getName().equals(Tab.MAIN)) {
            mainPc = Code.pc;
        }
        Code.put(Code.enter);
        Code.put(methodNameObj.getLevel());
        Code.put(methodNameObj.getLocalSymbols().size());
    }


    public void visit(MethodDecl methodDecl) {
        Obj methodNameObj = methodDecl.getMethodName().obj;
        if (methodNameObj.getType() == rs.etf.pp1.symboltable.Tab.noType) {
            Code.put(Code.exit);
            Code.put(Code.return_);
        } else {
            Code.put(Code.trap);
            Code.put(RuntimeError.DYNAMIC_TRACE_WITHOUT_RETURN.getCode());
        }
    }


    public void visit(ActParsEnd actParsEnd) {
        Designator methodDesignator = (actParsEnd.getParent() instanceof MethodCallDesignatorStatement)
                ? ((MethodCallDesignatorStatement) actParsEnd.getParent()).getDesignator()
                : ((MethodCallFactor) actParsEnd.getParent()).getDesignator();
        int offset = methodDesignator.obj.getAdr() - Code.pc;
        Obj thisParameterObj = thisParameterObjs.pop();
        if (methodDesignator.obj == Tab.lenMethod) {
            Code.put(Code.arraylength);
        } else if (!(methodDesignator.obj == Tab.ordMethod || methodDesignator.obj == Tab.chrMethod)) {
            if (!thisParameterObj.equals(rs.etf.pp1.symboltable.Tab.noObj)) {
                try {
                    InheritanceTreeNode thisParameterTypeNode = InheritanceTree
                            .getTreeNode((Tab.findObjForClass(thisParameterObj.getType())));
                    if (thisParameterTypeNode.getVMT().containsSameSignatureMethod(methodDesignator.obj)
                            && thisParameterTypeNode.hasChildren()) {
                        methodDesignator.traverseBottomUp(new ThisParameterLoader());
                        generateMethodInvocationCode(methodDesignator.obj);
                    } else {
                        Code.put(Code.call);
                        Code.put2(offset);
                    }
                } catch (WrongObjectException | WrongStructureException e) {
                    e.printStackTrace();
                }
            } else {
                Code.put(Code.call);
                Code.put2(offset);
            }
        }
    }


    public void visit(ReturnNothingStatement returnNothingStatement) {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }


    public void visit(ReturnExprStatement returnExprStatement) {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }


    public void visit(MethodCallDesignatorStatement methodCallDesignatorStatement) {
        if (methodCallDesignatorStatement.getDesignator().obj.getType() != rs.etf.pp1.symboltable.Tab.noType) {
            Code.put(Code.pop);
        }
    }


    public void visit(AssignmentDesignatorStatement assignmentDesignatorStatement) {
        Code.store(assignmentDesignatorStatement.getDesignator().obj);
    }


    public void visit(ReadStatement readStatement) {
        Struct designatorType = readStatement.getDesignator().obj.getType();

        if (designatorType.equals(rs.etf.pp1.symboltable.Tab.charType)) {
            Code.put(Code.bread);
        } else if (designatorType.equals(rs.etf.pp1.symboltable.Tab.intType)) {
            Code.put(Code.read);
        } else {
            int offset = Tab.readBoolMethod.getAdr() - Code.pc;
            Code.put(Code.call);
            Code.put2(offset);
        }
        Code.store(readStatement.getDesignator().obj);
    }


    public void visit(PrintExprStatement printExprStatement) {
        Struct exprType = printExprStatement.getExpr().obj.getType();

        Code.load(new Obj(Obj.Con, "width", rs.etf.pp1.symboltable.Tab.intType, 1, 0));
        if (exprType.equals(rs.etf.pp1.symboltable.Tab.charType)) {
            Code.put(Code.bprint);
        } else if (exprType.equals(rs.etf.pp1.symboltable.Tab.intType)) {
            Code.put(Code.print);
        } else {
            int offset = Tab.printBoolMethod.getAdr() - Code.pc;
            Code.put(Code.call);
            Code.put2(offset);
        }
    }


    public void visit(PrintExprIntConstStatement printExprIntConstStatement) {
        Struct exprType = printExprIntConstStatement.getExpr().obj.getType();

        Code.load(new Obj(Obj.Con, "width", rs.etf.pp1.symboltable.Tab.intType, printExprIntConstStatement.getIntValue(), 0));
        if (exprType.equals(rs.etf.pp1.symboltable.Tab.charType)) {
            Code.put(Code.bprint);
        } else if (exprType.equals(rs.etf.pp1.symboltable.Tab.intType)) {
            Code.put(Code.print);
        } else {
            int offset = Tab.printBoolMethod.getAdr() - Code.pc;
            Code.put(Code.call);
            Code.put2(offset);
        }
    }







    public void visit(DoWhileStatementStart doWhileStatementStart) {
        currentBreakJumps.push(new ArrayList<>());
        currentContinueJumps.push(new ArrayList<>());
        currentDoWhileStartAddress.push(Code.pc);
    }


    public void visit(DoWhileStatement doWhileStatement) {
        for (int address : currentBreakJumps.pop()) {
            Code.fixup(address);
        }
        int start = currentDoWhileStartAddress.pop();
        for (int address : currentSkipNextCondTerumps) {
            Code.put2(address, (start - address + 1));
        }
        currentSkipNextCondTerumps.clear();
        for (int address : currentNextCondTermJumps.pop()) {
            Code.fixup(address);
        }
    }


    public void visit(ConditionEnd conditionEnd) {
        if (conditionEnd.getParent() instanceof IfThenStatement
                || conditionEnd.getParent() instanceof IfThenElseStatement) {
            for (Integer address : currentSkipNextCondTerumps) {
                Code.fixup(address);
            }
            currentSkipNextCondTerumps.clear();
        } else {
            Code.putJump(0);
            currentSkipNextCondTerumps.add(Code.pc - 2);
        }
    }


    public void visit(Else else_) {
        Code.putJump(0);
        for (Integer address : currentNextCondTermJumps.pop()) {
            Code.fixup(address);
        }
        currentSkipElseJump.push(Code.pc - 2);
    }


    public void visit(IfThenStatement ifThenStatement) {
        for (Integer address : currentNextCondTermJumps.pop()) {
            Code.fixup(address);
        }
    }


    public void visit(IfThenElseStatement ifThenElseStatement) {
        Code.fixup(currentSkipElseJump.pop());
    }


    public void visit(BreakStatement breakStatement) {
        Code.putJump(0);
        currentBreakJumps.peek().add(Code.pc - 2);
    }


    public void visit(ContinueStatement continueStatement) {
        Code.putJump(0);
        currentContinueJumps.peek().add(Code.pc - 2);
    }


    public void visit(ConditionStart conditionStart) {
        if (conditionStart.getParent() instanceof DoWhileStatement) {
            List<Integer> continuesList = currentContinueJumps.pop();
            for (int address : continuesList) {
                Code.fixup(address);
            }
        }
        currentNextCondTermJumps.push(new ArrayList<>());
    }





    @Override
    public void visit(BoolCond boolCondFactor) {
        Code.load(new Obj(Obj.Con, "true", Tab.BOOL_TYPE, 1, 0));
        Code.putFalseJump(Code.eq, 0);
        currentNextCondTermJumps.peek().add(Code.pc - 2);
    }


    public void visit(IdentDesignator identDesignator) {
        int identDesignatorKind = identDesignator.obj.getKind();
        Obj obj = rs.etf.pp1.symboltable.Tab.noObj;
        if (!currentClassObj.equals(rs.etf.pp1.symboltable.Tab.noObj)) {
            obj = new Obj(Obj.Var, SemanticAnalyzer.THIS, currentClassObj.getType(), 0, 1);
            if (identDesignatorKind == Obj.Fld) {
                Code.load(obj);
            }
            if (identDesignatorKind == Obj.Meth) {
                Struct superclass = currentClassObj.getType();
                boolean found = false;
                while (superclass != null) {
                    if (superclass.getMembersTable().searchKey(identDesignator.obj.getName()) != null) {
                        found = true;
                        break;
                    }
                    superclass = superclass.getElemType();
                }
                if (found) {
                    Code.load(obj);
                }
            }
        }
        if (identDesignatorKind == Obj.Meth) {
            thisParameterObjs.push(obj);
        }
    }





    public void visit(MemberAccessDesignator memberAccessDesignator) {
        Code.load(memberAccessDesignator.getDesignatorStart().obj);
        if (memberAccessDesignator.obj.getKind() == Obj.Meth) {
            thisParameterObjs.push(memberAccessDesignator.getDesignatorStart().obj);
        }
    }


    public void visit(IdentDesignatorStart identDesignatorStart) {
        if (!currentClassObj.equals(rs.etf.pp1.symboltable.Tab.noObj)) {
            int identDesignatorStartKind = identDesignatorStart.obj.getKind();
            if (identDesignatorStartKind == Obj.Fld) {
                Obj obj = new Obj(Obj.Var, SemanticAnalyzer.THIS, currentClassObj.getType(), 0, 1);
                Code.load(obj);
            }
        }
    }


    public void visit(MemberAccessDesignatorStart memberAccessDesignatorStart) {
        Code.load(memberAccessDesignatorStart.getDesignatorStart().obj);
    }










    public void visit(DesignatorFactor designatorFactor) {
        Code.load(designatorFactor.obj);
    }


    public void visit(IntFactor intFactor) {
        Code.load(intFactor.obj);
    }


    public void visit(CharFactor charFactor) {
        Code.load(charFactor.obj);
    }


    public void visit(BoolFactor boolFactor) {
        Code.load(boolFactor.obj);
    }


    public void visit(NewScalarFactor newScalarFactor) {
        Code.put(Code.new_);
        try {
            Code.put2(Utils.sizeOfClassInstance(newScalarFactor.getType().obj.getType()));
        } catch (WrongStructureException e1) {
            e1.printStackTrace();
        }
        if (newScalarFactor.getType().obj.getType().getKind() == Struct.Class) {
            try {
                if (!InheritanceTree.getTreeNode(newScalarFactor.obj).getVMT().isEmpty()) {
                    Obj constObj = new Obj(Obj.Con, "", rs.etf.pp1.symboltable.Tab.intType, newScalarFactor.getType().obj.getAdr(), 1);
                    Code.put(Code.dup);
                    Code.load(constObj);
                    Code.put(Code.putfield);
                    Code.put2(0);
                    constObj.setAdr(newScalarFactor.getType().obj.getLevel());
                    Code.put(Code.dup);
                    Code.load(constObj);
                    Code.put(Code.putfield);
                    Code.put2(1);
                }
            } catch (WrongObjectException | WrongStructureException e) {
                e.printStackTrace();
            }
        }
    }


    public void visit(NewVectorFactor newVectorFactor) {
        Struct type = newVectorFactor.getType().obj.getType();
        Code.put(Code.newarray);
        Code.put(type.getKind() == Struct.Char ? 0 : 1);
    }

}