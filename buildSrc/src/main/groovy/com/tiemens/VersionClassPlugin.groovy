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
    project.extensions.create("versionClass", VersionClassPluginExtension)
    def versionClassConfiguration = project.versionClass

    project.getPlugins().apply(JavaPlugin.class)
    def genSrc = 'generated-src/version'
    def generatedSrcDir = new File(project.buildDir, genSrc)

    // See below on why templateName is hard-coded.
    // And, for that matter, why the directories are hard-coded too.
    // [versionClassConfiguration.templateName is still the default value here]
    def templateName = 
        "VersionClass.template"
        // NO! versionClassConfiguration.templateName

    // directories hard-coded for same reason as templateName hard-coded
    def fetch = fetchTemplateText(
            [ new File("src/main/resources/" + templateName),
              new File("buildSrc/src/main/resources/" + templateName),
              getClass().getClassLoader().getResource(templateName)])

    def templateText      = fetch['templateText']
    def templateFileInput = fetch['templateFileInput']


    def makeVersionClassTask = project.task('makeVersionClass') << {

      // at the time the versionClassConfiguration was constructed,
      // "project" did not have many properties set to their final value,
      // so "refresh" our values from project and System.env
      versionClassConfiguration.refresh(project, System.env)

      def templateModel = versionClassConfiguration
      def packageName = "${templateModel.packageName}"

      def outFilename = "java/" + 
                        packageName.replace('.', '/') + 
                        "/${templateModel.generatedClassName}.java"
      def outFile = new File(generatedSrcDir, outFilename)
      outFile.getParentFile().mkdirs()


      def engine = new SimpleTemplateEngine();
      def outputString = engine.createTemplate(templateText)
                               .make(templateModel.convertToMap());


      def f = new FileWriter(outFile)
      f.write(outputString)
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

    //
    // Task : INPUTS
    //
    makeVersionClassTask.getInputs().files(project.sourceSets.main.getAllSource())

    // This is so messed up...
    //  We can't have both:
    //     templateName is CONFIGURABLE                  and
    //     identify the template as an INPUT dependency
    // So, we do not allow templateName to be CONFIGURABLE
    if (templateFileInput) {
        makeVersionClassTask.getInputs().files(templateFileInput)
    } else {
        // println "No template file found, no input dependency added"
    }

    if( project.getBuildFile() != null && project.getBuildFile().exists() )
    {
      makeVersionClassTask.getInputs().files(project.getBuildFile())
    }

    //
    // Task : OUTPUTS
    //
    makeVersionClassTask.getOutputs().files(generatedSrcDir)


    project.getTasks().getByName('compileJava').dependsOn('compileVersionJava')
    project.getTasks().getByName('compileVersionJava').dependsOn('makeVersionClass')

//    TODO: what does this do?
//    project.getTasks().getByName('jar') {
//      from project.sourceSets.version.output
//    }
  }
}



// This class is both the:
//    Template Model      and
//    Plugin Configuration mechanism
//
class VersionClassPluginExtension {
    def now = "" + new Date() 
    def version = null
    def group = null
    def buildNumber = null
    def packageName = null
    def generatedClassName = "BuildVersion"

    def getUiVersion() {
        version +
        ( buildNumber.equals("") ? "" : (".b" + buildNumber) )
    }



    VersionClassPluginExtension() {
    }

    public void refresh(project, env) {
        if (project != null) {
            // project.PROPERTY could be "unspecified", 
            // but if ours is null, set it anyway

            if (! version) {

                version = project.version
            }
            if (! group) {
                group = project.group
            }
        }
        if (env != null) {
            def sourceBuildNumber = ''
            if (env.SOURCE_BUILD_NUMBER != null) {
                sourceBuildNumber = env.SOURCE_BUILD_NUMBER
            }

            if (! buildNumber) {
                buildNumber = sourceBuildNumber
            }
        }    
    }

    //
    // SimpleTemplateEngine needs an actual "Map" instance
    // This is a cheap-and-dirty way to get that Map instance:
    // The "depth > 10" prevents stack overflows on the runtime object
    //   "muck-if-ication" of the VersionClassPluginExtension - 
    //    stuff that your .template file is not going to reference anyway
    //
    public Map convertToMap() {
        return convertToMap(this, 1)
    }
    public Map convertToMap(object, depth) {
        return object?.properties.findAll{
            it.hasProperty('key') && (it.key != 'class') }
        .collectEntries {
            it.value == null || 
            it.value instanceof Serializable || 
            (depth > 10) ?
                    [it.key, it.value] :
                    [it.key,   convertToMap(it.value, depth + 1)]
        }
    }
}
