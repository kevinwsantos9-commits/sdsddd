# CONTINUATION — manual para outra IA

## Objetivo do mod
NIVEK: ECLIPSE (Fabric 1.21.8). Um meteoro cai perto do jogador. Ele toca o núcleo extraterrestre e se funde com ele (tentáculos, mutação nível 1). Depois evolui em 5 estágios com o ECLIPSE SYSTEM (interface), braço biomecânico, lâmina/garras, implantes, criaturas e chefes, e transformações finais (tentáculos, asas, Armadura Eclipse).

## História
Meteoro -> cratera -> núcleo alienígena (evento único por jogador) -> fusão cinematográfica -> mutação nível 1 -> evolução.

## Arquitetura
- `src/main`: lógica comum (servidor + cliente). Entrypoint `NivekEclipse`.
- `src/client`: render, GUI, modelos, input. Entrypoint `client.NivekEclipseClient`.
- Mod id `nivekeclipse`. Pacote `com.nivek.eclipse`.

## Estrutura atual
```
build.gradle, settings.gradle, gradle.properties, .github/workflows/build.yml
src/main/java/com/nivek/eclipse/
  NivekEclipse.java            (entrypoint; chama init dos sistemas)
  registry/ModBlocks.java      (bloco + item alien_core, aba criativa)
  data/ModAttachments.java     (METEOR_EVENT_DONE, MUTATION_LEVEL, ENERGY; persistentes; MUTATION_LEVEL sincroniza com todos os clientes, ENERGY só com o dono)
  data/EclipseData.java        (get/setMutationLevel)
  block/AlienCoreBlock.java    (toque chama FusionEvent.tryStart)
  event/FusionEvent.java       (sequência de fusão de 100 ticks; concede mutação 1 no fim)
  event/MeteorEvent.java       (gatilho, queda, cratera, núcleo; start() público)
  combat/AbilityPayload.java   (pacote C2S: id da habilidade)
  combat/CooldownPayload.java  (pacote S2C: recarga para o HUD)
  combat/CombatManager.java    (energia, cooldown, combos, Lâmina e Garras; registro dos pacotes)
  command/EclipseCommand.java  (/eclipse meteor | status | mutation <0-5> | reset)
src/client/java/com/nivek/eclipse/client/
  NivekEclipseClient.java      (G -> EclipseScreen; V/R -> AbilityPayload; layer do braço; HUD; receiver de recarga)
  ClientCombatState.java       (recarga como o HUD enxerga)
  hud/EclipseHud.java          (energia + recarga, canto inferior direito)
  gui/EclipseScreen.java       (painel, sidebar, clique nas abas, aba OVERVIEW)
  gui/EclipseTab.java          (enum das 11 abas + nota da fase)
  render/BioArmModel.java      (LayerDefinition + textura do braço)
  render/BioArmLayer.java      (RenderLayer no PlayerRenderer; idle/transformação/ataque)
  render/EclipseRenderState.java (interface implementada no PlayerRenderState via mixin)
  render/ArmTransformTracker.java (detecta mutação 0 -> 1 e calcula progresso)
  mixin/PlayerRenderStateMixin.java, mixin/PlayerRendererMixin.java
src/client/resources/nivekeclipse.client.mixins.json
src/main/resources/assets/nivekeclipse/textures/entity/bio_arm.png
src/main/resources/fabric.mod.json
src/main/resources/assets/nivekeclipse/ (blockstates, models/block, items, textures/block, lang en_us/pt_br)
```

