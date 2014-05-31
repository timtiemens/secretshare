/*******************************************************************************
 * Copyright (c) 2014 Tim Tiemens.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 *
 * Contributors:
 *     Tim Tiemens - initial API and implementation
 *******************************************************************************/
package com.tiemens

import groovy.text.SimpleTemplateEngine

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.plugins.JavaPlugin

import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

class VersionClassPlugin implements Plugin<Project> {

  VersionClassPlugin()  {
  }

  public genTemplateModel(project, env) {
        def sourceBuildNumber = ''
        if (env != null) {
            if (env.SOURCE_BUILD_NUMBER != null) {
                sourceBuildNumber = env.SOURCE_BUILD_NUMBER
            }
        }
        def templateModel = [:]
        templateModel['now'] = "" + new Date()
        templateModel['name'] = project == null ? "Name" : project.name;
        templateModel['version'] = project == null ? "Version" : project.version
        templateModel['group'] = project == null ? "Group" : project.group
        templateModel['buildNumber'] = sourceBuildNumber
        templateModel['uiVersion'] =
         templateModel['version'] +
         ( templateModel['buildNumber'].equals("") ? "" :
              (".b" + templateModel['buildNumber']) );


        return templateModel;
    }



    public Map fetchTemplateText(thingsThatHaveDotNewReader) {

        // this is set by the .each loop to the first:
        //   non-null thing AND if-file-then-file-exists
        // item in thingsThatHaveDotNewReader
        def savedHasNewReader = null

        thingsThatHaveDotNewReader.each {
          hasNewReader ->
            if (hasNewReader != null) {
                if (savedHasNewReader == null) {
                    boolean useIt = false;
                    if (hasNewReader instanceof File) {
                        File f = (File) hasNewReader;
                        if (f.canRead()) {
                            useIt = true
                        } else {
                            // println("file not readable: " + f);
                        }
                    } else {
                        // no further info, so just useIt
                        useIt = true
                    }

                    if (useIt) {
                        savedHasNewReader = hasNewReader

                    }
                }
            }
        }

        // convert the savedHasNewReader into something that has .getText()
        def hasGetText = null
        if (savedHasNewReader) {
            hasGetText = savedHasNewReader.newReader('UTF-8');
        }

        def templateText = null
        def templateFileInput = null;
        if (hasGetText == null) {
            templateText = "ERROR"
            templateFileInput = null
        } else {
            templateText = hasGetText.getText()
            if (savedHasNewReader instanceof File) {
                templateFileInput = savedHasNewReader
            }
        }

        return ['templateText': templateText,
                'templateFileInput': templateFileInput]
    }
    

  def void apply(Project project)
  {
    project.getPlugins().apply(JavaPlugin.class)
    def genSrc = 'generated-src/version'
    def generatedSrcDir = new File(project.buildDir, genSrc)

    def templateName = "VersionClass.template"

    def fetch = fetchTemplateText(
            [ new File("src/main/resources/" + templateName),
              new File("buildSrc/src/main/resources/" + templateName),
              getClass().getClassLoader().getResource(templateName)])

    def templateText      = fetch['templateText']
    def templateFileInput = fetch['templateFileInput']


    def makeVersionClassTask = project.task('makeVersionClass') << {
      def packageName = project.group + '.' + project.name
      packageName = packageName.replace('-','_')
      def outFilename = "java/" + packageName.replace('.', '/') + "/BuildVersion.java"
      def outFile = new File(generatedSrcDir, outFilename)
      outFile.getParentFile().mkdirs()

    def templateModel = genTemplateModel(project, System.env)
    templateModel['packageName'] = packageName;


      def engine = new SimpleTemplateEngine();
      def answer = engine.createTemplate(templateText).make(templateModel);

      def templateString = answer

      def f = new FileWriter(outFile)
      f.write(templateString)
      f.close()
    } // task

    project.sourceSets {
      version {
        java {
          srcDir project.buildDir.name + '/' + genSrc + '/java'
        }
      }
      main {
        java {
          srcDir project.buildDir.name + '/' + genSrc + '/java'
        }
      }
    }

    makeVersionClassTask.getInputs().files(project.sourceSets.main.getAllSource())
    if (templateFileInput) {
        makeVersionClassTask.getInputs().files(templateFileInput)
    } else {
        // println "No template file found, no input dependency added"
    }
    makeVersionClassTask.getOutputs().files(generatedSrcDir)
    if( project.getBuildFile() != null && project.getBuildFile().exists() )
    {
      makeVersionClassTask.getInputs().files(project.getBuildFile())
    }
    project.getTasks().getByName('compileJava').dependsOn('compileVersionJava')
    project.getTasks().getByName('compileVersionJava').dependsOn('makeVersionClass')

//    TODO: what does this do?
//    project.getTasks().getByName('jar') {
//      from project.sourceSets.version.output
//    }
  }
}


