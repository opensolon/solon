package org.noear.solon.data.sqlink.plugin.aot.service;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TaskEvent;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Options;
import io.github.kiryu1223.expressionTree.expressions.annos.Getter;
import io.github.kiryu1223.expressionTree.expressions.annos.Recode;
import io.github.kiryu1223.expressionTree.expressions.annos.Setter;
import io.github.kiryu1223.expressionTree.ext.IExtensionService;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.solon.data.sqlink.base.metaData.IConverter;
import org.noear.solon.data.sqlink.plugin.aot.data.AnonymousClassData;
import org.noear.solon.data.sqlink.plugin.aot.data.ClassData;
import org.noear.solon.data.sqlink.plugin.aot.data.NormalClassData;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SQLinkExtensionService implements IExtensionService
{
    private static final String projectVersion = "3.0.3-SNAPSHOT";
    private FileObject aotConfig;
    private boolean aotTime;
    private Set<String> AnonymousClassesName = new HashSet<>();
    private Set<String> classesName = new HashSet<>();

    @Override
    public void init(Context context)
    {
        checkAot(context);
        if (!aotTime) return;
        try
        {
            createAotFile(context);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void checkAot(Context context)
    {
        Options options = Options.instance(context);
        String aot = options.get("-Aot");
        aotTime = aot != null;
    }

    private void createAotFile(Context context) throws IOException
    {
        Filer filer = JavacProcessingEnvironment.instance(context).getFiler();
        aotConfig = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/native-image/org/noear/solon/data/sqlink/" + projectVersion + "/reflect-config.json");
    }

    @Override
    public void started(TaskEvent event) throws Throwable
    {
        if (!aotTime) return;
    }

    @Override
    public void finished(TaskEvent event) throws Throwable
    {
        if (!aotTime) return;
        recodeClasses(event);
        writeData(event);
    }

    private void recodeClasses(TaskEvent event)
    {
        if (event.getKind() != TaskEvent.Kind.ANALYZE) return;
        CompilationUnitTree compilationUnit = event.getCompilationUnit();
        for (Tree tree : compilationUnit.getTypeDecls())
        {
            if (!(tree instanceof JCTree.JCClassDecl)) continue;
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) tree;
            classDecl.accept(new TreeScanner()
            {
                @Override
                public void visitNewClass(JCTree.JCNewClass newClass)
                {
                    // 匿名类
                    if (newClass.getClassBody() != null)
                    {
                        JCTree.JCClassDecl classBody = newClass.getClassBody();
                        //获取父类符号
                        Symbol.ClassSymbol classSymbol = classBody.getExtendsClause().type.asElement().enclClass();
                        Getter getter = classSymbol.getAnnotation(Getter.class);
                        Setter setter = classSymbol.getAnnotation(Setter.class);
                        //检查是否有标记
                        if (getter != null || setter != null)
                        {
                            AnonymousClassesName.add(classBody.sym.flatName().toString());
                        }
                    }
                    else
                    {
                        super.visitNewClass(newClass);
                    }
                }

                @Override
                public void visitApply(JCTree.JCMethodInvocation methodInvocation)
                {
                    JCTree.JCExpression methodSelect = methodInvocation.getMethodSelect();
                    Symbol.MethodSymbol methodSymbol;

                    if (methodSelect instanceof JCTree.JCFieldAccess)
                    {
                        JCTree.JCFieldAccess select = (JCTree.JCFieldAccess) methodSelect;
                        methodSymbol = (Symbol.MethodSymbol) select.sym;
                    }
                    else
                    {
                        JCTree.JCIdent select = (JCTree.JCIdent) methodSelect;
                        methodSymbol = (Symbol.MethodSymbol) select.sym;
                    }

                    for (int i = 0; i < methodSymbol.getParameters().size(); i++)
                    {
                        Symbol.VarSymbol varSymbol = methodSymbol.getParameters().get(i);
                        if (varSymbol.getAnnotation(Recode.class) != null)
                        {
                            JCTree.JCExpression arg = methodInvocation.getArguments().get(i);
                            if (arg.type instanceof Type.ClassType)
                            {
                                Type.ClassType classType = (Type.ClassType) arg.type;
                                Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) classType.tsym;
                                // xxx
                                if (!classType.isParameterized())
                                {
                                    if (!classSymbol.isAnonymous())
                                    {
                                        classesName.add(classSymbol.flatName().toString());
                                    }
                                }
                                // xxx<1,2,..>
                                else
                                {
                                    Type type = classType.getTypeArguments().get(0);
                                    if (type instanceof Type.ClassType)
                                    {
                                        Type.ClassType t1Type = (Type.ClassType) type;
                                        Symbol.TypeSymbol element = t1Type.asElement();
                                        if (!element.isAnonymous())
                                        {
                                            classesName.add(element.flatName().toString());
                                        }
                                    }
                                    else if (type instanceof Type.CapturedType)
                                    {
                                        Type.CapturedType capturedType = (Type.CapturedType) type;
                                        classesName.add(capturedType.wildcard.type.asElement().flatName().toString());
                                    }
                                    else
                                    {
                                        // ???
                                    }
                                }
                            }
                            else
                            {
                                System.out.println(arg);
                                System.out.println(arg.type);
                            }
                        }
                    }
                    super.visitApply(methodInvocation);
                }
            });
            if (!classDecl.getImplementsClause().isEmpty())
            {
                //获取所有接口
                for (JCTree.JCExpression jcExpression : classDecl.getImplementsClause())
                {
                    //看看是不是转换器类
                    if (jcExpression.type.asElement().flatName().toString().equals(IConverter.class.getCanonicalName()))
                    {
                        //是的话先注册转换器
                        classesName.add(classDecl.sym.flatName().toString());
                        Type.ClassType classType = (Type.ClassType) jcExpression.type;
                        //然后注册转换器承载的类型
                        for (Type typeArgument : classType.getTypeArguments())
                        {
                            String typeName = typeArgument.asElement().flatName().toString();
                            //不注册java自有类
                            if (!typeName.startsWith("java"))
                            {
                                classesName.add(typeName);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    private boolean isWrite = false;

    private void writeData(TaskEvent event) throws IOException
    {
        if (event.getKind().name().equals("COMPILATION") && !isWrite)
        {
            isWrite = true;
            List<ClassData> classDataList = new ArrayList<>(classesName.size() + AnonymousClassesName.size());
            for (String name : classesName)
            {
                classDataList.add(new NormalClassData(name));
            }
            for (String name : AnonymousClassesName)
            {
                classDataList.add(new AnonymousClassData(name));
            }
            String stringify = ONode.stringify(classDataList, org.noear.snack.core.Options.def().add(Feature.PrettyFormat));
            Writer writer = aotConfig.openWriter();
            writer.write(stringify);
            writer.flush();
            writer.close();
        }
    }
}
