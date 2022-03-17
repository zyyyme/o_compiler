// generated with ast extension for cup
// version 0.8
// 16/2/2022 22:28:35


package o_project_compiler.ast;

public interface Visitor { 

    public void visit(ReturnType ReturnType);
    public void visit(ErrorProneGlobalVarDecl ErrorProneGlobalVarDecl);
    public void visit(DesignatorStart DesignatorStart);
    public void visit(FormParList FormParList);
    public void visit(Literal Literal);
    public void visit(LocalVarList LocalVarList);
    public void visit(ErrorProneFormPar ErrorProneFormPar);
    public void visit(FieldDeclList FieldDeclList);
    public void visit(StatementList StatementList);
    public void visit(LocalVar LocalVar);
    public void visit(Factor Factor);
    public void visit(CondTerm CondTerm);
    public void visit(ConstList ConstList);
    public void visit(DeclList DeclList);
    public void visit(Designator Designator);
    public void visit(CondFactor CondFactor);
    public void visit(Term Term);
    public void visit(Condition Condition);
    public void visit(ErrorProneFieldDecl ErrorProneFieldDecl);
    public void visit(ErrorProneCondition ErrorProneCondition);
    public void visit(ErrorProneSuperclass ErrorProneSuperclass);
    public void visit(Methods Methods);
    public void visit(ExprList ExprList);
    public void visit(ErrorProneGlobalVar ErrorProneGlobalVar);
    public void visit(Expr Expr);
    public void visit(ActPars ActPars);
    public void visit(Decl Decl);
    public void visit(Statement Statement);
    public void visit(ErrorProneExpr ErrorProneExpr);
    public void visit(MethodDeclList MethodDeclList);
    public void visit(GlobalVarList GlobalVarList);
    public void visit(Field Field);
    public void visit(FieldList FieldList);
    public void visit(FormPars FormPars);
    public void visit(LocalVarDeclList LocalVarDeclList);
    public void visit(ProgramEnd ProgramEnd);
    public void visit(Constructor Constructor);
    public void visit(MethodEnd MethodEnd);
    public void visit(DoWhileStatementStart DoWhileStatementStart);
    public void visit(Else Else);
    public void visit(ConditionEnd ConditionEnd);
    public void visit(ExprCondFactor ExprCondFactor);
    public void visit(ErrorCondition ErrorCondition);
    public void visit(CorrectCondition CorrectCondition);
    public void visit(BoolCond BoolCond);
    public void visit(ConditionStart ConditionStart);
    public void visit(ErrorExpr ErrorExpr);
    public void visit(CorrectExpr CorrectExpr);
    public void visit(ActParsEnd ActParsEnd);
    public void visit(SingleExprExprList SingleExprExprList);
    public void visit(MultipleExprExprList MultipleExprExprList);
    public void visit(VoidActPars VoidActPars);
    public void visit(NonVoidActPars NonVoidActPars);
    public void visit(ActParsStart ActParsStart);
    public void visit(DelimitedFactor DelimitedFactor);
    public void visit(NewVectorFactor NewVectorFactor);
    public void visit(NewScalarFactor NewScalarFactor);
    public void visit(BoolFactor BoolFactor);
    public void visit(CharFactor CharFactor);
    public void visit(IntFactor IntFactor);
    public void visit(MethodCallFactor MethodCallFactor);
    public void visit(DesignatorFactor DesignatorFactor);
    public void visit(FactorTerm FactorTerm);
    public void visit(TermExpr TermExpr);
    public void visit(MemberAccessDesignatorStart MemberAccessDesignatorStart);
    public void visit(IdentDesignatorStart IdentDesignatorStart);
    public void visit(MemberAccessDesignator MemberAccessDesignator);
    public void visit(IdentDesignator IdentDesignator);
    public void visit(DelimitedStatement DelimitedStatement);
    public void visit(PrintExprIntConstStatement PrintExprIntConstStatement);
    public void visit(PrintExprStatement PrintExprStatement);
    public void visit(ReadStatement ReadStatement);
    public void visit(ReturnExprStatement ReturnExprStatement);
    public void visit(ReturnNothingStatement ReturnNothingStatement);
    public void visit(ContinueStatement ContinueStatement);
    public void visit(BreakStatement BreakStatement);
    public void visit(DoWhileStatement DoWhileStatement);
    public void visit(IfThenElseStatement IfThenElseStatement);
    public void visit(IfThenStatement IfThenStatement);
    public void visit(MethodCallDesignatorStatement MethodCallDesignatorStatement);
    public void visit(AssignmentDesignatorStatement AssignmentDesignatorStatement);
    public void visit(EmptyStatementList EmptyStatementList);
    public void visit(NonEmptyStatementList NonEmptyStatementList);
    public void visit(MethodBodyStart MethodBodyStart);
    public void visit(ScalarLocalVar ScalarLocalVar);
    public void visit(VectorLocalVar VectorLocalVar);
    public void visit(SingleVarLocalVarList SingleVarLocalVarList);
    public void visit(MultipleVarLocalVarList MultipleVarLocalVarList);
    public void visit(LocalVarDecl LocalVarDecl);
    public void visit(EmptyLocalVarDeclList EmptyLocalVarDeclList);
    public void visit(NonEmptyLocalVarDeclList NonEmptyLocalVarDeclList);
    public void visit(ErrorFormPar ErrorFormPar);
    public void visit(ScalarFormPar ScalarFormPar);
    public void visit(VectorFormPar VectorFormPar);
    public void visit(SingleFormParFormParList SingleFormParFormParList);
    public void visit(MultipleFormParFormParList MultipleFormParFormParList);
    public void visit(VoidFormPars VoidFormPars);
    public void visit(NonVoidFormPars NonVoidFormPars);
    public void visit(MethodName MethodName);
    public void visit(VoidReturnType VoidReturnType);
    public void visit(NonVoidReturnType NonVoidReturnType);
    public void visit(MethodDecl MethodDecl);
    public void visit(EmptyMethodDeclList EmptyMethodDeclList);
    public void visit(NonEmptyMethodDeclList NonEmptyMethodDeclList);
    public void visit(VoidMethods VoidMethods);
    public void visit(NonVoidMethods NonVoidMethods);
    public void visit(ScalarField ScalarField);
    public void visit(VectorField VectorField);
    public void visit(SingleFieldFieldList SingleFieldFieldList);
    public void visit(MultipleFieldFieldList MultipleFieldFieldList);
    public void visit(ErrorFieldDecl ErrorFieldDecl);
    public void visit(ErrorFieldDecl2 ErrorFieldDecl2);
    public void visit(ErrorFieldDecl1 ErrorFieldDecl1);
    public void visit(CorrectFieldDecl CorrectFieldDecl);
    public void visit(EmptyFieldDeclList EmptyFieldDeclList);
    public void visit(NonEmptyFieldDeclList NonEmptyFieldDeclList);
    public void visit(ErrorSuperclass ErrorSuperclass);
    public void visit(VoidSuperclass VoidSuperclass);
    public void visit(NonVoidSuperclass NonVoidSuperclass);
    public void visit(ClassName ClassName);
    public void visit(ErrorGlobalVar ErrorGlobalVar);
    public void visit(ScalarGlobalVar ScalarGlobalVar);
    public void visit(VectorGlobalVar VectorGlobalVar);
    public void visit(SingleVarGlobalVarList SingleVarGlobalVarList);
    public void visit(MultipleVarGlobalVarList MultipleVarGlobalVarList);
    public void visit(ErrorGlobalVarDecl ErrorGlobalVarDecl);
    public void visit(ErrorGlobalVarDecll ErrorGlobalVarDecll);
    public void visit(VarDecl VarDecl);
    public void visit(CharLiteral CharLiteral);
    public void visit(BoolLiteral BoolLiteral);
    public void visit(IntLiteral IntLiteral);
    public void visit(Const Const);
    public void visit(SingleConstConstList SingleConstConstList);
    public void visit(MultipleConstConstList MultipleConstConstList);
    public void visit(Type Type);
    public void visit(ClassDecl ClassDecl);
    public void visit(GlobalVarDecl GlobalVarDecl);
    public void visit(EmptyDeclList EmptyDeclList);
    public void visit(NonEmptyDeclList NonEmptyDeclList);
    public void visit(ProgramName ProgramName);
    public void visit(Program Program);

}
