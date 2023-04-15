import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.misc.*;
import java.util.*;


public class CallGraphListener extends Java8BaseListener {

    static String packageName="";   //  keeps the package name.
    static String className="";     //  keeps the class name.
    static String methodName="";    //  keeps the method name.
    static String path="";          //  keeps the path of method declaration.
    static String content="";       //  keeps the content of dot notation.
    static List<String> declaredMethods = new  ArrayList<String>();     //  list of declared methods.
    static List<String> invokedMethods = new ArrayList<String>();       //  list of invoked methods.
    
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
        buf.append("node [style=filled,color=green, shape=circle]\n");  // makes the color of nodes green by default.
        for(int i=0;i<declaredMethods.size();i++){      //appends declared methods to buf.
            buf.append(declaredMethods.get(i)+"\n");
        }
        for(int i=0;i<invokedMethods.size();i++){
            if(!declaredMethods.contains(invokedMethods.get(i))){       //  checks if an invoked method is not declared.
                buf.append(invokedMethods.get(i)+"  [style=solid color=black, shape=circle, ]\n"); //  appends the line to buf that changes the color of undeclared methods to white. 
            }
        }
        buf.append(content);        //  append the content of dot notation to buf.
        buf.append("\n");
        buf.append("}");
        System.out.println(buf.toString());
    }



    @Override
    public void enterPackageDeclaration(Java8Parser.PackageDeclarationContext ctx){
        if(ctx.Identifier(1)!=null)
            packageName = ctx.Identifier(0)+"."+ctx.Identifier(1)+"/";  // gets package name from package declaration tokens.
        else
            packageName = ctx.Identifier(0)+"/";
    }

    @Override
    public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx){
        className = ctx.Identifier()+"/";   //  gets class name from normal class declaration tokens.
    }


    @Override
    public void enterMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx){
        methodName = ctx.Identifier()+"";   //  gets declared method name from method declarator tokens.
        String nodeName="\"" + packageName+className+methodName+"\"";
        declaredMethods.add(nodeName);
        path = nodeName;  
    }



    @Override
    public void enterA(Java8Parser.AContext ctx){
        String nodeName="\"" + packageName+className+ctx.methodName().Identifier()+"\"";
        invokedMethods.add(nodeName);
        content += path+"->"+nodeName+"\n";     //  adds line to content that creates an edge between declared method to invoked static class method.
    }


    @Override
    public void enterB(Java8Parser.BContext ctx){
        String nodeName="\"" + packageName+ctx.typeName().Identifier()+"/"+ctx.Identifier()+"\"";
        invokedMethods.add(nodeName);
        content += path +"->"+ nodeName + "\n";     //  adds line to content that creates an edge between declared method to invoked different class method.

    }




}
