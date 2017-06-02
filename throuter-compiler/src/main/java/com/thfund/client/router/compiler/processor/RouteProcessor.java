package com.thfund.client.router.compiler.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.thfund.client.router.annotation.ActivityRoute;
import com.thfund.client.router.annotation.FragmentRoute;
import com.thfund.client.router.compiler.Constants;
import com.thfund.client.router.compiler.utils.CollectionUtils;
import com.thfund.client.router.compiler.utils.Logger;
import com.thfund.client.router.compiler.utils.StringUtils;
import com.thfund.client.router.compiler.utils.TypeUtils;
import com.thfund.client.router.model.RouteMeta;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.thfund.client.router.compiler.Constants.ACTIVITY;
import static com.thfund.client.router.compiler.Constants.ANNOTATION_NANE_ACTIVITY_FORMER_ROUTE;
import static com.thfund.client.router.compiler.Constants.ANNOTATION_NANE_ACTIVITY_ROUTE;
import static com.thfund.client.router.compiler.Constants.I_ACTIVITY_FORMER_ROUTE;
import static com.thfund.client.router.compiler.Constants.I_ACTIVITY_ROUTE;
import static com.thfund.client.router.compiler.Constants.METHOD_LOAD_INTO;
import static com.thfund.client.router.compiler.Constants.SEPARATOR;
import static com.thfund.client.router.compiler.Constants.WARNING_TIPS;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * @author WayneWang
 * @since 2017/5/26 10:56
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({Constants.ANNOTATION_TYPE_ACTIVITY_ROUTE, Constants.ANNOTATION_TYPE_FRAGMENT_ROUTE})
public class RouteProcessor extends AbstractProcessor {
    private Filer mFiler;       // File util, write class file into disk.
    private Logger logger;
    private Types types;
    private Elements elements;
    private TypeUtils typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mFiler = processingEnv.getFiler();                  // Generate class.
        types = processingEnv.getTypeUtils();            // Get type utils.
        elements = processingEnv.getElementUtils();      // Get class meta.

        typeUtils = new TypeUtils(types, elements);
        logger = new Logger(processingEnv.getMessager());   // Package the log utils.
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isEmpty(set)) {
            return false;
        }

        Set<? extends Element> activityRouteElements = roundEnvironment.getElementsAnnotatedWith(ActivityRoute.class);
        Set<? extends Element> fragmentRouteElements = roundEnvironment.getElementsAnnotatedWith(FragmentRoute.class);
        try {
            logger.info(">>> Found routes, start... <<<");
            this.parseActivityRoutes(activityRouteElements);
            this.parseFragmentRoutes(fragmentRouteElements);

        } catch (Exception e) {
            logger.error(e);
        }
        return true;
    }

    private void parseActivityRoutes(Set<? extends Element> routeElements) throws IOException {
        TypeMirror type_Activity = elements.getTypeElement(ACTIVITY).asType();

        // Interface of ThRouter
        TypeElement type_IActivityRoute = elements.getTypeElement(I_ACTIVITY_ROUTE);
        TypeElement type_IActivityFormerRoute = elements.getTypeElement(I_ACTIVITY_FORMER_ROUTE);
        ClassName routeMetaCn = ClassName.get(RouteMeta.class);

        /*
            Build input type, format as :

            ```Map<String, RouteMeta>```
        */
        ParameterizedTypeName inputMapTypeOfRoute = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class)
        );

        /*
            Build input type, format as :

            ```Map<RouteMeta, String>```
        */
        ParameterizedTypeName inputMapTypeOfFormerRoute = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(RouteMeta.class),
                ClassName.get(String.class)
        );

        ParameterSpec routesParamSpec = ParameterSpec.builder(inputMapTypeOfRoute, "routeAtlas").build();
        ParameterSpec formerRoutesParamSpec = ParameterSpec.builder(
                inputMapTypeOfFormerRoute, "formerRoutesAtlas").build();

        /*
            Build method : 'loadInto' routes
        */
        MethodSpec.Builder loadIntoMethodOfRoutsBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(routesParamSpec);

        /*
            Build method : 'loadInto' formerRoutes
        */
        MethodSpec.Builder loadIntoMethodOfFormerRoutsBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(formerRoutesParamSpec);

        for (Element element : routeElements) {
            TypeMirror tm = element.asType();
            if (!types.isSubtype(tm, type_Activity)) {
                throw new RuntimeException("ThRouter::Compiler >>> " +
                        "ActivityRoute has modified a nonActivity class: " + tm);
            }
            ActivityRoute activityRoute = element.getAnnotation(ActivityRoute.class);
//            routeAtlas.put("activity_main", RouteMeta.build("1002", "com.thfund.client.router.demo.MainActivity"));
            ClassName className = ClassName.get((TypeElement) element);
            loadIntoMethodOfRoutsBuilder.addStatement("routeAtlas.put($S, $T.build($S, $S))",
                    activityRoute.routeKey(),
                    routeMetaCn,
                    activityRoute.bundleID(),
                    className);

            // Generate activity route file
            String activityRouteFile = className.simpleName() + SEPARATOR + ANNOTATION_NANE_ACTIVITY_ROUTE;
            JavaFile.builder(className.packageName(),
                    TypeSpec.classBuilder(activityRouteFile)
                            .addJavadoc(WARNING_TIPS)
                            .addSuperinterface(ClassName.get(type_IActivityRoute))
                            .addModifiers(PUBLIC)
                            .addMethod(loadIntoMethodOfRoutsBuilder.build())
                            .build()
            ).build().writeTo(mFiler);

            logger.info(">>> Generated activityRoutes: " + className + " <<<");

            boolean noFormerBundleID = StringUtils.isEmpty(activityRoute.formerBundleID());
            boolean noFormerClassName = StringUtils.isEmpty(activityRoute.formerClassName());

            if (noFormerBundleID && noFormerClassName) {}


            if (noFormerBundleID != noFormerClassName) {
                throw new RuntimeException("ThRouter::Compiler >>> " +
                        "Must specify both formerBundleID and formerClassName: " + tm);
            }

            loadIntoMethodOfFormerRoutsBuilder.addStatement("formerRoutesAtlas.put($T.build($S, $S), $S)",
                    routeMetaCn,
                    activityRoute.formerBundleID(),
                    activityRoute.formerClassName(),
                    activityRoute.routeKey()
                    );

            // Generate activity route former file
            String activityFormerRouteFile = className.simpleName() + SEPARATOR + ANNOTATION_NANE_ACTIVITY_FORMER_ROUTE;
            JavaFile.builder(className.packageName(),
                    TypeSpec.classBuilder(activityFormerRouteFile)
                            .addJavadoc(WARNING_TIPS)
                            .addSuperinterface(ClassName.get(type_IActivityFormerRoute))
                            .addModifiers(PUBLIC)
                            .addMethod(loadIntoMethodOfFormerRoutsBuilder.build())
                            .build()
            ).build().writeTo(mFiler);

            logger.info(">>> Generated activityFormerRoutes: " + className + " <<<");
        }
    }

    private void parseFragmentRoutes(Set<? extends Element> elements) {
    }
}
