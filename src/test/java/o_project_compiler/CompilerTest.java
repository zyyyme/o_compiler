
package o_project_compiler;

import org.junit.Test;
import rs.etf.pp1.mj.runtime.Run;

import java.io.*;

public class CompilerTest {

    private static final String path = "src" + File.separator + "test" + File.separator +"resources" + File.separator;

    @Test
    public void simpleCalculatorTest() throws Exception {
        System.out.println("\n\n1) Running Compiler for file \"" + path + "test_program.ol\"...\n\n");


        //Compilation
        Compiler.main(new String[]{path + "test_program.ol", path + "test.obj"});

        System.out.println("\n\n2) RunningVM...");
        final InputStream originalInputStream = System.in;
        final FileInputStream fileInputStream = new FileInputStream(new File(path + "input_stream.txt"));
        System.setIn(fileInputStream);


        Run.main(new String[]{path + "test.obj"});
        System.setIn(originalInputStream);
    }

}
