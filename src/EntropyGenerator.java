import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import codemining.ast.TreeNode;
import codemining.ast.java.AbstractJavaTreeExtractor;
import codemining.java.codeutils.JavaASTExtractor;
import codemining.lm.tsg.TSGNode;
import codemining.lm.tsg.TSGrammar;
import codemining.lm.tsg.TreeProbabilityComputer;
import codemining.util.serialization.ISerializationStrategy.SerializationException;
import codemining.util.serialization.Serializer;

public class EntropyGenerator {
	
	public static void main(String[] args) throws SerializationException, IOException{
		if(args.length < 2){
			System.err.println("<Path to Model> <path to Source Code File>");
			System.exit(-1);
		}
		TSGrammar<TSGNode> model = importModel(args[0]);
		List<ASTNode> sourceCode = parseAST(new File(args[1]));
		HashMap<ASTNode, Double> entropyResults = generateEntropy(model, sourceCode, "");
		
	}
	
	public static HashMap<ASTNode, Double> simpleAggregation(TSGrammar<TSGNode> model, File file, String filePath) throws SerializationException, IOException{
		List<ASTNode> sourceCode = parseAST(file);
		HashMap<ASTNode, Double> entropyResults = generateEntropy(model, sourceCode, filePath);
		return entropyResults;
	}

	public static HashMap<ASTNode, Double> generateEntropy(TSGrammar<TSGNode> model, List<ASTNode> stmts, String filePath) throws IOException{
		HashMap<ASTNode, Double> ASTEntropy = new HashMap<ASTNode, Double>();
		ArrayList<String> writeName = new ArrayList<String>();
		ArrayList<String> writeProb = new ArrayList<String>();
		final TreeProbabilityComputer<TSGNode> probabilityComputer = 
				new TreeProbabilityComputer<TSGNode>(model, false, TreeProbabilityComputer.TSGNODE_MATCHER);
		int index = 0;
		for(ASTNode node: stmts){
			final TreeNode<TSGNode> tsgTree = TSGNode.convertTree(((AbstractJavaTreeExtractor) model.getTreeExtractor()).getTree(node), 0);
			double prob = probabilityComputer.getLog2ProbabilityOf(tsgTree);
			ASTEntropy.put(node, prob);
			
			//Print statement for creating file for R
			writeName.add("Tree " + index + ": " + node);
			writeProb.add(index + ", " + prob);
			index++;
			
		}
		
		//System.out.println(writeName);
		//System.out.println(writeProb);
		//writeCalculation(writeName, writeProb, filePath);
		return ASTEntropy;
		
	}
	
	public static TSGrammar<TSGNode> importModel(String grammarPath) throws SerializationException{
		TSGrammar<TSGNode> model =
				(TSGrammar<TSGNode>) Serializer.getSerializer().deserializeFrom(grammarPath);
		return model;
	}
	
	public static List<ASTNode> parseAST(File sourceCode) throws IOException{
		final JavaASTExtractor astExtractor = new JavaASTExtractor(false);
		final ASTNode u = astExtractor.getAST(sourceCode);
		return decomposeASTNode(u);
		
	}
	
	public static void writeCalculation(ArrayList<String> name, ArrayList<String> prob, String filePath) throws IOException{
		FileWriter writerEntropy = new FileWriter(filePath+"_Entropy.txt"); 
		for(String str: prob) {
			writerEntropy.write(str);
			writerEntropy.write("\n");
		}
		writerEntropy.close();
		
		FileWriter writerNames = new FileWriter(filePath+"_Names.txt"); 
		for(String str: name) {
			writerNames.write(str);
			writerNames.write("\n");
			
		}
		writerNames.close();
	}
	
	/*
	 * Copied code from GenProg4Java ASTUtils
	 */
	public static List<ASTNode> decomposeASTNode(ASTNode node) {
		final List<ASTNode> decomposed = new LinkedList<ASTNode>();
		node.accept(new ASTVisitor() {
			public boolean visit(ArrayAccess node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(ArrayCreation node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(ArrayInitializer node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(Assignment node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(BooleanLiteral node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(BreakStatement node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(CastExpression node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(CatchClause node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(CharacterLiteral node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(ClassInstanceCreation node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(ConditionalExpression node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(ConstructorInvocation node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(ContinueStatement node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(Dimension node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(DoStatement node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(EmptyStatement node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(EnhancedForStatement node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(FieldAccess node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(FieldDeclaration node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(ForStatement node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(IfStatement node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(InfixExpression node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(Initializer node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(InstanceofExpression node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(MethodInvocation node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(NullLiteral node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(NumberLiteral node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(ParameterizedType node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(ParenthesizedExpression node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(PostfixExpression node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(PrefixExpression node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(PrimitiveType node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(QualifiedName node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(QualifiedType node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(ReturnStatement node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(SimpleName node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(SimpleType node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(SingleVariableDeclaration node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(StringLiteral node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(SuperConstructorInvocation node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(SuperFieldAccess node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(SuperMethodInvocation node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(SwitchCase node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(SwitchStatement node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(ThisExpression node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(ThrowStatement node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(TryStatement node) {
				decomposed.add(node);
				return true;
			}	
			public boolean visit(VariableDeclarationExpression node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(VariableDeclarationFragment node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(VariableDeclarationStatement node) {
				decomposed.add(node);
				return true;
			}
			public boolean visit(WhileStatement node) {
				decomposed.add(node);
				return true;
			}
		});
		return decomposed;
	}
	
}