## Sistemas implementados
- Fase 01: fundação.
- Fase 02: evento do meteoro (ver DEVELOPMENT_STATUS). Meteoro simulado em `MeteorEvent` (lista ACTIVE, tick no END_SERVER_TICK), sem entidade.
- Fase 03: fusão em `FusionEvent` (mapa ACTIVE por UUID, tick no END_SERVER_TICK). Núcleo e mutação só mudam no fim; cancelamento não perde nada.
- Fase 04 (etapa 4.1): ECLIPSE SYSTEM em `client/gui`. Tela própria sem widgets (desenha com GuiGraphics e trata clique em `mouseClicked`). Para adicionar conteúdo a uma aba, trate-a em `EclipseScreen.render` (como `renderOverview`) e remova o placeholder.
- Fase 05 (etapa 5.1): braço biomecânico como `RenderLayer` (registrado em `NivekEclipseClient` via `LivingEntityFeatureRendererRegistrationCallback`). O layer não conhece a entidade, só o `PlayerRenderState`; por isso os dados (mutação, progresso da transformação) são colocados no state por mixin (`PlayerRendererMixin`). Para novas peças, adicione partes em `BioArmModel` e anime em `BioArmLayer`.
- Fase 06 (etapa 6.1): o cliente só PEDE a habilidade (`AbilityPayload`); o servidor valida mutação, energia e cooldown em `CombatManager`. Para uma nova habilidade: novo id em `AbilityPayload` (e tamanho dos arrays de recarga: 3 posições, índice = id), `AbilityDef` + ramo em `CombatManager.handle`, tecla em `NivekEclipseClient`, linha no `EclipseHud`. Combos: janela de 40 ticks, golpe final no 3º (estado em `COMBO`).

## APIs e dependências
Fabric Loader 0.19.5, Fabric API 0.136.1+1.21.8 (Attachment API, ItemGroupEvents, ServerTickEvents, CommandRegistrationCallback v2), Loom 1.11-SNAPSHOT, mappings oficiais Mojang, Java 21.

## Convenções
- 1.21.8: cores do GuiGraphics sempre com alfa 0xFF; `mouseClicked(double,double,int)` (muda em 1.21.9+); KeyMapping com categoria String.
- 1.21.8: `PlayerRenderer` fica em `...entity.player` (vira `AvatarRenderer` na 1.21.9); `RenderLayer.render(PoseStack, MultiBufferSource, int, State, float, float)` (muda para `submit` na 1.21.9).
- 1.21.8: dano no servidor com `entity.hurtServer(ServerLevel, DamageSource, float)` (não `hurt`); rede com `PayloadTypeRegistry.playC2S()` + `ServerPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> ...)`.
- Mappings da Mojang: nomes de classes vêm deles (ex.: `ResourceLocation`, `ServerPlayer`), não Yarn.
- Uma classe de registro por tipo (itens, blocos, entidades...), sem duplicar registradores.
- Constantes em `NivekEclipse` (`MOD_ID`, `LOGGER`).
- Não chutar APIs: conferir na versão 1.21.8.

## Problemas e soluções
- O wrapper do Gradle não está no projeto; o CI instala o Gradle 8.14.3 direto. Localmente use Gradle 8.14+ instalado.

## Última fase concluída
Fase 01 (build confirmado). Fases 02, 03, 04.1, 05.1, 06.1 e 06.2 escritas; build pendente (usuário mandou seguir sem confirmar).

## Próxima tarefa exata
1. Rodar o build e corrigir erros das Fases 02 a 06.2. Riscos principais: `hurtServer`, `Entity.hurtMarked/push`, `HudRenderCallback`, callback de render, mixins, `syncWith`, `ServerPlayNetworking`/`ClientPlayNetworking`.
2. Testar: `/eclipse mutation 1`; V e R perto de mobs; encadear 3 golpes em 2 s (combo final); HUD; energia/recarga.
3. Atualizar DEVELOPMENT_STATUS.md com o resultado real.
4. Então: Fase 07 (implantes: cabeça, braços, costas, pernas, peitoral).

## Estado após a revisão estática (2026-11-13)
Build ainda não executado. Se rodar `gradle build --stacktrace` (ou o Actions) e der erro, corrigir pela ordem dos riscos listados em DEVELOPMENT_STATUS e registrar o resultado real lá. Só depois do build verde: testes em jogo e então novas etapas (ataque carregado opcional, Fase 07).

