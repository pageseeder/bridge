plugins {
  id("java-library")
  id("maven-publish")
  alias(libs.plugins.jreleaser)
}

val title: String by project
val gitName: String by project
val website: String by project
val globalVersion = file("version.txt").readText().trim()

version = globalVersion

allprojects {
  group   = "org.pageseeder.bridge"
  version = globalVersion

  apply(plugin = "java-library")
  apply(plugin = "maven-publish")

  configure<JavaPluginExtension> {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(11))
    }
  }

  repositories {
    mavenCentral()
  }

  publishing {
    publications {
      create<MavenPublication>("maven") {
        from(components["java"])
        // Optional but often useful to be explicit:
        artifactId = project.name

        pom {
          name.set(title)
          description.set(project.description)
          url.set(website)
          licenses {
            license {
              name.set("The Apache Software License, Version 2.0")
              url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
          }
          organization {
            name.set("Allette Systems")
            url.set("https://www.allette.com.au")
          }
          scm {
            url.set("git@github.com:pageseeder/${gitName}.git")
            connection.set("scm:git:git@github.com:pageseeder/${gitName}.git")
            developerConnection.set("scm:git:git@github.com:pageseeder/${gitName}.git")
          }
          developers {
            developer { name.set("Carlos Cabral"); email.set("ccabral@allette.com.au") }
            developer { name.set("Christophe Lauret"); email.set("clauret@weborganic.com") }
            developer { name.set("Jean-Baptiste Reure"); email.set("jbreure@weborganic.com") }
            developer { name.set("Philip Rutherford"); email.set("philipr@weborganic.com") }
          }
        }
      }
    }

    repositories {
      maven {
        // IMPORTANT: use rootProject's buildDir so all modules stage into one place
        url = rootProject.layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
      }
    }
  }
}

dependencies {

  api(libs.xmlwriter)
  api(libs.ecache)
  api(libs.cache.api)
  api(libs.jakarta.xml.bind) {
    because("JDK 11 does not include java this module (xml.bind) http://openjdk.java.net/jeps/320")
  }

  implementation(libs.slf4j.api)

  compileOnly (libs.jspecify)

  testImplementation(libs.junit)
  testImplementation(libs.slf4j.simple)

}

tasks.wrapper {
  gradleVersion = "8.14.4"
  distributionType = Wrapper.DistributionType.ALL
}

tasks.withType<Javadoc> {
  options {
    encoding = "UTF-8"
  }
}

jreleaser {
  configFile.set(file("jreleaser.toml"))
}
