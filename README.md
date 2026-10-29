# Netflix Android TV Compatibility

Aplicativo Android TV em Java para diagnóstico local de compatibilidade de dispositivos de streaming.

## O que verifica
- Fabricante, modelo, versão Android, SDK e arquitetura.
- Presença do aplicativo Netflix para Android TV (`com.netflix.ninja`).
- Disponibilidade do Widevine DRM e nível de segurança informado pelo provedor DRM.
- Estado de conectividade de rede.
- Orientações para conferir manualmente a certificação Google Play Protect.

## Limitações importantes
- O Android não oferece uma API pública geral que permita a um aplicativo comum consultar com confiabilidade o status de certificação Play Protect.
- O ESN da Netflix é um identificador interno do serviço. Este aplicativo não o lê, gera, altera ou falsifica.
- A presença do Widevine ou do nível L1, isoladamente, não garante que a Netflix autorize reprodução em HD/4K.
- O diagnóstico não altera configurações protegidas nem contorna verificações de segurança.

## Abrir no Android Studio
1. Extraia o ZIP.
2. Abra a pasta `Netflix-AndroidTV-Compatibility` no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Use JDK 17 e Android SDK 35 (o Android Studio pode baixar os componentes necessários).
5. Execute em um Android TV/TV Stick com depuração USB ativada.

## Conferir certificação Google Play Protect
Na TV, abra Google Play Store > Perfil/Configurações > Sobre > Certificação do Play Protect (os nomes podem variar). Se aparecer "Dispositivo não certificado", contate o fabricante/fornecedor para firmware oficial certificado. Não instale firmware aleatório nem compartilhe identificadores do dispositivo publicamente.

## Publicar no GitHub
Crie um repositório vazio chamado `Netflix-AndroidTV-Compatibility`, extraia os arquivos e execute na pasta:
```bash
git init
git add .
git commit -m "Initial Android TV compatibility diagnostics"
git branch -M main
git remote add origin https://github.com/SEU-USUARIO/Netflix-AndroidTV-Compatibility.git
git push -u origin main
```

## Requisitos
- Android Studio
- JDK 17
- Android SDK 35
