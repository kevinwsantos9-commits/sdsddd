# DEVELOPMENT_STATUS

- Projeto: NIVEK: ECLIPSE, versão 0.1.0
- Mod id: `nivekeclipse` | Pacote: `com.nivek.eclipse`
- Minecraft: 1.21.8 | Fabric Loader: 0.19.5 | Fabric API: 0.136.1+1.21.8
- Loom: 1.11-SNAPSHOT (Gradle 8.14.3 no CI) | Mappings: oficiais Mojang | Java: 21

## Fases concluídas
- FASE 01 — Fundação (build passou no GitHub Actions, confirmado pelo usuário)

## Fase atual (atualizado 6)
FASE 09 — Criaturas e chefes, etapa 9.1 escrita (NÃO compilada, NÃO testada). Pausa de fase: chefes (9.2) e entidades próprias com modelo/IA/animação NÃO iniciados. Pré-requisito pendente: build verde das Fases 02 a 09.1.

- Fase 09 etapa 9.1 (não verificada): `CreatureManager` — "Experimentos" são mobs vanilla alterados (zumbi "Experimento Falho", aranha "Experimento Rastejante") com vida (20+10*mutação) e dano (3+mutação) reforçados, nome visível, brilho, tag `nivekeclipse_experiment` e alvo travado no jogador; surgem a cada ~30 s com chance 1/3 perto (20-30 blocos) de jogadores com mutação >= 1, só no Overworld à noite, máx. 4 por perto; abate dá +5..8 material e +30 XP e conta no Attachment `experiment_kills` (sync só com o dono); aba BESTIARY mostra abates e as duas criaturas; `/eclipse experiment` força um; `/eclipse reset` zera abates; `MeteorEvent.isNightTime` agora é público
- Limitações 9.1: não são entidades próprias (sem modelo/textura/IA nova), sem chefes, sem drops físicos, aparecem só à noite (zumbi queima de dia), sem balanceamento. Riscos de API a conferir: `new Zombie(Level)`, `new Spider(EntityType, Level)`, `Entity.setGlowingTag`, `Mob.setTarget/setCanPickUpLoot`, `Level.hasChunkAt(BlockPos)`

## Fase atual (8.3)
FASE 08 — Evolução, etapa 8.3 escrita (NÃO compilada, NÃO testada). Fase 08 completa em código, exceto árvore de habilidades ramificada. Pausa de fase: Fase 09 NÃO iniciada. Pré-requisito pendente: build verde das Fases 02 a 08.3.

- Fase 08 etapa 8.3 (não verificada): 5 pesquisas permanentes (`Research`), compradas com material na aba RESEARCH (clique), validadas no servidor (`ResearchPayload`, `ResearchManager`): Tecido Regenerativo (m2, 40: +4 vida máx), Núcleo de Energia (m2, 50: +2 regen de energia), Fibras Densas (m3, 70: +0,3 resistência a repulsão), Instinto Predador (m3, 90: +25% dano de Lâmina/Garras), Metabolismo Evoluído (m4, 120: +50% XP); Attachment `research` (máscara, persistente, copyOnDeath, sync só com o dono); `/eclipse reset` zera
- Limitações 8.3: sem árvore ramificada (lista simples, sem pré-requisitos entre pesquisas), sem reembolso/respec, sem balanceamento
- Workflow: `.github/workflows/build.yml` agora compila a cada push/PR, guarda o jar em Artifacts e cria Release automática (`build-N` em push na main/master; nome da tag em tags `v*`)

## Fase atual (8.2)
FASE 08 — Evolução, etapa 8.2 escrita (NÃO compilada, NÃO testada). Pausa de fase: pesquisas e árvore de habilidades (8.3) NÃO iniciadas. Pré-requisito pendente: build verde das Fases 02 a 08.2.

- Fase 08 etapa 8.2 (não verificada): "Material Eclipse" como contador por jogador (Attachment `materials`, persistente, copyOnDeath, sync só com o dono; NÃO é item); matar mobs com mutação >= 1 dá material (hostis 1-3, outros 0-1) em `EvolutionManager.addMaterials`; instalar implante custa material (`ImplantSlot.cost`: cabeça 10, braços 15, costas 20, pernas 25, peitoral 30) e remover devolve o custo; aba IMPLANTS e OVERVIEW mostram o material; `/eclipse material <n>`; `/eclipse reset` zera; `/eclipse implants all` ignora custo (teste)
- Limitações 8.2: sem itens/drops físicos, sem outros tipos de material, valores sem balanceamento; `/eclipse implants none` não devolve material

## Fase atual (8.1)
FASE 08 — Evolução, etapa 8.1 escrita (NÃO compilada, NÃO testada). Pausa de fase: pesquisas e materiais (8.2/8.3) NÃO iniciados. Pré-requisito pendente: build verde das Fases 02 a 08.1.

