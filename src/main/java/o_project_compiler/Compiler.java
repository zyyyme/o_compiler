
package o_project_compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java_cup.runtime.Symbol;
import o_project_compiler.ast.Program;
import o_project_compiler.inheritancetree.InheritanceTree;
import o_project_compiler.inheritancetree.visitor.InheritanceTreePrinter;
import o_project_compiler.symboltable.Tab;
import o_project_compiler.util.Log4JUtils;
import o_project_compiler.vmt.VMTCodeGenerator;
import o_project_compiler.vmt.VMTCreator;
import o_project_compiler.vmt.VMTStartAddressGenerator;
import rs.etf.pp1.mj.runtime.Code;



public class Compiler {

    static {
        DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
        Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
    }

    private static final Logger LOGGER = Logger.getLogger(Compiler.class);

    public static void tsdump() {
        Tab.dump(LOGGER);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            LOGGER.error("Too few arguments. Usage: Compiler <source-file> <obj-file>");
            return;
        }
        File sourceFile = new File(args[0]);

        if (!sourceFile.exists()) {
            LOGGER.error("File \"" + sourceFile.getAbsolutePath() + "\" is not  found");
            return;
        }
        LOGGER.info("Compiling.... \"" + sourceFile.getAbsolutePath() + "\"...");
        try (BufferedReader br = new BufferedReader(new FileReader(sourceFile))) {
            Lexer lexer = new Lexer(br);
            Parser parser = new Parser(lexer);
            Symbol symbol = parser.parse();

            if (!parser.lexicalErrorDetected() && !parser.syntaxErrorDetected()) {
                LOGGER.info("No syntax errors in \"" + sourceFile.getAbsolutePath() + "\"");

                Program program = (Program) symbol.value;

                LOGGER.info("AST:\n" + program.toString(""));

                Tab.init();
                SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
                program.traverseBottomUp(semanticAnalyzer);

                tsdump();

                if (!semanticAnalyzer.semanticErrorDetected()) {

                    VMTCreator vmtCreator = new VMTCreator();
                    InheritanceTree.ROOT_NODE.accept(vmtCreator);

                    VMTStartAddressGenerator vmtStartAddressGenerator = new VMTStartAddressGenerator(
                            semanticAnalyzer.getStaticVarsCount());
                    InheritanceTree.ROOT_NODE.accept(vmtStartAddressGenerator);

                    Code.dataSize = semanticAnalyzer.getStaticVarsCount() + vmtStartAddressGenerator.getTotalVMTSize();

                    LOGGER.info("No semantic errors have been detected in \"" + sourceFile.getAbsolutePath() + "\"");

                    File objFile = new File(args[1]);
                    LOGGER.info("Generating bytecode file \"" + objFile.getAbsolutePath() + "\"...");
                    if (objFile.exists()) {
                        LOGGER.info("Deleting old bytecode file \"" + objFile.getAbsolutePath() + "\"...");
                        if (objFile.delete())
                            LOGGER.info("Old bytecode file \"" + objFile.getAbsolutePath() + "\" has been deleted.");
                        else
                            LOGGER.error("Old bytecode file \"" + objFile.getAbsolutePath() + "\" has not been deleted.");
                    }

                    CodeGenerator codeGenerator = new CodeGenerator();



                    program.traverseBottomUp(codeGenerator);

                    InheritanceTreePrinter inheritanceTreeNodePrinter = new InheritanceTreePrinter();
                    InheritanceTree.ROOT_NODE.accept(inheritanceTreeNodePrinter);

                    Code.mainPc = Code.pc;
                    Code.put(Code.enter);
                    Code.put(0);
                    Code.put(0);

                    VMTCodeGenerator vmtCodeGenerator = new VMTCodeGenerator();
                    InheritanceTree.ROOT_NODE.accept(vmtCodeGenerator);

                    Code.put(Code.call);
                    Code.put2(codeGenerator.getMainPc() - Code.pc + 1);
                    Code.put(Code.exit);
                    Code.put(Code.return_);

                    Code.write(new FileOutputStream(objFile));
                    LOGGER.info("Bytecode file \"" + objFile.getAbsolutePath() + "\" has been generated.");
                    LOGGER.info("Compilation of source file \"" + sourceFile.getAbsolutePath() + "\" has finished successfully.");
                } else {
                    LOGGER.error("Source file \"" + sourceFile.getAbsolutePath() + "\" contains semantic error(s)!");
                    LOGGER.error("Compilation of source file \"" + sourceFile.getAbsolutePath() + "\" has finished unsuccessfully.");
                }

            } else {
                if (parser.lexicalErrorDetected()) {
                    LOGGER.error("Source file \"" + sourceFile.getAbsolutePath() + "\" contains lexical error(s)!");
                }
                if (parser.syntaxErrorDetected()) {
                    LOGGER.error("Source file \"" + sourceFile.getAbsolutePath() + "\" contains syntax error(s)!");
                }
                LOGGER.error("Compilation of source file \"" + sourceFile.getAbsolutePath() + "\" has finished unsuccessfully.");
            }

        }
    }

}
