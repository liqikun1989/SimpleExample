pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            setUrl("https://maven.aliyun.com/repository/google/")
            name = "Google"
        }
        maven {
            setUrl("https://jitpack.io")
        }
        maven {
            setUrl("http://maven.aliyun.com/nexus/content/groups/public/")
            isAllowInsecureProtocol = true
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven {
            setUrl("https://maven.aliyun.com/repository/google/")
            name = "Google"
        }
        maven {
            setUrl("https://jitpack.io")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/public")
        }
    }
}
rootProject.name = "SimpleExample"
include(":app")