- Fase 08 etapa 8.1 (não verificada): os 5 estágios = nível de mutação 1..5; Attachment `evolution_xp` (progresso rumo ao próximo estágio; persistente, copyOnDeath, sync só com o dono); `EvolutionManager`: XP ao matar mobs (vida máxima; x2 para `Enemy`; só jogador com mutação >= 1 e < 5; usa `ServerLivingEntityEvents.AFTER_DEATH`), custo 100/300/700/1500 XP por estágio, evolução automática com mensagem, partículas e som; aba EVOLUTION funcional (estágio, barra de XP, lista dos 5 estágios); `/eclipse xp <n>` para teste; `/eclipse reset` zera o XP
- Limitações 8.1: sem pesquisas, materiais nem árvore de habilidades (8.2/8.3); evolução não tem animação própria além de partículas; estágios 2 a 5 ainda não mudam o visual; valores de XP sem balanceamento; risco de API a conferir: `ServerLivingEntityEvents.AFTER_DEATH`, `Enemy`, `SoundEvents.BEACON_POWER_SELECT`

## Fase atual (7.2)
FASE 07 — Implantes, etapa 7.2 escrita (NÃO compilada, NÃO testada). Pausa de fase: nada além da 7.2 foi iniciado. Pré-requisito pendente: build verde das Fases 02 a 07.2.

- Fase 07 etapa 7.2 (não verificada): visual dos implantes como `RenderLayer` (`ImplantLayer`) com modelo de caixas (`ImplantModel`: visor na cabeça, placa no peitoral, módulo nas costas, manga no braço esquerdo, caneleiras nas duas pernas), desenhados sobre as partes do `PlayerModel` (`head/body/leftArm/leftLeg/rightLeg.translateAndRotate`); máscara de implantes passa ao `PlayerRenderState` por mixin (novos get/set em `EclipseRenderState`); só renderiza com mutação >= 1; visível para todos os jogadores
- Limitações 7.2: peças são caixas provisórias, usam a textura do braço (`textures/entity/bio_arm.png`), sem emissividade, sem animação, não escondem sob armadura, braço slim pode desalinhar ~1px

## Fase atual (7.1)
FASE 07 — Implantes, etapa 7.1 escrita (NÃO compilada, NÃO testada). Pausa de fase: nada além da 7.1 foi iniciado. Pré-requisito pendente: build verde das Fases 02 a 06.2.

- Fase 07 etapa 7.1 (não verificada): 5 slots (`ImplantSlot`: cabeça, braços, costas, pernas, peitoral) com mutação mínima por slot (1,1,2,2,3); Attachment `implants` (máscara de bits, persistente, copyOnDeath, sync com todos); `ImplantPayload` (C2S, alterna instalar/remover; servidor valida mutação); `ImplantManager` aplica efeitos a cada 20 ticks: cabeça = visão noturna; braços +2 ataque; costas +3 queda segura; pernas +10% velocidade e +0,1 pulo; peitoral +4 armadura (modificadores permanentes idempotentes, só valem com mutação >= 1); aba IMPLANTS funcional (clique alterna); comandos `/eclipse implants all|none`; `/eclipse reset` zera implantes
- Limitações 7.1: sem custo/material (Fase 08), sem visual 3D dos implantes (7.2), visão noturna some ~15 s depois de remover a cabeça, valores sem balanceamento. Riscos de API a conferir no build: `Attributes.SAFE_FALL_DISTANCE`/`JUMP_STRENGTH`, `AttributeInstance.hasModifier/addPermanentModifier/removeModifier(ResourceLocation)`, `new MobEffectInstance(holder, int, int, bool, bool, bool)`

## Fase atual (anterior)
FASE 06 — Combate, etapa 6.2 escrita (aguardando build). As Fases 02, 03, 04.1, 05.1 e 06.1 também NÃO tiveram o build confirmado (usuário mandou seguir).

## Fases pendentes
04 ECLIPSE SYSTEM, 04 (conteúdo das abas), 05 (refinamento do braço, ver abaixo), 06 (ataque carregado, opcional), 07 Implantes, 08 Evolução, 09 Criaturas e chefes, 10 Transformações, 11 Finalização

## Funcionalidades implementadas
- Fase 01: projeto Gradle/Loom, entrypoints, workflow
- Fase 02 (não verificada): bloco/item Núcleo Alienígena (textura gerada, lang en/pt_br), dado por jogador `meteor_event_done` (Attachment persistente), gatilho (noite, céu aberto, Overworld, ~1/30 a cada 10 s), queda simulada no servidor com partículas e sons, cratera (raio 7, profundidade 4), núcleo no fundo, comandos `/eclipse meteor` e `/eclipse reset` (op 2)

- Fase 03 (não verificada): ao tocar o núcleo, sequência de 5 s (jogador preso no lugar, 6 tentáculos de partículas, sons, partículas de portal); no fim concede mutação nível 1 (Attachment `mutation_level`, persistente, copyOnDeath) e consome o núcleo; cancela sem perdas se morrer/desconectar; `/eclipse status` e `/eclipse reset` (agora zera também a mutação)

