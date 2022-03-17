
package o_project_compiler;

import o_project_compiler.ast.VisitorAdaptor;
import o_project_compiler.ast.PrintExprStatement;
import o_project_compiler.ast.VectorGlobalVar;
import o_project_compiler.ast.BoolCond;

import o_project_compiler.ast.MultipleExprExprList;
import o_project_compiler.ast.IdentDesignator;
import o_project_compiler.ast.ReadStatement;
import o_project_compiler.ast.MethodDecl;
import o_project_compiler.ast.NonEmptyStatementList;
import o_project_compiler.ast.CorrectExpr;
import o_project_compiler.ast.NonVoidFormPars;
import o_project_compiler.ast.MethodCallDesignatorStatement;

import o_project_compiler.ast.MethodEnd;
import o_project_compiler.ast.ExprCondFactor;
import o_project_compiler.ast.BreakStatement;

import o_project_compiler.ast.ContinueStatement;
import o_project_compiler.ast.Program;
import o_project_compiler.ast.NewScalarFactor;
import o_project_compiler.ast.VoidFormPars;

import o_project_compiler.ast.ScalarGlobalVar;
import o_project_compiler.ast.DelimitedFactor;

import o_project_compiler.ast.MemberAccessDesignator;



import o_project_compiler.ast.AssignmentDesignatorStatement;
import o_project_compiler.ast.CharLiteral;
import o_project_compiler.ast.MethodCallFactor;
import o_project_compiler.ast.FactorTerm;
import o_project_compiler.ast.BoolLiteral;

import o_project_compiler.ast.ReturnExprStatement;
import o_project_compiler.ast.DoWhileStatement;


import o_project_compiler.ast.ClassDecl;
import o_project_compiler.ast.IntLiteral;

import o_project_compiler.ast.ScalarFormPar;
import o_project_compiler.ast.Const;
import o_project_compiler.ast.VectorLocalVar;
import o_project_compiler.ast.NonVoidReturnType;
import o_project_compiler.ast.TermExpr;


import o_project_compiler.ast.CorrectCondition;
import o_project_compiler.ast.ProgramEnd;
import o_project_compiler.ast.NonVoidSuperclass;
import o_project_compiler.ast.ScalarField;
import o_project_compiler.ast.MethodBodyStart;

import o_project_compiler.ast.VectorField;
import o_project_compiler.ast.DesignatorFactor;


import o_project_compiler.ast.MethodName;
import o_project_compiler.ast.BoolFactor;


import o_project_compiler.ast.Type;
import o_project_compiler.ast.VoidSuperclass;

import o_project_compiler.ast.CharFactor;
import o_project_compiler.ast.IntFactor;
import o_project_compiler.ast.ProgramName;

import o_project_compiler.ast.VectorFormPar;
import o_project_compiler.ast.DoWhileStatementStart;

import o_project_compiler.ast.ClassName;
import o_project_compiler.ast.ScalarLocalVar;
import o_project_compiler.ast.VoidReturnType;
import o_project_compiler.ast.SyntaxNode;
import o_project_compiler.ast.NewVectorFactor;

import o_project_compiler.ast.PrintExprIntConstStatement;
import o_project_compiler.ast.IdentDesignatorStart;
import o_project_compiler.ast.MemberAccessDesignatorStart;
import o_project_compiler.ast.ReturnNothingStatement;
import java.util.Stack;

import o_project_compiler.exceptions.WrongObjectException;
import o_project_compiler.exceptions.WrongStructureException;
import o_project_compiler.inheritancetree.InheritanceTree;
import o_project_compiler.loggers.SemanticErrorLogger;

import o_project_compiler.loggers.SemanticErrorLogger.SemanticErrorKind;
import o_project_compiler.methodsignature.ClassMethodSignature;
import o_project_compiler.methodsignature.GlobalMethodSignature;
import o_project_compiler.methodsignature.MethodSignature;
import o_project_compiler.methodsignature.MethodSignatureGenerator;
import o_project_compiler.symboltable.Tab;
import o_project_compiler.util.Utils;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.structure.SymbolDataStructure;


public class SemanticAnalyzer extends VisitorAdaptor {

    private boolean semanticErrorDetected = false;

    private final SemanticErrorLogger semanticErrorLogger = new SemanticErrorLogger();

    public boolean semanticErrorDetected() {
        return semanticErrorDetected;
    }

    public void detectSemnaticError() {
        semanticErrorDetected = true;
    }

    private void detectSemanticError(Obj symbolObj, SyntaxNode syntaxNode, SemanticErrorKind semanticErrorKind,
            Object... context) {
        semanticErrorDetected = true;
        semanticErrorLogger.log(symbolObj, syntaxNode.getLine(), null, semanticErrorKind, context);
    }

    private void detectSemanticError() {
        semanticErrorDetected = true;
    }

    public static final String MAIN = "main";
    public static final String THIS = "this";
    private static final String VMT_POINTER = "$vmtPointer";
    private static final String CLASS_ID = "$classId";

    private enum ScopeType {
        UNIVERSE, PROGRAM, GLOBAL_METHOD, CLASS, CLASS_METHOD
    }

    private ScopeType currentScopeType = ScopeType.UNIVERSE;
    private Obj currentClassObj = Tab.noObj;
    private Obj currentMethodObj = rs.etf.pp1.symboltable.Tab.noObj;
    private int staticVarsCount = 0;
    private Struct currentType = Tab.noType;
    private Struct currentMethodReturnType = Tab.noType;
    private boolean voidMethod = false;
    private int formParCounter = 0;
    private boolean returnStatementFound = false;
    private int doWhileStatementCount = 0;
    private Scope programScope = null;
    private boolean detectErrors = true;
    public final Stack<Obj> thisParameterObjs = new Stack<>();

