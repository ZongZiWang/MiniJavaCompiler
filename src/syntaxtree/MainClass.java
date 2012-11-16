//
// Generated by JTB 1.3.2
//

package syntaxtree;

/**
 * Grammar production:
 * f0 -> "class"
 * f1 -> Identifier()
 * f2 -> "{"
 * f3 -> "public"
 * f4 -> "static"
 * f5 -> "void"
 * f6 -> "main"
 * f7 -> "("
 * f8 -> "String"
 * f9 -> "["
 * f10 -> "]"
 * f11 -> Identifier()
 * f12 -> ")"
 * f13 -> "{"
 * f14 -> PrintStatement()
 * f15 -> "}"
 * f16 -> "}"
 */
public class MainClass implements Node {
   public NodeToken f0;
   public Identifier f1;
   public NodeToken f2;
   public NodeToken f3;
   public NodeToken f4;
   public NodeToken f5;
   public NodeToken f6;
   public NodeToken f7;
   public NodeToken f8;
   public NodeToken f9;
   public NodeToken f10;
   public Identifier f11;
   public NodeToken f12;
   public NodeToken f13;
   public PrintStatement f14;
   public NodeToken f15;
   public NodeToken f16;

   public MainClass(NodeToken n0, Identifier n1, NodeToken n2, NodeToken n3, NodeToken n4, NodeToken n5, NodeToken n6, NodeToken n7, NodeToken n8, NodeToken n9, NodeToken n10, Identifier n11, NodeToken n12, NodeToken n13, PrintStatement n14, NodeToken n15, NodeToken n16) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
      f5 = n5;
      f6 = n6;
      f7 = n7;
      f8 = n8;
      f9 = n9;
      f10 = n10;
      f11 = n11;
      f12 = n12;
      f13 = n13;
      f14 = n14;
      f15 = n15;
      f16 = n16;
   }

   public MainClass(Identifier n0, Identifier n1, PrintStatement n2) {
      f0 = new NodeToken("class");
      f1 = n0;
      f2 = new NodeToken("{");
      f3 = new NodeToken("public");
      f4 = new NodeToken("static");
      f5 = new NodeToken("void");
      f6 = new NodeToken("main");
      f7 = new NodeToken("(");
      f8 = new NodeToken("String");
      f9 = new NodeToken("[");
      f10 = new NodeToken("]");
      f11 = n1;
      f12 = new NodeToken(")");
      f13 = new NodeToken("{");
      f14 = n2;
      f15 = new NodeToken("}");
      f16 = new NodeToken("}");
   }

   public void accept(visitor.Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(visitor.GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(visitor.GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(visitor.GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }
}

