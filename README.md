# DiceCraft

**DiceCraft** es un proyecto modular para Minecraft que introduce mecánicas de combate inspiradas en sistemas de rol clásicos, como *Dungeons & Dragons*. Combina un mod desarrollado con **Fabric** y un plugin diseñado para servidores **Paper**, ofreciendo una experiencia única para jugadores y administradores.

---

## Características principales

### **Fabric Mod**
- **Tiradas de dados para ataques**: Los jugadores realizan tiradas de *d20* al atacar, determinando el éxito del ataque según la armadura del objetivo.
- **Bonificaciones por armas**: Cada arma tiene un bonificador que influye en las tiradas de ataque y daño.
- **Daño personalizado**: El daño infligido se calcula con tiradas de dados específicos para cada tipo de arma (por ejemplo, `d4` para ataques básicos, `d12` para armas avanzadas).
- **Soporte para mobs**: Los mobs también realizan tiradas de ataque contra los jugadores, añadiendo un nivel de aleatoriedad al combate.

### **Paper Plugin**
- **Compatibilidad con servidores Paper**: Extiende las mecánicas del mod para servidores multijugador.
- **Configuración avanzada** *(en desarrollo)*:
    - Personalización de valores de armadura.
    - Ajustes en el daño según el tipo de mob.
    - Efectos especiales basados en los resultados de las tiradas.

---

## Instalación

### **Requisitos Previos**
- Minecraft 1.21.4+.
- [Fabric Loader](https://fabricmc.net/use) y [Fabric API](https://modrinth.com/mod/fabric-api) para el mod.
- Un servidor Paper para el plugin.

### **Instalación del Mod (Fabric)**
1. Descarga el archivo `.jar` desde `fabric-mod/build/libs/`.
2. Coloca el archivo `.jar` en la carpeta `mods/` de tu cliente o servidor Fabric.
3. Asegúrate de que Fabric API esté instalado.

### **Instalación del Plugin (Paper)**
1. Descarga el archivo `.jar` desde `paper-plugin/build/libs/`.
2. Coloca el archivo `.jar` en la carpeta `plugins/` de tu servidor Paper.
3. Reinicia el servidor para activar el plugin.

---

## Uso

### **Jugadores**
- Realiza un ataque normal con cualquier arma. Las mecánicas del mod calcularán automáticamente:
    - La tirada de ataque (*d20*).
    - Bonificaciones según el arma utilizada.
    - El daño infligido basado en la tirada de dados correspondiente.

### **Administradores**
- Ajusta las reglas de combate utilizando las configuraciones avanzadas (en desarrollo).
- Implementa personalizaciones desde un archivo de configuración en el servidor.

---

## Ejemplo de mecánicas

1. **Jugador ataca a un mob**:
    - Se realiza una tirada de ataque con un *d20*:
      ```
      Tirada de ataque: 15 + bonificador del arma.
      Valor de armadura del mob: 14.
      ```
    - Si la tirada supera el valor de armadura, el ataque tiene éxito.
    - Luego, se realiza una tirada de daño basada en el arma:
      ```
      Daño infligido: 1d8 (espada de hierro) → Resultado: 6.
      ```

2. **Mob ataca a un jugador**:
    - El mob realiza una tirada de ataque con un *d20* (sin bonificadores).
    - Si la tirada supera la armadura del jugador, el ataque tiene éxito, y el mod calcula el daño.

---

## Desarrollo

### **Compilación**
1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/tuusuario/DiceCraft.git
   cd DiceCraft