    public int getFormParCounter() {
        return formParCounter;
    }

    public int getStaticVarsCount() {
        return staticVarsCount;
    }

    public SemanticErrorLogger getSemanticErrorLogger() {
        return semanticErrorLogger;
    }



    private Obj findNearestDeclaration(String identName, boolean skipCurrentScope) {
        Obj result = rs.etf.pp1.symboltable.Tab.noObj;

        if (!skipCurrentScope) {
            result = findInCurrentScope(identName);

        }

        if (result == rs.etf.pp1.symboltable.Tab.noObj) {
            result = findInOuterScope(identName);

            if (result == rs.etf.pp1.symboltable.Tab.noObj) {

                Struct superclass = currentClassObj.getType().getElemType();
                Obj foundMethod;
                while (superclass != null) {
                    foundMethod = superclass.getMembersTable().searchKey(identName);
                    if (foundMethod != null) {
                        result = foundMethod;
                        break;
                    }
                    superclass = superclass.getElemType();
                }
                if (result == rs.etf.pp1.symboltable.Tab.noObj) {
                    result = findInSomeOuterScope(identName);

                }
            }
        }

        return result;
    }

    private Obj findNearestDeclaration(String identName, Obj instanceObj) {
        Obj result = rs.etf.pp1.symboltable.Tab.noObj;

        SymbolDataStructure targetSymbolDataStructure;
        if (currentClassObj.getType() == instanceObj.getType()) {

            targetSymbolDataStructure = Tab.currentScope.getOuter().getLocals();
        } else {
            targetSymbolDataStructure = instanceObj.getType().getMembersTable();
        }

        Struct superclass = instanceObj.getType();
        Obj foundMethod;
        while (targetSymbolDataStructure != null) {
            foundMethod = targetSymbolDataStructure.searchKey(identName);
            if (foundMethod != null) {
                result = foundMethod;
                break;
            }
            superclass = superclass.getElemType();
            targetSymbolDataStructure = superclass != null ? superclass.getMembersTable() : null;
        }

        return result;
    }

    private Obj findNearestDeclaration(MethodSignature classMethodSignature, Obj clss) {
        if (clss != null && clss.getType().getKind() == Struct.Class) {
            Struct currentClass = clss.getType().getElemType();
            while (currentClass != null) {
                Obj method = currentClass.getMembersTable().searchKey(classMethodSignature.getMethodName());
                if (method != null && method != rs.etf.pp1.symboltable.Tab.noObj && method.getKind() == Obj.Meth) {
                    try {
                        if (new ClassMethodSignature(method, Tab.noType).isInvokableBy(classMethodSignature)) {
                            return method;
                        }
                    } catch (WrongObjectException e) {
                        e.printStackTrace();
                    }
                }
                currentClass = currentClass.getElemType();
            }
        }
        return rs.etf.pp1.symboltable.Tab.noObj;
    }

    private void validateOverriding(MethodDecl methodDecl) {
        Struct clss = currentClassObj.getType().getElemType();
        Obj overridingMethod = methodDecl.getMethodName().obj;
        while (clss != null) {
            Obj overriddenMethod = clss.getMembersTable().searchKey(methodDecl.getMethodName().obj.getName());
            try {
                if (Utils.haveSameSignatures(overridingMethod, overriddenMethod)
                        && !Utils.returnTypesAssignmentCompatible(overridingMethod, overriddenMethod)) {
                    detectSemanticError(null, methodDecl, SemanticErrorKind.INCOMPATIBLE_RETURN_TYPE,
                            new ClassMethodSignature(overriddenMethod, clss));
                }
            } catch (WrongObjectException e) {
                e.printStackTrace();
            }
            clss = clss.getElemType();
        }
    }

    private Obj findInCurrentOrSomeOuterScope(String identName) {
        return Tab.find(identName);
    }

    private Obj findInCurrentScope(String identName) {
        Obj result = Tab.currentScope.findSymbol(identName);
        if (result == null) {
            result = rs.etf.pp1.symboltable.Tab.noObj;
        }
        return result;
    }

    private Obj findInOuterScope(String identName) {
        Obj result = Tab.currentScope.getOuter().findSymbol(identName);
        if (result == null) {
            result = rs.etf.pp1.symboltable.Tab.noObj;
        }
        return result;
    }

    private Obj findInSomeOuterScope(String identName) {
        Obj resultObj = null;
        for (Scope s = Tab.currentScope.getOuter(); s != null; s = s.getOuter()) {
            if (s.getLocals() != null) {
                resultObj = s.getLocals().searchKey(identName);
                if (resultObj != null) {
                    break;
                }
            }
        }
        return (resultObj != null) ? resultObj : rs.etf.pp1.symboltable.Tab.noObj;
    }

    private boolean isGlobalMethod(Obj method) {
        return programScope.getLocals().symbols().contains(method) || method == Tab.chrMethod
                || method == Tab.lenMethod || method == Tab.ordMethod;
    }


