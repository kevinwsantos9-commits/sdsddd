# NIVEK: ECLIPSE

Mod Fabric para Minecraft 1.21.8 (Java 21). Um meteoro cai, o jogador se funde com uma forma de vida extraterrestre e evolui.

Desenvolvimento em fases. Antes de continuar, leia `DEVELOPMENT_STATUS.md` e `CONTINUATION.md`.

## Build
- GitHub Actions: a cada push o build roda sozinho; o jar sai em Actions > build > Artifacts e, em push na main/master, tambem em Releases (build-N). Para uma versao fixa, crie uma tag `v*` (ex.: v0.2.0).
- Local: Gradle 8.14+ e JDK 21, `gradle build`. O jar fica em `build/libs/`.