## Fase 07.1 (Implantes) — como mexer
- `implant/ImplantSlot` (enum; ordem = índice usado no pacote e no bit), `implant/ImplantPayload`, `implant/ImplantManager` (toggle validado + `apply` idempotente a cada 20 ticks). Novo implante: novo valor no enum + ramo no `switch` de `ImplantManager.apply`.
- Dados: `ModAttachments.IMPLANTS`, `EclipseData.get/setImplants`. UI: aba IMPLANTS em `EclipseScreen` (`renderImplants` + clique em `mouseClicked`).
- Próxima tarefa: (1) build verde 02–07.1 e testes (`/eclipse mutation 3`, aba IMPLANTS, `/eclipse implants all`); (2) 7.2 visual dos implantes (RenderLayers por slot lendo `IMPLANTS` via mixin/RenderState, como o braço da Fase 05); (3) Fase 08.

## Fase 07.2 (visual dos implantes)
- `client/render/ImplantModel` (geometria; uma peça por slot, `leg` reaproveitada nas duas pernas) e `client/render/ImplantLayer` (desenha conforme a máscara). Registrados em `NivekEclipseClient` (model layer + `helper.register`). Mixins: `PlayerRenderStateMixin` guarda `implants`; `PlayerRendererMixin` preenche.
- Próxima tarefa: build verde 02–07.2 e teste visual (`/eclipse mutation 3`, `/eclipse implants all`, F5); depois Fase 08 (Evolução: cinco estágios, XP, pesquisas, materiais — materiais podem virar custo dos implantes).

## Fase 08.1 (Evolução)
- `evolution/EvolutionManager` (XP, curva `XP_TO_NEXT`, `addXp`, level-up). Dados: `ModAttachments.EVOLUTION_XP`, `EclipseData.get/setXp`. UI: `EclipseScreen.renderEvolution`.
- Próxima tarefa: build verde 02–08.1 e teste (`/eclipse mutation 1`, matar mobs ou `/eclipse xp 100`, aba EVOLUTION); depois 8.2 (materiais dropados por mobs, usados como custo de implantes) e 8.3 (pesquisas/árvore de habilidades nas abas RESEARCH).

## Fase 08.2 (Materiais)
- `ModAttachments.MATERIALS`, `EclipseData.get/setMaterials`, ganho em `EvolutionManager` (AFTER_DEATH), custo em `ImplantSlot.cost` e cobrança/devolução em `ImplantManager.toggle`.
- Próxima tarefa: build verde 02–08.2; testar (`/eclipse mutation 3`, `/eclipse material 50`, aba IMPLANTS); depois 8.3 (pesquisas na aba RESEARCH e árvore de habilidades, gastando material/XP).

## Fase 08.3 (Pesquisas) e workflow
- `research/Research` (enum), `ResearchPayload`, `ResearchManager`; `EclipseData.get/set/hasResearch`; ganchos em `CombatManager` (regen, dano) e `EvolutionManager` (XP); UI em `EclipseScreen.renderResearch`. `ImplantManager.setModifier` agora e publico e reutilizado.
- `.github/workflows/build.yml`: build em todo push/PR, artefato `nivek-eclipse-<run>`, Release automatica (precisa `permissions: contents: write`, ja no arquivo). Para versao fixa: `git tag v0.2.0 && git push --tags`.
- Proxima tarefa: build verde 02–08.3; testar (`/eclipse mutation 4`, `/eclipse material 300`, aba RESEARCH); depois Fase 09 (criaturas e chefes).

## Fase 09.1 (Experimentos)
- `creature/CreatureManager` (spawn periódico, configuração dos mobs, recompensa no AFTER_DEATH). Dados: `ModAttachments.EXPERIMENT_KILLS`, `EclipseData.get/setExperimentKills`. UI: `EclipseScreen.renderBestiary`.
- Próxima tarefa: build verde 02–09.1; testar (`/eclipse mutation 1`, `/eclipse experiment`, matar e ver aba BESTIARY); depois 9.2 (chefe: ex. mob grande com `ServerBossEvent`, fases e drops) e só depois considerar entidades próprias (registro `EntityType` com ResourceKey, renderer com RenderState da 1.21.8, atributos via `FabricDefaultAttributeRegistry`).

## Como continuar
Ler README, DEVELOPMENT_STATUS e CONTINUATION; não recriar o projeto, não apagar sistemas existentes, não alterar versões sem necessidade.