    public void visit(ProgramName programName) {
        String programIdent = programName.getIdent();

        Obj progObj = findInCurrentScope(programIdent);

        if (progObj == rs.etf.pp1.symboltable.Tab.noObj) {
            programName.obj = Tab.insert(Obj.Prog, programIdent, Tab.noType);
        } else {
            programName.obj = new Obj(Obj.Prog, programIdent, Tab.noType);
            detectSemanticError(programName.obj, programName, SemanticErrorKind.INVALID_PROG_NAME);
        }

        Tab.openScope();
        programScope = Tab.currentScope;
        currentScopeType = ScopeType.PROGRAM;
    }


    public void visit(ProgramEnd programEnd) {
        Obj mainObj = findInCurrentScope(MAIN);

        if (mainObj == Tab.noObj) {
            detectSemanticError(null, programEnd, SemanticErrorKind.MAIN_METHOD_DECLARATION_NOT_FOUND);
        }

        currentScopeType = ScopeType.UNIVERSE;
    }


    public void visit(Program program) {
        staticVarsCount = Tab.currentScope().getnVars();

        Tab.chainLocalSymbols(program.getProgramName().obj);

        Tab.closeScope();
    }


    public void visit(Type type) {
        String typeIdent = type.getIdent();

        Obj typeObj = findInCurrentOrSomeOuterScope(typeIdent);

        if (typeObj.getKind() == Obj.Type) {
            type.obj = typeObj;
        } else {
            type.obj = new Obj(Obj.Type, typeIdent, Tab.noType);
            detectSemanticError(type.obj, type, SemanticErrorKind.UNRESOLVED_TYPE);
        }

        currentType = type.obj.getType();
    }


    public void visit(NonVoidReturnType nonVoidReturnType) {
        currentMethodReturnType = nonVoidReturnType.getType().obj.getType();
        voidMethod = false;
    }


    public void visit(IntLiteral intLiteral) {
        intLiteral.obj = new Obj(Obj.Con, "", Tab.intType, intLiteral.getValue(), 0);
    }


    public void visit(CharLiteral charLiteral) {
        charLiteral.obj = new Obj(Obj.Con, "", Tab.charType, charLiteral.getValue(), 0);
    }


    public void visit(BoolLiteral boolLiteral) {
        boolLiteral.obj = new Obj(Obj.Con, "", Tab.BOOL_TYPE, boolLiteral.getValue() ? 1 : 0, 0);
    }


    public void visit(Const constant) {
        String constantIdent = constant.getIdent();

        Obj constantObj = findInCurrentScope(constantIdent);


        if (constantObj == rs.etf.pp1.symboltable.Tab.noObj) {
            constantObj = Tab.insert(Obj.Con, constant.getIdent(), currentType);
            Struct initializerType = constant.getLiteral().obj.getType();
            if (initializerType.equals(currentType)) {
                constantObj.setAdr(constant.getLiteral().obj.getAdr());
            } else {
                constantObj.setAdr(0);
                if (currentType != Tab.noType) {
                    if (Utils.isPrimitiveDataType(currentType)) {
                        detectSemanticError(null, constant, SemanticErrorKind.TYPE_MISMATCH, initializerType,
                                currentType);
                    } else {
                        detectSemanticError(new Obj(Obj.NO_VALUE, "", currentType), constant,
                                SemanticErrorKind.NON_PRIMITIVE_TYPE);
                    }
                } else {
                    detectSemanticError();
                }
            }
        } else {
            detectSemanticError(constantObj, constant, SemanticErrorKind.DUPLICATE_GLOBAL_NAME);
        }
    }


    public void visit(ScalarGlobalVar scalarGlobalVar) {
        String varIdent = scalarGlobalVar.getIdent();
        Obj varObj = findInCurrentScope(varIdent);

        if (varObj == rs.etf.pp1.symboltable.Tab.noObj) {
            Tab.insert(Obj.Var, varIdent, currentType);
        } else {
            detectSemanticError(varObj, scalarGlobalVar, SemanticErrorKind.DUPLICATE_GLOBAL_NAME);
        }
    }


    public void visit(ScalarField scalarField) {
        String fieldIdent = scalarField.getIdent();
        Obj fieldObj = findInCurrentScope(fieldIdent);

        if (fieldObj == rs.etf.pp1.symboltable.Tab.noObj) {
            currentClassObj.setAdr(currentClassObj.getAdr() + 1);
            Tab.insert(Obj.Fld, fieldIdent, currentType).setAdr(currentClassObj.getAdr());
        } else {
            detectSemanticError(fieldObj, scalarField, SemanticErrorKind.DUPLICATE_MEMBER, currentClassObj);
        }
    }


    public void visit(ScalarLocalVar scalarLocalVar) {
        String varIdent = scalarLocalVar.getIdent();
        Obj localVarObj = findInCurrentScope(varIdent);

        if (localVarObj == rs.etf.pp1.symboltable.Tab.noObj) {
            Tab.insert(Obj.Var, varIdent, currentType);
        } else {
            detectSemanticError(localVarObj, scalarLocalVar, SemanticErrorKind.DUPLICATE_LOCAL_VARIABLE);
        }
    }


    public void visit(VectorGlobalVar vectorGlobalVar) {
        String varIdent = vectorGlobalVar.getIdent();
        Obj varObj = findInCurrentScope(varIdent);

        if (varObj == rs.etf.pp1.symboltable.Tab.noObj) {
            Tab.insert(Obj.Var, varIdent, new Struct(Struct.Array, currentType));
        } else {
            detectSemanticError(varObj, vectorGlobalVar, SemanticErrorKind.DUPLICATE_GLOBAL_NAME);
        }
    }


