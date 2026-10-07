# Ghost SU — implementação do redesenho aprovado

## Escopo aprovado

- APK nativo existente em `/tmp/ghost-su`, pacote `com.ghostsu.manager`, release não-debug e integração própria do KernelSU/ksud/ghostlock intacta.
- Manter as cinco abas sempre navegáveis, inclusive Ghost quando KernelSU não estiver instalado; preservar operações reais e avisos de compatibilidade.
- Recriar as cinco abas em português nas variantes Material e Miuix, adotando a marca Ghost SU, logo A e paleta violeta-azulada comum. Dois estilos visuais sobre a mesma lógica de domínio; não transformar os protótipos HTML em WebViews.
- Início: kernel abreviado (`6.6.118`); ocultar versão do gerenciador, fingerprint, cards de recurso que levam ao GitHub e notificações/checagem de atualização do aplicativo. Manter estado real, dispositivo e segurança; não ocultar incompatibilidade.
- Root: lista real de apps, pesquisa, ordenação, filtro de apps do sistema, registros, badges e acesso ao perfil sem conceder privilégio pela lista.
- Módulos: catálogo, busca, alternância, ações/WebUI, updates **de módulos**, remoção e ZIP; respeitar estado de safe mode/Magisk; nunca afirmar que um módulo ilustrativo está instalado.
- Ghost: usar o estilo selecionado (não forçar Miuix), exibir pronto/ativo segundo leitura do KernelSU, controles Modo seguro e Shizuku inicialmente desligados, perfil e dados reais do dispositivo (POCO F7, SoC reportado, RAM lida em runtime, arquitetura, Android e kernel abreviado); manter fluxo de execução, perfil, logs e erros. Abreviado apenas para exibição, sem alterar string original do kernel usada pelos perfis.
- Ajustes: seleção de UI Material/Miuix e tema, checagem de atualizações de módulos, recursos condicionados ao kernel, log e Sobre. Remover a opção e o job de checagem de atualização **do aplicativo**. Tela Sobre com botão exclusivo `Criador → https://t.me/Root2022`; créditos e licenças em texto sem redirecionamentos ao GitHub.
- Estado ativo de root pós-soft-reboot deriva da API nativa, não do último log; Ghost continua acessível para reativação após reinício completo. Não habilitar root automático nem Shizuku por implicação visual.

## Estrutura

- `app/src/main/java/me/weishu/kernelsu/ui/screen/{home,superuser,module,ghost,settings,about}`: apresentações Compose/fluxos de cada aba.
- `app/src/main/java/me/weishu/kernelsu/ghost`: UI e estado do exploit, mantidos separados do Manager.
- `app/src/main/java/me/weishu/kernelsu/ui/theme` e `data/repository/SettingsRepositoryImpl.kt`: paleta compartilhada e preferências preservadas.
- `app/src/main/res`: ícone aprovado, imagens e textos de localização.
- `app/src/main/jniLibs`, `profile-core`, `uapi`: componentes existentes de root/kernel, preservados.

## Design

**Movimento:** dark-mode utilitário/Material Expressive com acentos Miuix discretos. **Princípios:** status acima de decoração; ações diferenciadas de informação; grupos legíveis; segurança visível e reversível. **Cor:** violeta Ghost como seed compartilhada; verde reservado exclusivamente a status real ativo, cinza a indisponível. **Layout:** cartões por tarefa, cabeçalho leve e barra inferior consistente; Miuix usa superfícies mais arredondadas. **Motivos:** ícone fantasma aprovado, status de sessão, etiquetas de perfil. **Interação:** pesquisa e detalhes nativos, controles com estado acessível, confirmações existentes para operações destrutivas. **Animação:** preservar transições Compose existentes, sem movimentos que escondam estados críticos. **Tipografia:** sans nativa Android, título grande, rótulos curtos e números monoespaçados quando necessário. **Marca:** Ghost SU — Manager KernelSU com reativação de root em uma só interface; técnico, claro, prudente. Microcopy: “Root temporário ativo”; “Após reiniciar, volte à aba Ghost”.
