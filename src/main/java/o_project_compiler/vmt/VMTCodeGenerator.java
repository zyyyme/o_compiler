

package o_project_compiler.vmt;

import o_project_compiler.inheritancetree.InheritanceTreeNode;
import o_project_compiler.inheritancetree.visitor.InheritanceTreeVisitor;

public class VMTCodeGenerator implements InheritanceTreeVisitor {


    public void visit(InheritanceTreeNode node) {
        node.getVMT().generateCreationCode();
    }

}
