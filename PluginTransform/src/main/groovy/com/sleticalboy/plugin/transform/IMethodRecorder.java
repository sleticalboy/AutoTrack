package com.sleticalboy.plugin.transform;

import java.util.Set;
import org.gradle.api.Project;

/**
 * Created on 2022/4/28
 *
 * @author binlee
 */
public interface IMethodRecorder {

  void prepare(Project project);

  void record(Set<String> records);
}