    public void visit(VectorField vectorField) {
        String fieldIdent = vectorField.getIdent();
        Obj fieldObj = findInCurrentScope(fieldIdent);

        if (fieldObj == rs.etf.pp1.symboltable.Tab.noObj) {
            currentClassObj.setAdr(currentClassObj.getAdr() + 1);
            Tab.insert(Obj.Fld, fieldIdent, new Struct(Struct.Array, currentType)).setAdr(currentClassObj.getAdr());
        } else {
            detectSemanticError(fieldObj, vectorField, SemanticErrorKind.DUPLICATE_MEMBER);
        }
    }


    public void visit(VectorLocalVar vectorLocalVar) {
        String varIdent = vectorLocalVar.getIdent();
        Obj varObj = findInCurrentScope(varIdent);

        if (varObj == rs.etf.pp1.symboltable.Tab.noObj) {
            Tab.insert(Obj.Var, varIdent, new Struct(Struct.Array, currentType));
        } else {
            detectSemanticError(varObj, vectorLocalVar, SemanticErrorKind.DUPLICATE_LOCAL_VARIABLE);
        }
    }


    public void visit(ClassName className) {
        String classIdent = className.getIdent();

        Obj classObj = findInCurrentScope(classIdent);

        if (classObj == rs.etf.pp1.symboltable.Tab.noObj) {
            className.obj = currentClassObj = Tab.insert(Obj.Type, className.getIdent(), new Struct(Struct.Class));
            currentClassObj.setLevel(Tab.nextClassId());
        } else {
            className.obj = currentClassObj = new Obj(Obj.Type, className.getIdent(), new Struct(Struct.Class));
            detectSemanticError(className.obj, className, SemanticErrorKind.DUPLICATE_GLOBAL_NAME);
        }

        Tab.openScope();
        currentScopeType = ScopeType.CLASS;
    }


    public void visit(NonVoidSuperclass nonVoidSuperclass) {
        Struct superclassType = nonVoidSuperclass.getType().obj.getType();
        if (superclassType != Tab.noType) {
            if (superclassType.getKind() == Struct.Class && superclassType != currentClassObj.getType()) {
                Obj superclassObj = nonVoidSuperclass.getType().obj;
                try {
                    InheritanceTree.putTreeNode(currentClassObj, superclassObj);
                } catch (WrongObjectException | WrongStructureException e) {
                    e.printStackTrace();
                }
                currentClassObj.setAdr(superclassObj.getAdr());
                currentClassObj.getType().setElementType(superclassType);
            } else {
                currentClassObj.getType().setElementType(Tab.noType);
                detectSemanticError(nonVoidSuperclass.getType().obj, nonVoidSuperclass,
                        SemanticErrorKind.INVALID_SUPERCLASS);
            }
        }
    }


    public void visit(VoidSuperclass voidSuperclass) {
        Tab.insert(Obj.Fld, VMT_POINTER, Tab.intType);
        Tab.insert(Obj.Fld, CLASS_ID, Tab.intType);
        currentClassObj.setAdr(1);
        try {
            InheritanceTree.putTreeNode(currentClassObj);
        } catch (WrongObjectException | WrongStructureException e) {
            e.printStackTrace();
        }
    }


    public void visit(ClassDecl classDecl) {
        Tab.chainLocalSymbols(classDecl.getClassName().obj.getType());

        Tab.closeScope();

        currentScopeType = ScopeType.PROGRAM;
        currentClassObj = Tab.noObj;
    }


    public void visit(VoidFormPars voidFormPars) {
        if (formParCounter == 0 && currentScopeType == ScopeType.CLASS_METHOD) {
            Tab.insert(Obj.Var, THIS, currentClassObj.getType());
            formParCounter++;
        }
        currentMethodObj.setLevel(formParCounter);
    }


    public void visit(NonVoidFormPars nonVoidFormPars) {
        currentMethodObj.setLevel(formParCounter);
    }


    public void visit(ScalarFormPar scalarFormPar) {
        if (formParCounter == 0 && currentScopeType == ScopeType.CLASS_METHOD) {
            Tab.insert(Obj.Var, THIS, currentClassObj.getType());
            formParCounter++;
        }

        String scalarFormParIdent = scalarFormPar.getIdent();

        Obj formParObj = findInCurrentScope(scalarFormParIdent);

        if (formParObj == rs.etf.pp1.symboltable.Tab.noObj) {
            formParObj = Tab.insert(Obj.Var, scalarFormParIdent, scalarFormPar.getType().obj.getType());
            formParObj.setFpPos(formParCounter++);
        } else {
            detectSemanticError(formParObj, scalarFormPar, SemanticErrorKind.DUPLICATE_PARAMETER);
        }
    }


    public void visit(VectorFormPar vectorFormPar) {
        if (formParCounter == 0 && currentScopeType == ScopeType.CLASS_METHOD) {
            Tab.insert(Obj.Var, THIS, currentClassObj.getType());
            formParCounter++;
        }

        String vectorFormParIdent = vectorFormPar.getIdent();

        Obj formParObj = findInCurrentScope(vectorFormParIdent);

        if (formParObj == rs.etf.pp1.symboltable.Tab.noObj) {
            formParObj = Tab.insert(Obj.Var, vectorFormParIdent,
                    new Struct(Struct.Array, vectorFormPar.getType().obj.getType()));
            formParObj.setFpPos(formParCounter++);
        } else {
            detectSemanticError(formParObj, vectorFormPar, SemanticErrorKind.DUPLICATE_PARAMETER);
        }
    }


    public void visit(VoidReturnType voidReturnType) {
        currentMethodReturnType = Tab.noType;
        voidMethod = true;
    }


