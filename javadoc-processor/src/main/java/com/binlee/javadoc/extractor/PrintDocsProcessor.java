package com.binlee.javadoc.extractor;

import com.google.auto.service.AutoService;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
@SupportedAnnotationTypes("*")
public class PrintDocsProcessor extends AbstractProcessor {

  private static final String TAG = "PrintDocsProcessor";

  @Override public synchronized void init(ProcessingEnvironment procEnv) {
    super.init(procEnv);
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.RELEASE_8;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) return false;
    Log.v(TAG, "process() start with: " + annotations);
    JavadocPrinter.print(roundEnv.getRootElements(), processingEnv.getElementUtils(), TAG);
    return true;
  }
}