- Fase 04 etapa 4.1 (não verificada): `mutation_level` sincroniza com o cliente (syncWith VAR_INT, targetOnly); tecla G abre o ECLIPSE SYSTEM (só com mutação >= 1); tela com 11 abas e navegação lateral por clique; aba OVERVIEW funcional (jogador, estágio, mutação x/5, barra); demais abas são placeholders; `/eclipse mutation <0-5>` para teste

- Fase 05 etapa 5.1 (não verificada): braço biomecânico (ombro, antebraço, garras) desenhado como RenderLayer sobre o braço direito de qualquer jogador com mutação >= 1 (mutação agora sincroniza com todos os clientes); textura 64x32 gerada por script; animações: idle (pulso + dedos), transformação (cresce em 40 ticks quando a mutação passa de 0 para >=1), ataque (garras abrem com `attackTime`); 2 mixins client (PlayerRenderState ganha dados extras; PlayerRenderer.extractRenderState preenche)

- Fase 06 etapa 6.1 (não verificada): energia Eclipse 0-100 (persistente, sync só com o dono, regen +2 a cada 10 ticks com mutação >= 1), pacote cliente->servidor `AbilityPayload`, `CombatManager` (valida mutação, cooldown 30 ticks, custo 20); Lâmina Biológica (tecla V): golpe em cone de 3,5 blocos, 8 de dano, partículas (varredura + arco de chamas de alma) e som; barra de energia na aba OVERVIEW

- Fase 06 etapa 6.2 (não verificada): Garras (tecla R: custo 8, recarga 10 ticks, 4 de dano, alcance 2,8, riscos de partículas CRIT); combos (janela de 40 ticks, 3 golpes: x1.0, x1.25, x1.5 + golpe final com repulsão e explosão visual); `CooldownPayload` servidor->cliente; HUD no canto inferior direito (barra de energia + recarga de V e R); `AbilityDef` em `CombatManager`

## Bugs conhecidos / limitações
- Fase 06.2: ataque carregado NÃO implementado; HUD usa `HudRenderCallback` (depreciado em versões novas do Fabric API); combo e golpes podem ferir aliados/jogadores; garras sem modelo/animação 3D própria; sem balanceamento
- Fase 05: modelo simples (caixas), pensado para braço clássico (4px; skin slim pode desalinhar 1px); só o braço direito; textura procedural provisória; sem emissividade; animações simples. Mixin com defaultRequire=1: se o descriptor estiver errado, o jogo falha ao abrir com erro claro. `ArmTransformTracker` não limpa entradas de quem sai (vazamento pequeno)
- Fase 04: abas além de OVERVIEW só mostram "Disponivel em breve" (o conteúdo depende dos sistemas das Fases 05–10); sem animações/ícones/texturas de UI; sem toggle da tecla G dentro da tela (ESC fecha)
- Fusão: sem invulnerabilidade durante a sequência; qualquer jogador sem mutação pode usar o núcleo
- Fusão: tentáculos são só partículas (sem modelo); sem câmera cinematográfica (exige código de cliente)
- Meteoro é só partículas (sem entidade/modelo): visível apenas a ~32 blocos
- Sem tremor de tela (exige pacote de rede)
- Núcleo só mostra mensagem ao toque; fusão é a Fase 03
- Núcleo é indestrutível em survival (strength -1)

## Último resultado da compilação
Fases 02, 03, 04.1, 05.1, 06.1 e 06.2: AINDA NÃO COMPILADAS (nenhuma IA até aqui teve Gradle/Maven disponível).

## Revisão estática (sessão de 2026-11-13, sem compilar)
Todo o código das Fases 02 a 06.2 foi relido contra a API da 1.21.8 (Mojang mappings + Fabric API 0.136.1). Não foi encontrado nenhum erro de compilação certo. Ajustes feitos:
- `MeteorEvent`: `level.isNight()` trocado por checagem de `getDayTime()` (13000..23000), porque `isNight` foi renomeado em versões recentes e não deu para conferir na 1.21.8.
- `MeteorEvent`, `FusionEvent`, `CombatManager`: estado estático limpo em `ServerLifecycleEvents.SERVER_STOPPED` (evita segurar `ServerLevel` antigo ao trocar de mundo).
- Cliente: `ArmTransformTracker.clear()` em `ClientPlayConnectionEvents.DISCONNECT` (resolve o vazamento citado nas limitações da Fase 05).
Pontos ainda NÃO confirmados (conferir primeiro se o build falhar): assinatura do callback de `LivingEntityFeatureRendererRegistrationCallback` (`helper.register`), `HudRenderCallback` (depreciado), `Entity.hurtMarked`/`push`, descriptor do mixin `extractRenderState`, `syncWith` + `AttachmentSyncPredicate`.
Observação: o código da 6.2 (Garras, combos, HUD, `CooldownPayload`) já estava neste zip. Não foi escrito nada novo da 6.2 nesta sessão; a próxima etapa só começa depois do build verde.