    public void visit(MethodName methodName) {
        String methodIdent = methodName.getIdent();

        currentScopeType = (currentScopeType == ScopeType.PROGRAM) ? ScopeType.GLOBAL_METHOD : ScopeType.CLASS_METHOD;

        Obj methodObj = findInCurrentScope(methodIdent);

        if (methodObj == rs.etf.pp1.symboltable.Tab.noObj) {
            methodName.obj = Tab.insert(Obj.Meth, methodIdent, currentMethodReturnType);
        } else {
            if (currentScopeType == ScopeType.CLASS_METHOD) {
                detectSemanticError(methodObj, methodName, SemanticErrorKind.DUPLICATE_MEMBER, currentClassObj);
            } else {
                detectSemanticError(methodObj, methodName, SemanticErrorKind.DUPLICATE_GLOBAL_NAME);
            }
            methodName.obj = new Obj(Obj.Meth, methodIdent, currentMethodReturnType);
        }

        currentMethodObj = methodName.obj;
        Tab.openScope();
        formParCounter = 0;
    }


    public void visit(MethodBodyStart methodBodyStart) {
        Tab.chainLocalSymbols(currentMethodObj);
    }


    public void visit(MethodDecl methodDecl) {
        String methodIdent = methodDecl.getMethodName().getIdent();

        if (methodIdent.equals(MAIN) && currentScopeType == ScopeType.GLOBAL_METHOD) {
            if (!(methodDecl.getReturnType() instanceof VoidReturnType)) {
                detectSemanticError(null, methodDecl, SemanticErrorKind.NON_VOID_MAIN);
            }
            if (!(methodDecl.getFormPars() instanceof VoidFormPars)) {
                detectSemanticError(null, methodDecl, SemanticErrorKind.MAIN_WITH_PARAMETERS);
            }
        }

        if (currentScopeType == ScopeType.CLASS_METHOD) {
            validateOverriding(methodDecl);
        }

        currentMethodObj = rs.etf.pp1.symboltable.Tab.noObj;
        Tab.closeScope();
        currentScopeType = (currentScopeType == ScopeType.GLOBAL_METHOD) ? ScopeType.PROGRAM : ScopeType.CLASS;
    }


    public void visit(ReturnNothingStatement returnNothingStatement) {
        if (!voidMethod) {
            if (!currentMethodObj.getType().equals(Tab.noType)
                    && !(currentMethodObj.getName().equals(MAIN) && currentScopeType == ScopeType.GLOBAL_METHOD)) {
                detectSemanticError(currentMethodObj, returnNothingStatement, SemanticErrorKind.RETURN_NOT_FOUND);
            } else {
                detectSemanticError();
            }
        }
        returnStatementFound = true;
    }



    @Override
    public void visit(BoolCond boolCondFactor) {
        if( boolCondFactor !=null){

             Boolean boolValue = boolCondFactor.getValue();

            if (boolValue.toString()!="false"||boolValue.toString()!="true") {
                detectSemanticError(null, boolCondFactor, SemanticErrorKind.TYPE_MISMATCH, boolCondFactor.obj.getType(),
                        Tab.BOOL_TYPE);
            } else {
                detectSemanticError();
                detectErrors = false;
            }}

    }



    public void visit(ReturnExprStatement returnExprStatement) {
        returnStatementFound = true;
        if (voidMethod) {
            detectSemanticError(null, returnExprStatement, SemanticErrorKind.RETURNED_VALUE_FROM_VOID_METHOD);
        } else {
            Obj exprObj = returnExprStatement.getExpr().obj;
            Struct exprStruct = exprObj.getType();
            if (!Utils.assignableTo(exprStruct, currentMethodReturnType)) {
                if (!currentMethodReturnType.equals(Tab.noType)
                        && !(currentMethodObj.getName().equals(MAIN) && currentScopeType == ScopeType.GLOBAL_METHOD)) {
                    if (exprStruct != rs.etf.pp1.symboltable.Tab.noType || exprObj.getKind() == Obj.Meth) {
                        detectSemanticError(null, returnExprStatement, SemanticErrorKind.TYPE_MISMATCH, exprStruct,
                                currentMethodReturnType);
                    }
                } else {
                    detectSemanticError();
                }
            }
        }
    }


    public void visit(MethodEnd methodEnd) {
        if (!voidMethod && !returnStatementFound) {
            if (!currentMethodObj.getType().equals(Tab.noType)
                    && !(currentMethodObj.getName().equals(MAIN) && currentScopeType == ScopeType.GLOBAL_METHOD)) {
                detectSemanticError(currentMethodObj, methodEnd, SemanticErrorKind.RETURN_NOT_FOUND);
            } else {
                detectSemanticError();
            }
        }
        returnStatementFound = false;
    }


    public void visit(NonEmptyStatementList nonEmptyStatementList) {
        detectErrors = true;
    }


    public void visit(AssignmentDesignatorStatement assignmentDesignatorStatement) {
        Obj exprObj = ((CorrectExpr) assignmentDesignatorStatement.getErrorProneExpr()).getExpr().obj;
        Struct exprStruct = exprObj.getType();
        Struct designatorStruct = assignmentDesignatorStatement.getDesignator().obj.getType();
        if (assignmentDesignatorStatement.getDesignator().obj.getKind() == Obj.Con) {
            detectSemanticError(assignmentDesignatorStatement.getDesignator().obj, assignmentDesignatorStatement,
                    SemanticErrorKind.ASSIGINING_SYMBOLIC_CONSTANT);
            return;
        }
        if (!Utils.assignableTo(exprStruct, designatorStruct)) {
            if ((exprStruct.getKind() != Struct.None && designatorStruct.getKind() != Struct.None)
                    || (exprStruct.getKind() == Struct.None && exprObj.getKind() == Obj.Meth)) {
                detectSemanticError(null, assignmentDesignatorStatement, SemanticErrorKind.TYPE_MISMATCH, exprStruct,
                        designatorStruct);
            } else {
                detectSemanticError();
            }
        }
    }


