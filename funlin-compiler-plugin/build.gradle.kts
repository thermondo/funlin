plugins {
    alias(deps.plugins.kotlin.jvm)
    alias(deps.plugins.ksp)
}

dependencies {
    implementation(deps.autoService)
    ksp(deps.autoService.ksp)
    compileOnly(deps.kotlinCompilerEmbeddable)
    compileOnly(deps.kotlin.stdlib)
}