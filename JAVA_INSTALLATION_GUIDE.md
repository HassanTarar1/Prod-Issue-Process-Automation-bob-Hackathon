# Java Installation Guide for Windows

## Problem
You're getting the error: `JAVA_HOME is not set and no 'java' command could be found in your PATH`

This means Java is not installed on your system. This guide will help you install Java and configure it properly.

## Solution: Install Java JDK

### Option 1: Install Java 21 LTS (Recommended for this project)

#### Step 1: Download Java JDK
1. Visit the official Oracle JDK download page:
   - **Oracle JDK 21**: https://www.oracle.com/java/technologies/downloads/#java21
   - OR **OpenJDK 21** (Free, no Oracle account needed): https://adoptium.net/temurin/releases/?version=21

2. For **Adoptium OpenJDK** (Easier, Recommended):
   - Go to: https://adoptium.net/temurin/releases/?version=21
   - Select:
     - **Operating System**: Windows
     - **Architecture**: x64 (for 64-bit Windows)
     - **Package Type**: JDK
     - **Version**: 21 (LTS)
   - Click **Download** for the `.msi` installer

#### Step 2: Install Java
1. Run the downloaded `.msi` installer
2. **IMPORTANT**: During installation, check the box that says:
   - ✅ **"Set JAVA_HOME variable"**
   - ✅ **"Add to PATH"**
   - ✅ **"Associate .jar files"**
3. Click **Next** and complete the installation
4. Default installation path is usually: `C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot\`

#### Step 3: Verify Installation
Open a **NEW** PowerShell window (important - close old ones) and run:
```powershell
java -version
```

Expected output:
```
openjdk version "21.0.x" 2024-xx-xx LTS
OpenJDK Runtime Environment Temurin-21.0.x+x (build 21.0.x+x-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.x+x (build 21.0.x+x-LTS, mixed mode, sharing)
```

Also verify JAVA_HOME:
```powershell
echo $env:JAVA_HOME
```

Expected output:
```
C:\Program Files\Eclipse Adoptium\jdk-21.0.x-hotspot
```

### Option 2: Manual JAVA_HOME Configuration (If installer didn't set it)

If Java is installed but JAVA_HOME is not set:

#### Step 1: Find Java Installation Path
```powershell
Get-ChildItem "C:\Program Files" -Recurse -Filter "java.exe" -ErrorAction SilentlyContinue | Select-Object Directory
```

Common paths:
- `C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot\`
- `C:\Program Files\Java\jdk-21`
- `C:\Program Files\OpenJDK\jdk-21.x.x`

#### Step 2: Set JAVA_HOME Environment Variable

**Method A: Using PowerShell (Permanent)**
```powershell
# Replace with your actual Java path
$javaPath = "C:\Program Files\Eclipse Adoptium\jdk-21.0.1-hotspot"

# Set JAVA_HOME for current user
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", $javaPath, [System.EnvironmentVariableTarget]::User)

# Add to PATH
$currentPath = [System.Environment]::GetEnvironmentVariable("Path", [System.EnvironmentVariableTarget]::User)
$newPath = "$currentPath;$javaPath\bin"
[System.Environment]::SetEnvironmentVariable("Path", $newPath, [System.EnvironmentVariableTarget]::User)
```

**Method B: Using System Properties (GUI)**
1. Press `Win + X` and select **System**
2. Click **Advanced system settings**
3. Click **Environment Variables**
4. Under **User variables**, click **New**:
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Eclipse Adoptium\jdk-21.0.1-hotspot` (your path)
5. Find **Path** variable, click **Edit**
6. Click **New** and add: `%JAVA_HOME%\bin`
7. Click **OK** on all dialogs

#### Step 3: Restart PowerShell
Close all PowerShell/Command Prompt windows and open a new one.

### Option 3: Quick Install Using Chocolatey (Advanced)

If you have Chocolatey package manager:
```powershell
# Install Chocolatey first if not installed
# Run PowerShell as Administrator
choco install openjdk21 -y
```

### Option 4: Quick Install Using winget (Windows 11)

If you have Windows 11 or Windows 10 with winget:
```powershell
# Run PowerShell as Administrator
winget install EclipseAdoptium.Temurin.21.JDK
```

## After Installation: Run the Backend

Once Java is installed and JAVA_HOME is set:

### Step 1: Verify Java Installation
```powershell
java -version
echo $env:JAVA_HOME
```

### Step 2: Navigate to Backend Directory
```powershell
cd "C:\Users\hassa\Downloads\New folder\Prod-Issue-Process-Automation-bob-Hackathon\backend"
```

### Step 3: Build the Project
```powershell
./gradlew build
```

### Step 4: Run the Backend
```powershell
./gradlew bootRun
```

Expected output:
```
> Task :bootRun

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

IBM Bob Hackathon Application
Started MainApplication in X.XXX seconds
```

## Troubleshooting

### Issue 1: "java is not recognized"
**Solution**: Java is not in PATH. Follow the manual configuration steps above.

### Issue 2: "JAVA_HOME is set to an invalid directory"
**Solution**: 
1. Check if the path exists: `Test-Path $env:JAVA_HOME`
2. Make sure it points to the JDK root (not the bin folder)
3. Correct path should contain folders like: bin, lib, include

### Issue 3: "Unsupported class file major version"
**Solution**: You need Java 21 or higher. Check version with `java -version`

### Issue 4: Gradle build fails
**Solution**:
```powershell
# Clean and rebuild
./gradlew clean build --refresh-dependencies
```

### Issue 5: Permission denied on gradlew
**Solution**:
```powershell
# Make gradlew executable
icacls gradlew /grant Everyone:F
```

## Quick Reference Commands

```powershell
# Check Java version
java -version

# Check JAVA_HOME
echo $env:JAVA_HOME

# Check if Java is in PATH
where.exe java

# List all Java installations
Get-ChildItem "C:\Program Files" -Recurse -Filter "java.exe" -ErrorAction SilentlyContinue

# Test Gradle
cd backend
./gradlew --version

# Build project
./gradlew build

# Run backend
./gradlew bootRun

# Clean build
./gradlew clean build
```

## Recommended Java Version for This Project

- **Minimum**: Java 21 (LTS)
- **Recommended**: Java 21 LTS
- **Also works with**: Java 22, Java 23, Java 25 (if available)

The project is configured for Java 25 in build.gradle, but Java 21 will work fine.

## Alternative: Use Java 17 LTS

If you prefer Java 17 (more stable):

1. Download Java 17 from: https://adoptium.net/temurin/releases/?version=17
2. Update `backend/build.gradle`:
   ```gradle
   java {
       toolchain {
           languageVersion = JavaLanguageVersion.of(17)
       }
   }
   ```

## Need Help?

If you're still having issues:
1. Make sure you've closed and reopened PowerShell after installation
2. Restart your computer if environment variables aren't being recognized
3. Check Windows Defender/Antivirus isn't blocking Java
4. Try running PowerShell as Administrator

---

**Once Java is installed, return to the main README.md for project setup instructions.**