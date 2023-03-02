package com.github.linyuzai.inherit.processor;

import com.github.linyuzai.inherit.processor.handler.InheritHandler;
import com.github.linyuzai.inherit.processor.utils.InheritFlag;
import com.github.linyuzai.inherit.processor.utils.InheritUtils;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.util.*;

/**
 * 继承处理器
 */
@SuppressWarnings("unchecked")
public abstract class AbstractInheritProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        JavacElements elementUtils = (JavacElements) processingEnv.getElementUtils();
        Trees trees = Trees.instance(processingEnv);
        TreeMaker treeMaker = TreeMaker.instance(context);
        Names names = Names.instance(context);
        //扫描到的 @InheritXXX
        for (TypeElement annotation : annotations) {
            //获得标注了 @InheritXXX 的类
            Set<? extends Element> targetClassElements = roundEnv.getElementsAnnotatedWith(annotation);
            //遍历这些类
            for (Element targetClassElement : targetClassElements) {
                //只有在类上生效
                if (!InheritUtils.isClass(targetClassElement)) {
                    continue;
                }
                //获得 Target类 的语法树
                JCTree.JCClassDecl targetClassDef = (JCTree.JCClassDecl) elementUtils.getTree(targetClassElement);
                //获得 Target类 上的 @InheritXXX 注解
                Collection<AnnotationMirror> annotationsWithRepeat = getAnnotationsWithRepeat(targetClassElement);
                //遍历 @InheritXXX 注解
                for (AnnotationMirror inheritAnnotation : annotationsWithRepeat) {
                    //获得 @InheritXXX 注解属性
                    Map<String, Object> attributes = getAttributes(inheritAnnotation);
                    //要继承的 Class
                    Collection<Type.ClassType> sources = convertClass((Collection<Attribute.Class>) attributes.get("sources"));
                    //是否处理父类
                    Boolean inheritSuper = (Boolean) attributes.getOrDefault("inheritSuper", false);
                    //排除的字段
                    Collection<String> excludeFields = convertString((Collection<Attribute.Constant>) attributes.getOrDefault("excludeFields", Collections.emptyList()));
                    //排除的方法
                    Collection<String> excludeMethods = convertString((Collection<Attribute.Constant>) attributes.getOrDefault("excludeMethods", Collections.emptyList()));
                    //继承标识 flag
                    Collection<String> flags = convertEnum((Collection<Attribute.Enum>) attributes.getOrDefault("flags", Collections.emptyList()));
                    //通过继承标识 flag 获得对应的处理器
                    Collection<InheritHandler> handlers = InheritUtils.getInheritHandlers(flags);
                    //遍历所有要继承的 Class
                    for (Type.ClassType sourceClass : sources) {
                        //继承 Class
                        inheritClass(sourceClass, targetClassDef, inheritSuper,
                                excludeFields, excludeMethods, flags, handlers,
                                treeMaker, names, trees, elementUtils, 0);
                    }
                }
            }
        }
        return true;
    }

    /**
     * 是否要继承字段
     */
    protected boolean inheritFields() {
        return false;
    }

    /**
     * 是否要继承方法
     */
    protected boolean inheritMethods() {
        return false;
    }

    /**
     * 获得注解名称
     */
    protected abstract String getAnnotationName();

    /**
     * 获得 Repeatable 注解名称
     */
    protected abstract String getRepeatableAnnotationName();

    /**
     * 解析 Repeatable 注解中的所有注解
     */
    private Collection<AnnotationMirror> getAnnotationsWithRepeat(Element element) {
        Collection<AnnotationMirror> annotations = new ArrayList<>();
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            if (InheritUtils.isAnnotation(annotation, getAnnotationName())) {
                annotations.add(annotation);
            } else if (InheritUtils.isAnnotation(annotation, getRepeatableAnnotationName())) {
                for (AnnotationValue value : annotation.getElementValues().values()) {
                    List<AnnotationMirror> children = (List<AnnotationMirror>) value.getValue();
                    annotations.addAll(children);
                }
            }
        }
        return annotations;
    }

    /**
     * 获得注解上的属性
     */
    private Map<String, Object> getAttributes(AnnotationMirror annotation) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet()) {
            Symbol.MethodSymbol key = (Symbol.MethodSymbol) entry.getKey();
            attributes.put(key.getQualifiedName().toString(), entry.getValue().getValue());
        }
        return attributes;
    }

    private Collection<Type.ClassType> convertClass(Collection<Attribute.Class> classes) {
        Collection<Type.ClassType> classTypes = new ArrayList<>();
        for (Attribute.Class attribute : classes) {
            classTypes.add((Type.ClassType) attribute.getValue());
        }
        return classTypes;
    }

    private Collection<String> convertString(Collection<Attribute.Constant> constants) {
        Collection<String> set = new HashSet<>();
        for (Attribute.Constant constant : constants) {
            set.add(constant.value.toString());
        }
        return set;
    }

    private Collection<String> convertEnum(Collection<Attribute.Enum> enums) {
        Collection<String> set = new HashSet<>();
        for (Attribute.Enum attribute : enums) {
            set.add(attribute.value.toString());
        }
        return set;
    }

    private void inheritClass(Type.ClassType sourceClass,
                              JCTree.JCClassDecl targetClassDef,
                              Boolean inheritSuper,
                              Collection<String> excludeFields,
                              Collection<String> excludeMethods,
                              Collection<String> flags,
                              Collection<InheritHandler> handlers,
                              TreeMaker treeMaker,
                              Names names,
                              Trees trees,
                              JavacElements elementUtils,
                              int level) {
        //获得要继承的 Class 的语法树
        JCTree.JCClassDecl sourceClassDef = (JCTree.JCClassDecl) elementUtils.getTree(sourceClass.asElement());
        //继承父类
        if (inheritSuper) {
            //获得父类信息
            Symbol.TypeSymbol symbol = sourceClass.supertype_field.tsym;
            if (symbol != null) {
                Type type = symbol.asType();
                if (type instanceof Type.ClassType) {
                    //处理父类
                    inheritClass((Type.ClassType) type, targetClassDef, true,
                            excludeFields, excludeMethods, flags, handlers,
                            treeMaker, names, trees, elementUtils, level + 1);
                }
            }
        }

        //JCTree.JCCompilationUnit compilationUnit = treeMaker.toplevel; 拿不到
        JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit) trees
                .getPath(targetClassDef.sym).getCompilationUnit();
        //继承字段
        if (inheritFields()) {
            //如果要处理自身的字段或方法
            //level == 0 控制只处理一次
            if (level == 0 && flags.contains(InheritFlag.OWN.name())) {
                for (JCTree def : targetClassDef.defs) {
                    //如果是非静态的字段
                    if (InheritUtils.isNonStaticVariable(def)) {
                        JCTree.JCVariableDecl varDef = (JCTree.JCVariableDecl) def;
                        //处理字段的 flag
                        for (InheritHandler handler : handlers) {
                            handler.handle(varDef, targetClassDef, treeMaker, names, level);
                        }
                    }
                }
            }
            //复制字段
            for (JCTree sourceDef : sourceClassDef.defs) {
                //如果是非静态的字段
                if (InheritUtils.isNonStaticVariable(sourceDef)) {
                    JCTree.JCVariableDecl sourceVarDef = (JCTree.JCVariableDecl) sourceDef;
                    //不在排除的字段名称之内
                    if (!excludeFields.contains(sourceVarDef.name.toString())) {
                        //Class 中未定义
                        if (!InheritUtils.isFieldDefined(targetClassDef, sourceVarDef)) {
                            //添加字段
                            Symbol.ClassSymbol sym = (Symbol.ClassSymbol) ((JCTree.JCIdent) sourceVarDef.vartype).sym;
                            Name name = sym.name;
                            Symbol.PackageSymbol owner = (Symbol.PackageSymbol) sym.owner;
                            JCTree.JCFieldAccess select = treeMaker.Select(treeMaker.Ident(owner.fullname), name);
                            JCTree.JCImport jcImport = treeMaker.Import(select, false);
                            if (!InheritUtils.isImportDefined(compilationUnit, jcImport)) {
                                compilationUnit.defs = compilationUnit.defs.append(jcImport);
                            }
                            targetClassDef.defs = targetClassDef.defs.append(sourceVarDef);
                        }
                        //处理字段的 flag
                        for (InheritHandler handler : handlers) {
                            handler.handle(sourceDef, targetClassDef, treeMaker, names, level);
                        }
                    }
                }
            }
        }
        //继承方法
        if (inheritMethods()) {
            for (JCTree sourceDef : sourceClassDef.defs) {
                //如果是非静态的方法
                if (InheritUtils.isNonStaticMethod(sourceDef)) {
                    JCTree.JCMethodDecl sourceMethodDef = (JCTree.JCMethodDecl) sourceDef;
                    //不在排除的方法名称之内
                    if (!excludeMethods.contains(sourceMethodDef.name.toString())) {
                        //Class 中未定义
                        if (!InheritUtils.isMethodDefined(targetClassDef, sourceMethodDef)) {
                            //添加方法
                            targetClassDef.defs = targetClassDef.defs.append(sourceMethodDef);
                        }
                        //处理方法的 flag
                        for (InheritHandler handler : handlers) {
                            handler.handle(sourceDef, targetClassDef, treeMaker, names, level);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Arrays.asList(getAnnotationName(), getRepeatableAnnotationName()));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}
