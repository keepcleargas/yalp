package yalp.db.jpa;

import javassist.CtClass;
import javassist.CtMethod;
import yalp.classloading.ApplicationClasses.ApplicationClass;
import yalp.classloading.enhancers.Enhancer;

/**
 * Enhance JPABase entities classes
 */
public class JPAEnhancer extends Enhancer {

    public void enhanceThisClass(ApplicationClass applicationClass) throws Exception {
        CtClass ctClass = makeClass(applicationClass);

        if (!ctClass.subtypeOf(classPool.get("yalp.db.jpa.JPABase"))) {
            return;
        }

        // Enhance only JPA entities
        if (!hasAnnotation(ctClass, "javax.persistence.Entity")) {
            return;
        }

        String entityName = ctClass.getName();

        // count
        CtMethod count = CtMethod.make("public static long count() { return yalp.db.jpa.JPQL.instance.count(\"" + entityName + "\"); }", ctClass);
        ctClass.addMethod(count);

        // count2
        CtMethod count2 = CtMethod.make("public static long count(String query, Object[] params) { return yalp.db.jpa.JPQL.instance.count(\"" + entityName + "\", query, params); }", ctClass);
        ctClass.addMethod(count2);

        // findAll
        CtMethod findAll = CtMethod.make("public static java.util.List findAll() { return yalp.db.jpa.JPQL.instance.findAll(\"" + entityName + "\"); }", ctClass);
        ctClass.addMethod(findAll);

        // findById
        CtMethod findById = CtMethod.make("public static yalp.db.jpa.JPABase findById(Object id) { return yalp.db.jpa.JPQL.instance.findById(\"" + entityName + "\", id); }", ctClass);
        ctClass.addMethod(findById);

        // find
        CtMethod find = CtMethod.make("public static yalp.db.jpa.GenericModel.JPAQuery find(String query, Object[] params) { return yalp.db.jpa.JPQL.instance.find(\"" + entityName + "\", query, params); }", ctClass);
        ctClass.addMethod(find);

        // find
        CtMethod find2 = CtMethod.make("public static yalp.db.jpa.GenericModel.JPAQuery find() { return yalp.db.jpa.JPQL.instance.find(\"" + entityName + "\"); }", ctClass);
        ctClass.addMethod(find2);

        // all
        CtMethod all = CtMethod.make("public static yalp.db.jpa.GenericModel.JPAQuery all() { return yalp.db.jpa.JPQL.instance.all(\"" + entityName + "\"); }", ctClass);
        ctClass.addMethod(all);

        // delete
        CtMethod delete = CtMethod.make("public static int delete(String query, Object[] params) { return yalp.db.jpa.JPQL.instance.delete(\"" + entityName + "\", query, params); }", ctClass);
        ctClass.addMethod(delete);

        // deleteAll
        CtMethod deleteAll = CtMethod.make("public static int deleteAll() { return yalp.db.jpa.JPQL.instance.deleteAll(\"" + entityName + "\"); }", ctClass);
        ctClass.addMethod(deleteAll);

        // findOneBy
        CtMethod findOneBy = CtMethod.make("public static yalp.db.jpa.JPABase findOneBy(String query, Object[] params) { return yalp.db.jpa.JPQL.instance.findOneBy(\"" + entityName + "\", query, params); }", ctClass);
        ctClass.addMethod(findOneBy);

        // create
        CtMethod create = CtMethod.make("public static yalp.db.jpa.JPABase create(String name, yalp.mvc.Scope.Params params) { return yalp.db.jpa.JPQL.instance.create(\"" + entityName + "\", name, params); }", ctClass);
        ctClass.addMethod(create);

        // Done.
        applicationClass.enhancedByteCode = ctClass.toBytecode();
        ctClass.defrost();
    }

}