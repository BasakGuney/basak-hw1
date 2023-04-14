import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.misc.*;
import java.util.*;


public class CallGraphListener extends Java8BaseListener {

    static String packageName="";   //  keeps the package name
    static String className="";     //  keeps the class name
    static String methodName="";    //  keeps the method name
    static String path="";          //  to keep the path of the declared method
    static String content="";       // content of dot notation
    static List<String> declaredMethods = new  ArrayList<String>();     //  list of declared methods
    static List<String> invokedMethods = new ArrayList<String>();       //  list of invoked methods
    
public static void main(String[] args) throws Exception {
    ANTLRInputStream input = new ANTLRInputStream(System.in);
    Java8Lexer lexer = new Java8Lexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    Java8Parser parser = new Java8Parser(tokens);
    ParseTree tree = parser.compilationUnit();
    ParseTreeWalker walker = new ParseTreeWalker();
    CallGraphListener listener = new CallGraphListener();
    // This is where we trigger the walk of the tree using our listener.
    walker.walk(listener, tree);

    StringBuilder buf = new StringBuilder();
    buf.append("digraph G {\n");
    buf.append("node [style=filled,color=green, shape=circle]\n");  //  creates all the nodes in green by default.
    buf.append(content);    //  appends content of doth notation to buf.
    buf.append("\n");
    for(int i=0;i<invokedMethods.size();i++){       
        if(!declaredMethods.contains(invokedMethods.get(i))){   // checks if an invoked method is not declared. 
            buf.append(invokedMethods.get(i)+"  [style=solid color=black, shape=circle, ]\n");  // append the line that changes color of the undeclared method to white, to buf.
        }
    }
    buf.append("}");
    System.out.println(buf.toString());
    

}



@Override
public void enterPackageDeclaration(Java8Parser.PackageDeclarationContext ctx){
    packageName = ctx.Identifier(0)+"."+ctx.Identifier(1)+"/";      
}

@Override
public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx){
    className = ctx.Identifier()+"/";
}


@Override
public void enterMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx){
    methodName = ctx.Identifier()+"";
    String nodeName="\"" + packageName+className+methodName+"\"";
    declaredMethods.add(nodeName);  //  adds declared method to declaredMethods list.
    path = nodeName+"->";
}

@Override
public void enterMethodBody(Java8Parser.MethodBodyContext ctx){
    if(!(ctx.getText().contains(";"))){
        String nodeName="\""+packageName+className+methodName+"\"";
        declaredMethods.add(nodeName);
        content += nodeName + "\n";
    }
}


@Override
public void enterA(Java8Parser.AContext ctx){
    String nodeName="\"" + packageName+className+ctx.methodName().Identifier()+"\"";
    invokedMethods.add(nodeName);
    content += path+nodeName+"\n";
}


@Override
public void enterB(Java8Parser.BContext ctx){
    String nodeName="\"" + packageName+ctx.typeName().Identifier()+"/"+ctx.Identifier()+"\"";
    invokedMethods.add(nodeName);
    content += path + nodeName + "\n";

}




}
