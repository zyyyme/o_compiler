package o_project_compiler.inheritancetree.visitor;

import java.util.ArrayList;
import java.util.List;

import o_project_compiler.inheritancetree.InheritanceTreeNode;

public class LeafNodeVisitor implements InheritanceTreeVisitor {

    public List<InheritanceTreeNode> leafNodes = new ArrayList<>();

    public void visit(InheritanceTreeNode node) {
        if (node.childrenNum() == 0) {
            leafNodes.add(node);
        }
    }

    public List<InheritanceTreeNode> getLeafNodes() {

        return leafNodes;
    }

}