    public void visit(MethodCallDesignatorStatement methodCallDesignatorStatement) {
        Obj methodObj = methodCallDesignatorStatement.getDesignator().obj;

        MethodSignatureGenerator invokedMethodSignatureGenerator = new MethodSignatureGenerator();
        methodCallDesignatorStatement.traverseTopDown(invokedMethodSignatureGenerator);
        if (methodObj.getKind() != Obj.Meth) {
            if (invokedMethodSignatureGenerator.getMethodSignature() instanceof ClassMethodSignature) {
                ClassMethodSignature classMethodSignature = (ClassMethodSignature) invokedMethodSignatureGenerator
                        .getMethodSignature();
                if (classMethodSignature.getThisParameterType() != Tab.noType
                        && classMethodSignature.getThisParameterType().getElemType() != Tab.noType
                        && !classMethodSignature.containsUndeclaredType()) {
                    if (classMethodSignature.getThisParameterType().getKind() != Struct.Class) {
                        detectSemanticError(null, methodCallDesignatorStatement, SemanticErrorKind.UNINVOKABLE_METHOD,
                                invokedMethodSignatureGenerator.getMethodSignature(),
                                classMethodSignature.getThisParameterType());
                    } else {
                        detectSemanticError(null, methodCallDesignatorStatement, SemanticErrorKind.UNDEFINED_METHOD,
                                invokedMethodSignatureGenerator.getMethodSignature());
                    }
                }
            } else {
                GlobalMethodSignature globalMethodSignature = (GlobalMethodSignature) invokedMethodSignatureGenerator
                        .getMethodSignature();
                if (!globalMethodSignature.containsUndeclaredType()) {
                    detectSemanticError(null, methodCallDesignatorStatement, SemanticErrorKind.UNDEFINED_METHOD,
                            invokedMethodSignatureGenerator.getMethodSignature());
                }
            }
        } else {
            MethodSignature methodSignature = null;
            try {
                if (isGlobalMethod(methodObj)) {
                    methodSignature = new GlobalMethodSignature(methodObj);
                } else {
                    methodSignature = new ClassMethodSignature(methodObj, thisParameterObjs.peek().getType());
                }
            } catch (WrongObjectException ignored) {
            }
            if (methodSignature != null) {
                if (!methodSignature.isInvokableBy(invokedMethodSignatureGenerator.getMethodSignature())) {
                    Obj overriddenMethodObj = findNearestDeclaration(
                            invokedMethodSignatureGenerator.getMethodSignature(), thisParameterObjs.pop());
                    if (overriddenMethodObj.equals(rs.etf.pp1.symboltable.Tab.noObj)) {
                        if (!invokedMethodSignatureGenerator.getMethodSignature().containsUndeclaredType()) {
                            detectSemanticError(null, methodCallDesignatorStatement,
                                    SemanticErrorKind.INAPPLICABLE_METHOD, methodSignature.toString(),
                                    invokedMethodSignatureGenerator.getMethodSignature().getParameterList());
                        } else {
                            detectSemanticError();
                        }
                    } else {
                        methodCallDesignatorStatement.getDesignator().obj = overriddenMethodObj;
                    }
                }
            } else {
                detectSemanticError();
            }
        }
    }








    public void visit(DoWhileStatementStart doWhileStatementStart) {
        doWhileStatementCount++;
    }


    public void visit(DoWhileStatement doWhileStatement) {
        doWhileStatementCount--;
    }


    public void visit(BreakStatement breakStatement) {
        if (doWhileStatementCount <= 0) {
            detectSemanticError(null, breakStatement, SemanticErrorKind.MISPLACED_BREAK);
        }
    }


    public void visit(ContinueStatement continueStatement) {
        if (doWhileStatementCount <= 0) {
            detectSemanticError(null, continueStatement, SemanticErrorKind.MISPLACED_CONTINUE);
        }
    }


    public void visit(ReadStatement readStatement) {
        Struct designatorType = readStatement.getDesignator().obj.getType();

        if (!designatorType.equals(Tab.intType) && !designatorType.equals(Tab.charType) && !designatorType.equals(Tab.BOOL_TYPE)) {
            if (!designatorType.equals(Tab.noType) && !(designatorType.getKind() == Struct.Array
                    && designatorType.getElemType().equals(Tab.noType))) {
                detectSemanticError(readStatement.getDesignator().obj, readStatement,
                        SemanticErrorKind.NON_PRIMITIVE_TYPE);
            } else {
                detectSemanticError();
            }
        }
    }


    public void visit(PrintExprStatement printExprStatement) {
        Struct exprType = printExprStatement.getExpr().obj.getType();

        if (!exprType.equals(Tab.intType) && !exprType.equals(Tab.charType) && !exprType.equals(Tab.BOOL_TYPE)) {
            if (!exprType.equals(Tab.noType)
                    && !(exprType.getKind() == Struct.Array && exprType.getElemType().equals(Tab.noType))) {
                detectSemanticError(printExprStatement.getExpr().obj, printExprStatement,
                        SemanticErrorKind.NON_PRIMITIVE_TYPE);
            } else {
                detectSemanticError();
            }
        }
    }


