# DiceCraft

**DiceCraft** es un proyecto modular para Minecraft que introduce mecánicas de combate inspiradas en sistemas de rol clásicos, como *Dungeons & Dragons*. Combina un mod desarrollado con **Fabric** y un plugin diseñado para servidores **Paper**, ofreciendo una experiencia única para jugadores y administradores.

---

## Características principales

### **Fabric Mod**
- **Tiradas de dados para ataques**: Los jugadores realizan tiradas de *d20* al atacar, determinando el éxito del ataque según la armadura del objetivo.
- **Bonificaciones por armas**: Cada arma tiene un bonificador que influye en las tiradas de ataque y daño.
- **Daño personalizado**: El daño infligido se calcula con tiradas de dados específicos para cada tipo de arma (por ejemplo, `d4` para ataques básicos, `d12` para armas avanzadas).
- **Soporte para mobs**:
    - Los mobs también realizan tiradas de ataque contra los jugadores, añadiendo un nivel de aleatoriedad al combate.
    - **Dados de daño específicos**: Cada tipo de mob tiene dados de daño configurables que determinan el daño que infligen (por ejemplo, `d6` para zombis, `d8` para esqueletos).
    - **Configuración avanzada**: Los valores de daño para cada mob pueden ajustarse en los archivos de configuración.
El daño, los dados y los bonificadores son configurables.


### **Paper Plugin *(en desarrollo)***
- **Compatibilidad con servidores Paper**: Extiende las mecánicas del mod para servidores multijugador.
- **Configuración avanzada** *(en desarrollo)*:
    - Ajustes en el daño según el tipo de mob.
    - Efectos especiales basados en los resultados de las tiradas.

---

## Instalación

### **Requisitos Previos**
- Minecraft 1.21.4+.
- [Fabric Loader](https://fabricmc.net/use) y [Fabric API](https://modrinth.com/mod/fabric-api) para el mod.
- Un servidor Paper para el plugin.

---

## Uso

### **Jugadores**
- Realiza un ataque normal con cualquier arma. Las mecánicas del mod calcularán automáticamente:
    - La tirada de ataque (*d20*).
    - Bonificaciones según el arma utilizada.
    - El daño infligido basado en la tirada de dados correspondiente.
Todo es configurable


### **Administradores**
- Ajusta las reglas de combate utilizando las configuraciones avanzadas (en desarrollo).
- Configura los dados de daño para mobs en el archivo de configuración del mod.

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
    - Si la tirada supera la armadura del jugador, el ataque tiene éxito.
    - El daño se determina con un dado específico configurado para el mob:
      ```
      Daño infligido por zombi: 1d6 → Resultado: 4.
      ```

---

## Desarrollo

### **Compilación**
1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/tuusuario/DiceCraft.git
   cd DiceCraft
