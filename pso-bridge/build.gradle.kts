description = "Servlets and Berlioz generators for the bridge in Berlioz"

// Dependencies of the project
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

tasks.withType<Javadoc>().configureEach {
  isFailOnError = false
}
