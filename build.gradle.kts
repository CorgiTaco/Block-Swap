plugins {
    id("earth.terrarium.cloche") version "0.8.20"
}

group = "dev.corgitaco"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.jt-dev.tech/releases")
    maven("https://maven.jt-dev.tech/snapshots")

    cloche {
        librariesMinecraft()
        main()

        mavenFabric()
        mavenNeoforged()
    }
}

cloche {
    minecraftVersion = "1.21.1"


    metadata {
        modId = "blockswap"
        name = "Block Swap"
        license = "ARR"
        description = "Hello!"
        dependency {
            modId = "dataanchor"
            required = true
            version("2.0.0.3")
        }
    }

    common {
        dependencies {
            compileOnly("io.github.llamalad7:mixinextras-fabric:${project.properties["mixinextras_version"]}")
        }
    }

    forge {
        loaderVersion = "52.1.0"

        runs {
            server()
            client()
        }
        val mixinExtras = module("io.github.llamalad7:mixinextras-forge:${project.properties["mixinextras_version"]}")


        include(mixinExtras)
        project.dependencies.add("forgeAnnotationProcessor", mixinExtras)

        dependencies {
            modApi(mixinExtras)
            modApi("dev.corgitaco:Data_Anchor-forge-${minecraftVersion.get()}:${project.properties["data_anchor_version"]}")
        }
    }

    neoforge {
        loaderVersion = "21.1.135"
        mixins.from(file("src/common/blockswap.mixins.json"))

        data()

        runs {
            server()
            client()
            data()
        }

        dependencies {
            modApi("dev.corgitaco:Data_Anchor-neoforge-${minecraftVersion.get()}:${project.properties["data_anchor_version"]}")
        }
    }

    fabric {
        loaderVersion = "0.16.10"
        mixins.from(file("src/common/blockswap.mixins.json"))

        metadata {
            entrypoint("main", "dev.corgitaco.blockswap.fabric.FabricBlockSwap::initialize")
            dependency {
                modId = "fabric-api"
                required = true
            }
        }

        data()
        client()

        dependencies {
            fabricApi("0.115.2+1.21.1")
            modApi("dev.corgitaco:Data_Anchor-fabric-${minecraftVersion.get()}:${project.properties["data_anchor_version"]}")
        }

        runs {
            server()
            client()
            data()
        }
    }
}
