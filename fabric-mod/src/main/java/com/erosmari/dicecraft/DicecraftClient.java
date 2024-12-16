package com.erosmari.dicecraft;

import net.fabricmc.api.ClientModInitializer;

public class DicecraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        System.out.println("Dicecraft client-side initialized!");

        // Aquí puedes añadir lógica específica del cliente, como:
        // - Configuración de teclas (KeyBindings)
        // - Registro de partículas o texturas personalizadas
        // - Manejo de interfaces gráficas (GUIs)
    }
}
