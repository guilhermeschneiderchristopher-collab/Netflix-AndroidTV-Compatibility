package com.example.netflixcompat;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.media.MediaDrm;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;
import java.util.UUID;

public class MainActivity extends Activity {
    private static final UUID WIDEVINE_UUID =
            new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);

    private LinearLayout results;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(28), dp(22), dp(28), dp(22));
        root.setBackgroundColor(Color.rgb(18, 22, 30));
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("Netflix Android TV • Diagnóstico");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER_VERTICAL);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Verificações informativas. Nenhuma configuração do aparelho será alterada.");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(15);
        subtitle.setPadding(0, dp(8), 0, dp(16));
        root.addView(subtitle);

        Button refresh = new Button(this);
        refresh.setText("Executar diagnóstico novamente");
        refresh.setOnClickListener(v -> runDiagnostics());
        root.addView(refresh);

        results = new LinearLayout(this);
        results.setOrientation(LinearLayout.VERTICAL);
        results.setPadding(0, dp(14), 0, 0);
        root.addView(results);

        setContentView(scroll);
        runDiagnostics();
    }

    private void runDiagnostics() {
        results.removeAllViews();

        addSection("INFORMAÇÕES DO DISPOSITIVO");
        addResult("Fabricante", Build.MANUFACTURER);
        addResult("Modelo", Build.MODEL);
        addResult("Marca", Build.BRAND);
        addResult("Android", Build.VERSION.RELEASE);
        addResult("Nível da API", String.valueOf(Build.VERSION.SDK_INT));
        addResult("Build", Build.DISPLAY);
        addResult("Arquiteturas", join(Build.SUPPORTED_ABIS));

        addSection("APLICATIVO NETFLIX");
        boolean installed = isPackageInstalled("com.netflix.ninja");
        addResult("Netflix para Android TV", installed
                ? "Encontrado (com.netflix.ninja)"
                : "Não encontrado");
        addResult("Observação", "A presença do app não confirma autorização de reprodução.");

        addSection("WIDEVINE DRM");
        inspectWidevine();

        addSection("REDE");
        addResult("Conectividade", getNetworkStatus());

        addSection("CERTIFICAÇÃO E ESN");
        addResult("Play Protect", "Não consultável por API pública confiável para apps comuns.");
        addResult("Como conferir", "Google Play Store > Perfil/Configurações > Sobre > Certificação do Play Protect.");
        addResult("ESN Netflix", "Identificador interno. Não é lido, criado nem modificado por este diagnóstico.");

        addSection("PRÓXIMOS PASSOS");
        addResult("Se não certificado", "Solicite ao fabricante firmware oficial certificado para o modelo exato.");
        addResult("Se Widevine indisponível", "Consulte o fabricante sobre suporte DRM oficial. Não instale imagens desconhecidas.");
        addResult("Qualidade de vídeo", "Widevine L1, sozinho, não garante HD/4K na Netflix.");
    }

    private void inspectWidevine() {
        MediaDrm drm = null;
        try {
            if (Build.VERSION.SDK_INT < 18) {
                addResult("Widevine", "API MediaDrm indisponível nesta versão.");
                return;
            }
            drm = new MediaDrm(WIDEVINE_UUID);
            addResult("Widevine", "Provedor DRM disponível");
            String level = safeProperty(drm, "securityLevel");
            addResult("Nível de segurança", level == null ? "Não informado pelo provedor" : level);
            String systemId = safeProperty(drm, "systemId");
            addResult("System ID", systemId == null ? "Não informado" : systemId);
        } catch (Exception | LinkageError e) {
            addResult("Widevine", "Não foi possível inicializar: " + e.getClass().getSimpleName());
        } finally {
            if (drm != null) {
                try { drm.release(); } catch (Exception ignored) { }
            }
        }
    }

    private String safeProperty(MediaDrm drm, String key) {
        try {
            String value = drm.getPropertyString(key);
            return (value == null || value.trim().isEmpty()) ? null : value;
        } catch (Exception | LinkageError ignored) {
            return null;
        }
    }

    private boolean isPackageInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String getNetworkStatus() {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return "Não foi possível consultar";
            Network network = cm.getActiveNetwork();
            if (network == null) return "Sem rede ativa";
            NetworkCapabilities caps = cm.getNetworkCapabilities(network);
            if (caps == null) return "Estado desconhecido";
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return "Wi-Fi conectado";
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return "Ethernet conectada";
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return "Rede móvel conectada";
            return "Rede conectada";
        } catch (Exception e) {
            return "Não foi possível consultar";
        }
    }

    private void addSection(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(Color.rgb(100, 190, 255));
        view.setTextSize(17);
        view.setPadding(0, dp(18), 0, dp(8));
        results.addView(view);
    }

    private void addResult(String label, String value) {
        TextView view = new TextView(this);
        view.setText(label + ": " + value);
        view.setTextColor(Color.WHITE);
        view.setTextSize(15);
        view.setPadding(dp(10), dp(9), dp(10), dp(9));
        view.setFocusable(true);
        view.setBackgroundColor(Color.rgb(34, 41, 53));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(5);
        results.addView(view, lp);
    }

    private String join(String[] values) {
        if (values == null || values.length == 0) return "Não informado";
        StringBuilder out = new StringBuilder();
        for (String value : values) {
            if (out.length() > 0) out.append(", ");
            out.append(value);
        }
        return out.toString();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
