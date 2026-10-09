# Ghost SU

Gerenciador Android independente baseado no Manager do KernelSU, com a aba Ghost integrada para execução de root temporário, diagnóstico do dispositivo, logs e perfis compatíveis.

> **Aviso:** este projeto modifica componentes de baixo nível do Android. Use somente em aparelhos compatíveis e mantenha uma forma de recuperação disponível. Não há garantia de funcionamento em todos os kernels, ROMs ou módulos.

## Principais características

- Pacote do aplicativo: `com.ghostsu.manager`.
- Interface em português brasileiro, com estilos Material e Miuix.
- Abas Início, Root, Módulos, Ghost e Ajustes.
- Root temporário pela aba Ghost, com logs da execução e reativação após reinício quando necessário.
- Informações do dispositivo, kernel, RAM, CPU, arquitetura e segurança do sistema.
- Integração do `ksud` e do módulo KernelSU correspondente ao Ghost SU.
- Proteção para o soft reboot com Vector: a ação só é liberada quando o hook de compatibilidade verificado está presente.
- Modo seguro e Shizuku desligados por padrão.
- Atualizações do aplicativo desativadas; atualizações de módulos permanecem separadas.

## Dispositivos suportados

A lista de perfis e dispositivos suportados está disponível em [português brasileiro](docs/kernel_profiles/SUPPORTED_DEVICES_PT-BR.md), preservando as versões exatas dos kernels e os identificadores dos modelos.

## Build local

Requisitos principais:

- Android SDK com a plataforma e Build Tools usadas pelo projeto.
- JDK 21.
- Android NDK 27.2.12479018.
- Rust e `cargo-ndk` para reconstruir o `ksud` Android.
- CMake e Make para a biblioteca nativa GhostLock.

A assinatura do APK é opcional para o build local e deve ser configurada somente por propriedades locais, conforme `sign.example.properties`. **Nenhuma chave privada ou senha de assinatura é publicada neste repositório.**

Para uma compilação de verificação:

```bash
./gradlew :app:assembleRelease
```

A reconstrução integral do `ksud` deve ser feita a partir de `native/KernelSU/userspace/ksud`, substituindo o binário Android em `app/src/main/jniLibs/arm64-v8a/libksud.so` após a compilação. A biblioteca GhostLock nativa e o extrator são mantidos em `app/src/main/jniLibs/arm64-v8a/` para que o checkout publicado corresponda ao APK distribuído.

## Fontes nativas

- `native/KernelSU/`: fontes GPL-3.0 do KernelSU usadas para o `ksud`, o módulo e componentes relacionados. As alterações de marca do instalador estão preservadas nesse diretório.
- `native/GhostLock/`: fontes Apache-2.0 do GhostLock usadas para o núcleo nativo e perfis de execução, incluindo as adaptações específicas do Ghost SU.
- `profile-core/` e `uapi/`: componentes auxiliares usados pela integração do aplicativo.

Os arquivos de licença e atribuição estão em `LICENSE` e `app/src/main/assets/licenses/`.

## Compatibilidade

A implementação foi preparada e validada no cenário de um POCO F7 com kernel Android 15/6.6.118 e Vector 2.2. Isso não significa compatibilidade automática com outros aparelhos. Módulos que alteram Zygisk, inicialização ou serviços do sistema podem exigir procedimentos próprios de recuperação.

## Identidade e distribuição

Este é o **Ghost SU**, uma variante independente com identidade, pacote e integração próprios. O APK release deve ser assinado pelo mantenedor da distribuição; os usuários precisam manter a mesma assinatura para atualizar uma instalação existente.