    public void visit(PrintExprIntConstStatement printExprIntConstStatement) {
        Struct exprType = printExprIntConstStatement.getExpr().obj.getType();

        if (!exprType.equals(Tab.intType) && !exprType.equals(Tab.charType) && !exprType.equals(Tab.BOOL_TYPE)) {
            if (!exprType.equals(Tab.noType)
                    && !(exprType.getKind() == Struct.Array && exprType.getElemType().equals(Tab.noType))) {
                detectSemanticError(printExprIntConstStatement.getExpr().obj, printExprIntConstStatement,
                        SemanticErrorKind.NON_PRIMITIVE_TYPE);
            } else {
                detectSemanticError();
            }
        }
    }


    public void visit(MultipleExprExprList multipleExprExprList) {
        detectErrors = true;
    }



















    public void visit(TermExpr termExpr) {
        termExpr.obj = termExpr.getTerm().obj;
    }








    public void visit(FactorTerm factorTerm) {
        factorTerm.obj = factorTerm.getFactor().obj;
        if (factorTerm.obj.getType() == Tab.noType && factorTerm.obj.getKind() != Obj.Meth) {
            detectErrors = false;
        }
    }





    public void visit(DesignatorFactor designatorFactor) {
        designatorFactor.obj = designatorFactor.getDesignator().obj;
    }


    public void visit(MethodCallFactor methodCallFactor) {
        Obj methodObj = methodCallFactor.getDesignator().obj;

        MethodSignatureGenerator invokedMethodSignatureGenerator = new MethodSignatureGenerator();
        methodCallFactor.traverseTopDown(invokedMethodSignatureGenerator);
        if (methodObj.getKind() != Obj.Meth) {
            if (invokedMethodSignatureGenerator.getMethodSignature() instanceof ClassMethodSignature) {
                ClassMethodSignature classMethodSignature = (ClassMethodSignature) invokedMethodSignatureGenerator
                        .getMethodSignature();
                if (classMethodSignature.getThisParameterType() != Tab.noType
                        && classMethodSignature.getThisParameterType().getElemType() != Tab.noType
                        && !classMethodSignature.containsUndeclaredType()) {
                    if (classMethodSignature.getThisParameterType().getKind() != Struct.Class) {
                        detectSemanticError(null, methodCallFactor, SemanticErrorKind.UNINVOKABLE_METHOD,
                                invokedMethodSignatureGenerator.getMethodSignature(),
                                classMethodSignature.getThisParameterType());
                    } else {
                        detectSemanticError(null, methodCallFactor, SemanticErrorKind.UNDEFINED_METHOD,
                                invokedMethodSignatureGenerator.getMethodSignature());
                    }
                }
            } else {
                GlobalMethodSignature globalMethodSignature = (GlobalMethodSignature) invokedMethodSignatureGenerator
                        .getMethodSignature();
                if (!globalMethodSignature.containsUndeclaredType()) {
                    detectSemanticError(null, methodCallFactor, SemanticErrorKind.UNDEFINED_METHOD,
                            invokedMethodSignatureGenerator.getMethodSignature());
                }
            }
        } else {
            MethodSignature methodSignature = null;
            try {
                if (isGlobalMethod(methodObj)) {
                    methodSignature = new GlobalMethodSignature(methodObj);
                } else {
                    methodSignature = new ClassMethodSignature(methodObj, thisParameterObjs.peek().getType());
                }
            } catch (WrongObjectException ignored) {
            }
            if (methodSignature != null) {
                if (!methodSignature.isInvokableBy(invokedMethodSignatureGenerator.getMethodSignature())) {
                    Obj overriddenMethodObj = findNearestDeclaration(
                            invokedMethodSignatureGenerator.getMethodSignature(), thisParameterObjs.pop());
                    if (overriddenMethodObj.equals(rs.etf.pp1.symboltable.Tab.noObj)) {
                        if (!invokedMethodSignatureGenerator.getMethodSignature().containsUndeclaredType()) {
                            detectSemanticError(null, methodCallFactor, SemanticErrorKind.INAPPLICABLE_METHOD,
                                    methodSignature.toString(),
                                    invokedMethodSignatureGenerator.getMethodSignature().getParameterList());
                        } else {
                            detectSemanticError();
                        }
                    } else {
                        methodObj = overriddenMethodObj;
                    }
                }
            } else {
                detectSemanticError();
            }
        }
        methodCallFactor.obj = methodObj;
    }


    public void visit(IntFactor intFactor) {
        intFactor.obj = new Obj(Obj.Con, "", Tab.intType, intFactor.getValue(), 1);
    }


    public void visit(CharFactor charFactor) {
        charFactor.obj = new Obj(Obj.Con, "", Tab.charType, charFactor.getValue(), 1);
    }


    public void visit(BoolFactor boolFactor) {
        boolFactor.obj = new Obj(Obj.Con, "", Tab.BOOL_TYPE, boolFactor.getValue() ? 1 : 0, 1);
    }


    public void visit(NewScalarFactor newScalarFactor) {
        newScalarFactor.obj = newScalarFactor.getType().obj;
    }


    public void visit(NewVectorFactor newVectorFactor) {
        newVectorFactor.obj = new Obj(Obj.Var, "", new Struct(Struct.Array, newVectorFactor.getType().obj.getType()));
    }


