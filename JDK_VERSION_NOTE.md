# JDK Version Note for Android Studio Users

## Important: Android Studio JDK Versions

When setting the Gradle JDK in Android Studio, you may see different options depending on your Android Studio version:

### Common Options You'll See:

1. **JAVA_HOME** - Your system's default Java installation
2. **GRADLE_LOCAL_JAVA_HOME** - A locally configured Java for Gradle
3. **jbr-17** - JetBrains Runtime 17 (older Android Studio versions)
4. **jbr-21** - JetBrains Runtime 21 (newer Android Studio versions like Iguana, Jellyfish, Koala)
5. **Embedded JDK** - Generic label that points to the JBR version

### What You Should Select

✅ **Always select the "jbr" option** (either jbr-17 or jbr-21)

This is Android Studio's embedded JetBrains Runtime, which is:
- Pre-configured and tested with Android development
- Guaranteed to work with Android Gradle Plugin
- Doesn't require separate Java installation
- The recommended option by Google

### Which JBR Version?

Both JBR-17 and JBR-21 work perfectly with this project:
- **JBR-17**: Comes with Android Studio Hedgehog and earlier
- **JBR-21**: Comes with Android Studio Iguana (2023.2.1) and newer

**The project works with both!** Just select whichever "jbr" version you have available.

### How to Set It

1. Open Android Studio
2. Go to: **File → Settings** (on Windows/Linux) or **Android Studio → Preferences** (on Mac)
3. Navigate to: **Build, Execution, Deployment → Build Tools → Gradle**
4. Under "Gradle JDK", select:
   - **jbr-21** (if available) - Recommended for newer Android Studio
   - **jbr-17** (if available) - Fine for older Android Studio
   - **Embedded JDK** (if that's how it's labeled)

### Don't Select JAVA_HOME or GRADLE_LOCAL_JAVA_HOME

Unless you have a specific reason to use a custom Java installation:
- Avoid selecting **JAVA_HOME** (may point to incompatible Java version)
- Avoid selecting **GRADLE_LOCAL_JAVA_HOME** (may not be configured)

The embedded JBR is always the safest choice.

### My Android Studio Version

To check which version you have:
1. **Help → About** (on Windows/Linux) or **Android Studio → About Android Studio** (on Mac)
2. Look for version like:
   - **Hedgehog (2023.1.1)** - Has JBR-17
   - **Iguana (2023.2.1)** - Has JBR-21
   - **Jellyfish (2023.3.1)** - Has JBR-21
   - **Koala (2024.1.1)** - Has JBR-21

### Bottom Line

🎯 **If you see "jbr-21" in your dropdown, select it!**

The project is configured to work with any JDK 17+ version, so both JBR-17 and JBR-21 are perfect.

### Compatibility Table

| Android Studio Version | Default JBR | Project Compatibility |
|------------------------|-------------|----------------------|
| Hedgehog (2023.1.1)   | JBR-17      | ✅ Fully Compatible  |
| Iguana (2023.2.1)     | JBR-21      | ✅ Fully Compatible  |
| Jellyfish (2023.3.1)  | JBR-21      | ✅ Fully Compatible  |
| Koala (2024.1.1)      | JBR-21      | ✅ Fully Compatible  |

All versions work! The project requires JDK 17 minimum, so both JBR-17 and JBR-21 satisfy this requirement.
