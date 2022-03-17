
package o_project_compiler.vmt;

import o_project_compiler.inheritancetree.InheritanceTreeNode;
import o_project_compiler.inheritancetree.visitor.InheritanceTreeVisitor;


public class VMTStartAddressGenerator implements InheritanceTreeVisitor {

    private final int firstVMTStartAddress;
    private int currentVMTStartAddress;

    public VMTStartAddressGenerator(int firstVMTStartAddress) {
        this.firstVMTStartAddress = currentVMTStartAddress = firstVMTStartAddress;
    }


    public void visit(InheritanceTreeNode node) {
        VMT vmt = node.getVMT();
        vmt.setStartAddress(currentVMTStartAddress);
        node.getClss().setAdr(currentVMTStartAddress);
        currentVMTStartAddress += vmt.getSize();
    }

    public int getTotalVMTSize() {
        return currentVMTStartAddress - firstVMTStartAddress;
    }

}