    public void visit(DelimitedFactor delimitedFactor) {
        delimitedFactor.obj = delimitedFactor.getExpr().obj;
    }


    public void visit(IdentDesignator identDesignator) {
        String identDesignatorIdent = identDesignator.getIdent();
        Obj identObj;
        SyntaxNode parent = identDesignator.getParent();

        if (currentScopeType == ScopeType.CLASS_METHOD) {
            identObj = findNearestDeclaration(identDesignatorIdent,
                    (parent instanceof MethodCallFactor) || (parent instanceof MethodCallDesignatorStatement));
        } else { // currentScopeType == GLOBAL_METHOD
            if ((parent instanceof MethodCallFactor) || (parent instanceof MethodCallDesignatorStatement)) {
                identObj = findInSomeOuterScope(identDesignatorIdent);
            } else {
                identObj = findInCurrentOrSomeOuterScope(identDesignatorIdent);
            }
        }
        if (identObj == Tab.noObj || identObj.getKind() == Obj.Type || identObj.getKind() == Obj.Prog) {
            identObj = new Obj(Obj.NO_VALUE, identDesignatorIdent, Tab.noType);
            if (!(parent instanceof MethodCallFactor) && !(parent instanceof MethodCallDesignatorStatement)) {
                detectSemanticError(identObj, identDesignator, SemanticErrorKind.UNRESOLVED_VARIABLE);
            } else {
                detectSemanticError();
            }
        }

        identDesignator.obj = identObj;

        if (identObj.getKind() == Obj.Meth) {
            thisParameterObjs.push(new Obj(Obj.Var, "this", currentClassObj.getType(), 0, 1));
        }
    }





    public void visit(MemberAccessDesignator memberAccessDesignator) {
        String memberName = memberAccessDesignator.getIdent();

        Obj designatorStartObj = memberAccessDesignator.getDesignatorStart().obj;
        Obj memberAccessDesignatorObj = new Obj(Obj.NO_VALUE, memberName, Tab.noType);

        if (designatorStartObj.getType().getKind() != Struct.Class) {
            if (designatorStartObj.getType() != Tab.noType
                    && designatorStartObj.getType().getElemType() != Tab.noType) {
                detectSemanticError(null, memberAccessDesignator, SemanticErrorKind.ACCESSING_MEMBER_OF_NON_OBJECT,
                        designatorStartObj.getType());
            } else {
                detectSemanticError();
            }
        } else {
            memberAccessDesignatorObj = findNearestDeclaration(memberName, designatorStartObj);
            if (memberAccessDesignatorObj == rs.etf.pp1.symboltable.Tab.noObj) {
                memberAccessDesignatorObj = new Obj(Obj.NO_VALUE, memberName, Tab.noType);
                detectSemanticError(memberAccessDesignatorObj, memberAccessDesignator,
                        SemanticErrorKind.UNRESOLVED_MEMBER);
                memberAccessDesignator.obj = Tab.noObj;
            }
        }

        memberAccessDesignator.obj = memberAccessDesignatorObj;

        if (memberAccessDesignator.obj.getKind() == Obj.Meth) {
            thisParameterObjs.push(memberAccessDesignator.getDesignatorStart().obj);
        }
    }


    public void visit(IdentDesignatorStart identDesignatorStart) {
        String identDesignatorStartIdent = identDesignatorStart.getIdent();
        Obj identObj;

        if (currentScopeType == ScopeType.CLASS_METHOD) {
            identObj = findNearestDeclaration(identDesignatorStartIdent, false);
        } else { // currentScopeType == GLOBAL_METHOD
            identObj = findInCurrentOrSomeOuterScope(identDesignatorStartIdent);
        }
        if (identObj == Tab.noObj || identObj.getKind() == Obj.Type || identObj.getKind() == Obj.Prog) {
            identObj = new Obj(Obj.NO_VALUE, identDesignatorStartIdent, Tab.noType);
            detectSemanticError(identObj, identDesignatorStart, SemanticErrorKind.UNRESOLVED_VARIABLE);
        }

        identDesignatorStart.obj = identObj;
    }




    public void visit(MemberAccessDesignatorStart memberAccessDesignatorStart) {
        String memberName = memberAccessDesignatorStart.getIdent();

        Obj designatorStartObj = memberAccessDesignatorStart.getDesignatorStart().obj;
        Obj memberAccessDesignatorStartObj = new Obj(Obj.NO_VALUE, memberName, Tab.noType);

        if (designatorStartObj.getType().getKind() != Struct.Class) {
            if (designatorStartObj.getType() != Tab.noType
                    && designatorStartObj.getType().getElemType() != Tab.noType) {
                detectSemanticError(null, memberAccessDesignatorStart, SemanticErrorKind.ACCESSING_MEMBER_OF_NON_OBJECT,
                        designatorStartObj.getType());
            } else {
                detectSemanticError();
            }
        } else {
            memberAccessDesignatorStartObj = findNearestDeclaration(memberName, designatorStartObj);
            if (memberAccessDesignatorStartObj == Tab.noObj) {
                memberAccessDesignatorStartObj = new Obj(Obj.NO_VALUE, memberName, Tab.noType);
                detectSemanticError(memberAccessDesignatorStartObj, memberAccessDesignatorStart,
                        SemanticErrorKind.UNRESOLVED_MEMBER);
                memberAccessDesignatorStart.obj = Tab.noObj;
            }
        }

        memberAccessDesignatorStart.obj = memberAccessDesignatorStartObj;
    }



}